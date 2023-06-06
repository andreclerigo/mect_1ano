echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the assault party 1 node."

sshpass -f password ssh sd106@l040101-ws07.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirAssaultParty.zip sd106@l040101-ws07.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the assault party 1 node."
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirAssaultParty.zip'

echo "Executing program at the assault party 1 node."
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'cd test/HeistToMuseum/dirAssaultParty ; chmod +x *.sh ; ./assParty_com_d.sh sd106 22157 0 7'

echo "Assault Party Server 1 shutdown."