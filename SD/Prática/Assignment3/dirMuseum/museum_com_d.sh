CODEBASE="http://l040101-ws09.ua.pt/"$1"/classes/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=true\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumMuseum 22155 l040101-ws01.ua.pt 22150
