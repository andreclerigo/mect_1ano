echo "Cleaning ports."
sshpass -f password ssh sd106@l040101-ws10.ua.pt 'killall -9 -u sd106 java'

echo "Transferring data to the general repository node."

sshpass -f password ssh sd106@l040101-ws10.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws10.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirGeneralRepos.zip sd106@l040101-ws10.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the general repository node."
sshpass -f password ssh sd106@l040101-ws10.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirGeneralRepos.zip'

echo "Executing program at the general repository node."
sshpass -f password ssh sd106@l040101-ws10.ua.pt 'cd test/HeistToMuseum/dirGeneralRepos ; chmod +x *.sh ; ./repos_com_d.sh sd106'

echo "Server shutdown."
sshpass -f password ssh sd106@l040101-ws10.ua.pt 'cd test/HeistToMuseum/dirGeneralRepos ; less logger'
sshpass -f password scp sd106@l040101-ws10.ua.pt:test/HeistToMuseum/dirGeneralRepos/logger /home/$USER/test/HeistToMuseum/

echo "General Repository Server shutdown."