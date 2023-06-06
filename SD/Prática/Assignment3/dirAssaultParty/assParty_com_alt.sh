CODEBASE="file:///home/"$1"/test/HeistToMuseum/dirAssParty0/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumAssaultParty $2 localhost 22150 $3
