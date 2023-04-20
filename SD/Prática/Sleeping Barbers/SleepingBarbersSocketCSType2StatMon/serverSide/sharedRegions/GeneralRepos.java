package serverSide.sharedRegions;

import serverSide.main.*;
import clientSide.entities.*;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.Objects;

/**
 *  General Repository of information.
 *
 *    It is responsible to keep the visible internal state of the problem and to provide means for it
 *    to be printed in the logging file.
 *    It is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There are no internal synchronization points.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class GeneralRepos
{
  /**
   *  Name of the logging file.
   */

   private String logFileName;

  /**
   *  Number of iterations of the customer life cycle.
   */

   private int nIter;

  /**
   *  State of the barbers.
   */

   private final int [] barberState;

  /**
   *  State of the customers.
   */

   private final int [] customerState;

  /**
   *   Number of entity groups requesting the shutdown.
   */

   private int nEntities;

  /**
   *   Instantiation of a general repository object.
   */

   public GeneralRepos ()
   {
      logFileName = "logger";
      nIter = 0;
      barberState = new int [SimulPar.M];
      for (int i = 0; i < SimulPar.M; i++)
        barberState[i] = BarberStates.SLEEPING;
      customerState = new int [SimulPar.N];
      for (int i = 0; i < SimulPar.N; i++)
        customerState[i] = CustomerStates.DAYBYDAYLIFE;
      nEntities = 0;
   }

  /**
   *   Operation initialization of simulation.
   *
   *   New operation.
   *
   *     @param logFileName name of the logging file
   *     @param nIter number of iterations of the customer life cycle
   */

   public synchronized void initSimul (String logFileName, int nIter)
   {
      if (!Objects.equals (logFileName, ""))
         this.logFileName = logFileName;
      this.nIter = nIter;
      reportInitialStatus ();
   }

  /**
   *   Operation server shutdown.
   *
   *   New operation.
   */

   public synchronized void shutdown ()
   {
       nEntities += 1;
       if (nEntities >= SimulPar.E)
          ServerSleepingBarbersGeneralRepos.waitConnection = false;
   }

  /**
   *   Set barber state.
   *
   *     @param id barber id
   *     @param state barber state
   */

   public synchronized void setBarberState (int id, int state)
   {
      barberState[id] = state;
      reportStatus ();
   }

  /**
   *   Set customer state.
   *
   *     @param id customer id
   *     @param state customer state
   */

   public synchronized void setCustomerState (int id, int state)
   {
      customerState[id] = state;
      reportStatus ();
   }

  /**
   *   Set barber and customer state.
   *
   *     @param barberId barber id
   *     @param barberState barber state
   *     @param customerId customer id
   *     @param customerState customer state
   */

   public synchronized void setBarberCustomerState (int barberId, int barberState, int customerId, int customerState)
   {
      this.barberState[barberId] = barberState;
      this.customerState[customerId] = customerState;
      reportStatus ();
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
