package clientSide.main;

import clientSide.entities.*;
import clientSide.stubs.*;
import serverSide.main.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Client side of the Sleeping Barbers (barbers).
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class ClientSleepingBarbersBarber
{
  /**
   *  Main method.
   *
   *    @param args runtime arguments
   *        args[0] - name of the platform where is located the barber shop server
   *        args[1] - port nunber for listening to service requests
   *        args[2] - name of the platform where is located the general repository server
   *        args[3] - port nunber for listening to service requests
   */

   public static void main (String [] args)
   {
      String barberShopServerHostName;                               // name of the platform where is located the barber shop server
      int barberShopServerPortNumb = -1;                             // port number for listening to service requests
      String genReposServerHostName;                                 // name of the platform where is located the general repository server
      int genReposServerPortNumb = -1;                               // port number for listening to service requests
      Barber [] barber = new Barber [SimulPar.M];                    // array of barber threads
      BarberShopStub bShopStub;                                      // remote reference to the barber shop
      GeneralReposStub genReposStub;                                 // remote reference to the general repository


     /* getting problem runtime parameters */

      if (args.length != 4)
         { GenericIO.writelnString ("Wrong number of parameters!");
           System.exit (1);
         }
      barberShopServerHostName = args[0];
      try
      { barberShopServerPortNumb = Integer.parseInt (args[1]);
      }
      catch (NumberFormatException e)
      { GenericIO.writelnString ("args[1] is not a number!");
        System.exit (1);
      }
      if ((barberShopServerPortNumb < 4000) || (barberShopServerPortNumb >= 65536))
         { GenericIO.writelnString ("args[1] is not a valid port number!");
           System.exit (1);
         }
      genReposServerHostName = args[2];
      try
      { genReposServerPortNumb = Integer.parseInt (args[3]);
      }
      catch (NumberFormatException e)
      { GenericIO.writelnString ("args[3] is not a number!");
        System.exit (1);
      }
      if ((genReposServerPortNumb < 4000) || (genReposServerPortNumb >= 65536))
         { GenericIO.writelnString ("args[3] is not a valid port number!");
           System.exit (1);
         }

     /* problem initialization */

      bShopStub = new BarberShopStub (barberShopServerHostName, barberShopServerPortNumb);
      genReposStub = new GeneralReposStub (genReposServerHostName, genReposServerPortNumb);
      for (int i = 0; i < SimulPar.M; i++)
        barber[i] = new Barber ("barb_" + (i+1), i, bShopStub);

     /* start of the simulation */

      for (int i = 0; i < SimulPar.M; i++)
        barber[i].start ();

     /* waiting for the end of the simulation */

      GenericIO.writelnString ();
      for (int i = 0; i < SimulPar.M; i++)
      { while (barber[i].isAlive ())
        { bShopStub.endOperation (i);
          Thread.yield ();
        }
        try
        { barber[i].join ();
        }
        catch (InterruptedException e) {}
        GenericIO.writelnString ("The barber " + (i+1) + " has terminated.");
      }
      GenericIO.writelnString ();
      bShopStub.shutdown ();
      genReposStub.shutdown ();
   }
}
