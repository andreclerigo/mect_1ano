package entities;

import sharedRegions.BarberShop;

/**
 *   Barber thread.
 *
 *   It simulates the barber life cycle.
 *   Static solution.
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
   *  Reference to the barber shop.
   */

   private final BarberShop bShop;

  /**
   *   Instantiation of a barber thread.
   *
   *     @param name thread name
   *     @param barberId barber id
   *     @param bShop reference to the barber shop
   */

   public Barber (String name, int barberId, BarberShop bShop)
   {
      super (name);
      this.barberId = barberId;
      barberState = BarberStates.SLEEPING;
      this.bShop = bShop;
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
      { endOp = bShop.goToSleep ();                        // the barber sleeps while waiting for a customer to service
        if (endOp) break;                                  // check for end of operations
        customerId = bShop.callACustomer ();               // the barber has waken up and calls next customer
        cutHair ();                                        // the barber cuts the customer hair
        bShop.receivePayment (customerId);                 // the barber finishes his service and receives payment for it
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
