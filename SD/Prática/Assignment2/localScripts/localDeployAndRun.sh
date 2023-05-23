# loop 100 times
for i in $(seq 1 100); do
    echo "Start of iteration $i"

    xterm -T "General Repository" -fa "Monospace" -fs 10 -e "./localGeneralReposDeployAndRun.sh" &
    sleep 1
    xterm -T "Assault Party 1" -fa "Monospace" -fs 10 -e "./localAssaultParty0DeployAndRun.sh" &
    sleep 1
    xterm -T "Assault Party 2" -fa "Monospace" -fs 10 -e "./localAssaultParty1DeployAndRun.sh" &
    sleep 1
    xterm -T "Museum" -fa "Monospace" -fs 10 -e "./localMuseumDeployAndRun.sh" &
    sleep 1
    xterm -T "Concentration Site" -fa "Monospace" -fs 10 -e "./localConcentrationSiteDeployAndRun.sh" &
    sleep 1
    xterm -T "Control Site" -fa "Monospace" -fs 10 -e "./localControlSiteDeployAndRun.sh" &
    sleep 1
    xterm -T "Ordinary Thief" -fa "Monospace" -fs 10 -e "./localOrdinaryThiefDeployAndRun.sh" &
    sleep 1
    xterm -T "Master Thief" -fa "Monospace" -fs 10 -e "./localMasterThiefDeployAndRun.sh"

    #echo "Validating the execution of the distributed solution (locally)."
    # after the required output is found, execute the rest of the script
    #echo "Extract the number of paintings from the output of the logger."
    num_paintings=$(echo $(tail -n 1 ~/test/HeistToMuseum/dirGeneralRepos/logger) | grep -oP '\d+')

    #echo "Extract the numbers of paintings in each room to sum them."
    values_to_sum=$(echo $(head -n 9 ~/test/HeistToMuseum/dirGeneralRepos/logger | grep -oP '\d+') | awk '{print $26, $28, $30, $32, $34}')

    # calculate the sum of the values
    sum_values=0
    for value in $values_to_sum; do
        sum_values=$((sum_values + 10#$value))
    done

    # compare the number of paintings with the sum of the values
    if [ "$num_paintings" -eq "$sum_values" ]; then
       echo "SUCCESS: Number of paintings ($num_paintings) is equal to the sum of the specified values ($sum_values)."
    else
       echo "ERROR: Number of paintings ($num_paintings) does not match the sum of the specified values ($sum_values)."
    fi

    sleep 2

    echo "End of iteration $i"
done
