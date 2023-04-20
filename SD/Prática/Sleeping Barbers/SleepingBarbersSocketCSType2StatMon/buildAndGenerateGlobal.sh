echo "Compiling source code."
javac */*.java */*/*.java
echo "Distributing intermediate code to the different execution environments."
echo "  General Repository of Information"
rm -rf dirGeneralRepos
mkdir -p dirGeneralRepos dirGeneralRepos/serverSide dirGeneralRepos/serverSide/main dirGeneralRepos/serverSide/entities dirGeneralRepos/serverSide/sharedRegions \
         dirGeneralRepos/clientSide dirGeneralRepos/clientSide/entities dirGeneralRepos/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerSleepingBarbersGeneralRepos.class dirGeneralRepos/serverSide/main
cp serverSide/entities/GeneralReposClientProxy.class dirGeneralRepos/serverSide/entities
cp serverSide/sharedRegions/GeneralReposInterface.class serverSide/sharedRegions/GeneralRepos.class dirGeneralRepos/serverSide/sharedRegions
cp clientSide/entities/BarberStates.class clientSide/entities/CustomerStates.class dirGeneralRepos/clientSide/entities
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ServerCom.class dirGeneralRepos/commInfra
echo "  Barber Shop"
rm -rf dirBarberShop
mkdir -p dirBarberShop dirBarberShop/serverSide dirBarberShop/serverSide/main dirBarberShop/serverSide/entities dirBarberShop/serverSide/sharedRegions \
         dirBarberShop/clientSide dirBarberShop/clientSide/entities dirBarberShop/clientSide/stubs dirBarberShop/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerSleepingBarbersBarberShop.class dirBarberShop/serverSide/main
cp serverSide/entities/BarberShopClientProxy.class dirBarberShop/serverSide/entities
cp serverSide/sharedRegions/GeneralReposInterface.class serverSide/sharedRegions/BarberShopInterface.class serverSide/sharedRegions/BarberShop.class dirBarberShop/serverSide/sharedRegions
cp clientSide/entities/BarberStates.class clientSide/entities/CustomerStates.class clientSide/entities/BarberCloning.class clientSide/entities/CustomerCloning.class \
   dirBarberShop/clientSide/entities
cp clientSide/stubs/GeneralReposStub.class dirBarberShop/clientSide/stubs
cp commInfra/*.class dirBarberShop/commInfra
echo "  Barbers"
rm -rf dirBarbers
mkdir -p dirBarbers dirBarbers/serverSide dirBarbers/serverSide/main dirBarbers/clientSide dirBarbers/clientSide/main dirBarbers/clientSide/entities \
         dirBarbers/clientSide/stubs dirBarbers/commInfra
cp serverSide/main/SimulPar.class dirBarbers/serverSide/main
cp clientSide/main/ClientSleepingBarbersBarber.class dirBarbers/clientSide/main
cp clientSide/entities/Barber.class clientSide/entities/BarberStates.class dirBarbers/clientSide/entities
cp clientSide/stubs/GeneralReposStub.class clientSide/stubs/BarberShopStub.class dirBarbers/clientSide/stubs
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ClientCom.class dirBarbers/commInfra
echo "  Customers"
rm -rf dirCustomers
mkdir -p dirCustomers dirCustomers/serverSide dirCustomers/serverSide/main dirCustomers/clientSide dirCustomers/clientSide/main dirCustomers/clientSide/entities \
         dirCustomers/clientSide/stubs dirCustomers/commInfra
cp serverSide/main/SimulPar.class dirCustomers/serverSide/main
cp clientSide/main/ClientSleepingBarbersCustomer.class dirCustomers/clientSide/main
cp clientSide/entities/Customer.class clientSide/entities/CustomerStates.class dirCustomers/clientSide/entities
cp clientSide/stubs/GeneralReposStub.class clientSide/stubs/BarberShopStub.class dirCustomers/clientSide/stubs
cp commInfra/Message.class commInfra/MessageType.class commInfra/MessageException.class commInfra/ClientCom.class dirCustomers/commInfra
echo "Compressing execution environments."
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
