echo "Transfering data to the ordinary thief node."
sshpass -f password ssh sd106@l040101-ws08.ua.pt 'mkdir -p test/HeistToMuseum'
sshpass -f password ssh sd106@l040101-ws08.ua.pt 'rm -rf test/HeistToMuseum/*'
sshpass -f password scp dirOrdThief.zip sd106@l040101-ws08.ua.pt:test/HeistToMuseum

echo "Decompressing data sent to the ordinary thief node."
sshpass -f password ssh sd106@l040101-ws08.ua.pt 'cd test/HeistToMuseum ; unzip -uq dirOrdThief.zip'

echo "Executing program at the server ordinary thief."
sshpass -f password ssh sd106@l040101-ws08.ua.pt 'cd test/HeistToMuseum/dirOrdThief ; java clientSide.main.ClientOrdinaryThief l040101-ws04.ua.pt 22153 l040101-ws02.ua.pt 22151 l040101-ws03.ua.pt 22152 l040101-ws05.ua.pt 22154 l040101-ws06.ua.pt 22155 l040101-ws01.ua.pt 22150'

echo "Ordinary Thief Server shutdown."
