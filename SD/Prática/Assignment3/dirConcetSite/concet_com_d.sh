CODEBASE="http://l040101-ws06.ua.pt/"$1"/classes/"
java -Djava.rmi.server.codebase=$CODEBASE\
     -Djava.rmi.server.useCodebaseOnly=true\
     -Djava.security.policy=java.policy\
     serverSide.main.ServerHeistToMuseumConcetSite 22154 l040101-ws01.ua.pt 22150
