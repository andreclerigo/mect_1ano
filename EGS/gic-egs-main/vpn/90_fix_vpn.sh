#!/bin/bash
 
IF=$1
STATUS=$2
VPN_IP="193.136.82.35"

function remove_default_device {
  if [[ -d /sys/class/net/tunsnx ]]; then
    logger -s "TUNSNX exists..."
    DEVICE=$(ip route get $VPN_IP | awk -F"dev" '/dev/{print $NF;}' | awk '{print $1;}')
    logger -s "DEVICE for $VPN_IP: $DEVICE"
    
    if [[ "tunsnx" != "$DEVICE" ]]; then
      ROUTE=$(ip route get $VPN_IP | awk -F"src" '/dev/{print $1;}')
      logger -s "$ROUTE"
      ip route del $ROUTE
    fi
  fi
}

if [ "$IF" == "tun0" ]
then
    case "$2" in
        up)
        logger -s "NM Script up triggered"
        remove_default_device
        ;;
        down)
        logger -s "NM Script down triggered"
        ;;
        pre-up)
        logger -s "NM Script pre-up triggered"
        ;;
        post-down)
        logger -s "NM Script post-down triggered"
        ;;
        *)
        ;;
    esac
fi