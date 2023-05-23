xterm  -T "General Repository" -hold -e "./GeneralReposDeployAndRun.sh" &
sleep 2
xterm  -T "Barber Shop" -hold -e "./BarberShopDeployAndRun.sh" &
sleep 1
xterm  -T "Barbers" -hold -e "./BarbersDeployAndRun.sh" &
xterm  -T "Customers" -hold -e "./CustomersDeployAndRun.sh" &
