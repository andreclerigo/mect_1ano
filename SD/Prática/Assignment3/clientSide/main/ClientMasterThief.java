package clientSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import clientSide.entities.*;
import serverSide.main.*;
import interfaces.*;

/**
 *   Client side of the Heist to Museum problem (Master Thief).
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on Java RMI.
 */
public class ClientMasterThief
{
    /**
     *  Main method.
     *
     *    @param args runtime arguments
     *        args[0] - name of the platform where is located the RMI registering service
     *        args[1] - port number where the registering service is listening to service requests
     */
    public static void main(String[] args) {
        MasterThief masterThief;                                                        // Master Thief thread
        String rmiRegHostName;                                         // Name of the platform where is located the RMI registering service
        int rmiRegPortNumb = -1;                                       // Port number where the registering service is listening to service request

        /* Getting problem runtime parameters */
        if(args.length != 2) {
            System.out.println("Wrong number of parameters!");
            System.exit(1);
        }

        rmiRegHostName = args[0];

        try {
            rmiRegPortNumb = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            System.out.println("args[1] is not a number!");
            System.exit(1);
        }
        if ((rmiRegPortNumb < 4000) || (rmiRegPortNumb >= 65536)) {
            System.out.println("args[1] is not a valid port number!");
            System.exit(1);
        }

        /* problem initialisation */
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

        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch(RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            controlSiteStub = (ControlSiteInterface) registry.lookup(nameEntryControlSite);
        } catch(RemoteException e) {
            System.out.println("Control Site lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch(NotBoundException e) {
            System.out.println("Control Site not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            concetSiteStub = (ConcetSiteInterface) registry.lookup(nameEntryConcetSite);
        } catch(RemoteException e) {
            System.out.println("Concet Site lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch(NotBoundException e) {
            System.out.println("Concet Site not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            assaultPartyStubs[0] = (AssaultPartyInterface) registry.lookup(nameEntryAssaultParty1);
        } catch(RemoteException e) {
            System.out.println("Assault Party 1 lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch(NotBoundException e) {
            System.out.println("Assault Party 1 not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            assaultPartyStubs[1] = (AssaultPartyInterface) registry.lookup(nameEntryAssaultParty2);
        } catch(RemoteException e) {
            System.out.println("Assault Party 2 lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch(NotBoundException e) {
            System.out.println("Assault Party 2 not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        masterThief = new MasterThief("MasterThief", controlSiteStub, concetSiteStub, assaultPartyStubs);

        /* start simulation */
        System.out.println("Launching Master Thief Thread ");
        masterThief.start();

        /* waiting for the end of the simulation */
        try {
            masterThief.join();
        } catch(InterruptedException e) {}
        System.out.println("The Master Thief thread has terminated.");

        try {
            controlSiteStub.shutdown();
        } catch(RemoteException e) {
            System.out.println("Control Site shutdown exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            concetSiteStub.shutdown();
        } catch(RemoteException e) {
            System.out.println("Concet Site shutdown exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            assaultPartyStubs[0].shutdown();
        } catch(RemoteException e) {
            System.out.println("Assault Party 1 shutdown exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            assaultPartyStubs[1].shutdown();
        } catch(RemoteException e) {
            System.out.println("Assault Party 2 shutdown exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
