echo "Transfering data to the museum node."
sshpass -f password ssh sd106@l040101-ws04.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws04.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirMuseum.zip sd106@l040101-ws04.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the museum node."
sshpass -f password ssh sd106@l040101-ws04.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirMuseum.zip'

echo "Executing program at the server museum."
sshpass -f password ssh sd106@l040101-ws04.ua.pt 'cd test/HeistToMuseum/dirMuseum ; java serverSide.main.ServerHeistToMuseumMuseum 22153 l040101-ws01.ua.pt 22150 l040101-ws05.ua.pt 22154 l040101-ws06.ua.pt 22155'

echo "Museum Server shutdown."

