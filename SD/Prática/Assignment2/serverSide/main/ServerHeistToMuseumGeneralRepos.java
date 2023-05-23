package serverSide.main;

import serverSide.entities.*;
import serverSide.sharedRegions.*;
import commInfra.*;
import genclass.GenericIO;

import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 *    Server side of the General Repository.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ServerHeistToMuseumGeneralRepos {

    /**
     *   Flag signaling the service is active.
     */
    public static boolean waitConnection;

    /**
     *   Main method.
     *
     *     @param args runtime arguments
     *      args[0] - port number for listening to service requests
     */
    public static void main (String [] args)
    {
        GeneralRepos repos;                                                     // general repository (service to be rendered)
        GeneralReposInterface reposInterface;                                   // interface to the general repository
        ServerCom scon, sconi;                                                  // communication channels
        int portNumb = -1;                                                      // port number for listening to service requests

        if (args.length != 1) {
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

        /* service is established */
        repos = new GeneralRepos("logger");                          // general repository service is instantiated
        reposInterface = new GeneralReposInterface(repos);                      // interface to the general repository is instantiated

        scon = new ServerCom(portNumb);                                         // listening channel at the public port is established
        scon.start();
        GenericIO.writelnString("Service is established!");
        GenericIO.writelnString("Server is listening for service requests.");

        /* service request processing */
        GeneralReposClientProxy cliProxy;                                        // service provider agent

        waitConnection = true;
        while (waitConnection) {
            try {
                sconi = scon.accept();                                          // enter listening procedure
                cliProxy = new GeneralReposClientProxy(sconi, reposInterface); // start a service provider agent to address
                cliProxy.start ();                                              // the request of service
                scon.setTimeout(1000);
            } catch (SocketTimeoutException e) {
            } catch (SocketException e) {
            }
        }
        scon.end();                                                            // operations termination
        GenericIO.writelnString("[General Repos] Server was shutdown.");
    }
}
