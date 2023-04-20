echo "Transfering data to the general repository node."
sshpass -f password ssh ruib@l040101-ws02.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password ssh ruib@l040101-ws02.ua.pt 'rm -rf test/SleepingBarbers/*'
sshpass -f password scp dirGeneralRepos.zip ruib@l040101-ws02.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the general repository node."
sshpass -f password ssh ruib@l040101-ws02.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirGeneralRepos.zip'
echo "Executing program at the server general repository."
sshpass -f password ssh ruib@l040101-ws02.ua.pt 'cd test/SleepingBarbers/dirGeneralRepos ; java serverSide.main.ServerSleepingBarbersGeneralRepos 22002'
echo "Server shutdown."
sshpass -f password ssh ruib@l040101-ws02.ua.pt 'cd test/SleepingBarbers/dirGeneralRepos ; less stat'
