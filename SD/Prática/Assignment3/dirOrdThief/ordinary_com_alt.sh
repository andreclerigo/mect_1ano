CODEBASE="file:///home/"$1"/test/HeistToMuseum/dirOrdinaryThieves/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=false\
     clientSide.main.ClientOrdinaryThief localhost 22150
