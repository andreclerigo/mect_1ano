#!/bin/bash
if [ -z "$1" ] || [ -z "$2" ]; then
	echo Not enough input arguments provided
	echo Please specify interface name and IP/NETMASK
	echo Example: ./create_batman_interface wlan0 10.1.1.1/24

else
	sudo systemctl stop dhcpcd.service
	sudo ip link set $1 down
	sudo iw $1 set type ibss
	sudo ifconfig $1 mtu 1500
	sudo iwconfig $1 channel 11
	sudo ip link set $1 up
	sudo iw $1 ibss join adhoctest 2462

	sudo modprobe batman-adv
	sudo batctl if add $1
	sudo ip link set up dev $1
	sudo ip link set up dev bat0
	sudo ifconfig bat0 $2

	sudo batctl gw_mode server

	sleep 45

	sudo /home/rpi-3/iptables.sh eth0 bat0	
	echo "Finished setting BATMAN and IPtables"
fi
