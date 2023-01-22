from mininet.topo import Topo
from mininet.node import RemoteController, OVSSwitch
from mininet.net import Mininet
from mininet.cli import CLI
import requests

# Create a custom topology
class MyTopo(Topo):
    def __init__(self, *args, **kwargs):
        Topo.__init__(self, *args, **kwargs)
        
        # creating switches
        s1 = self.addSwitch('s1', dpid='1')
        s2 = self.addSwitch('s2', dpid='2')
        s3 = self.addSwitch('s3', dpid='3')
        s4 = self.addSwitch('s4', dpid='4')

        # creagting hosts 
        h11 = self.addHost('h11', ip='10.10.0.1/24')
        h12 = self.addHost('h12', ip='10.10.0.2/24')
        h13 = self.addHost('h13', ip='10.10.0.3/24')
        h14 = self.addHost('h14', ip='10.10.0.4/24')
        h21 = self.addHost('h21', ip='10.20.0.1/24')
        h22 = self.addHost('h22', ip='10.20.0.2/24')
        h23 = self.addHost('h23', ip='10.20.0.3/24')
        # wrong ip for h24 using vlan10 address range
        h24 = self.addHost('h24', ip='10.20.0.4/24')
        h25 = self.addHost('h25', ip='10.20.0.5/24')
        h31 = self.addHost('h31', ip='10.30.0.1/24')
        h32 = self.addHost('h32', ip='10.30.0.2/24')
        h33 = self.addHost('h33', ip='10.30.0.3/24')
        h34 = self.addHost('h34', ip='10.30.0.4/24')    
        
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

        # Connections for s4
        self.addLink(s4, h14, port1=1, port2=1)
        self.addLink(s4, h25, port1=2, port2=1)
        self.addLink(s4, h33, port1=3, port2=1)
        self.addLink(s4, h34, port1=5, port2=1)

        # Connections between switches
        self.addLink(s1, s2, port1=5, port2=5)
        self.addLink(s1, s3, port1=4, port2=4)
        self.addLink(s2, s3, port1=4, port2=5)
        self.addLink(s2, s4, port1=6, port2=4)

def main():
    net = Mininet(topo=MyTopo(), switch=OVSSwitch, cleanup=True, controller=RemoteController('c0', ip='localhost', port=6653, protocols="OpenFlow13"))
    
    # Set the parameters for the traffic management configuration
    data = {
      "acls": [
        {
          "rule": {
            "actions": {
              "allow": 1
            },
            "dl_type": 2048,
            "nw_proto": 6
          },
          "name": "tcp_allow"
        }
      ],
      "dp_id": "0x1",
      "interfaces": [
        {
          "name": "eth1",
          "native_vlan": 1
        }
      ]
    }

    # Set the parameters for the traffic management configuration
    data2 = {
        "dp_id": "0x1",
        "interfaces": [
            {
            "name": "eth1",
            "native_vlan": 1
            }
        ]
    }

    api_url = "http://localhost:9302/api/v1/config"

    response = requests.post(api_url, json=data)
    print(response.status_code)
    
    response = requests.post(api_url, json=data2)
    print(response.status_code)
    
    api_url = "http://localhost:9302/api/v1/switches"
    params = {
        "dp_id": "0x1"
    }

    response = requests.get(api_url, params=params)
    print(response.text)
    print(response.status_code)

    net.start()

    CLI(net)

    net.stop()

if __name__ == "__main__":
    main()