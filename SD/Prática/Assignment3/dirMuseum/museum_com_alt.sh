CODEBASE="file:///home/"$1"/test/HeistToMuseum/dirMuseum/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumMuseum 22155 localhost 22150
