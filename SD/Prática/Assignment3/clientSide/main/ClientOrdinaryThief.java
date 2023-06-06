package clientSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import clientSide.entities.*;
import genclass.*;
import serverSide.main.*;
import interfaces.*;

/**
 *   Client side of the Heist to Museum problem (Ordinary Thief).
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on Java RMI.
 */
public class ClientOrdinaryThief
{
    /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the RMI registering service
     *        args[1] - port number where the registering service is listening to service requests
     *
     */
    public static void main (String[] args)
    {
        String rmiRegHostName;                                         // name of the platform where is located the RMI registering service
        int rmiRegPortNumb = -1;                                       // port number where the registering service is listening to service request

        /* getting problem runtime parameters */
        if (args.length != 2) {
            System.out.println("Wrong number of parameters!");
            System.exit (1);
        }
        rmiRegHostName = args[0];
        try {
            rmiRegPortNumb = Integer.parseInt (args[1]);
        } catch (NumberFormatException e) {
            System.out.println("args[1] is not a number!");
            System.exit (1);
        }
        if ((rmiRegPortNumb < 4000) || (rmiRegPortNumb >= 65536)) {
            System.out.println("args[1] is not a valid port number!");
            System.exit (1);
        }

        /* problem initialisation */
        String nameEntryGeneralRepos = "GeneralRepository";                             // public name of the general repository object
        GeneralReposInterface genReposStub = null;                                      // remote reference to the general repository object
        String nameEntryMuseum = "Museum";                                              // public name of the museum object
        MuseumInterface museumStub = null;                                              // remote reference to the museum object
        String nameEntryControlSite = "ControlSite";                                    // public name of the control site object
        ControlSiteInterface controlSiteStub = null;                                    // remote reference to the control site object
        String nameEntryConcetSite = "ConcetSite";                                      // public name of the concentration site object
        ConcetSiteInterface concetSiteStub = null;                                      // remote reference to the concentration site object

        AssaultPartyInterface [] assaultPartyStubs = new AssaultPartyInterface[2];      // Remote reference to the Assault Party stubs
        String nameEntryAssaultParty1 = "AssaultParty0";                                // public name of the assault party 1 object
        assaultPartyStubs[0] = null;                                                    // remote reference to the assault party 1 object
        String nameEntryAssaultParty2 = "AssaultParty1";                                // public name of the assault party 2 object
        assaultPartyStubs[1] = null;                                                    // remote reference to the assault party 2 object

        Registry registry = null;                                                       // remote reference for registration in the RMI registry service

        OrdinaryThief [] ordinaryThief = new OrdinaryThief [SimulPar.ORDINARY_THIEVES]; // array of ordinary thieves threads
        int [] agilityPerThief = new int [SimulPar.ORDINARY_THIEVES];                   // agility of each ordinary thief

        try {
            registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            genReposStub = (GeneralReposInterface) registry.lookup(nameEntryGeneralRepos);
        } catch (RemoteException e) {
            System.out.println("GeneralRepos lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("GeneralRepos not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            museumStub = (MuseumInterface) registry.lookup(nameEntryMuseum);
        } catch (RemoteException e) {
            System.out.println("Museum lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Museum not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            controlSiteStub = (ControlSiteInterface) registry.lookup(nameEntryControlSite);
        } catch (RemoteException e) {
            System.out.println("Control Site lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Control Site not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            concetSiteStub = (ConcetSiteInterface) registry.lookup(nameEntryConcetSite);
        } catch (RemoteException e) {
            System.out.println("Concentration Site lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Concentration Site not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            assaultPartyStubs[0] = (AssaultPartyInterface) registry.lookup(nameEntryAssaultParty1);
        } catch (RemoteException e) {
            System.out.println("Assault Party 1 lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Assault Party 1 not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            assaultPartyStubs[1] = (AssaultPartyInterface) registry.lookup(nameEntryAssaultParty2);
        } catch (RemoteException e) {
            System.out.println("Assault Party 2 lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Assault Party 2 not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Setting ordinary thieves agility
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            agilityPerThief[i] = (int) (Math.random() * (SimulPar.MAX_THIEVES_DISPLACEMENT - SimulPar.MIN_THIEVES_DISPLACEMENT + 1)) + SimulPar.MIN_THIEVES_DISPLACEMENT;
        }

        // Sending agility to the General Repository
        try{
            genReposStub.setOrdinaryThievesAgility(agilityPerThief);
        } catch (RemoteException e) {
            System.out.println("Remote exception setting ordinary thieves agility: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

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

        /* Museum shutdown */
        try {
            museumStub.shutdown();
        } catch (RemoteException e) {
            System.out.println("Remote exception shutting down museum: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        /* General Repository shutdown */
        try {
            genReposStub.shutdown();
        } catch (RemoteException e) {
            System.out.println("Remote exception shutting down general repository: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
