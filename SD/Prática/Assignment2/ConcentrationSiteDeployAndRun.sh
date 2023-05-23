echo "Transferring data to the concentration site node."
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirConcetSite.zip sd106@l040101-ws03.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the concentration site node."
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirConcetSite.zip'

echo "Executing program at the server concentration site."
sshpass -f password ssh sd106@l040101-ws03.ua.pt 'cd test/HeistToMuseum/dirConcetSite ; java serverSide.main.ServerHeistToMuseumConcetSite 22152 l040101-ws01.ua.pt 22150 l040101-ws05.ua.pt 22154 l040101-ws06.ua.pt 22155 l040101-ws04.ua.pt 22153'

echo "Concentration Site Server shutdown."
