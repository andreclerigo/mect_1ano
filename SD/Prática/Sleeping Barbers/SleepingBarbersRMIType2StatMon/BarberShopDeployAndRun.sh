echo "Transfering data to the barber shop node."
sshpass -f password ssh ruib@l040101-ws06.ua.pt 'mkdir -p test/SleepingBarbers'
sshpass -f password ssh ruib@l040101-ws06.ua.pt 'rm -rf test/SleepingBarbers/*'
sshpass -f password scp dirBarberShop.zip ruib@l040101-ws06.ua.pt:test/SleepingBarbers
echo "Decompressing data sent to the barber shop node."
sshpass -f password ssh ruib@l040101-ws06.ua.pt 'cd test/SleepingBarbers ; unzip -uq dirBarberShop.zip'
echo "Executing program at the barber shop node."
sshpass -f password ssh ruib@l040101-ws06.ua.pt 'cd test/SleepingBarbers/dirBarberShop ; ./bshop_com_d.sh ruib'
