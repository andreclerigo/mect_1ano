echo "Transfering data to the control site node."
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirControlSite.zip sd106@l040101-ws02.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the control site node."
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirControlSite.zip'

echo "Executing program at the server control site."
sshpass -f password ssh sd106@l040101-ws02.ua.pt 'cd test/HeistToMuseum/dirControlSite ; java serverSide.main.ServerHeistToMuseumControlSite 22151 l040101-ws01.ua.pt 22150 l040101-ws05.ua.pt 22154 l040101-ws06.ua.pt 22155'

echo "Control Site Server shutdown."

