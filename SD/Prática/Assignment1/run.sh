#!/bin/bash

# function to perform cleanup actions
function cleanup {
    echo -e "\nCleaning up before terminating..."
    # add your cleanup actions here
    exit 0
}

# register the cleanup function to be executed on SIGINT (Ctrl + C) signal
trap cleanup SIGINT

# check if the program has been compiled before
if [ ! -f HeistToMuseum.class ]
then
    # compile the program
    javac -cp ../lib/genclass.jar:./entities:./commInfra:./sharedRegions main/HeistToMuseum.java entities/*.java commInfra/*.java sharedRegions/*.java main/*.java
fi

# run the program 1 time
echo -e "\nRun n.o " 1
java -cp .:../lib/genclass.jar:./entities:./commInfra:./sharedRegions main.HeistToMuseum
