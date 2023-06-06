CODEBASE="file:///home/"$1"/test/HeistToMuseum/dirGeneralRepos/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumGeneralRepos 22152 localhost 22150
