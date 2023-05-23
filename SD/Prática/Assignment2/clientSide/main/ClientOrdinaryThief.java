package clientSide.main;

import clientSide.entities.OrdinaryThief;
import clientSide.stubs.*;

/**
 *   Client side of the Heist to Museum problem (Ordinary Thief).
 *   Implementation of a client-server model of type 2 (server replication).
 *	 Communication is based on a communication channel under the TCP protocol.
 */
public class ClientOrdinaryThief
{
    /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the Museum server
     *        args[1] - port number for listening to service requests
     *		  args[2] - name of the platform where is located the Control Site server
     *        args[3] - port number for listening to service requests
     *        args[4] - name of the platform where is located the Concentration Site server
     *        args[5] - port number for listening to service requests
     *        args[6] - name of the platform where is located the Assault Party 1 server
     *        args[7] - port number for listening to service requests
     *        args[8] - name of the platform where is located the Assault Party 2 server
     *        args[9] - port number for listening to service requests
     *        args[10] - name of the platform where is located the General Repository server
     *        args[11] - port number for listening to service requests
     */
    public static void main(String[] args) {
        OrdinaryThief [] ordinaryThief = new OrdinaryThief [SimulPar.ORDINARY_THIEVES]; // array of ordinary thieves threads
        MuseumStub museumStub;			                                                // Remote reference to the Museum stub
        ConcetSiteStub concetSiteStub;			                                        // Remote reference to the Concentration Site stub
        ControlSiteStub controlSiteStub;		                                        // Remote reference to the Control Site stub
        AssaultPartyStub [] assaultPartyStubs = new AssaultPartyStub[2];	            // Remote reference to the Assault Party stubs
        GeneralReposStub genReposStub;                                      // Remote reference to the General Repository stub
        int [] agilityPerThief = new int [SimulPar.ORDINARY_THIEVES];                   // agility of each ordinary thief

        // Name of the platforms where kitchen and bar servers are located
        String museumServerHostName;
        String controlSiteServerHostName;
        String concetSiteServerHostName;
        String assaultParty1ServerHostName;
        String assaultParty2ServerHostName;
        String genReposServerHostName;

        // Port numbers for listening to service requests
        int museumServerPortNumb = -1;
        int controlSiteServerPortNumb = -1;
        int concetSiteServerPortNumb = -1;
        int assaultParty1ServerPortNumb = -1;
        int assaultParty2ServerPortNumb = -1;
        int genReposServerPortNumb = -1;

        /* Getting problem runtime parameters */
        if(args.length != 12) {
            System.out.println("Wrong number of parameters!");
            System.exit(1);
        }

        // Get museum parameters
        museumServerHostName = args[0];
        try {
            museumServerPortNumb = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("args[1] is not a number!");
            System.exit(1);
        }
        if ( (museumServerPortNumb < 22150) || (museumServerPortNumb > 22159) ) {
            System.out.println("args[1] is not a valid port number!");
            System.exit(1);
        }

        // Get control site parameters
        controlSiteServerHostName = args[2];
        try {
            controlSiteServerPortNumb = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("args[3] is not a number!");
            System.exit(1);
        }
        if ( (controlSiteServerPortNumb < 22150) || (controlSiteServerPortNumb > 22159) ) {
            System.out.println("args[3] is not a valid port number!");
            System.exit(1);
        }

        // Get concentration site parameters
        concetSiteServerHostName = args[4];
        try {
            concetSiteServerPortNumb = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            System.out.println("args[5] is not a number!");
            System.exit(1);
        }
        if ( (concetSiteServerPortNumb < 22150) || (concetSiteServerPortNumb > 22159) ) {
            System.out.println("args[5] is not a valid port number!");
            System.exit(1);
        }

        // Get assault party 1 parameters
        assaultParty1ServerHostName = args[6];
        try {
            assaultParty1ServerPortNumb = Integer.parseInt(args[7]);
        } catch (NumberFormatException e) {
            System.out.println("args[7] is not a number!");
            System.exit(1);
        }
        if ( (assaultParty1ServerPortNumb < 22150) || (assaultParty1ServerPortNumb > 22159) ) {
            System.out.println("args[7] is not a valid port number!");
            System.exit(1);
        }

        // Get assault party 2 parameters
        assaultParty2ServerHostName = args[8];
        try {
            assaultParty2ServerPortNumb = Integer.parseInt(args[9]);
        } catch (NumberFormatException e) {
            System.out.println("args[9] is not a number!");
            System.exit(1);
        }
        if ( (assaultParty2ServerPortNumb < 22150) || (assaultParty2ServerPortNumb > 22159) ) {
            System.out.println("args[9] is not a valid port number!");
            System.exit(1);
        }

        // Get general repository parameters
        genReposServerHostName = args[10];
        try {
            genReposServerPortNumb = Integer.parseInt(args[11]);
        } catch (NumberFormatException e) {
            System.out.println("args[11] is not a number!");
            System.exit(1);
        }
        if ( (genReposServerPortNumb < 22150) || (genReposServerPortNumb > 22159) ) {
            System.out.println("args[11] is not a valid port number!");
            System.exit(1);
        }

        /* problem initialisation */
        museumStub = new MuseumStub(museumServerHostName, museumServerPortNumb);
        controlSiteStub = new ControlSiteStub(controlSiteServerHostName, controlSiteServerPortNumb);
        concetSiteStub = new ConcetSiteStub(concetSiteServerHostName, concetSiteServerPortNumb);
        assaultPartyStubs[0] = new AssaultPartyStub(assaultParty1ServerHostName, assaultParty1ServerPortNumb);
        assaultPartyStubs[1] = new AssaultPartyStub(assaultParty2ServerHostName, assaultParty2ServerPortNumb);
        genReposStub = new GeneralReposStub(genReposServerHostName, genReposServerPortNumb);

        // Setting ordinary thieves agility
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            agilityPerThief[i] = (int) (Math.random() * (SimulPar.MAX_THIEVES_DISPLACEMENT - SimulPar.MIN_THIEVES_DISPLACEMENT + 1)) + SimulPar.MIN_THIEVES_DISPLACEMENT;
        }

        // Send agility to the Repos
        genReposStub.setOrdinaryThievesAgility(agilityPerThief);

        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            ordinaryThief[i] = new OrdinaryThief("Thief_" + (i + 1), i, agilityPerThief[i], museumStub, controlSiteStub, concetSiteStub, assaultPartyStubs);
        }

        /* start of the simulation */
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            ordinaryThief[i].start();
        }

        /* waiting for the end of the simulation */
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            try {
                ordinaryThief[i].join ();
            } catch (InterruptedException ignored) {}

            System.out.println("The ordinary thief " + (i+1) + " has terminated.");
        }

        museumStub.shutdown();
        genReposStub.shutdown();
    }
}
