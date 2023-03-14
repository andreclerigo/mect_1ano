package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Barber shop.
 *
 *    It is responsible to keep a continuously updated account of the customers inside the barber shop
 *    and is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There is one internal synchronization point: one per each customer, where he both waits his turn to cut the hair
 *    or sits on the cutting chair while having his hair cut.
 */

public class BarberShop
{
  /**
   *  Number of active barbers.
   */

   private int nOcCutChair;

  /**
   *  State of occupation of the cutting chairs.
   */

   private final boolean [] stateCutChair;

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
   *  Barber shop instantiation.
   *
   *    @param repos reference to the general repository
   */

   public BarberShop (GeneralRepos repos)
   {
      nOcCutChair = 0;
      stateCutChair = new boolean [SimulPar.M];
      for (int i = 0; i < SimulPar.M; i++)
        stateCutChair[i] = false;
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
      int barberId,                                        // barber id
          customerId;                                      // customer id

      customerId = ((Customer) Thread.currentThread ()).getCustomerId ();
      cust[customerId] = (Customer) Thread.currentThread ();
      cust[customerId].setCustomerState (CustomerStates.WANTTOCUTHAIR);
      repos.setCustomerState (customerId, cust[customerId].getCustomerState ());

      if (sitCustomer.full ())                             // the customer checks how full is the barber shop
         return (false);                                   // if it is packed full, he leaves to come back later

      if (nOcCutChair < SimulPar.M)                                  // the customer checks whether there are free cutting chairs
         { barberId = allocCuttingChair ();                          // yes, there is, he sits in one and
           new Barber ("Barb_" + (barberId+1), barberId, customerId, this).start ();               // wakes up a barber
         }
         else { cust[customerId].setCustomerState (CustomerStates.WAITTURN);
                repos.setCustomerState (customerId, cust[customerId].getCustomerState ());
                try
                { sitCustomer.write (customerId);                    // the customer sits down to wait for his turn
                }
                catch (MemException e)
                { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
                  System.exit (1);
                }
              }

      while (((Customer) Thread.currentThread ()).getCustomerState () != CustomerStates.DAYBYDAYLIFE)
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
   *  It is called by a barber when there are no more customers to be serviced.
   */

   public synchronized void goToSleep ()
   {
      ((Barber) Thread.currentThread ()).setBarberState (BarberStates.SLEEPING);
      repos.setBarberState (((Barber) Thread.currentThread ()).getBarberId (),
                            ((Barber) Thread.currentThread ()).getBarberState ());
   }

  /**
   *  Operation greet a customer.
   *
   *  It is called by a barber if a customer has requested his service.
   *
   *    @param customerId customer id
   */

   public synchronized void greetCustomer (int customerId)
   {
      int barberId;                                        // barber id

      barberId = ((Barber) Thread.currentThread ()).getBarberId ();
      ((Barber) Thread.currentThread ()).setBarberState (BarberStates.INACTIVITY);
      cust[customerId].setCustomerState (CustomerStates.CUTTHEHAIR);
      repos.setBarberCustomerState (barberId, ((Barber) Thread.currentThread ()).getBarberState (),
                                    customerId, cust[customerId].getCustomerState ());
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

      cust[customerId].setCustomerState (CustomerStates.DAYBYDAYLIFE);
      repos.setCustomerState (customerId, cust[customerId].getCustomerState ());

      notifyAll ();                                        // the customer settles the account
   }

  /**
   *  Operation call next customer.
   *
   *    @return customer id, if there is a customer waiting to have his hair cut -
   *            -1, otherwise
   */

   public synchronized int callNextCustomer ()
   {
      int barberId,                                        // barber id
          customerId = -1;                                 // customer id (by default, there is no customer waiting)

      if (sitCustomer.empty ())                            // check if there is a customer waiting to have his hair cut
         { // nobody is waiting
           barberId = ((Barber) Thread.currentThread ()).getBarberId ();
           freeCuttingChair (barberId);
         }
         else { // there is at least one customer waiting
                try
                { customerId = sitCustomer.read ();                            // the barber calls the customer
                  if ((customerId < 0) || (customerId >= SimulPar.N))
                     throw new MemException ("illegal customer id!");
                }
                catch (MemException e)
                { GenericIO.writelnString ("Retrieval of customer id from waiting FIFO failed: " + e.getMessage ());
                  System.exit (1);
                }
                cust[customerId].setCustomerState (CustomerStates.CUTTHEHAIR);
                repos.setCustomerState (customerId, cust[customerId].getCustomerState ());
              }

      return (customerId);
   }

  /**
   *  Allocation of a cutting chair.
   *
   *  Internal operation.
   *
   *    @return cutting chair id
   */

   private int allocCuttingChair ()
   {
      int chairId = 0;                                     // cutting chair id

      do
      { if (!stateCutChair[chairId])
           { stateCutChair[chairId] = true;
             nOcCutChair += 1;
             break;
           }
        chairId += 1;
      } while (chairId < SimulPar.M);

      return (chairId);
   }

  /**
   *  Freeing of a cutting chair.
   *
   *    @param chairId cutting chair id
   */

   private void freeCuttingChair (int chairId)
   {
      nOcCutChair -= 1;
      stateCutChair[chairId] = false;
   }
}
