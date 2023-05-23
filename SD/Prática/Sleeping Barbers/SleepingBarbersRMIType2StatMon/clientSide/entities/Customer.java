package clientSide.entities;

import java.rmi.*;
import interfaces.*;
import genclass.GenericIO;

/**
 *    Customer thread.
 *
 *      It simulates the customer life cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on remote calls under Java RMI.
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
   *  Remote reference to the barber shop.
   */

   private final BarberShopInterface bShopStub;

  /**
   *  Number of iterations of the customer life cycle.
   */

   private int nIter;

  /**
   *   Instantiation of a customer thread.
   *
   *     @param name thread name
   *     @param customerId customer id
   *     @param bShopStub remote reference to the barber shop
   *     @param nIter number of iterations of the customer life cycle
   */

   public Customer (String name, int customerId, BarberShopInterface bShopStub, int nIter)
   {
      super (name);
      this.customerId = customerId;
      customerState = CustomerStates.DAYBYDAYLIFE;
      this.bShopStub = bShopStub;
      this.nIter = nIter;
   }

  /**
   *   Life cycle of the customer.
   */

   @Override
   public void run ()
   {
      for (int i = 0; i < nIter; i++)
      { carryingOutNormalDuties ();                        // the customer carries out normal duties
        while (!goCutHair ())                              // the customer checks if he can cut his hair
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
   *  Customer tries to cut his hair.
   *
   *  Remote operation.
   *
   *     @return true, if he did manage to cut his hair -
   *             false, otherwise
   */

   private boolean goCutHair ()
   {
      ReturnBoolean ret = null;                            // return value

      try
      { ret = bShopStub.goCutHair (customerId);
      }
      catch (RemoteException e)
      { GenericIO.writelnString ("Customer " + customerId + " remote exception on goCutHair: " + e.getMessage ());
        System.exit (1);
      }
      customerState = ret.getIntStateVal ();
      return ret.getBooleanVal ();
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
