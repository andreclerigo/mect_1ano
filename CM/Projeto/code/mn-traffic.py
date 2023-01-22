# Start by importing the necessary modules
from mininet.net import Mininet
from mininet.node import RemoteController, OVSSwitch
from mininet.cli import CLI
from mininet.log import lg, info, setLogLevel
import threading

# Create a Mininet object and add a Faucet controller
net = Mininet(switch=OVSSwitch)
c1 = net.addController(name='c1', controller=RemoteController, ip='localhost', port=6653)

# Add an OVS switch and connect it to the controller
s1 = net.addSwitch('s1')

# Add two hosts and connect them to the switch
h1 = net.addHost('h1')
h2 = net.addHost('h2')
net.addLink(h1, s1, port1=1, port2=1)
net.addLink(h2, s1, port1=1, port2=2)
setLogLevel('info')

# Start the Mininet network
net.start()

# Set up the rules on the OVS switch to give priority to traffic to h2
#s1.cmd('ovs-ofctl add-flow s1 priority=10,in_port=1,actions=output:2')
#s1.cmd('ovs-ofctl add-flow s1 priority=5,in_port=2,actions=output:1')

# Set up a trigger to monitor the traffic to h2
def traffic_monitor(h2): 
    while True:
        #h1.cmd('iperf -c 10.0.0.2 -b 1M -t 2')
        # Calculate the percentage of available bandwidth that is being used to send traffic to h2
        traffic = h2.cmd('ifstat -i h2-eth1 1 1')
        try:
            #info(traffic.split())
            #info('\n')
            traffic = float(traffic.split()[-2])
        except:
            traffic = 0
    
        if traffic > 1:
            info(traffic)
            info('\n')
        if traffic > 50:
            # If more than 50% of the available bandwidth is being used, update the rule on the OVS switch to give even higher priority to traffic to h2
            info('aconteceu\n')
            s1.cmd('ovs-ofctl add-flow s1 priority=15,in_port=1,actions=drop')
         
# Start the traffic monitor
#traffic_monitor()
#net.addTask(traffic_monitor)

# Start the traffic monitor in a separate thread
thread = threading.Thread(target=traffic_monitor, args=(h2,))
thread.start()

# Enter the Mininet CLI to interact with the network
CLI(net)

# Stop the Mininet network
net.stop()
