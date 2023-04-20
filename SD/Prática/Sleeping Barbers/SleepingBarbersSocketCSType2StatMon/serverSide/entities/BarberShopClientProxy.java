package serverSide.entities;

import serverSide.sharedRegions.*;
import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *  Service provider agent for access to the Barber Shop.
 *
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class BarberShopClientProxy extends Thread implements BarberCloning, CustomerCloning
{
  /**
   *  Number of instantiayed threads.
   */

   private static int nProxy = 0;

  /**
   *  Communication channel.
   */

   private ServerCom sconi;

  /**
   *  Interface to the Barber Shop.
   */

   private BarberShopInterface bShopInter;

  /**
   *  Barber identification.
   */

   private int barberId;

  /**
   *  Barber state.
   */

   private int barberState;

  /**
   *  Customer identification.
   */

   private int customerId;

  /**
   *  Customer state.
   */

   private int customerState;

  /**
   *  Instantiation of a client proxy.
   *
   *     @param sconi communication channel
   *     @param bShopInter interface to the barber shop
   */

   public BarberShopClientProxy (ServerCom sconi, BarberShopInterface bShopInter)
   {
      super ("BarberShopProxy_" + BarberShopClientProxy.getProxyId ());
      this.sconi = sconi;
      this.bShopInter = bShopInter;
   }

  /**
   *  Generation of the instantiation identifier.
   *
   *     @return instantiation identifier
   */

   private static int getProxyId ()
   {
      Class<?> cl = null;                                            // representation of the BarberShopClientProxy object in JVM
      int proxyId;                                                   // instantiation identifier

      try
      { cl = Class.forName ("serverSide.entities.BarberShopClientProxy");
      }
      catch (ClassNotFoundException e)
      { GenericIO.writelnString ("Data type BarberShopClientProxy was not found!");
        e.printStackTrace ();
        System.exit (1);
      }
      synchronized (cl)
      { proxyId = nProxy;
        nProxy += 1;
      }
      return proxyId;
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
   *  Life cycle of the service provider agent.
   */

   @Override
   public void run ()
   {
      Message inMessage = null,                                      // service request
              outMessage = null;                                     // service reply

     /* service providing */

      inMessage = (Message) sconi.readObject ();                     // get service request
      try
      { outMessage = bShopInter.processAndReply (inMessage);         // process it
      }
      catch (MessageException e)
      { GenericIO.writelnString ("Thread " + getName () + ": " + e.getMessage () + "!");
        GenericIO.writelnString (e.getMessageVal ().toString ());
        System.exit (1);
      }
      sconi.writeObject (outMessage);                                // send service reply
      sconi.close ();                                                // close the communication channel
   }
}
