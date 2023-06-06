echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'killall -9 -u sd106 rmiregistry'

echo "Transferring data to the RMI registry node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'rm -rf test/HeistToMuseum/*'

sshpass -f password ssh sd106@l040101-ws01.ua.pt 'mkdir -p Public/classes/interfaces'
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'rm -rf Public/classes/interfaces/*'

sshpass -f password scp dirRMIRegistry.zip sd106@l040101-ws01.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the RMI registry node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirRMIRegistry.zip'
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum/dirRMIRegistry ; cp interfaces/*.class /home/sd106/Public/classes/interfaces ; cp set_rmiregistry_d.sh /home/sd106'

echo "Executing program at the RMI registry node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'chmod +x *.sh ; ./set_rmiregistry_d.sh sd106 22150'

echo "RMI registry node shutdown."
