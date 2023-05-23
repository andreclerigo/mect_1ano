echo "Compiling source code."
javac -cp ../lib/genclass.jar ../*/*.java ../*/*/*.java

echo "Distributing intermediate code to the different execution environments."

echo "  General Repository"
rm -rf dirGeneralRepos
mkdir -p dirGeneralRepos dirGeneralRepos/serverSide dirGeneralRepos/serverSide/main dirGeneralRepos/serverSide/entities dirGeneralRepos/serverSide/sharedRegions \
         dirGeneralRepos/clientSide dirGeneralRepos/clientSide/entities dirGeneralRepos/commInfra
cp ../serverSide/main/SimulPar.class ../serverSide/main/ServerHeistToMuseumGeneralRepos.class dirGeneralRepos/serverSide/main
cp ../serverSide/entities/GeneralReposClientProxy.class dirGeneralRepos/serverSide/entities
cp ../serverSide/sharedRegions/GeneralReposInterface.class ../serverSide/sharedRegions/GeneralRepos.class dirGeneralRepos/serverSide/sharedRegions
cp ../clientSide/entities/MasterThiefStates.class ../clientSide/entities/OrdinaryThiefStates.class dirGeneralRepos/clientSide/entities
cp ../commInfra/Message.class ../commInfra/MessageType.class ../commInfra/MessageException.class ../commInfra/ServerCom.class dirGeneralRepos/commInfra

echo "  Control Site"
rm -rf dirControlSite
mkdir -p dirControlSite dirControlSite/serverSide dirControlSite/serverSide/main dirControlSite/serverSide/entities dirControlSite/serverSide/sharedRegions \
        dirControlSite/clientSide dirControlSite/clientSide/entities dirControlSite/clientSide/stubs dirControlSite/commInfra
cp ../serverSide/main/SimulPar.class ../serverSide/main/ServerHeistToMuseumControlSite.class dirControlSite/serverSide/main
cp ../serverSide/entities/ControlSiteClientProxy.class dirControlSite/serverSide/entities
cp ../serverSide/sharedRegions/GeneralReposInterface.class ../serverSide/sharedRegions/ControlSiteInterface.class ../serverSide/sharedRegions/ControlSite.class ../serverSide/sharedRegions/ControlSite\$ExcursionInfo.class dirControlSite/serverSide/sharedRegions
cp ../clientSide/entities/MasterThiefStates.class ../clientSide/entities/OrdinaryThiefStates.class ../clientSide/entities/MasterThiefCloning.class ../clientSide/entities/OrdinaryThiefCloning.class \
  dirControlSite/clientSide/entities
cp ../clientSide/stubs/GeneralReposStub.class ../clientSide/stubs/AssaultPartyStub.class ../clientSide/stubs/MuseumStub.class dirControlSite/clientSide/stubs
cp ../commInfra/*.class dirControlSite/commInfra

echo "  Concet Site"
rm -rf dirConcetSite
mkdir -p dirConcetSite dirConcetSite/serverSide dirConcetSite/serverSide/main dirConcetSite/serverSide/entities dirConcetSite/serverSide/sharedRegions \
         dirConcetSite/clientSide dirConcetSite/clientSide/entities dirConcetSite/clientSide/stubs dirConcetSite/commInfra
cp ../serverSide/main/SimulPar.class ../serverSide/main/ServerHeistToMuseumConcetSite.class dirConcetSite/serverSide/main
cp ../serverSide/entities/ConcetSiteClientProxy.class dirConcetSite/serverSide/entities
cp ../serverSide/sharedRegions/GeneralReposInterface.class ../serverSide/sharedRegions/ConcetSiteInterface.class ../serverSide/sharedRegions/ConcetSite.class dirConcetSite/serverSide/sharedRegions
cp ../clientSide/entities/MasterThiefStates.class ../clientSide/entities/OrdinaryThiefStates.class ../clientSide/entities/MasterThiefCloning.class ../clientSide/entities/OrdinaryThiefCloning.class \
   dirConcetSite/clientSide/entities
cp ../clientSide/stubs/GeneralReposStub.class ../clientSide/stubs/AssaultPartyStub.class ../clientSide/stubs/MuseumStub.class dirConcetSite/clientSide/stubs
cp ../commInfra/*.class dirConcetSite/commInfra

echo "  Assault Party"
rm -rf dirAssaultParty
mkdir -p dirAssaultParty dirAssaultParty/serverSide dirAssaultParty/serverSide/main dirAssaultParty/serverSide/entities dirAssaultParty/serverSide/sharedRegions \
         dirAssaultParty/clientSide dirAssaultParty/clientSide/entities dirAssaultParty/clientSide/stubs dirAssaultParty/commInfra
cp ../serverSide/main/SimulPar.class ../serverSide/main/ServerHeistToMuseumAssaultParty.class dirAssaultParty/serverSide/main
cp ../serverSide/entities/AssaultPartyClientProxy.class dirAssaultParty/serverSide/entities
cp ../serverSide/sharedRegions/GeneralReposInterface.class ../serverSide/sharedRegions/AssaultPartyInterface.class ../serverSide/sharedRegions/AssaultParty.class dirAssaultParty/serverSide/sharedRegions
cp ../clientSide/entities/MasterThiefStates.class ../clientSide/entities/OrdinaryThiefStates.class ../clientSide/entities/MasterThiefCloning.class ../clientSide/entities/OrdinaryThiefCloning.class \
   dirAssaultParty/clientSide/entities
cp ../clientSide/stubs/GeneralReposStub.class dirAssaultParty/clientSide/stubs
cp ../commInfra/*.class dirAssaultParty/commInfra

echo "  Museum"
rm -rf dirMuseum
mkdir -p dirMuseum dirMuseum/serverSide dirMuseum/serverSide/main dirMuseum/serverSide/entities dirMuseum/serverSide/sharedRegions dirMuseum/clientSide \
         dirMuseum/clientSide/entities dirMuseum/clientSide/stubs dirMuseum/commInfra
cp ../serverSide/main/SimulPar.class ../serverSide/main/ServerHeistToMuseumMuseum.class dirMuseum/serverSide/main
cp ../serverSide/entities/MuseumClientProxy.class dirMuseum/serverSide/entities
cp ../serverSide/sharedRegions/GeneralReposInterface.class ../serverSide/sharedRegions/MuseumInterface.class ../serverSide/sharedRegions/Museum.class ../serverSide/sharedRegions/Museum\$Room.class dirMuseum/serverSide/sharedRegions
cp ../clientSide/entities/MasterThiefStates.class ../clientSide/entities/OrdinaryThiefStates.class ../clientSide/entities/MasterThiefCloning.class ../clientSide/entities/OrdinaryThiefCloning.class \
   dirMuseum/clientSide/entities
cp ../clientSide/stubs/GeneralReposStub.class ../clientSide/stubs/AssaultPartyStub.class dirMuseum/clientSide/stubs
cp ../commInfra/*.class dirMuseum/commInfra

echo "  Master Thief"
rm -rf dirMstThief
mkdir -p dirMstThief dirMstThief/serverSide dirMstThief/serverSide/main dirMstThief/clientSide dirMstThief/clientSide/main dirMstThief/clientSide/entities \
         dirMstThief/clientSide/stubs dirMstThief/commInfra
cp ../serverSide/main/SimulPar.class dirMstThief/serverSide/main
cp ../clientSide/main/ClientMasterThief.class dirMstThief/clientSide/main
cp ../clientSide/entities/MasterThief.class ../clientSide/entities/MasterThiefStates.class dirMstThief/clientSide/entities
# Should I copy all of the Stubs? Does it make sense? Like, the Museum is optional, right?
cp ../clientSide/stubs/GeneralReposStub.class ../clientSide/stubs/ControlSiteStub.class ../clientSide/stubs/AssaultPartyStub.class ../clientSide/stubs/ConcetSiteStub.class ../clientSide/stubs/MuseumStub.class dirMstThief/clientSide/stubs
cp ../commInfra/Message.class ../commInfra/MessageType.class ../commInfra/MessageException.class ../commInfra/ClientCom.class dirMstThief/commInfra

echo "  Ordinary Thief"
rm -rf dirOrdThief
mkdir -p dirOrdThief dirOrdThief/serverSide dirOrdThief/serverSide/main dirOrdThief/clientSide dirOrdThief/clientSide/main dirOrdThief/clientSide/entities \
         dirOrdThief/clientSide/stubs dirOrdThief/commInfra
cp ../serverSide/main/SimulPar.class dirOrdThief/serverSide/main
cp ../clientSide/main/ClientOrdinaryThief.class dirOrdThief/clientSide/main
cp ../clientSide/entities/OrdinaryThief.class ../clientSide/entities/OrdinaryThiefStates.class dirOrdThief/clientSide/entities
cp ../clientSide/stubs/GeneralReposStub.class ../clientSide/stubs/ControlSiteStub.class ../clientSide/stubs/AssaultPartyStub.class ../clientSide/stubs/ConcetSiteStub.class ../clientSide/stubs/MuseumStub.class dirOrdThief/clientSide/stubs
cp ../commInfra/Message.class ../commInfra/MessageType.class ../commInfra/MessageException.class ../commInfra/ClientCom.class dirOrdThief/commInfra


echo "Compressing execution environments."

echo "  General Repository of Information"
rm -f  dirGeneralRepos.zip
zip -rq dirGeneralRepos.zip dirGeneralRepos

echo "  Control Site"
rm -f dirControlSite.zip
zip -rq dirControlSite.zip dirControlSite

echo "  Concet Site"
rm -f dirConcetSite.zip
zip -rq dirConcetSite.zip dirConcetSite

echo "  Assault Party"
rm -f  dirAssaultParty.zip
zip -rq dirAssaultParty.zip dirAssaultParty

echo "  Museum"
rm -f  dirMuseum.zip
zip -rq dirMuseum.zip dirMuseum

echo "  Master Thief"
rm -f  dirMstThief.zip
zip -rq dirMstThief.zip dirMstThief

echo "  Ordinary Thief"
rm -f  dirOrdThief.zip
zip -rq dirOrdThief.zip dirOrdThief


echo "Deploying and decompressing execution environments."

mkdir -p /home/$USER/test/HeistToMuseum
rm -rf /home/$USER/test/HeistToMuseum/*
cp dirGeneralRepos.zip /home/$USER/test/HeistToMuseum
cp dirControlSite.zip /home/$USER/test/HeistToMuseum
cp dirConcetSite.zip /home/$USER/test/HeistToMuseum
cp dirAssaultParty.zip /home/$USER/test/HeistToMuseum
cp dirMuseum.zip /home/$USER/test/HeistToMuseum
cp dirMstThief.zip /home/$USER/test/HeistToMuseum
cp dirOrdThief.zip /home/$USER/test/HeistToMuseum
cp ../lib/genclass.jar /home/$USER/test/HeistToMuseum

cd /home/$USER/test/HeistToMuseum
unzip -q dirGeneralRepos.zip
unzip -q dirControlSite.zip
unzip -q dirConcetSite.zip
unzip -q dirAssaultParty.zip
unzip -q dirMuseum.zip
unzip -q dirMstThief.zip
unzip -q dirOrdThief.zip
