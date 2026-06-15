#!/usr/bin/env bash
# https://github.com/vishnubob/wait-for-it (trimmed and included)
set -e

TIMEOUT=30

usage() {
  echo "Usage: $0 host:port [-t timeout] -- command args"
  exit 1
}

if [ $# -lt 1 ]; then
  usage
fi

HOSTPORT=$1
shift

if [ "$1" = "-t" ]; then
  shift
  TIMEOUT=$1
  shift
fi

if [ $# -gt 0 ] && [ "$1" = "--" ]; then
  shift
fi

HOST=$(echo $HOSTPORT | cut -d: -f1)
PORT=$(echo $HOSTPORT | cut -d: -f2)

start_ts=$(date +%s)
while :; do
  if nc -z "$HOST" "$PORT" 2>/dev/null; then
    break
  fi
  now=$(date +%s)
  elapsed=$((now - start_ts))
  if [ $elapsed -ge $TIMEOUT ]; then
    echo "Timed out waiting for $HOSTPORT after ${TIMEOUT}s"
    exit 1
  fi
  sleep 1
done

exec "$@"
