echo "Transfering data to the Control Site node"
cd /home/$USER/test/HeistToMuseum/dirControlSite
java -cp ".:../genclass.jar" serverSide.main.ServerHeistToMuseumControlSite 22151 127.0.0.1 22150 127.0.0.1 22154 127.0.0.1 22155
echo "Control Site Server Shutdown" 
