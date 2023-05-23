CODEBASE="file:///home/"$1"/test/SleepingBarbers/dirBarbers/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     clientSide.main.ClientSleepingBarbersBarber localhost 22000
