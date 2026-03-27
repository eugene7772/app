#!/bin/bash
set -e

echo "Starting restore..."

pg_restore \
  -U "$POSTGRES_USER" \
  -d "$POSTGRES_DB" \
  --no-owner \
  --clean \
  --if-exists \
  /dump/dump.backup

echo "Restore done"