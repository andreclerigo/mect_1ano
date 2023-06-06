echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the ordinary thieves node."

sshpass -f password ssh sd106@l040101-ws03.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirOrdThief.zip sd106@l040101-ws03.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the ordinary thieves node."
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirOrdThief.zip'

echo "Executing program at the ordinary thieves node."
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'cd test/HeistToMuseum/dirOrdThief ; chmod +x *.sh ; ./ordinary_com_d.sh'

echo "Client Ordinary Thieves shutdown."