package main;

import entities.*;
import sharedRegions.*;
import genclass.GenericIO;
import genclass.FileOp;

/**
 *   Simulation of the Problem of the Sleeping Barbers.
 *   Static solution based on a priori reasoning to terminate the barbers threads.
 */

public class SleepingBarbers
{
  /**
   *    Main method.
   *
   *    @param args runtime arguments
   */

   public static void main (String [] args)
   {
      Barber [] barber = new Barber [SimulPar.M];          // array of barber threads
      Customer [] customer = new Customer [SimulPar.N];    // array of customer threads
      BarberShop bShop;                                    // reference to the barber shop
      GeneralRepos repos;                                  // reference to the general repository
      int nIter;                                           // number of iterations of the customer life cycle
      String fileName;                                     // logging file name
      char opt;                                            // selected option
      boolean success;                                     // end of operation flag

     /* problem initialization */

      GenericIO.writelnString ("\n" + "      Problem of the Sleeping Barbers\n");
      GenericIO.writeString ("Number of iterations of the customer life cycle? ");
      nIter = GenericIO.readlnInt ();
      do
      { GenericIO.writeString ("Logging file name? ");
        fileName = GenericIO.readlnString ();
        if (FileOp.exists (".", fileName))
           { do
             { GenericIO.writeString ("There is already a file with this name. Delete it (y - yes; n - no)? ");
               opt = GenericIO.readlnChar ();
             } while ((opt != 'y') && (opt != 'n'));
             if (opt == 'y')
                success = true;
                else success = false;
           }
           else success = true;
      } while (!success);

      repos = new GeneralRepos (fileName, nIter);
      bShop = new BarberShop (nIter, repos);
      for (int i = 0; i < SimulPar.M; i++)
        barber[i] = new Barber ("Barb_" + (i+1), i, bShop);
      for (int i = 0; i < SimulPar.N; i++)
        customer[i] = new Customer ("Cust_" + (i+1), i, bShop, nIter);

     /* start of the simulation */

      for (int i = 0; i < SimulPar.M; i++)
        barber[i].start ();
      for (int i = 0; i < SimulPar.N; i++)
        customer[i].start ();

     /* waiting for the end of the simulation */

      GenericIO.writelnString ();
      for (int i = 0; i < SimulPar.N; i++)
      { try
        { customer[i].join ();
        }
        catch (InterruptedException e) {}
        GenericIO.writelnString ("The customer " + (i+1) + " has terminated.");
      }
      GenericIO.writelnString ();
      for (int i = 0; i < SimulPar.M; i++)
      { try
        { barber[i].join ();
        }
        catch (InterruptedException e) {}
        GenericIO.writelnString ("The barber " + (i+1) + " has terminated.");
      }
      GenericIO.writelnString ();
    }
}
