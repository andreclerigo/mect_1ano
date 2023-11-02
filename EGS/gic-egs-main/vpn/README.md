# VPN

To access the k3s cluster you need to use one or two VPNs.
Inside the university (while connected to the eduroam), you only need to use
OpenVPN to connect to the Cluster.

Outside of the university (for example your home connection), you need to connect
to the [University VPN](https://go.ua.pt/) first.

In Linux, there is a conflicting route being injected.
To overcome this you can use the `90_fix_vpn.sh` configuration.
Simply copy this file into `/etc/NetworkManager/dispatcher.d/`.

Configure the OpenVPN using Network Manager and the provided files.
Add an additional DNS to the configuration `10.110.1.254`.
This will allow you to access the deployments in the cluster from a name.