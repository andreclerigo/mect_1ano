echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the registry node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password scp dirRegistry.zip sd106@l040101-ws01.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the registry node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirRegistry.zip'

echo "Executing program at the registry node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum/dirRegistry ; chmod +x *.sh ; ./registry_com_d.sh sd106'

echo "Registry node shutdown."