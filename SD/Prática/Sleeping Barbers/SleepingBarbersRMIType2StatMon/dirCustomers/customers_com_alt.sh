CODEBASE="file:///home/"$1"/test/SleepingBarbers/dirCustomers/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     clientSide.main.ClientSleepingBarbersCustomer localhost 22000 stat 3
