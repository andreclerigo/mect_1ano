echo "Transfering data to the Assault Party node 0"
cd /home/$USER/test/HeistToMuseum/dirAssaultParty
java -cp ".:../genclass.jar" serverSide.main.ServerHeistToMuseumAssaultParty 22154 127.0.0.1 22150 0
echo "Assault Party Server 0 Shutdown" 
