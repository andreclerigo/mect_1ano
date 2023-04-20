echo "Transfering data to the barbers node."
sshpass -f password ssh ruib@l040101-ws03.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password ssh ruib@l040101-ws03.ua.pt 'rm -rf test/SleepingBarbers/*'
sshpass -f password scp dirBarbers.zip ruib@l040101-ws03.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the barbers node."
sshpass -f password ssh ruib@l040101-ws03.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirBarbers.zip'
echo "Executing program at the barbers node."
sshpass -f password ssh ruib@l040101-ws03.ua.pt 'cd test/SleepingBarbers/dirBarbers ; java clientSide.main.ClientSleepingBarbersBarber l040101-ws07.ua.pt 22001 l040101-ws02.ua.pt 22002'
