# Clients
## Client Z 
### Z1
Net range: <br>
(10.0.1.0 - 10.0.1.255 /24) <br>
Address: <br> 
10.0.1.1 /24 <br>
### Z2 
Net range: <br>
(10.0.2.0 - 10.0.2.255 /24) <br>
Address: <br>
10.0.2.2 /24 <br>
### Z3 
Net range:<br>
(10.0.3.0 - 10.0.3.255 /24) <br>
Address: 10.0.3.3 /24<br>

## Client Y
### Y - VLAN 20
Net range: <br>
(10.1.1.0 - 10.1.1.255 /24) <br>
**Y1-VLAN20** <br>
Address: <br>
10.1.1.1 /24 <br>
**Y2-VLAN20** <br>
Address: <br>
10.1.1.2 /24 <br>

### Y - VLAN 30 
Net range: <br>
(10.1.2.0 - 10.1.2.255 /24) <br>
**Y1-VLAN30** <br>
Address: <br>
10.1.2.1 /24 <br>
**Y2-VLAN30** <br>
Address: <br>
10.1.2.2 /24 <br>

## Client X
Net range:
(10.2.0.0 - 10.2.3.255 /22)
### X1
Address:
10.2.1.1 /22
### X2
Address:
10.2.2.2 /22
### X3
Address:
10.2.3.3 /22

<br>

# LISBOA

## Router Lisboa
### f0/0
**To Router Coimbra** <br>
Address: 10.0.0.1 /29
### f0/1
**To DCL1** <br>
Address: 10.0.0.9 /29
### lo0
Address: 10.0.1.11 /32

## DCL1
### eth0
**To Router Lisboa** <br>
Address: 10.0.0.10 /29
### eth1
**To Client X1** <br>
Address: 10.2.1.4 /22
### eth2
**To Client Y1** <br>
1. VLAN 20 - eth2.2 
Address: 10.1.1.4 /24
2. VLAN 30 - eth2.3
Address: 10.1.2.4 /24
### lo0
Address:  10.0.1.1 /32

# Coimbra

## Router Coimbra
### f0/0
**To Router Lisboa** <br>
Address: 10.0.0.2 /29
### f0/1
**To Router Porto** <br>
Address: 10.0.0.17 /29
### f1/0
**To Router Aveiro** <br>
Address: 10.0.0.25 /29
### f1/1
**To DCC1** <br>
Address: 10.0.0.33 /29
### lo0
Address:  10.0.1.3 /32

## DCC1
### f0/0
**To Router Coimbra** <br>
Address: 10.0.0.34 /29
### f0/1
**To Client Z1** <br>
Address: 10.0.1.2 /24
### lo0
Address:  10.0.1.4 /32

# Porto

## Router Porto
### f0/0
**To Router Coimbra** <br>
Address: 10.0.0.18 /29
### f0/1
**To Router Aveiro** <br>
Address: 10.0.0.41 /29
### f1/0
**To DCP1** <br>
Address: 10.0.0.49 /29
### f1/1
**To DCP2** <br>
Address: 10.0.0.57 /29
### lo0
Address:  10.0.1.5 /32

## DCP1
### f0/0
**To Router Porto** <br>
Address: 10.0.0.50 /29
### f0/1
**To Client Z2** <br>
Address: 10.0.2.1 /24
### lo0
Address:  10.0.1.6 /32

## DCP2
### eth0
**To Router Porto** <br>
Address: 10.0.0.58 /29
### eth1
**To Client Y2** <br>
1. VLAN 20 - eth1.2
Address: 10.1.1.5 /24
2. VLAN 30 - eth1.3
Address: 10.1.2.5 /24
### eth2
**To Client X2** <br>
Address: 10.2.2.4 /22
### lo0
Address:  10.0.1.7 /32


# Aveiro

## Router Aveiro
### f0/0
**To Router Coimbra** <br>
Address: 10.0.0.26 /29
### f0/1
**To Router Porto** <br>
Address: 10.0.0.42 /29
### f1/0
**To DCA1** <br> 
Address: 10.0.0.65 /29
### f1/1
**To DCA2** <br>
Address: 10.0.0.73 /29
### lo0
Address:  10.0.1.8 /32

## DCA1
### eth0
**To Router Aveiro** <br>
Address: 10.0.0.66 /29
### eth1
**To Client X3** <br>
Address: 10.2.3.4 /22
### lo0
Address:  10.0.1.9 /32

## DCA2
### f0/0
**To Router Aveiro** <br>
Address: 10.0.0.74 /29
### f0/1
**To Client Z3** <br>
Address: 10.0.3.1 /24
### lo0
Address:  10.0.1.10 /32