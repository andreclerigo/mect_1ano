echo "Transfering data to the assault party 1 node."
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirAssaultParty.zip sd106@l040101-ws06.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the assault party 1 node."
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirAssaultParty.zip'

echo "Executing program at the server assault party 1."
sshpass -f password ssh sd106@l040101-ws06.ua.pt 'cd test/HeistToMuseum/dirAssaultParty ; java serverSide.main.ServerHeistToMuseumAssaultParty 22155 l040101-ws01.ua.pt 22150 1'

echo "Assault Party 1 Server shutdown."




