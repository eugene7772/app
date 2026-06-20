#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<SQL
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${REPLICATION_USER}') THEN
    CREATE ROLE ${REPLICATION_USER} WITH REPLICATION LOGIN PASSWORD '${REPLICATION_PASSWORD}';
  END IF;
END
\$\$;
SQL

cat >> "$PGDATA/postgresql.conf" <<CONF
wal_level = replica
max_wal_senders = 10
max_replication_slots = 10
wal_keep_size = 4096MB
hot_standby = on
listen_addresses = '*'
CONF

cat >> "$PGDATA/pg_hba.conf" <<CONF
host replication ${REPLICATION_USER} all md5
host all all all scram-sha-256
CONF
