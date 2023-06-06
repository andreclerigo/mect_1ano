CODEBASE="file:///home/"$1"/test/HeistToMuseum/dirControlSite/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumControlSite 22153 localhost 22150
