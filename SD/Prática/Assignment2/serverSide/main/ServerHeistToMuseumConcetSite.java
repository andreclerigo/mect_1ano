package serverSide.main;

import genclass.GenericIO;
import serverSide.entities.*;
import serverSide.sharedRegions.*;
import clientSide.stubs.*;
import commInfra.*;
import java.net.*;

/**
 *    Server side of the Concentration Site.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ServerHeistToMuseumConcetSite
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
     *      args[3] - name of the platform where is located the Assault Party 1 server
     *      args[4] - port number for listening to service requests
     *      args[5] - name of the platform where is located the Assault Party 2 server
     *      args[6] - port number for listening to service requests
     *      args[7] - name of the platform where is located the Museum server
     *      args[8] - port number for listening to service requests
     */
    public static void main (String [] args)
    {
        ConcetSite concetSite;                                                  // concentration site (service to be rendered)
        ConcetSiteInterface concetSiteInter;                                    // interface to the concetration site
        GeneralReposStub reposStub;                                             // stub to the general repository
        AssaultPartyStub [] assaultPartiesStubs = new AssaultPartyStub[2];      // stubs to the assault parties
        MuseumStub museumStub;                                                  // stub to the museum
        ServerCom scon, sconi;                                                  // communication channels
        int portNumb = -1;                                                      // port number for listening to service requests
        String reposServerName;                                                 // name of the platform where is located the server for the general repository
        String assaultParty1ServerHostName;                                     // name of the platform where is located the server for the assault party 1
        String assaultParty2ServerHostName;                                     // name of the platform where is located the server for the assault party 2
        String museumServerHostName;                                            // name of the platform where is located the server fot the museum
        int reposPortNumb = -1;                                                 // port number where the server for the general repository is listening to service requests
        int assaultParty1ServerPortNumb = -1;                                   // port number where the server for the assault party 1 is listening to service requests
        int assaultParty2ServerPortNumb = -1;                                   // port number where the server for the assault party 2 is listening to service requests
        int museumServerPortNumb = -1;                                          // port number where the server for the museum is listening to service requests

        if (args.length != 9) {
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

        // Get assault party 1 parameters
        assaultParty1ServerHostName = args[3];
        try {
            assaultParty1ServerPortNumb = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            GenericIO.writelnString("args[5] is not a number!");
            System.exit(1);
        }
        if ( (assaultParty1ServerPortNumb < 22150) || (assaultParty1ServerPortNumb > 22159) ) {
            GenericIO.writelnString("args[5] is not a valid port number!");
            System.exit(1);
        }

        // Get assault party 2 parameters
        assaultParty2ServerHostName = args[5];
        try {
            assaultParty2ServerPortNumb = Integer.parseInt(args[6]);
        } catch (NumberFormatException e) {
            GenericIO.writelnString("args[6] is not a number!");
            System.exit(1);
        }
        if ( (assaultParty2ServerPortNumb < 22150) || (assaultParty2ServerPortNumb > 22159) ) {
            GenericIO.writelnString("args[7] is not a valid port number!");
            System.exit(1);
        }

        // Get museum parameters
        museumServerHostName = args[7];
        try {
            museumServerPortNumb = Integer.parseInt(args[8]);
        } catch (NumberFormatException e) {
            GenericIO.writelnString("args[6] is not a number!");
            System.exit(1);
        }
        if ( (museumServerPortNumb < 22150) || (museumServerPortNumb > 22159) ) {
            GenericIO.writelnString("args[7] is not a valid port number!");
            System.exit(1);
        }

        /* service is established */
        reposStub = new GeneralReposStub(reposServerName, reposPortNumb);       // communication to the general repository is instantiated
        assaultPartiesStubs[0] = new AssaultPartyStub(assaultParty1ServerHostName, assaultParty1ServerPortNumb);
        assaultPartiesStubs[1] = new AssaultPartyStub(assaultParty2ServerHostName, assaultParty2ServerPortNumb);
        museumStub = new MuseumStub(museumServerHostName, museumServerPortNumb);

        concetSite = new ConcetSite(assaultPartiesStubs, museumStub, reposStub);// service is instantiated
        concetSiteInter = new ConcetSiteInterface(concetSite);                  // interface to the service is instantiated
        scon = new ServerCom(portNumb);                                         // listening channel at the public port is established
        scon.start();
        GenericIO.writelnString("Service is established!");
        GenericIO.writelnString("Server is listening for service requests.");

        /* service request processing */
        ConcetSiteClientProxy cliProxy;                                         // service provider agent

        waitConnection = true;
        while (waitConnection) {
            try {
                sconi = scon.accept();                                          // enter listening procedure
                cliProxy = new ConcetSiteClientProxy(sconi, concetSiteInter);   // start a service provider agent to address
                cliProxy.start();                                               //   the request of service
                scon.setTimeout(1000);
            } catch (SocketTimeoutException e) {
            } catch (SocketException e) {
            }
        }
        scon.end();                                                             // operations termination
        GenericIO.writelnString("[Concentration Site] Server was shutdown.");
    }
}
