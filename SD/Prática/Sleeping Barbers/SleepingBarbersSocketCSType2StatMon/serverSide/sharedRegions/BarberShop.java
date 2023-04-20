package serverSide.sharedRegions;

import serverSide.main.*;
import serverSide.entities.*;
import clientSide.entities.*;
import clientSide.stubs.*;
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
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class BarberShop
{
  /**
   *  Number of hair cuts which have been requested and not serviced yet.
   */

   private int nReqCut;

  /**
   *  Reference to barber threads.
   */

   private final BarberShopClientProxy [] barb;

  /**
   *  Reference to customer threads.
   */

   private final BarberShopClientProxy [] cust;

  /**
   *   Waiting seats occupation.
   */

   private MemFIFO<Integer> sitCustomer;

  /**
   *   Reference to the stub of the general repository.
   */

   private final GeneralReposStub reposStub;

  /**
   *   Number of entity groups requesting the shutdown.
   */

   private int nEntities;

  /**
   *  Instantiation of a barber shop object.
   *
   *      @param reposStub reference to the stub of the general repository
   */

   public BarberShop (GeneralReposStub reposStub)
   {
      nReqCut = 0;
      barb = new BarberShopClientProxy [SimulPar.M];
      for (int i = 0; i < SimulPar.M; i++)
        barb[i] = null;
      cust = new BarberShopClientProxy [SimulPar.N];
      for (int i = 0; i < SimulPar.N; i++)
        cust[i] = null;
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
   *    @return true, if he did manage to cut his hair -
   *            false, otherwise
   */

   public synchronized boolean goCutHair ()
   {
      int customerId;                                      // customer id

      customerId = ((BarberShopClientProxy) Thread.currentThread ()).getCustomerId ();
      cust[customerId] = (BarberShopClientProxy) Thread.currentThread ();
      cust[customerId].setCustomerState (CustomerStates.WANTTOCUTHAIR);
      reposStub.setCustomerState (customerId, cust[customerId].getCustomerState ());

      if (sitCustomer.full ())                             // the customer checks how full is the barber shop
         return (false);                                   // if it is packed full, he leaves to come back later

      cust[customerId].setCustomerState (CustomerStates.WAITTURN);
      reposStub.setCustomerState (customerId, cust[customerId].getCustomerState ());
      nReqCut += 1;                                        // the customer requests a hair cut service,

      try
      { sitCustomer.write (customerId);                    // the customer sits down to wait for his turn
      }
      catch (MemException e)
      { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
          System.exit (1);
      }

      notifyAll ();                                        // the customer lets his presence be known

      while (((BarberShopClientProxy) Thread.currentThread ()).getCustomerState () != CustomerStates.DAYBYDAYLIFE)
      { /* the customer waits for the service to be executed */
        try
        { wait ();
        }
        catch (InterruptedException e) {}
      }

      return (true);                                       // the customer leaves the barber shop after being serviced
   }

  /**
   *  Operation go to sleep.
   *
   *  It is called by a barber while waiting for customers to be serviced.
   *
   *    @return true, if his life cycle has come to an end -
   *            false, otherwise
   */

   public synchronized boolean goToSleep ()
   {
      int barberId;                                        // barber id

      barberId = ((BarberShopClientProxy) Thread.currentThread ()).getBarberId ();
      while (nReqCut == 0)                                 // the barber waits for a service request
      { barb[barberId] = (BarberShopClientProxy) Thread.currentThread ();
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
   *    @return customer id
   */

   public synchronized int callACustomer ()
   {
      int barberId,                                        // barber id
          customerId;                                      // customer id

      barberId = ((BarberShopClientProxy) Thread.currentThread ()).getBarberId ();
      ((BarberShopClientProxy) Thread.currentThread ()).setBarberState (BarberStates.INACTIVITY);
      reposStub.setBarberState (barberId, ((BarberShopClientProxy) Thread.currentThread ()).getBarberState ());

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

      cust[customerId].setCustomerState (CustomerStates.CUTTHEHAIR);
      reposStub.setCustomerState (customerId, cust[customerId].getCustomerState ());

      return (customerId);
   }

  /**
   *  Operation receive payment.
   *
   *  It is called by a barber after finishing the customer hair cut.
   *
   *    @param customerId customer id
   */

   public synchronized void receivePayment (int customerId)
   {
      int barberId;                                        // barber id

      barberId = ((BarberShopClientProxy) Thread.currentThread ()).getBarberId ();
      ((BarberShopClientProxy) Thread.currentThread ()).setBarberState (BarberStates.SLEEPING);
      cust[customerId].setCustomerState (CustomerStates.DAYBYDAYLIFE);
      reposStub.setBarberCustomerState (barberId, ((BarberShopClientProxy) Thread.currentThread ()).getBarberState (),
                                        customerId, cust[customerId].getCustomerState ());

      notifyAll ();                                        // the customer settles the account
   }

  /**
   *  Operation end of work.
   *
   *   New operation.
   *
   *      @param barbId barber id
   */

   public synchronized void endOperation (int barberId)
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
   */

   public synchronized void shutdown ()
   {
       nEntities += 1;
       if (nEntities >= SimulPar.E)
          ServerSleepingBarbersBarberShop.waitConnection = false;
       notifyAll ();                                        // the barber may now terminate
   }
}
