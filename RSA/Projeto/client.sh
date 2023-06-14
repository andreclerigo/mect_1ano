#!/bin/bash

# Checking the input parameters
if [ $# -lt 3 ]; then
    echo "Usage: $0 <Server IP> <Remote Path> <Distance> [<Packet Size>]"
    exit 1
fi

# Server IP, distance, and remote path
SERVER_IP=$1
DISTANCE=$3
REMOTE_PATH=$2
SERVER_PORT="5201"

# Optional packet size
PACKET_SIZE=${4:-}

# JSON file name
JSON_FILE="json_file_${DISTANCE}"
[ -n "$PACKET_SIZE" ] && JSON_FILE+="_ps${PACKET_SIZE}"

echo "JSON file created."

# Perform Ping, RTT, Jitter, PDR tests
PING_COMMAND="ping -c 4"
IPERF_COMMAND="iperf3 -c $SERVER_IP -p $SERVER_PORT -u -i 0.1 -b 65M"
if [ -n "$PACKET_SIZE" ]; then
    PING_COMMAND+=" -s $PACKET_SIZE"
    IPERF_COMMAND+=" -l $PACKET_SIZE"
fi
echo "Retrieving RTT Data..."

RTT_DATA=$($PING_COMMAND $SERVER_IP | tail -1 | awk '{print $4}')
RTT_MIN=$(echo $RTT_DATA | cut -d '/' -f 1)
RTT_AVG=$(echo $RTT_DATA | cut -d '/' -f 2)
RTT_MAX=$(echo $RTT_DATA | cut -d '/' -f 3)
RTT_MDEV=$(echo $RTT_DATA | cut -d '/' -f 4)
echo "RTT Data retrieved."

echo "Retrieving Jitter and Throughput from iperf..."
IPERF_OUTPUT=$($IPERF_COMMAND)
echo -e "iperf3 output:\n$IPERF_OUTPUT"
JITTER=$(echo "$IPERF_OUTPUT" | grep 'receiver' | awk '{print $9}')
THROUGHPUT=$(echo "$IPERF_OUTPUT" | grep 'receiver' | awk '{print $7}')
echo "Jitter and Throughput retrieved."
echo "Retrieving PDR from iperf..."

LOST_DATAGRAMS=$(echo "$IPERF_OUTPUT" | grep -oP '(\d+)(?=\/\d+ \(\d+%\))' | tail -1)
TOTAL_DATAGRAMS=$(echo "$IPERF_OUTPUT" | grep -oP '(?<=\/)(\d+)(?= \(\d+%\))' | tail -1)
# Check if TOTAL_DATAGRAMS is not 0 to avoid division by zero
if [ $TOTAL_DATAGRAMS -ne 0 ]; then
    PDR=$((100 - (100 * $LOST_DATAGRAMS / $TOTAL_DATAGRAMS)))
else
    PDR=0
fi
echo "PDR Retrieved."

# Write test results to JSON file
# ping comes with min/avg/max/mdev
echo "Writing to JSON file..."
echo "{
  \"RTT_MIN\": \"$RTT_MIN\",
  \"RTT_AVG\": \"$RTT_AVG\",
  \"RTT_MAX\": \"$RTT_MAX\",
  \"RTT_MDEV\": \"$RTT_MDEV\",
  \"JITTER\": \"$JITTER\",
  \"THROUGHPUT\": \"$THROUGHPUT\",
  \"PDR\": \"$PDR\"
}" > $JSON_FILE

echo "Data saved to $JSON_FILE"

# Concatenate the remote path with another string
REMOTE_PATH="/home/rpi-3/rpi2/$REMOTE_PATH"
mv $JSON_FILE $REMOTE_PATH
#scp $JSON_FILE rpi-3@10.1.1.3:$REMOTE_PATH
#rm $JSON_FILE

