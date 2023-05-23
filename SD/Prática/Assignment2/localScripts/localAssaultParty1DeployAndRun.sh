echo "Transfering data to the Assault Party node 1"
cd /home/$USER/test/HeistToMuseum/dirAssaultParty
java -cp ".:../genclass.jar" serverSide.main.ServerHeistToMuseumAssaultParty 22155 127.0.0.1 22150 1
echo "Assault Party Server 1 Shutdown" 
