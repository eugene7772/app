#!/bin/sh
set -e

export PGPASSWORD="$POSTGRES_PASSWORD"
EXPECTED_WORKERS="${WORKER_COUNT:-5}"

echo "Waiting for coordinator..."
until pg_isready -h citus-master -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
  sleep 2
done

echo "Waiting for $EXPECTED_WORKERS worker containers to be running..."
while true; do
  COUNT=$(docker ps \
    --filter "label=com.docker.compose.project=${COMPOSE_PROJECT_NAME}" \
    --filter "label=com.docker.compose.service=worker" \
    --format '{{.Names}}' | wc -l)

  echo "Currently running workers: $COUNT / $EXPECTED_WORKERS"

  if [ "$COUNT" -ge "$EXPECTED_WORKERS" ]; then
    break
  fi

  sleep 2
done

echo "Waiting for every worker to accept connections..."
docker ps \
  --filter "label=com.docker.compose.project=${COMPOSE_PROJECT_NAME}" \
  --filter "label=com.docker.compose.service=worker" \
  --format '{{.Names}}' | while read NAME; do
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
docker ps \
  --filter "label=com.docker.compose.project=${COMPOSE_PROJECT_NAME}" \
  --filter "label=com.docker.compose.service=worker" \
  --format '{{.Names}}' | while read NAME; do
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