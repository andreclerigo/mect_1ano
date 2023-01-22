#!/bin/python3

from mininet.net import Mininet
from mininet.cli import CLI
from mininet.log import setLogLevel
from mininet.node import Host, RemoteController, OVSSwitch
#from mininet.link import TCLink, Intf   # if I want to add a TCLink to a switch on addLink
from mininet.topo import Topo

"""
Notacao: hij: host j of VLAN i
s1-s2: vlans 1,2,3
s1-s3: vlans 1,3
s3-s2: vlans 2

  h11                 h12
     \               /
      \             /  
       \   ____    /
 h21--- s1 ==== s2 ---h22
      / ||     /   \ 
 h24_/  ||    /     \_h32
(wrong) ||   /
  (IP)  ||  /
        || /
        ||/ 
        s3
       /|\ 
      / | \ 
     /  |  \ 
   h31 h23  h13
"""

class TopoExample(Topo):
    def __init__(self, *args, **kwargs):
        Topo.__init__(self, *args, **kwargs)
        
        # creating switches
        s1 = self.addSwitch('s1', dpid='1')
        s2 = self.addSwitch('s2', dpid='2')
        s3 = self.addSwitch('s3', dpid='3')
        
        # creagting hosts 
        h11 = self.addHost('h11', ip='10.10.0.1/24')
        h12 = self.addHost('h12', ip='10.10.0.2/24')
        h13 = self.addHost('h13', ip='10.10.0.3/24')
        h21 = self.addHost('h21', ip='10.20.0.1/24')
        h22 = self.addHost('h22', ip='10.20.0.2/24')
        h23 = self.addHost('h23', ip='10.20.0.3/24')
        # wrong ip for h24 using vlan10 address range
        h24 = self.addHost('h24', ip='10.10.0.4/24')
        h31 = self.addHost('h31', ip='10.30.0.1/24')
        h32 = self.addHost('h32', ip='10.30.0.2/24')

        # Connections for s1
        self.addLink(s1, h11, port1=1, port2=1)
        self.addLink(s1, h21, port1=2, port2=1)
        self.addLink(s1, h24, port1=3, port2=1)
        
        # Connections for s2
        self.addLink(s2, h12, port1=1, port2=1)
        self.addLink(s2, h22, port1=2, port2=1)
        self.addLink(s2, h32, port1=3, port2=1)
 
        # Connections for s3
        self.addLink(s3, h13, port1=1, port2=1)
        self.addLink(s3, h23, port1=2, port2=1)
        self.addLink(s3, h31, port1=3, port2=1)

        # Connections between switches
        self.addLink(s1, s2, port1=5, port2=5)
        self.addLink(s1, s3, port1=4, port2=4)
        self.addLink(s2, s3, port1=4, port2=5)


def main():
    net = Mininet(topo=TopoExample(), switch=OVSSwitch, cleanup=True, controller=RemoteController('c0', ip='localhost', port=6653, protocols="OpenFlow13"))

    net.start()

    CLI(net)

    net.stop()


if __name__ == "__main__":
    setLogLevel('info')
    main()
