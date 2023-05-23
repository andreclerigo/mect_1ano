echo "Transfering data to the RMIregistry node."
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'rm -rf test/SleepingBarbers/*'
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'mkdir -p Public/classes/interfaces'
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'rm -rf Public/classes/interfaces/*'
sshpass -f password scp dirRMIRegistry.zip ruib@l040101-ws08.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the RMIregistry node."
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirRMIRegistry.zip'
sshpass -f password ssh ruib@l040101-ws08.ua.pt 'cd test/SleepingBarbers/dirRMIRegistry ; cp interfaces/*.class /home/ruib/Public/classes/interfaces ; cp set_rmiregistry_d.sh /home/ruib'
echo "Executing program at the RMIregistry node."
sshpass -f password ssh ruib@l040101-ws08.ua.pt './set_rmiregistry_d.sh ruib 22000'
