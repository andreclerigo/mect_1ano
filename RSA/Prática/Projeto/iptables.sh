#!/bin/bash

hostgwint="$1"
apint="$2"

if [ -z "$hostgwint" ] || [ -z "$apint" ]; then
  echo "Usage: $0 <hostgwint> <apint>"
  exit 1
fi

iptables -t nat -A POSTROUTING -o $hostgwint -j MASQUERADE
iptables -A FORWARD -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT
iptables -A FORWARD -i $apint -o $hostgwint -j ACCEPT
