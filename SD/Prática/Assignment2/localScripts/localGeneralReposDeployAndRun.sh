echo "Transfering data to the General Repository node"
cd /home/$USER/test/HeistToMuseum/dirGeneralRepos
java -cp ".:../genclass.jar" serverSide.main.ServerHeistToMuseumGeneralRepos 22150
echo "General Repository Server Shutdown" 
