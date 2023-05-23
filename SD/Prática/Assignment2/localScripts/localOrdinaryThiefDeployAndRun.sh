echo "Transfering data to the Ordinary Thief node"
cd /home/$USER/test/HeistToMuseum/dirOrdThief
java -cp ".:../genclass.jar" clientSide.main.ClientOrdinaryThief 127.0.0.1 22153 127.0.0.1 22151 127.0.0.1 22152 127.0.0.1 22154 127.0.0.1 22155 127.0.0.1 22150
echo "Ordinary Thief Client Shutdown" 
