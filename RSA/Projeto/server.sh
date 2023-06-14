#!/bin/bash

# Server port
SERVER_PORT="5201"

# Start iperf server
# The -D flag is used to run it in the background (as a daemon)
iperf3 -s -p $SERVER_PORT -D

echo "The server is listening in the background on port $SERVER_PORT..."
