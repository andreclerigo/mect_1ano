echo "Compiling source code."
javac */*.java */*/*.java
echo "Distributing intermediate code to the different execution environments."
echo "  RMI registry"
rm -rf dirRMIRegistry/interfaces
mkdir -p dirRMIRegistry/interfaces
cp interfaces/*.class dirRMIRegistry/interfaces
echo "  Register Remote Objects"
rm -rf dirRegistry/serverSide dirRegistry/interfaces
mkdir -p dirRegistry/serverSide dirRegistry/serverSide/main dirRegistry/serverSide/objects dirRegistry/interfaces
cp serverSide/main/ServerRegisterRemoteObject.class dirRegistry/serverSide/main
cp serverSide/objects/RegisterRemoteObject.class dirRegistry/serverSide/objects
cp interfaces/Register.class dirRegistry/interfaces
echo "  General Repository of Information"
rm -rf dirGeneralRepos/serverSide dirGeneralRepos/clientSide dirGeneralRepos/interfaces
mkdir -p dirGeneralRepos/serverSide dirGeneralRepos/serverSide/main dirGeneralRepos/serverSide/objects dirGeneralRepos/interfaces \
         dirGeneralRepos/clientSide dirGeneralRepos/clientSide/entities
cp serverSide/main/SimulPar.class serverSide/main/ServerSleepingBarbersGeneralRepos.class dirGeneralRepos/serverSide/main
cp serverSide/objects/GeneralRepos.class dirGeneralRepos/serverSide/objects
cp interfaces/Register.class interfaces/GeneralReposInterface.class dirGeneralRepos/interfaces
cp clientSide/entities/BarberStates.class clientSide/entities/CustomerStates.class dirGeneralRepos/clientSide/entities
echo "  Barber Shop"
rm -rf dirBarberShop/serverSide dirBarberShop/clientSide dirBarberShop/interfaces dirBarberShop/commInfra
mkdir -p dirBarberShop/serverSide dirBarberShop/serverSide/main dirBarberShop/serverSide/objects dirBarberShop/interfaces \
         dirBarberShop/clientSide dirBarberShop/clientSide/entities dirBarberShop/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerSleepingBarbersBarberShop.class dirBarberShop/serverSide/main
cp serverSide/objects/BarberShop.class dirBarberShop/serverSide/objects
cp interfaces/*.class dirBarberShop/interfaces
cp clientSide/entities/BarberStates.class clientSide/entities/CustomerStates.class dirBarberShop/clientSide/entities
cp commInfra/*.class dirBarberShop/commInfra
echo "  Barbers"
rm -rf dirBarbers/serverSide dirBarbers/clientSide dirBarbers/interfaces
mkdir -p dirBarbers/serverSide dirBarbers/serverSide/main dirBarbers/clientSide dirBarbers/clientSide/main dirBarbers/clientSide/entities \
         dirBarbers/interfaces
cp serverSide/main/SimulPar.class dirBarbers/serverSide/main
cp clientSide/main/ClientSleepingBarbersBarber.class dirBarbers/clientSide/main
cp clientSide/entities/Barber.class clientSide/entities/BarberStates.class dirBarbers/clientSide/entities
cp interfaces/BarberShopInterface.class interfaces/GeneralReposInterface.class interfaces/ReturnBoolean.class interfaces/ReturnInt.class dirBarbers/interfaces
echo "  Customers"
rm -rf dirCustomers/serverSide dirCustomers/clientSide dirCustomers/interfaces
mkdir -p dirCustomers/serverSide dirCustomers/serverSide/main dirCustomers/clientSide dirCustomers/clientSide/main dirCustomers/clientSide/entities \
         dirCustomers/interfaces
cp serverSide/main/SimulPar.class dirCustomers/serverSide/main
cp clientSide/main/ClientSleepingBarbersCustomer.class dirCustomers/clientSide/main
cp clientSide/entities/Customer.class clientSide/entities/CustomerStates.class dirCustomers/clientSide/entities
cp interfaces/BarberShopInterface.class interfaces/GeneralReposInterface.class interfaces/ReturnBoolean.class interfaces/ReturnInt.class dirCustomers/interfaces
echo "Compressing execution environments."
echo "  RMI registry"
rm -f  dirRMIRegistry.zip
zip -rq dirRMIRegistry.zip dirRMIRegistry
echo "  Register Remote Objects"
rm -f  dirRegistry.zip
zip -rq dirRegistry.zip dirRegistry
echo "  General Repository of Information"
rm -f  dirGeneralRepos.zip
zip -rq dirGeneralRepos.zip dirGeneralRepos
echo "  Barber Shop"
rm -f  dirBarberShop.zip
zip -rq dirBarberShop.zip dirBarberShop
echo "  Barbers"
rm -f  dirBarbers.zip
zip -rq dirBarbers.zip dirBarbers
echo "  Customers"
rm -f  dirCustomers.zip
zip -rq dirCustomers.zip dirCustomers
