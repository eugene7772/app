#!/bin/bash
set -e

export PGPASSWORD="$POSTGRES_PASSWORD"

echo "Waiting for coordinator..."
until pg_isready -h citus-master -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  sleep 2
done

echo "Waiting for worker 1..."
until pg_isready -h citus-worker-1 -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  sleep 2
done

echo "Waiting for worker 2..."
until pg_isready -h citus-worker-2 -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  sleep 2
done

echo "Ensuring Citus extension exists..."
psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "CREATE EXTENSION IF NOT EXISTS citus;"

echo "Setting coordinator host..."
psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT citus_set_coordinator_host('citus-master');"

echo "Adding workers if missing..."
psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<'SQL'
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_dist_node
    WHERE nodename = 'citus-worker-1' AND nodeport = 5432
  ) THEN
    PERFORM citus_add_node('citus-worker-1', 5432);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_dist_node
    WHERE nodename = 'citus-worker-2' AND nodeport = 5432
  ) THEN
    PERFORM citus_add_node('citus-worker-2', 5432);
  END IF;
END
$$;
SQL

echo "Distributing tables if needed..."
psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<'SQL'
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_dist_partition
    WHERE logicalrelid = 'public.dialog'::regclass
  ) THEN
    PERFORM create_distributed_table('public.dialog', 'id');
  END IF;

  IF NOT EXISTS (
    SELECT 1
    FROM pg_dist_partition
    WHERE logicalrelid = 'public.message'::regclass
  ) THEN
    PERFORM create_distributed_table('public.message', 'dialog_id', colocate_with => 'public.dialog');
  END IF;
END
$$;
SQL

echo "Bootstrap done"