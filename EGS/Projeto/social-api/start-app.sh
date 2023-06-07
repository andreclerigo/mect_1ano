#!/bin/sh

set -e

/wait-for-rabbitmq.sh rabbitmq
/wait-for-graylog.sh graylog

echo "Starting app"
npm run dev
