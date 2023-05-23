package clientSide.entities;

import java.rmi.*;
import interfaces.*;
import genclass.GenericIO;

/**
 *    Barber thread.
 *
 *      It simulates the barber life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on remote calls under Java RMI.
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
   *  Remote reference to the barber shop.
   */

   private final BarberShopInterface bShopStub;

  /**
   *   Instantiation of a barber thread.
   *
   *     @param name thread name
   *     @param barberId barber id
   *     @param bShop remote reference to the barber shop
   */

   public Barber (String name, int barberId, BarberShopInterface bShopStub)
   {
      super (name);
      this.barberId = barberId;
      barberState = BarberStates.SLEEPING;
      this.bShopStub = bShopStub;
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
      { endOp = goToSleep ();                              // the barber sleeps while waiting for a customer to service
        if (endOp) break;                                  // check for end of operations
        customerId = callACustomer ();                     // the barber has waken up and calls next customer
        cutHair ();                                        // the barber cuts the customer hair
        receivePayment (customerId);                       // the barber finishes his service and receives payment for it
      }
   }

  /**
   *  Barber goes to sleep.
   *
   *  Remote operation.
   *
   *     @return true, if his life cycle has come to an end -
   *             false, otherwise
   */

   private boolean goToSleep ()
   {
      boolean ret = false;                                 // return value

      try
      { ret = bShopStub.goToSleep (barberId);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber " + barberId + " remote exception on goToSleep: " + e.getMessage ());
        System.exit (1);
      }
      return ret;
   }

  /**
   *  Barber calls a customer.
   *
   *  Remote operation.
   *
   *     @return identification of a customer
   */

   private int callACustomer ()
   {
      ReturnInt ret = null;                                // return value

      try
      { ret = bShopStub.callACustomer (barberId);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber " + barberId + " remote exception on callACustomer: " + e.getMessage ());
        System.exit (1);
      }
      barberState = ret.getIntStateVal ();
      return ret.getIntVal ();
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

  /**
   *  Barber receives payment.
   *
   *  Remote operation.
   *
   *     @param customerId identification of a customer
   */

   private void receivePayment (int customerId)
   {
      try
      { barberState = bShopStub.receivePayment (barberId, customerId);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Barber " + barberId + " remote exception on receivePayment: " + e.getMessage ());
        System.exit (1);
      }
   }
}
