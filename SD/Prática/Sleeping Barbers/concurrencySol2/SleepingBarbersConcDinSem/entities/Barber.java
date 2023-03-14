package entities;

import sharedRegions.BarberShop;

/**
 *   Barber thread.
 *
 *   It simulates the barber life cycle.
 *   Dynamic solution.
 */

public class Barber extends Thread
{
  /**
   *  Barber identification.
   */

   private int barberId;

  /**
   *  Customer identification.
   */

   private int customerId;

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
   *     @param customerId customer id
   *     @param bShop reference to the barber shop
   */

   public Barber (String name, int barberId, int customerId, BarberShop bShop)
   {
      super (name);
      this.barberId = barberId;
      this.customerId = customerId;
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
      bShop. greetCustomer (customerId);                             // upon waking up, the barber greets the customer
      do
      { cutHair ();                                                  // the barber cuts the customer hair
        bShop.receivePayment (customerId);                           // the barber finishes his service and receives payment for it
      } while ((customerId = bShop.callNextCustomer ()) != -1);      // the barber checks the waiting chairs, if there are a
                                                                     // customer waiting, he calls him
      bShop.goToSleep ();                                            // the barber goes to sleep
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
