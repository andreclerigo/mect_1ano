package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Barber shop.
 *
 *    It is responsible to keep a continuously updated account of the customers inside the barber shop
 *    and is implemented using semaphores for synchronization.
 *    All public methods are executed in mutual exclusion.
 *    There are two internal synchronization points: a single blocking point for the barbers, where they wait for a customer;
 *    and an array of blocking points, one per each customer, where he both waits his turn to cut the hair and sits on the
 *    cutting chair while having his hair cut.
 */

public class BarberShop
{
  /**
   *  Reference to customer threads.
   */

   private final Customer [] cust;

  /**
   *   Waiting seats occupation.
   */

   private MemFIFO<Integer> sitCustomer;

  /**
   *   Reference to the general repository.
   */

   private final GeneralRepos repos;

  /**
   *  Semaphore to ensure mutual exclusion on the execution of public methods.
   */

   private final Semaphore access;

  /**
   *  Blocking semaphore for the barber threads.
   */

   private final Semaphore sleep;

  /**
   *  Array of blocking semaphores for the customer threads.
   */

   private final Semaphore [] waitService;

  /**
   *  Total number of iterations of the life cycle of the customers.
   */

   private int nGlobIter;

  /**
   *  Number of barber threads currently active.
   */

   private int nActBarber;

  /**
   *  Barber shop instantiation.
   *
   *    @param nIter number of iterations of the customer life cycle
   *    @param repos reference to the general repository
   */

   public BarberShop (int nIter, GeneralRepos repos)
   {
      cust = new Customer [SimulPar.N];
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
      this.repos = repos;
      access = new Semaphore ();
      access.up ();
      sleep = new Semaphore ();
      waitService = new Semaphore [SimulPar.N];
      for (int i = 0; i < SimulPar.N; i++)
        waitService[i] = new Semaphore ();
      nGlobIter = nIter * SimulPar.N;
      nActBarber = SimulPar.M;
   }

  /**
   *  Operation go cut the hair.
   *
   *  It is called by a customer when he goes to the barber shop to try and cut his hair.
   *
   *    @return true, if he did manage to cut his hair -
   *            false, otherwise
   */

   public boolean goCutHair ()
   {
      int customerId;                                      // customer id

      access.down ();                                      // enter critical region
      customerId = ((Customer) Thread.currentThread ()).getCustomerId ();
      cust[customerId] = (Customer) Thread.currentThread ();
      cust[customerId].setCustomerState (CustomerStates.WANTTOCUTHAIR);
      repos.setCustomerState (customerId, cust[customerId].getCustomerState ());

      if (sitCustomer.full ())                             // the customer checks how full is the barber shop
         { access.up ();                                   // exit critical region
           return (false);                                 // if it is packed full, he leaves to come back later
         }

      cust[customerId].setCustomerState (CustomerStates.WAITTURN);
      repos.setCustomerState (customerId, cust[customerId].getCustomerState ());

      try
      { sitCustomer.write (customerId);                    // the customer sits down to wait for his turn
      }
      catch (MemException e)
      { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
        access.up ();                                      // exit critical region
        System.exit (1);
      }

      sleep.up ();                                         // the customer requests a hair cut service and lets his presence be known
      access.up ();                                        // exit critical region

      waitService[customerId].down ();                     // the customer waits for the service to be executed

      return (true);                                       // the customer leaves the barber shop after being serviced
   }

  /**
   *  Operation go to sleep.
   *
   *  It is called by a barber while waiting for customers to be serviced.
   */

   public void goToSleep ()
   {
     sleep.down ();                                        // the barber waits for a service request
   }

  /**
   *  Operation call a customer.
   *
   *  It is called by a barber if a customer has requested his service.
   *
   *    @return customer id
   */

   public int callACustomer ()
   {
      int barberId,                                                  // barber id
          customerId;                                                // customer id

      access.down ();                                                // enter critical region
      barberId = ((Barber) Thread.currentThread ()).getBarberId ();
      ((Barber) Thread.currentThread ()).setBarberState (BarberStates.INACTIVITY);
      repos.setBarberState (barberId, ((Barber) Thread.currentThread ()).getBarberState ());

      try
      { customerId = sitCustomer.read ();                            // the barber calls the customer
        if ((customerId < 0) || (customerId >= SimulPar.N))
           throw new MemException ("illegal customer id!");
      }
      catch (MemException e)
      { GenericIO.writelnString ("Retrieval of customer id from waiting FIFO failed: " + e.getMessage ());
        customerId = -1;
        access.up ();                                                // exit critical region
        System.exit (1);
      }

      cust[customerId].setCustomerState (CustomerStates.CUTTHEHAIR);
      repos.setCustomerState (customerId, cust[customerId].getCustomerState ());
      nGlobIter -= 1;                                                // the barber decrements the number of services still to be performed
      access.up ();                                                  // exit critical region

      return (customerId);
   }

  /**
   *  Operation receive payment.
   *
   *  It is called by a barber after finishing the customer hair cut.
   *
   *    @param customerId customer id
   */

   public void receivePayment (int customerId)
   {
      int barberId;                                        // barber id

      access.down ();                                      // enter critical region
      barberId = ((Barber) Thread.currentThread ()).getBarberId ();
      ((Barber) Thread.currentThread ()).setBarberState (BarberStates.SLEEPING);
      cust[customerId].setCustomerState (CustomerStates.DAYBYDAYLIFE);
      repos.setBarberCustomerState (barberId, ((Barber) Thread.currentThread ()).getBarberState (),
                                    customerId, cust[customerId].getCustomerState ());

      waitService[customerId].up ();                       // the customer settles the account
      access.up ();                                        // exit critical region
   }

  /**
   *  Operation of testing the continuation of operations.
   *
   *  It is called by a barber to check if he must continue active.
   *
   *    @return true, if his duties are still required -
   *            false, otherwise
   */

   public boolean goOn ()
   {
      boolean end;                                         // flag signaling end of operationss

      access.down ();                                      // enter critical region
      end = (nGlobIter < nActBarber);                      // the barber checks whether there are more companions
                                                           //   than the number of services still to be performed
      if (end) nActBarber -= 1;                            // he signals his duties has come to an end
      access.up ();                                        // exit critical region

      return (!end);
   }
}
