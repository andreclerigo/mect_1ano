#!/bin/sh

set -e

host="$1"
shift
cmd="$@"

until nc -z "$host" 9000; do
  >&2 echo "Graylog is unavailable - sleeping"
  sleep 5
done

>&2 echo "Graylog is up - executing command"
exec $cmd