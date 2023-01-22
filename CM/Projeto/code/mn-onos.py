#!/bin/python3

from mininet.net import Mininet
from mininet.cli import CLI
from mininet.log import setLogLevel
from mininet.node import Host, RemoteController, OVSSwitch
#from mininet.link import TCLink, Intf   # if I want to add a TCLink to a switch on addLink
from mininet.topo import Topo

class TopoExample(Topo):
    def __init__(self, *args, **kwargs):
        Topo.__init__(self, *args, **kwargs)

        s1 = self.addSwitch('s1', mac="00:00:00:00:00:01", protocols="OpenFlow13")
        s2 = self.addSwitch('s2', mac="00:00:00:00:00:02", protocols="OpenFlow13")
        
        h1 = self.addHost('h1', ip='10.0.0.1/24')
        h2 = self.addHost('h2', ip='10.0.0.2/24')
        h3 = self.addHost('h3', ip='10.0.0.3/24')

        self.addLink(s1, s2, port1=1, port2=1) # connect s1/1 <-> s2/1

        self.addLink(s1, h1, port1=2, port2=1) # connect s1/2 <-> h1
        self.addLink(s1, h3, port1=3, port2=1)
        self.addLink(s2, h2, port1=2, port2=1) # connect s2/2 <-> h2

def main():
    net = Mininet(topo=TopoExample(), switch=OVSSwitch, cleanup=True, controller=RemoteController('c0', ip='localhost', port=6653, protocols="OpenFlow13"))

    net.start()

    CLI(net)

    net.stop()


if __name__ == "__main__":
    setLogLevel('info')
    main()
