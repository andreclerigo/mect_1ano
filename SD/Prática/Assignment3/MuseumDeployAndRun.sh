echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws09.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the museum node."

sshpass -f password ssh sd106@l040101-ws09.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws09.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirMuseum.zip sd106@l040101-ws09.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the museum node."
sshpass -f password ssh sd106@l040101-ws09.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirMuseum.zip'

echo "Executing program at the museum node."
sshpass -f password ssh sd106@l040101-ws09.ua.pt 'cd test/HeistToMuseum/dirMuseum ; chmod +x *.sh ; ./museum_com_d.sh sd106'

echo "Museum Server shutdown."