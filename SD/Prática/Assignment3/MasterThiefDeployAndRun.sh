echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the master thief node."

sshpass -f password ssh sd106@l040101-ws02.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirMstThief.zip sd106@l040101-ws02.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the master thief node."
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirMstThief.zip'

echo "Executing program at the master thief node."
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'cd test/HeistToMuseum/dirMstThief ; chmod +x *.sh ; ./master_com_d.sh'

echo "Client Master Thief shutdown."