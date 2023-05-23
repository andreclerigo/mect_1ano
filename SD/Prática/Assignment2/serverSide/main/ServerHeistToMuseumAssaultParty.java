package serverSide.main;

import genclass.GenericIO;
import serverSide.entities.*;
import serverSide.sharedRegions.*;
import clientSide.stubs.*;
import commInfra.*;
import java.net.*;

/**
 *    Server side of the Assault Party.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ServerHeistToMuseumAssaultParty
{
    /**
     *   Flag signaling the service is active.
     */
    public static boolean waitConnection;

    /**
     *   Main method.
     *
     *     @param args runtime arguments
     *      args[0] - port number for listening to service requests
     *      args[1] - name of the platform where is located the General Repository server
     *      args[2] - port number for listening to service requests
     *      args[3] - identification of the assault party
     */
    public static void main (String [] args)
    {
        AssaultParty assaultParty;                                              // assault party (service to be rendered)
        AssaultPartyInterface assaultPartyInter;                                // interface to the assault party
        GeneralReposStub reposStub;                                             // stub to the general repository
        ServerCom scon, sconi;                                                  // communication channels
        int portNumb = -1;                                                      // port number for listening to service requests
        String reposServerName;                                                 // name of the platform where is located the server for the general repository
        int reposPortNumb = -1;                                                 // port number where the server for the general repository is listening to service requests
        int id = -1;                                                            // assault party identification
        int targetRoomId = -1;                                                  // target room for the assault party

        if (args.length != 4) {
            GenericIO.writelnString("Wrong number of parameters!");
            System.exit(1);
        }
        try {
            portNumb = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            GenericIO.writelnString("args[0] is not a number!");
            System.exit(1);
        }

        if ((portNumb < 4000) || (portNumb >= 65536)) {
            GenericIO.writelnString("args[0] is not a valid port number!");
            System.exit(1);
        }
        reposServerName = args[1];
        try {
            reposPortNumb = Integer.parseInt (args[2]);
        } catch (NumberFormatException e) {
            GenericIO.writelnString("args[2] is not a number!");
            System.exit(1);
        }
        if ((reposPortNumb < 4000) || (reposPortNumb >= 65536)) {
            GenericIO.writelnString("args[2] is not a valid port number!");
            System.exit(1);
        }

        // Identification of the party
        try {
            id = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            GenericIO.writelnString("args[3] is not a number!");
            System.exit(1);
        }
        if ( (id < 0) || (id > 1) ) {
            GenericIO.writelnString("args[3] is not a valid assault party id!");
            System.exit(1);
        }

        /* service is established */
        reposStub = new GeneralReposStub(reposServerName, reposPortNumb);           // communication to the general repository is instantiated
        assaultParty = new AssaultParty(id, targetRoomId, reposStub);               // service is instantiated
        assaultPartyInter = new AssaultPartyInterface(assaultParty);                // interface to the service is instantiated
        scon = new ServerCom(portNumb);                                             // listening channel at the public port is established
        scon.start();
        GenericIO.writelnString("Service is established!");
        GenericIO.writelnString("Server is listening for service requests.");

        /* service request processing */
        AssaultPartyClientProxy cliProxy;                                           // service provider agent

        waitConnection = true;
        while (waitConnection) {
            try {
                sconi = scon.accept();                                              // enter listening procedure
                cliProxy = new AssaultPartyClientProxy(sconi, assaultPartyInter);   // start a service provider agent to address
                cliProxy.start();                                                   // the request of service
                scon.setTimeout(1000);
            } catch (SocketTimeoutException e) {
            } catch (SocketException e) {
            }
        }
        scon.end();                                                                 // operations termination
        GenericIO.writelnString("[Assault Party] Server was shutdown.");
    }
}
