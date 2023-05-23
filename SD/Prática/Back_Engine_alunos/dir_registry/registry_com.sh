java -Djava.rmi.server.codebase="http://localhost/ruib/classes/"\
     -Djava.rmi.server.useCodebaseOnly=true\
     -Djava.security.policy=java.policy\
     registry.ServerRegisterRemoteObject
