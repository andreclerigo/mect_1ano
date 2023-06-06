xterm  -T "RMI registry" -fa "Monospace" -fs 10 -hold -e "./RMIRegistryDeployAndRun.sh" &
sleep 10
xterm  -T "Registry" -fa "Monospace" -fs 10 -hold -e "./RegistryDeployAndRun.sh" &
sleep 10
xterm -T "General Repository" -fa "Monospace" -fs 10 -e "./GeneralReposDeployAndRun.sh" &
sleep 12
xterm -T "Assault Party 1" -fa "Monospace" -fs 10 -e "./AssaultParty0DeployAndRun.sh" &
sleep 3
xterm -T "Assault Party 2" -fa "Monospace" -fs 10 -e "./AssaultParty1DeployAndRun.sh" &
sleep 3
xterm -T "Museum" -fa "Monospace" -fs 10 -e "./MuseumDeployAndRun.sh" &
sleep 3
xterm -T "Concentration Site" -fa "Monospace" -fs 10 -e "./ConcetSiteDeployAndRun.sh" &
sleep 4
xterm -T "Control Site" -fa "Monospace" -fs 10 -e "./ControlSiteDeployAndRun.sh" &
sleep 10
xterm -T "Ordinary Thief" -fa "Monospace" -fs 10 -e "./OrdinaryThievesDeployAndRun.sh" &
sleep 10
xterm -T "Master Thief" -fa "Monospace" -fs 10 -e "./MasterThiefDeployAndRun.sh" &
