CODEBASE="file:///home/"$1"/test/HeistToMuseum/dirConcetSite/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumConcetSite 22154 localhost 22150
