CODEBASE="file:///home/"$1"/test/SleepingBarbers/dirGeneralRepos/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerSleepingBarbersGeneralRepos 22002 localhost 22000
