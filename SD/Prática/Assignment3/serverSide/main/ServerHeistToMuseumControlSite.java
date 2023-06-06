package serverSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import serverSide.objects.*;
import interfaces.*;

/**
 *    Server side of the Control Site.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ServerHeistToMuseumControlSite
{
    /**
     *   Flag signaling the end of operations.
     */
    public static boolean end = false;

    /**
     *   Main method.
     *
     *     @param args runtime arguments
     *      args[0] - port number for listening to service requests
     *      args[1] - name of the platform where is located the RMI registering service
     *      args[2] - port number where the registering service is listening to service requests
     */
    public static void main (String [] args) {
        int portNumb = -1;                                                      // port number for listening to service requests
        String rmiRegHostName;                                                  // name of the platform where is located the RMI registering service
        int rmiRegPortNumb = -1;                                                // port number where the registering service is listening to service requests

        if (args.length != 3) {
            System.out.println("Wrong number of parameters!");
            System.exit(1);
        }

        try {
            portNumb = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("args[0] is not a number!");
            System.exit(1);
        }

        if ((portNumb < 4000) || (portNumb >= 65536)) {
            System.out.println("args[0] is not a valid port number!");
            System.exit(1);
        }

        rmiRegHostName = args[1];

        try {
            rmiRegPortNumb = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("args[2] is not a number!");
            System.exit(1);
        }

        if ((rmiRegPortNumb < 4000) || (rmiRegPortNumb >= 65536)) {
            System.out.println("args[2] is not a valid port number!");
            System.exit(1);
        }

        /* create and install the security manager */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.out.println("Security manager was installed!");

        /* get a remote reference to the general repository object and the assault parties objects */
        String nameEntryGeneralRepos = "GeneralRepository";                      // public name of the general repository object
        GeneralReposInterface reposStub = null;                                  // remote reference to the general repository object
        String[] nameEntryAssaultParty = {"AssaultParty0", "AssaultParty1"};
        AssaultPartyInterface[] assaultPartiesStubs = new AssaultPartyInterface[2];

        Registry registry = null;                                                // remote reference for registration in the RMI registry service

        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            reposStub = (GeneralReposInterface) registry.lookup(nameEntryGeneralRepos);
        } catch (RemoteException e) {
            System.out.println("GeneralReposRemoteObject lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("GeneralReposRemoteObject not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        for (int i = 0; i < 2; i++) {
            try {
                assaultPartiesStubs[i] = (AssaultPartyInterface) registry.lookup(nameEntryAssaultParty[i]);
            } catch (RemoteException e) {
                System.out.println("AssaultParty" + (i + 1) + "RemoteObject lookup exception: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            } catch (NotBoundException e) {
                System.out.println("AssaultParty" + (i + 1) + "RemoteObject not bound exception: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }

        /* instantiate a control site object */
        ControlSite controlSite = new ControlSite(assaultPartiesStubs, reposStub);                    // control site object
        ControlSiteInterface controlSiteStub = null;                                  // remote reference to the control site object

        try {
            controlSiteStub = (ControlSiteInterface) UnicastRemoteObject.exportObject(controlSite, portNumb);
        } catch (RemoteException e) {
            System.out.println("ControlSite stub generation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        /* register it with the general registry service */
        String nameEntryBase = "RegisterHandler";                                  // public name of the object that enables the registration
        // of other remote objects
        String nameEntryObject = "ControlSite";                                    // public name of the control site object
        Register reg = null;                                                       // remote reference to the object that enables the registration
        // of other remote objects

        try {
            reg = (Register) registry.lookup(nameEntryBase);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            reg.bind(nameEntryObject, controlSiteStub);
        } catch (RemoteException e) {
            System.out.println("ControlSite registration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("ControlSite already bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("ControlSite object was registered!");

        /* wait for the end of operations */
        System.out.println("ControlSite is in operation!");

        try {
            while (!end)
                synchronized (Class.forName("serverSide.main.ServerHeistToMuseumControlSite")) {
                    try {
                        (Class.forName("serverSide.main.ServerHeistToMuseumControlSite")).wait();
                    } catch (InterruptedException e) {
                        System.out.println("ControlSite main thread was interrupted!");
                    }
                }
        } catch (ClassNotFoundException e) {
            System.out.println("The data type ControlSite was not found (blocking)!");
            e.printStackTrace();
            System.exit(1);
        }

        /* server shutdown */

        boolean shutdownDone = false;                                             // flag signalling the shutdown of the control site service

        try {
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("ControlSite deregistration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("ControlSite not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("ControlSite was deregistered!");

        try {
            shutdownDone = UnicastRemoteObject.unexportObject(controlSite, true);
        } catch (NoSuchObjectException e) {
            System.out.println("ControlSite unexport exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        if (shutdownDone) {
            System.out.println("ControlSite was shutdown!");
        }
    }

    /**
     *   Close of operations.
     */
    public static void shutdown() {
        end = true;
        try {
            synchronized (Class.forName("serverSide.main.ServerHeistToMuseumControlSite")) {
                (Class.forName("serverSide.main.ServerHeistToMuseumControlSite")).notify();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("The data type ControlSite was not found (waking up)!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
