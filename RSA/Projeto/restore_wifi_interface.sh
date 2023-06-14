#!/bin/bash
if [ -z "$1" ]; then
	echo "Not enough input arguments provided"
	echo "Please specify interface name"
	echo "Example: ./restore_wifi_interface wlan0"
else
	# Remove interface from BATMAN-adv and disable bat0
	sudo batctl gw_mode off
	sudo batctl if del $1
	sudo ip link set down dev bat0
	sudo batctl meshif bat0 interface destroy

	# Stop the IBSS mode on the Wi-Fi interface
	sudo ip link set $1 down
	sudo iw $1 set type managed
	sudo ip link set $1 up

	sudo systemctl start dhcpcd.service
	sleep 5
	sudo wpa_cli -i wlan0 reconfigure
	sleep 60
	sudo ip route add default via 192.168.154.79 dev wlan0 metric 100

	# Add Google DNS servers to /etc/resolv.conf
	echo "nameserver 8.8.8.8" | sudo tee /etc/resolv.conf > /dev/null
	echo "nameserver 8.8.4.4" | sudo tee -a /etc/resolv.conf > /dev/null
fi
