package serverSide.objects;

import java.rmi.registry.*;
import java.rmi.*;
import interfaces.*;
import serverSide.main.*;
import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *  Barber shop.
 *
 *    It is responsible to keep a continuously updated account of the customers inside the barber shop
 *    and is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There are two internal synchronization points: a single blocking point for the barbers, where they wait for a customer;
 *    and an array of blocking points, one per each customer, where he both waits his turn to cut the hair and sits on the
 *    cutting chair while having his hair cut.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on Java RMI.
 */

public class BarberShop implements BarberShopInterface
{
  /**
   *  Number of hair cuts which have been requested and not serviced yet.
   */

   private int nReqCut;

  /**
   *  Reference to barber threads.
   */

   private final Thread [] barb;

  /**
   *  Reference to customer threads.
   */

   private final Thread [] cust;

  /**
   *  State of the customers.
   */

   private final int [] customerState;

  /**
   *   Waiting seats occupation.
   */

   private MemFIFO<Integer> sitCustomer;

  /**
   *   Remote reference to the general repository.
   */

   private final GeneralReposInterface reposStub;

  /**
   *   Number of entity groups requesting the shutdown.
   */

   private int nEntities;

  /**
   *  Instantiation of a barber shop object.
   *
   *      @param reposStub reference to the stub of the general repository
   */

   public BarberShop (GeneralReposInterface reposStub)
   {
      nReqCut = 0;
      barb = new Thread [SimulPar.M];
      for (int i = 0; i < SimulPar.M; i++)
        barb[i] = null;
      cust = new Thread [SimulPar.N];
      customerState = new int [SimulPar.N];
      for (int i = 0; i < SimulPar.N; i++)
      { cust[i] = null;
        customerState[i] = -1;
      }
      try
      { sitCustomer = new MemFIFO<> (new Integer [SimulPar.K]);
      }
      catch (MemException e)
      { GenericIO.writelnString ("Instantiation of waiting FIFO failed: " + e.getMessage ());
        sitCustomer = null;
        System.exit (1);
      }
      nEntities = 0;
      this.reposStub = reposStub;
   }

  /**
   *  Operation go cut the hair.
   *
   *  It is called by a customer when he goes to the barber shop to try and cut his hair.
   *
   *     @param customerId identification of the customer
   *     @return true, if he did manage to cut his hair -
   *             false, otherwise
   *             and his state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   @Override
   public synchronized ReturnBoolean goCutHair (int customerId) throws RemoteException
   {
      cust[customerId] = Thread.currentThread ();
      customerState[customerId] = CustomerStates.WANTTOCUTHAIR;
      try
      { reposStub.setCustomerState (customerId, customerState[customerId]);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Customer " + customerId + " remote exception on goCutHair - setCustomerState 1: " + e.getMessage ());
        System.exit (1);
      }
      if (sitCustomer.full ())                             // the customer checks how full is the barber shop
         {                                                 // if it is packed full, he leaves to come back later
           return new ReturnBoolean (false, customerState[customerId]);
         }

      customerState[customerId] = CustomerStates.WAITTURN;
      try
      { reposStub.setCustomerState (customerId, customerState[customerId]);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Customer " + customerId + " remote exception on goCutHair - setCustomerState 2: " + e.getMessage ());
        System.exit (1);
      }
      nReqCut += 1;                                        // the customer requests a hair cut service,

      try
      { sitCustomer.write (customerId);                    // the customer sits down to wait for his turn
      }
      catch (MemException e)
      { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
        System.exit (1);
      }

      notifyAll ();                                        // the customer lets his presence be known

      while (customerState[customerId] != CustomerStates.DAYBYDAYLIFE)
      { /* the customer waits for the service to be executed */
        try
        { wait ();
        }
        catch (InterruptedException e) {}
      }

                                                           // the customer leaves the barber shop after being serviced
      return new ReturnBoolean (true, customerState[customerId]);
   }

  /**
   *  Operation go to sleep.
   *
   *  It is called by a barber while waiting for customers to be serviced.
   *
   *     @param barberId identification of the barber
   *     @return true, if his life cycle has come to an end -
   *             false, otherwise
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   @Override
   public synchronized boolean goToSleep (int barberId) throws RemoteException
   {
      while (nReqCut == 0)                                 // the barber waits for a service request
      { barb[barberId] = Thread.currentThread ();
        try
        { wait ();
        }
        catch (InterruptedException e)
        { barb[barberId] = null;
          return true;                                     // the barber life cycle has come to an end
        }
      }

      if (nReqCut > 0) nReqCut -= 1;                       // the barber takes notice some one has requested his service
      barb[barberId] = null;

      return false;
   }

  /**
   *  Operation call a customer.
   *
   *  It is called by a barber if a customer has requested his service.
   *
   *     @param barberId identification of the barber
   *     @return customer id and the barber state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   @Override
   public synchronized ReturnInt callACustomer (int barberId) throws RemoteException
   {
      int barberState,                                     // state of the barber
          customerId;                                      // customer id

      barberState = BarberStates.INACTIVITY;
      try
      { reposStub.setBarberState (barberId, barberState);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber " + barberId + " remote exception on callACustomer - setBarberState: " + e.getMessage ());
        System.exit (1);
      }

      try
      { customerId = sitCustomer.read ();                            // the barber calls the customer
        if ((customerId < 0) || (customerId >= SimulPar.N))
           throw new MemException ("illegal customer id!");
      }
      catch (MemException e)
      { GenericIO.writelnString ("Retrieval of customer id from waiting FIFO failed: " + e.getMessage ());
        customerId = -1;
        System.exit (1);
      }

      customerState[customerId] = CustomerStates.CUTTHEHAIR;
      try
      { reposStub.setCustomerState (customerId, customerState[customerId]);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Customer " + customerId + " remote exception on callACustomer - setCustomerState: " + e.getMessage ());
        System.exit (1);
      }

      return new ReturnInt (customerId, barberState);
   }

  /**
   *  Operation receive payment.
   *
   *  It is called by a barber after finishing the customer hair cut.
   *
   *     @param barberId identification of the barber
   *     @param customerId identification of the customer
   *     @return state of the barber
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   @Override
   public synchronized int receivePayment (int barberId, int customerId) throws RemoteException
   {
      int barberState;                                     // state of the barber

      barberState = BarberStates.SLEEPING;
      customerState[customerId] = CustomerStates.DAYBYDAYLIFE;
      try
      { reposStub.setBarberCustomerState (barberId, barberState, customerId, customerState[customerId]);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber " + barberId + " - Customer " + customerId +
                                 " remote exception on receivePayment - : setBarberCustomerState" + e.getMessage ());
        System.exit (1);
      }
      notifyAll ();                                        // the customer settles the account
      return barberState;
   }

  /**
   *  Operation end of work.
   *
   *   New operation.
   *
   *      @param barbId barber id
   *      @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                              service fails
   */

   @Override
   public synchronized void endOperation (int barberId) throws RemoteException
   {
      while (nEntities == 0)
      { /* the barber waits for the termination of the customers */
        try
        { wait ();
        }
        catch (InterruptedException e) {}
      }
      if (barb[barberId] != null)
         barb[barberId].interrupt ();
   }


  /**
   *   Operation server shutdown.
   *
   *   New operation.
   *
   *      @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                              service fails
   */

   @Override
   public synchronized void shutdown () throws RemoteException
   {
       nEntities += 1;
       if (nEntities >= SimulPar.E)
          ServerSleepingBarbersBarberShop.shutdown ();
       notifyAll ();                                       // the barber may now terminate
   }
}
