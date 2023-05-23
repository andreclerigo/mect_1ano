package clientSide.main;

import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.*;
import clientSide.entities.*;
import serverSide.main.*;
import interfaces.*;
import genclass.GenericIO;

/**
 *    Client side of the Sleeping Barbers (barbers).
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on Java RMI.
 */

public class ClientSleepingBarbersBarber
{
  /**
   *  Main method.
   *
   *    @param args runtime arguments
   *        args[0] - name of the platform where is located the RMI registering service
   *        args[1] - port number where the registering service is listening to service requests
   */

   public static void main (String [] args)
   {
      String rmiRegHostName;                                         // name of the platform where is located the RMI registering service
      int rmiRegPortNumb = -1;                                       // port number where the registering service is listening to service requests


     /* getting problem runtime parameters */

      if (args.length != 2)
         { GenericIO.writelnString ("Wrong number of parameters!");
           System.exit (1);
         }
      rmiRegHostName = args[0];
      try
      { rmiRegPortNumb = Integer.parseInt (args[1]);
      }
      catch (NumberFormatException e)
      { GenericIO.writelnString ("args[1] is not a number!");
        System.exit (1);
      }
      if ((rmiRegPortNumb < 4000) || (rmiRegPortNumb >= 65536))
         { GenericIO.writelnString ("args[1] is not a valid port number!");
           System.exit (1);
         }

     /* problem initialization */

      String nameEntryGeneralRepos = "GeneralRepository";            // public name of the general repository object
      GeneralReposInterface reposStub = null;                        // remote reference to the general repository object
      String nameEntryBarberShop = "BarberShop";                     // public name of the barber shop object
      BarberShopInterface bShopStub = null;                          // remote reference to the barber shop object
      Registry registry = null;                                      // remote reference for registration in the RMI registry service
      Barber [] barber = new Barber [SimulPar.M];                    // array of barber threads

      try
      { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("RMI registry creation exception: " + e.getMessage ());
        e.printStackTrace ();
        System.exit (1);
      }

      try
      { reposStub = (GeneralReposInterface) registry.lookup (nameEntryGeneralRepos);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("GeneralRepos lookup exception: " + e.getMessage ());
        e.printStackTrace ();
        System.exit (1);
      }
      catch (NotBoundException e)
      { GenericIO.writelnString ("GeneralRepos not bound exception: " + e.getMessage ());
        e.printStackTrace ();
        System.exit (1);
      }

      try
      { bShopStub = (BarberShopInterface) registry.lookup (nameEntryBarberShop);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("BarberShop lookup exception: " + e.getMessage ());
        e.printStackTrace ();
        System.exit (1);
      }
      catch (NotBoundException e)
      { GenericIO.writelnString ("BarberShop not bound exception: " + e.getMessage ());
        e.printStackTrace ();
        System.exit (1);
      }

      for (int i = 0; i < SimulPar.M; i++)
        barber[i] = new Barber ("barb_" + (i+1), i, bShopStub);

     /* start of the simulation */

      for (int i = 0; i < SimulPar.M; i++)
        barber[i].start ();

     /* waiting for the end of the simulation */

      for (int i = 0; i < SimulPar.M; i++)
      { while (barber[i].isAlive ())
        { try
          { bShopStub.endOperation (i);
          }
          catch (RemoteException e)
          { GenericIO.writelnString ("Barber generator remote exception on BarberShop endOperation: " + e.getMessage ());
            System.exit (1);
          }
          Thread.yield ();
        }
        try
        { barber[i].join ();
        }
        catch (InterruptedException e) {}
        GenericIO.writelnString ("The barber " + (i+1) + " has terminated.");
      }
      GenericIO.writelnString ();

      try
      { bShopStub.shutdown ();
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber generator remote exception on BarberShop shutdown: " + e.getMessage ());
        System.exit (1);
      }
      try
      { reposStub.shutdown ();
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber generator remote exception on GeneralRepos shutdown: " + e.getMessage ());
        System.exit (1);
      }
   }
}
