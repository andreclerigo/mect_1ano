CODEBASE="file:///home/"$1"/test/SleepingBarbers/dirBarberShop/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerSleepingBarbersBarberShop 22003 localhost 22000
