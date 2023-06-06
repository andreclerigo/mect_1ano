echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the concentration site node."

sshpass -f password ssh sd106@l040101-ws06.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirConcetSite.zip sd106@l040101-ws06.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the concentration site node."
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirConcetSite.zip'

echo "Executing program at the concentration site node."
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'cd test/HeistToMuseum/dirConcetSite ; chmod +x *.sh ; ./concet_com_d.sh sd106'

echo "Concet Site Server shutdown."