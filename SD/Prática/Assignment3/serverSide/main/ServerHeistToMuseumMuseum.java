package serverSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import serverSide.objects.*;
import interfaces.*;

/**
 *    Server side of the Museum Site.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class ServerHeistToMuseumMuseum
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

        /* get a remote reference to the general repository object, museum object and assault parties*/
        String nameEntryGeneralRepos = "GeneralRepository";                             // public name of the general repository object
        GeneralReposInterface reposStub = null;                                         // remote reference to the general repository object
        String[] nameEntryAssaultParty = {"AssaultParty0", "AssaultParty1"};   // public name of the assault parties objects
        AssaultPartyInterface[] assaultPartiesStubs = new AssaultPartyInterface[2];     // remote reference to the assault parties objects

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

        int [] paintingPerRoom = new int [SimulPar.ROOMS_NUMBER];               // number of paintings in each room
        int [] distancePerRoom = new int [SimulPar.ROOMS_NUMBER];               // distance between the room to the concentration site
        // Setting the rooms attributes
        for (int i = 0; i < SimulPar.ROOMS_NUMBER; i++) {
            paintingPerRoom[i] = (int) (Math.random() * (SimulPar.MAX_PAINTINGS - SimulPar.MIN_PAINTINGS + 1)) + SimulPar.MIN_PAINTINGS;
            distancePerRoom[i] = (int) (Math.random() * (SimulPar.MAX_ROOM_DISTANCE - SimulPar.MIN_ROOM_DISTANCE + 1)) + SimulPar.MIN_ROOM_DISTANCE;
        }

        try {
            reposStub.setDistancePerRoom(distancePerRoom);
        } catch (RemoteException e) {
            System.out.println("Remote exception thrown at setDistancePerRoom: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            reposStub.setPaintingsPerRoom(paintingPerRoom);
        } catch (RemoteException e) {
            System.out.println("Remote exception thrown at setPaintingsPerRoom: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        /* instantiate a museum object */
        Museum museum = new Museum(paintingPerRoom, distancePerRoom, assaultPartiesStubs, reposStub);                    // museum object
        MuseumInterface museumStub = null;                                             // remote reference to the museum site object

        try {
            museumStub = (MuseumInterface) UnicastRemoteObject.exportObject(museum, portNumb);
        } catch (RemoteException e) {
            System.out.println("Museum stub generation exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        /* register it with the general registry service */
        String nameEntryBase = "RegisterHandler";                                  // public name of the object that enables the registration
        // of other remote objects
        String nameEntryObject = "Museum";                                         // public name of the museum object
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
            reg.bind(nameEntryObject, museumStub);
        } catch (RemoteException e) {
            System.out.println("Museum registration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("Museum already bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Museum object was registered!");

        /* wait for the end of operations */
        System.out.println("Museum is in operation!");

        try {
            while (!end)
                synchronized (Class.forName("serverSide.main.ServerHeistToMuseumMuseum")) {
                    try {
                        (Class.forName("serverSide.main.ServerHeistToMuseumMuseum")).wait();
                    } catch (InterruptedException e) {
                        System.out.println("Museum main thread was interrupted!");
                    }
                }
        } catch (ClassNotFoundException e) {
            System.out.println("The data type Museum was not found (blocking)!");
            e.printStackTrace();
            System.exit(1);
        }

        /* server shutdown */

        boolean shutdownDone = false;                                             // flag signalling the shutdown of the museum site service

        try {
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("Museum deregistration exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Museum not bound exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Museum was deregistered!");

        try {
            shutdownDone = UnicastRemoteObject.unexportObject(museum, true);
        } catch (NoSuchObjectException e) {
            System.out.println("Museum unexport exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        if (shutdownDone) {
            System.out.println("Museum was shutdown!");
        }
    }

    /**
     *   Close of operations.
     */
    public static void shutdown() {
        end = true;
        try {
            synchronized (Class.forName("serverSide.main.ServerHeistToMuseumMuseum")) {
                (Class.forName("serverSide.main.ServerHeistToMuseumMuseum")).notify();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("The data type Museum was not found (waking up)!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
