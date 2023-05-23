echo "Transfering data to the Master Thief node"
cd /home/$USER/test/HeistToMuseum/dirMstThief
java -cp ".:../genclass.jar" clientSide.main.ClientMasterThief 127.0.0.1 22151 127.0.0.1 22152 127.0.0.1 22154 127.0.0.1 22155
echo "Master Thief Client Shutdown" 
