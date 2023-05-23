echo "Transfering data to the customers node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'rm -rf test/SleepingBarbers/*'
sshpass -f password scp dirCustomers.zip ruib@l040101-ws04.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the customers node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirCustomers.zip'
echo "Executing program at the customers node."
sshpass -f password ssh ruib@l040101-ws04.ua.pt 'cd test/SleepingBarbers/dirCustomers ; ./customers_com_d.sh'
