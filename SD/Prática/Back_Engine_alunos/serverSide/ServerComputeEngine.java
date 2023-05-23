package serverSide;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import genclass.GenericIO;
import interfaces.Compute;
import interfaces.Register;

/**
 *   Instantiation and registering of a remote object that will run locally code transferred to it.
 *
 *   Communication is based in Java RMI.
 */

public class ServerComputeEngine
{
  /**
   *  Main method.
   *
   *    @param args runtime arguments
   */

   public static void main(String[] args)
   {
    /* get location of the registry service */

     String rmiRegHostName;
     int rmiRegPortNumb;

     GenericIO.writeString ("Name of the processing node where the registering service is located? ");
     rmiRegHostName = GenericIO.readlnString ();
     GenericIO.writeString ("Port number where the registering service is listening to? ");
     rmiRegPortNumb = GenericIO.readlnInt ();

    /* create and install the security manager */

     if (System.getSecurityManager () == null)
        System.setSecurityManager (new SecurityManager ());
     GenericIO.writelnString ("Security manager was installed!");

    /* instantiate a remote object that runs mobile code and generate a stub for it */

     ComputeEngine engine = new ComputeEngine ();
     Compute engineStub = null;
     int listeningPort = 22002;                                      /* it should be set accordingly in each case */

     try
     { engineStub = (Compute) UnicastRemoteObject.exportObject (engine, listeningPort);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("ComputeEngine stub generation exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("Stub was generated!");

    /* register it with the general registry service */

     String nameEntryBase = "RegisterHandler";
     String nameEntryObject = "Compute";
     Registry registry = null;
     Register reg = null;

     try
     { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("RMI registry creation exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("RMI registry was created!");

     try
     { reg = (Register) registry.lookup (nameEntryBase);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("RegisterRemoteObject lookup exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     catch (NotBoundException e)
     { GenericIO.writelnString ("RegisterRemoteObject not bound exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }

     try
     { reg.bind (nameEntryObject, engineStub);
     }
     catch (RemoteException e)
     { GenericIO.writelnString ("ComputeEngine registration exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     catch (AlreadyBoundException e)
     { GenericIO.writelnString ("ComputeEngine already bound exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     GenericIO.writelnString ("ComputeEngine object was registered!");
 }
}
