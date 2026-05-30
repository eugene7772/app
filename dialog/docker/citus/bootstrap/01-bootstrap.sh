#!/bin/sh
set -e

export PGPASSWORD="$POSTGRES_PASSWORD"
WORKER_HOSTS="${WORKER_HOSTS:-worker}"

echo "Waiting for coordinator..."
until pg_isready -h citus-master -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  sleep 2
done

echo "Waiting for worker nodes to accept connections..."
for NAME in $WORKER_HOSTS; do
  until pg_isready -h "$NAME" -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
    sleep 2
  done
done

echo "Setting coordinator host..."
psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" \
  -c "CREATE EXTENSION IF NOT EXISTS citus;"
psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" \
  -c "SELECT citus_set_coordinator_host('citus-master');"

echo "Registering workers..."
for NAME in $WORKER_HOSTS; do
  psql -h citus-master -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<SQL
DO \$\$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_dist_node
    WHERE nodename = '$NAME' AND nodeport = 5432
  ) THEN
    PERFORM citus_add_node('$NAME', 5432);
  END IF;
END
\$\$;
SQL
done
