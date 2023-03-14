package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.Objects;

/**
 *  General Repository.
 *
 *    It is responsible to keep the visible internal state of the problem and to provide means for it
 *    to be printed in the logging file.
 *    It is implemented using semaphores for synchronization.
 *    All public methods are executed in mutual exclusion.
 *    There are no internal synchronization points.
 */

public class GeneralRepos
{
  /**
   *  Name of the logging file.
   */

   private final String logFileName;

  /**
   *  Number of iterations of the customer life cycle.
   */

   private final int nIter;

  /**
   *  State of the barbers.
   */

   private final int [] barberState;

  /**
   *  State of the customers.
   */

   private final int [] customerState;

  /**
   *  Semaphore to ensure mutual exclusion on the execution of public methods.
   */

   private final Semaphore access;

  /**
   *   Instantiation of a general repository object.
   *
   *     @param logFileName name of the logging file
   *     @param nIter number of iterations of the customer life cycle
   */

   public GeneralRepos (String logFileName, int nIter)
   {
      if ((logFileName == null) || Objects.equals (logFileName, ""))
         this.logFileName = "logger";
         else this.logFileName = logFileName;
      this.nIter = nIter;
      barberState = new int [SimulPar.M];
      for (int i = 0; i < SimulPar.M; i++)
        barberState[i] = BarberStates.SLEEPING;
      customerState = new int [SimulPar.N];
      for (int i = 0; i < SimulPar.N; i++)
        customerState[i] = CustomerStates.DAYBYDAYLIFE;
      access = new Semaphore ();
      access.up ();
      reportInitialStatus ();
   }

  /**
   *   Set barber state.
   *
   *     @param id barber id
   *     @param state barber state
   */

   public void setBarberState (int id, int state)
   {
      access.down ();                                      // enter critical region
      barberState[id] = state;
      reportStatus ();
      access.up ();                                        // exit critical region
   }

  /**
   *   Set customer state.
   *
   *     @param id customer id
   *     @param state customer state
   */

   public void setCustomerState (int id, int state)
   {
      access.down ();                                      // enter critical region
      customerState[id] = state;
      reportStatus ();
      access.up ();                                        // exit critical region
   }

  /**
   *   Set barber and customer state.
   *
   *     @param barberId barber id
   *     @param barberState barber state
   *     @param customerId customer id
   *     @param customerState customer state
   */

   public void setBarberCustomerState (int barberId, int barberState, int customerId, int customerState)
   {
      access.down ();                                      // enter critical region
      this.barberState[barberId] = barberState;
      this.customerState[customerId] = customerState;
      reportStatus ();
      access.up ();                                        // exit critical region
   }

  /**
   *  Write the header to the logging file.
   *
   *  The barbers are sleeping and the customers are carrying out normal duties.
   *  Internal operation.
   */

   private void reportInitialStatus ()
   {
      TextFile log = new TextFile ();                      // instantiation of a text file handler

      if (!log.openForWriting (".", logFileName))
         { GenericIO.writelnString ("The operation of creating the file " + logFileName + " failed!");
           System.exit (1);
         }
      log.writelnString ("                Problem of the Sleeping Barbers");
      log.writelnString ("\nNumber of iterations = " + nIter + "\n");
      log.writelnString (" Barber 1  Barber 2  Custmr 1  Custmr 2  Custmr 3  Custmr 4  Custmr 5");
      if (!log.close ())
         { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
           System.exit (1);
         }
      reportStatus ();
   }

  /**
   *  Write a state line at the end of the logging file.
   *
   *  The current state of the barbers and the customers is organized in a line to be printed.
   *  Internal operation.
   */

   private void reportStatus ()
   {
      TextFile log = new TextFile ();                      // instantiation of a text file handler

      String lineStatus = "";                              // state line to be printed

      if (!log.openForAppending (".", logFileName))
         { GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
           System.exit (1);
         }
      for (int i = 0; i < SimulPar.M; i++)
        switch (barberState[i])
        { case BarberStates.SLEEPING:   lineStatus += " SLEEPING ";
                                        break;
          case BarberStates.INACTIVITY: lineStatus += " ACTIVICT ";
                                        break;
        }
      for (int i = 0; i < SimulPar.N; i++)
        switch (customerState[i])
        { case CustomerStates.DAYBYDAYLIFE:  lineStatus += " DAYBYDAY ";
                                             break;
          case CustomerStates.WANTTOCUTHAIR: lineStatus += " WANTCUTH ";
                                             break;
          case CustomerStates.WAITTURN:      lineStatus += " WAITTURN ";
                                             break;
          case CustomerStates.CUTTHEHAIR:    lineStatus += " CUTTHAIR ";
                                             break;
        }
      log.writelnString (lineStatus);
      if (!log.close ())
         { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
           System.exit (1);
         }
   }
}
