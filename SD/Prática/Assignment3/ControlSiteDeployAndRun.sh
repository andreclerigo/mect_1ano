echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws05.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the control site node."

sshpass -f password ssh sd106@l040101-ws05.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws05.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirControlSite.zip sd106@l040101-ws05.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the control site node."
sshpass -f password ssh sd106@l040101-ws05.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirControlSite.zip'

echo "Executing program at the control site node."
sshpass -f password ssh sd106@l040101-ws05.ua.pt 'cd test/HeistToMuseum/dirControlSite ; chmod +x *.sh ; ./control_com_d.sh sd106'

echo "Control Site Server shutdown."