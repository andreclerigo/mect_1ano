package clientSide.entities;

import clientSide.stubs.*;

/**
 *    Customer thread.
 *
 *      It simulates the customer life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public class Customer extends Thread
{
  /**
   *  Customer identification.
   */

   private int customerId;

  /**
   *  Customer state.
   */

   private int customerState;

  /**
   *  Reference to the stub of the barber shop.
   */

   private final BarberShopStub bShopStub;

  /**
   *  Number of iterations of the customer life cycle.
   */

   private int nIter;

  /**
   *   Instantiation of a customer thread.
   *
   *     @param name thread name
   *     @param customerId customer id
   *     @param bShopStub reference to the stub of the barber shop
   *     @param nIter number of iterations of the customer life cycle
   */

   public Customer (String name, int customerId, BarberShopStub bShopStub, int nIter)
   {
      super (name);
      this.customerId = customerId;
      customerState = CustomerStates.DAYBYDAYLIFE;
      this.bShopStub = bShopStub;
      this.nIter = nIter;
   }

  /**
   *   Set customer id.
   *
   *     @param id customer id
   */

   public void setCustomerId (int id)
   {
      customerId = id;
   }

  /**
   *   Get customer id.
   *
   *     @return customer id
   */

   public int getCustomerId ()
   {
      return customerId;
   }

  /**
   *   Set customer state.
   *
   *     @param state new customer state
   */

   public void setCustomerState (int state)
   {
      customerState = state;
   }

  /**
   *   Get customer state.
   *
   *     @return customer state
   */

   public int getCustomerState ()
   {
      return customerState;
   }

  /**
   *   Life cycle of the customer.
   */

   @Override
   public void run ()
   {
      for (int i = 0; i < nIter; i++)
      { carryingOutNormalDuties ();                        // the customer carries out normal duties
        while (!bShopStub.goCutHair ())                    // the customer checks if he can cut his hair
          haveACupOfCoffee ();                             // if the barber shop is full, he tries later
      }
   }

  /**
   *  Living normal life.
   *
   *  Internal operation.
   */

   private void carryingOutNormalDuties ()
   {
      try
      { sleep ((long) (1 + 40 * Math.random ()));
      }
      catch (InterruptedException e) {}
   }

  /**
   *  Have a cup of coffee.
   *
   *  Internal operation.
   */

   private void haveACupOfCoffee ()
   {
      try
      { sleep ((long) (1 + 10 * Math.random ()));
      }
      catch (InterruptedException e) {}
   }
}
