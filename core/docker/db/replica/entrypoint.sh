#!/bin/bash
set -e

if [ ! -s "$PGDATA/PG_VERSION" ]; then
  rm -rf "$PGDATA"/*
  SLOT_NAME="${REPLICATION_SLOT:-$(hostname | tr '-' '_')}"

  until pg_isready -h db-primary -p 5432 -U "$POSTGRES_USER" -d "$POSTGRES_DB"; do
    sleep 2
  done

  PGPASSWORD="$POSTGRES_PASSWORD" psql \
    -h db-primary \
    -U "$POSTGRES_USER" \
    -d "$POSTGRES_DB" \
    -v ON_ERROR_STOP=1 \
    -c "SELECT pg_create_physical_replication_slot('${SLOT_NAME}', true) WHERE NOT EXISTS (SELECT 1 FROM pg_replication_slots WHERE slot_name = '${SLOT_NAME}');"

  PGPASSWORD="$REPLICATION_PASSWORD" pg_basebackup \
    -h db-primary \
    -D "$PGDATA" \
    -U "$REPLICATION_USER" \
    -S "$SLOT_NAME" \
    -v \
    -P \
    -R \
    -X stream

  chown -R postgres:postgres "$PGDATA"
  chmod 700 "$PGDATA"
fi

exec gosu postgres postgres
