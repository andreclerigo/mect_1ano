package clientSide.main;

import clientSide.stubs.*;
import clientSide.entities.*;

/**
 *   Client side of the Heist to Museum problem (Master Thief).
 *   Implementation of a client-server model of type 2 (server replication).
 *	 Communication is based on a communication channel under the TCP protocol.
 */
public class ClientMasterThief
{
    /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the Control Site server
     *        args[1] - port number for listening to service requests
     *		  args[2] - name of the platform where is located the Concentration Site server
     *        args[3] - port number for listening to service requests
     *        args[4] - name of the platform where is located the Assault Party 1 server
     *        args[5] - port number for listening to service requests
     *        args[6] - name of the platform where is located the Assault Party 2 server
     *        args[7] - port number for listening to service requests
     */
    public static void main(String[] args) {
        MasterThief masterThief;		                                    // Master Thief Thread
        ControlSiteStub controlSiteStub;		                            // Remote reference to the Control Site stub
        ConcetSiteStub concetSiteStub;			                            // Remote reference to the Concentration Site stub
        AssaultPartyStub [] assaultPartyStubs = new AssaultPartyStub[2];	// Remote reference to the Assault Parties stubs

        // Name of the platforms where kitchen and bar servers are located
        String controlSiteServerHostName;
        String concetSiteServerHostName;
        String assaultParty1ServerHostName;
        String assaultParty2ServerHostName;

        // Port numbers for listening to service requests
        int controlSiteServerPortNumb = -1;
        int concetSiteServerPortNumb = -1;
        int assaultParty1ServerPortNumb = -1;
        int assaultParty2ServerPortNumb = -1;

        /* Getting problem runtime parameters */
        if(args.length != 8) {
            System.out.println("Wrong number of parameters!");
            System.exit(1);
        }

        // Get control site parameters
        controlSiteServerHostName = args[0];
        try {
            controlSiteServerPortNumb = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("args[1] is not a number!");
            System.exit(1);
        }
        if ( (controlSiteServerPortNumb < 22150) || (controlSiteServerPortNumb > 22159) ) {
            System.out.println("args[1] is not a valid port number!");
            System.exit(1);
        }

        // Get concentration site parameters
        concetSiteServerHostName = args[2];
        try {
            concetSiteServerPortNumb = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("args[3] is not a number!");
            System.exit(1);
        }
        if( (concetSiteServerPortNumb < 22150) || (concetSiteServerPortNumb > 22159) ) {
            System.out.println("args[3] is not a valid port number!");
            System.exit(1);
        }

        // Get assault party 1 parameters
        assaultParty1ServerHostName = args[4];
        try {
            assaultParty1ServerPortNumb = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            System.out.println("args[5] is not a number!");
            System.exit(1);
        }
        if ( (assaultParty1ServerPortNumb < 22150) || (assaultParty1ServerPortNumb > 22159) ) {
            System.out.println("args[5] is not a valid port number!");
            System.exit(1);
        }

        // Get assault party 2 parameters
        assaultParty2ServerHostName = args[6];
        try {
            assaultParty2ServerPortNumb = Integer.parseInt(args[7]);
        } catch (NumberFormatException e) {
            System.out.println("args[7] is not a number!");
            System.exit(1);
        }
        if ( (assaultParty2ServerPortNumb < 22150) || (assaultParty2ServerPortNumb > 22159) ) {
            System.out.println("args[7] is not a valid port number!");
            System.exit(1);
        }

        /* problem initialisation */
        controlSiteStub = new ControlSiteStub(controlSiteServerHostName, controlSiteServerPortNumb);
        concetSiteStub = new ConcetSiteStub(concetSiteServerHostName, concetSiteServerPortNumb);
        assaultPartyStubs[0] = new AssaultPartyStub(assaultParty1ServerHostName, assaultParty1ServerPortNumb);
        assaultPartyStubs[1] = new AssaultPartyStub(assaultParty2ServerHostName, assaultParty2ServerPortNumb);
        masterThief = new MasterThief("MasterThief", controlSiteStub, concetSiteStub, assaultPartyStubs);

        /* start simulation */
        System.out.println("Launching Master Thief Thread ");
        masterThief.start();

        /* waiting for the end of the simulation */
        try {
            masterThief.join();
        } catch(InterruptedException e) {}
        System.out.println("The Master Thief thread has terminated.");

        controlSiteStub.shutdown();
        concetSiteStub.shutdown();
        assaultPartyStubs[0].shutdown();
        assaultPartyStubs[1].shutdown();
    }
}
