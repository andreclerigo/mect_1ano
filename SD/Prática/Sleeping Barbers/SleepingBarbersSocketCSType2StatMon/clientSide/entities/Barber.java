package clientSide.entities;

import clientSide.stubs.*;

/**
 *    Barber thread.
 *
 *      It simulates the barber life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public class Barber extends Thread
{
  /**
   *  Barber identification.
   */

   private int barberId;

  /**
   *  Barber state.
   */

   private int barberState;

  /**
   *  Reference to the stub of the barber shop.
   */

   private final BarberShopStub bShopStub;

  /**
   *   Instantiation of a barber thread.
   *
   *     @param name thread name
   *     @param barberId barber id
   *     @param bShop reference to the barber shop
   */

   public Barber (String name, int barberId, BarberShopStub bShopStub)
   {
      super (name);
      this.barberId = barberId;
      barberState = BarberStates.SLEEPING;
      this.bShopStub = bShopStub;
   }

  /**
   *   Set barber id.
   *
   *     @param id barber id
   */

   public void setBarberId (int id)
   {
      barberId = id;
   }

  /**
   *   Get barber id.
   *
   *     @return barber id
   */

   public int getBarberId ()
   {
      return barberId;
   }

  /**
   *   Set barber state.
   *
   *     @param state new barber state
   */

   public void setBarberState (int state)
   {
      barberState = state;
   }

  /**
   *   Get barber state.
   *
   *     @return barber state
   */

   public int getBarberState ()
   {
      return barberState;
   }

  /**
   *   Life cycle of the barber.
   */

   @Override
   public void run ()
   {
      int customerId;                                      // customer id
      boolean endOp;                                       // flag signaling end of operations

      while (true)
      { endOp = bShopStub.goToSleep ();                    // the barber sleeps while waiting for a customer to service
        if (endOp) break;                                  // check for end of operations
        customerId = bShopStub.callACustomer ();           // the barber has waken up and calls next customer
        cutHair ();                                        // the barber cuts the customer hair
        bShopStub.receivePayment (customerId);             // the barber finishes his service and receives payment for it
      }
   }

  /**
   *  Cutting the customer hair.
   *
   *  Internal operation.
   */

   private void cutHair ()
   {
      try
      { sleep ((long) (1 + 100 * Math.random ()));
      }
      catch (InterruptedException e) {}
   }
}
