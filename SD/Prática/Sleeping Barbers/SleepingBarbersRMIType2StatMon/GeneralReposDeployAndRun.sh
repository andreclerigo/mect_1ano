echo "Transfering data to the general repository node."
sshpass -f password ssh ruib@l040101-ws07.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password ssh ruib@l040101-ws07.ua.pt 'rm -rf test/SleepingBarbers/*'
sshpass -f password scp dirGeneralRepos.zip ruib@l040101-ws07.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the general repository node."
sshpass -f password ssh ruib@l040101-ws07.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirGeneralRepos.zip'
echo "Executing program at the general repository node."
sshpass -f password ssh ruib@l040101-ws07.ua.pt 'cd test/SleepingBarbers/dirGeneralRepos ; ./repos_com_d.sh ruib'
echo "Server shutdown."
sshpass -f password ssh ruib@l040101-ws07.ua.pt 'cd test/SleepingBarbers/dirGeneralRepos ; less stat'
