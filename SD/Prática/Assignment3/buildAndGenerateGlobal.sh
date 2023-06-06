echo "Compiling source code."
javac -source 8 -target 8 -cp lib/genclass.jar */*.java */*/*.java

echo "Distributing intermediate code to the different execution environments."

echo "  RMI registry"
rm -rf dirRMIRegistry/interfaces
mkdir -p dirRMIRegistry/interfaces
cp interfaces/*.class dirRMIRegistry/interfaces

echo "  Register Remote Object"
rm -rf dirRegistry/serverSide dirRegistry/interfaces
mkdir -p dirRegistry/serverSide dirRegistry/serverSide/main dirRegistry/serverSide/objects dirRegistry/interfaces
cp serverSide/main/ServerRegisterRemoteObject.class dirRegistry/serverSide/main
cp serverSide/objects/RegisterRemoteObject.class dirRegistry/serverSide/objects
cp interfaces/Register.class dirRegistry/interfaces

echo "  General Repository"
rm -rf dirGeneralRepos/serverSide dirGeneralRepos/clientSide dirGeneralRepos/interfaces
mkdir -p dirGeneralRepos/serverSide dirGeneralRepos/serverSide/main dirGeneralRepos/serverSide/objects dirGeneralRepos/interfaces \
         dirGeneralRepos/clientSide dirGeneralRepos/clientSide/entities
cp serverSide/main/SimulPar.class serverSide/main/ServerHeistToMuseumGeneralRepos.class dirGeneralRepos/serverSide/main
cp serverSide/objects/GeneralRepos.class dirGeneralRepos/serverSide/objects
cp interfaces/Register.class interfaces/GeneralReposInterface.class dirGeneralRepos/interfaces
cp clientSide/entities/MasterThiefStates.class clientSide/entities/OrdinaryThiefStates.class dirGeneralRepos/clientSide/entities

echo "  Control Site"
rm -rf dirControlSite/serverSide dirControlSite/clientSide dirControlSite/interfaces dirControlSite/commInfra
mkdir -p dirControlSite/serverSide dirControlSite/serverSide/main dirControlSite/serverSide/objects dirControlSite/interfaces \
        dirControlSite/clientSide dirControlSite/clientSide/entities dirControlSite/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerHeistToMuseumControlSite.class dirControlSite/serverSide/main
cp serverSide/objects/ControlSite.class serverSide/objects/ControlSite\$ExcursionInfo.class dirControlSite/serverSide/objects
cp interfaces/*.class dirControlSite/interfaces
cp clientSide/entities/MasterThiefStates.class clientSide/entities/OrdinaryThiefStates.class dirControlSite/clientSide/entities
cp commInfra/*.class dirControlSite/commInfra

echo "  Concet Site"
rm -rf dirConcetSite/serverSide dirConcetSite/clientSide dirConcetSite/interfaces dirConcetSite/commInfra
mkdir -p dirConcetSite/serverSide dirConcetSite/serverSide/main dirConcetSite/serverSide/objects dirConcetSite/interfaces \
         dirConcetSite/clientSide dirConcetSite/clientSide/entities dirConcetSite/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerHeistToMuseumConcetSite.class dirConcetSite/serverSide/main
cp serverSide/objects/ConcetSite.class dirConcetSite/serverSide/objects
cp interfaces/*.class dirConcetSite/interfaces
cp clientSide/entities/MasterThiefStates.class clientSide/entities/OrdinaryThiefStates.class dirConcetSite/clientSide/entities
cp commInfra/*.class dirConcetSite/commInfra

echo "  Assault Party"
rm -rf dirAssaultParty/serverSide dirAssaultParty/clientSide dirAssaultParty/interfaces dirAssaultParty/commInfra
mkdir -p dirAssaultParty/serverSide dirAssaultParty/serverSide/main dirAssaultParty/serverSide/objects dirAssaultParty/interfaces \
         dirAssaultParty/clientSide dirAssaultParty/clientSide/entities dirAssaultParty/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerHeistToMuseumAssaultParty.class dirAssaultParty/serverSide/main
cp serverSide/objects/AssaultParty.class dirAssaultParty/serverSide/objects
cp interfaces/*.class dirAssaultParty/interfaces
cp clientSide/entities/MasterThiefStates.class clientSide/entities/OrdinaryThiefStates.class dirAssaultParty/clientSide/entities
cp commInfra/*.class dirAssaultParty/commInfra

echo "  Museum"
rm -rf dirMuseum/serverSide dirMuseum/clientSide dirMuseum/interfaces dirMuseum/commInfra
mkdir -p dirMuseum/serverSide dirMuseum/serverSide/main dirMuseum/serverSide/objects dirMuseum/interfaces dirMuseum/clientSide \
         dirMuseum/clientSide/entities dirMuseum/commInfra
cp serverSide/main/SimulPar.class serverSide/main/ServerHeistToMuseumMuseum.class dirMuseum/serverSide/main
cp serverSide/objects/Museum.class serverSide/objects/Museum\$Room.class  dirMuseum/serverSide/objects
cp interfaces/*.class dirMuseum/interfaces
cp clientSide/entities/MasterThiefStates.class clientSide/entities/OrdinaryThiefStates.class dirMuseum/clientSide/entities
cp commInfra/*.class dirMuseum/commInfra

echo "  Master Thief"
rm -rf dirMstThief/serverSide dirMstThief/clientSide dirMstThief/interfaces
mkdir -p dirMstThief/serverSide dirMstThief/serverSide/main dirMstThief/clientSide dirMstThief/clientSide/main dirMstThief/clientSide/entities \
         dirMstThief/interfaces
cp serverSide/main/SimulPar.class dirMstThief/serverSide/main
cp clientSide/main/ClientMasterThief.class dirMstThief/clientSide/main
cp clientSide/entities/MasterThief.class clientSide/entities/MasterThiefStates.class dirMstThief/clientSide/entities
cp interfaces/AssaultPartyInterface.class interfaces/ControlSiteInterface.class interfaces/ConcetSiteInterface.class interfaces/ReturnInt.class interfaces/ReturnBoolean.class dirMstThief/interfaces

echo "  Ordinary Thief"
rm -rf dirOrdThief/serverSide dirOrdThief/clientSide dirOrdThief/interfaces
mkdir -p dirOrdThief/serverSide dirOrdThief/serverSide/main dirOrdThief/clientSide dirOrdThief/clientSide/main dirOrdThief/clientSide/entities \
         dirOrdThief/interfaces
cp serverSide/main/SimulPar.class dirOrdThief/serverSide/main
cp clientSide/main/ClientOrdinaryThief.class dirOrdThief/clientSide/main
cp clientSide/entities/OrdinaryThief.class clientSide/entities/OrdinaryThiefStates.class dirOrdThief/clientSide/entities
cp interfaces/AssaultPartyInterface.class interfaces/ConcetSiteInterface.class interfaces/ControlSiteInterface.class interfaces/MuseumInterface.class interfaces/GeneralReposInterface.class interfaces/ReturnBoolean.class interfaces/ReturnInt.class dirOrdThief/interfaces

echo "Compressing execution environments."

echo "  RMI registry"
rm -f  dirRMIRegistry.zip
zip -rq dirRMIRegistry.zip dirRMIRegistry

echo "  Register Remote Objects"
rm -f dirRegistry.zip
zip -rq dirRegistry.zip dirRegistry

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

