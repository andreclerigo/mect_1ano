package serverSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import serverSide.objects.*;
import interfaces.*;

/**
 *    Server side of the Assault Party.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ServerHeistToMuseumAssaultParty
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
     *      args[3] - id of assault party
     */
    public static void main (String [] args) {
        int portNumb = -1;                                                      // port number for listening to service requests
        String rmiRegHostName;                                                  // name of the platform where is located the RMI registering service
        int rmiRegPortNumb = -1;                                                // port number where the registering service is listening to service requests
        int assPartyId = -1;                                                    // id of assault party

        if (args.length != 4) {
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

        try{
            assPartyId = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("args[3] is not a number!");
            System.exit(1);
        }

        if (assPartyId > 1 || assPartyId < 0) {
            System.out.println("args[3] is not a valid assault party id!");
            System.exit(1);
        }

        /* create and install the security manager */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.out.println("Security manager was installed!");

    
        Registry registry = null;                                                // remote reference for registration in the RMI registry service

        try {
            registry = LocateRegistry.getRegistry(rmiRegHostName, rmiRegPortNumb);
        } catch (RemoteException e) {
            System.out.println("RMI registry creation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        /* get a remote reference to the general repository object, museum object and assault parties*/
        String nameEntryGeneralRepos = "GeneralRepository";                             // public name of the general repository object
        GeneralReposInterface reposStub = null;                                         // remote reference to the general repository object

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

        // TODO: check if this works: params targetRoomId=-1, id is args[3] (tratar disto) 
        /* instantiate an assault party object */          
        AssaultParty assaultParty = new AssaultParty(assPartyId,-1, reposStub);     // assault party object
        AssaultPartyInterface assaultPartyStub = null;                              // remote reference to the assault party object

        try {
            assaultPartyStub = (AssaultPartyInterface) UnicastRemoteObject.exportObject(assaultParty, portNumb);
        } catch (RemoteException e) {
            System.out.println("Assault Party stub generation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        /* register it with the general registry service */
        String nameEntryBase = "RegisterHandler";                                   // public name of the object that enables the registration
                                                                                    // of other remote objects
        String nameEntryObject = "AssaultParty" + assPartyId;                                    // public name of the assault party object
        Register reg = null;                                                        // remote reference to the object that enables the registration
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
            reg.bind(nameEntryObject, assaultPartyStub);
        } catch (RemoteException e) {
            System.out.println("Assault Party registration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("Assault Party already bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Assault Party object was registered!");

        /* wait for the end of operations */
        System.out.println("Assault Party is in operation!");

        try {
            while (!end)
                synchronized (Class.forName("serverSide.main.ServerHeistToMuseumAssaultParty")) {
                    try {
                        (Class.forName("serverSide.main.ServerHeistToMuseumAssaultParty")).wait();
                    } catch (InterruptedException e) {
                        System.out.println("Assault Party main thread was interrupted!");
                    }
                }
        } catch (ClassNotFoundException e) {
            System.out.println("The data type Assault Party was not found (blocking)!");
            e.printStackTrace();
            System.exit(1);
        }

        /* server shutdown */

        boolean shutdownDone = false;                                             // flag signalling the shutdown of the assault party service

        try {
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("Assault Party deregistration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Assault Party not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Assault Party was deregistered!");

        try {
            shutdownDone = UnicastRemoteObject.unexportObject(assaultParty, true);
        } catch (NoSuchObjectException e) {
            System.out.println("Assault Party unexport exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        if (shutdownDone) {
            System.out.println("Assault Party was shutdown!");
        }
    }

    /**
     *   Close of operations.
     */
    public static void shutdown() {
        end = true;
        try {
            synchronized (Class.forName("serverSide.main.ServerHeistToMuseumAssaultParty")) {
                (Class.forName("serverSide.main.ServerHeistToMuseumAssaultParty")).notify();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("The data type Assault Party was not found (waking up)!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
