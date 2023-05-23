echo "Transfering data to the master thief node."
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirMstThief.zip sd106@l040101-ws07.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the master thief node."
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirMstThief.zip'

echo "Executing program at the server master thief."
sshpass -f password ssh sd106@l040101-ws07.ua.pt 'cd test/HeistToMuseum/dirMstThief ; java clientSide.main.ClientMasterThief l040101-ws02.ua.pt 22151 l040101-ws03.ua.pt 22152 l040101-ws05.ua.pt 22154 l040101-ws06.ua.pt 22155'

echo "Master Thief Server shutdown."

