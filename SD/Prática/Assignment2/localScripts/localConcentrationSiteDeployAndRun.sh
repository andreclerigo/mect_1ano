echo "Transfering data to the Concentration Site node"
cd /home/$USER/test/HeistToMuseum/dirConcetSite
java -cp ".:../genclass.jar" serverSide.main.ServerHeistToMuseumConcetSite 22152 127.0.0.1 22150 127.0.0.1 22154 127.0.0.1 22155 127.0.0.1 22153
echo "Concentration Site Server Shutdown" 
