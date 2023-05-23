echo "Transfering data to the registry node."
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password scp dirRegistry.zip ruib@l040101-ws08.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the registry node."
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirRegistry.zip'
echo "Executing program at the registry node."
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'cd test/SleepingBarbers/dirRegistry ; ./registry_com_d.sh ruib'
