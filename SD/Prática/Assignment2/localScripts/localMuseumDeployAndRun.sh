echo "Transfering data to the Museum node"
cd /home/$USER/test/HeistToMuseum/dirMuseum
java -cp ".:../genclass.jar" serverSide.main.ServerHeistToMuseumMuseum 22153 127.0.0.1 22150 127.0.0.1 22154 127.0.0.1 22155
echo "Museum Server Shutdown" 
