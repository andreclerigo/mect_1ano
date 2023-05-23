xterm  -T "RMI registry" -hold -e "./RMIRegistryDeployAndRun.sh" &
sleep 4
xterm  -T "Registry" -hold -e "./RegistryDeployAndRun.sh" &
sleep 4
xterm  -T "General Repository" -hold -e "./GeneralReposDeployAndRun.sh" &
sleep 2
xterm  -T "Barber Shop" -hold -e "./BarberShopDeployAndRun.sh" &
sleep 1
xterm  -T "Barbers" -hold -e "./BarbersDeployAndRun.sh" &
xterm  -T "Customers" -hold -e "./CustomersDeployAndRun.sh" &
