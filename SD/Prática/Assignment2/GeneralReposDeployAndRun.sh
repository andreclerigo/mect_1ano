echo "Transferring data to the general repository node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirGeneralRepos.zip sd106@l040101-ws01.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the general repository node."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirGeneralRepos.zip'

echo "Executing program at the server general repository."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum/dirGeneralRepos ; java serverSide.main.ServerHeistToMuseumGeneralRepos 22150'

echo "General Repository Server shutdown."
sshpass -f password ssh sd106@l040101-ws01.ua.pt 'cd test/HeistToMuseum/dirGeneralRepos ; less logger'


