package clientSide.stubs;

import serverSide.main.*;
import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *  Stub to the barber shop.
 *
 *    It instantiates a remote reference to the barber shop.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class BarberShopStub
{
  /**
   *  Name of the platform where is located the barber shop server.
   */

   private String serverHostName;

  /**
   *  Port number for listening to service requests.
   */

   private int serverPortNumb;

  /**
   *   Instantiation of a stub to the barber shop.
   *
   *     @param serverHostName name of the platform where is located the barber shop server
   *     @param serverPortNumb port number for listening to service requests
   */

   public BarberShopStub (String serverHostName, int serverPortNumb)
   {
      this.serverHostName = serverHostName;
      this.serverPortNumb = serverPortNumb;
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
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())                                           // waits for a connection to be established
      { try
        { Thread.currentThread ().sleep ((long) (10));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.REQCUTH, ((Customer) Thread.currentThread ()).getCustomerId (),
                                ((Customer) Thread.currentThread ()).getCustomerState ());
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if ((inMessage.getMsgType () != MessageType.CUTHDONE) && (inMessage.getMsgType() != MessageType.BSHOPF))
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if (inMessage.getCustId () != ((Customer) Thread.currentThread ()).getCustomerId ())
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid customer id!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if ((inMessage.getCustState () < CustomerStates.DAYBYDAYLIFE) || (inMessage.getCustState () > CustomerStates.CUTTHEHAIR))
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid customer state!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
      ((Customer) Thread.currentThread ()).setCustomerState (inMessage.getCustState ());
      return (inMessage.getMsgType() == MessageType.CUTHDONE);
   }

  /**
   *  Operation go to sleep.
   *
   *  It is called by a barber while waiting for customers to be serviced.
   *
   *    @return true, if his life cycle has come to an end -
   *            false, otherwise
   */

   public boolean goToSleep ()
   {
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())                                           // waits for a connection to be established
      { try
        { Thread.currentThread ().sleep ((long) (10));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.SLEEP, ((Barber) Thread.currentThread ()).getBarberId ());
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType () != MessageType.SLEEPDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if (inMessage.getBarbId () != ((Barber) Thread.currentThread ()).getBarberId ())
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid barber id!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
      return inMessage.getEndOp ();
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
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())                                           // waits for a connection to be established
      { try
        { Thread.currentThread ().sleep ((long) (10));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.CALLCUST, ((Barber) Thread.currentThread ()).getBarberId (),
                                ((Barber) Thread.currentThread ()).getBarberState ());
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType () != MessageType.CCUSTDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if (inMessage.getBarbId () != ((Barber) Thread.currentThread ()).getBarberId ())
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid barber id!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if ((inMessage.getBarbState () < BarberStates.SLEEPING) || (inMessage.getBarbState () > BarberStates.INACTIVITY))
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid barber state!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if ((inMessage.getCustId () < 0) || (inMessage.getCustId () >= SimulPar.N))
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid customer id!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
      ((Barber) Thread.currentThread ()).setBarberState (inMessage.getBarbState ());
      return inMessage.getCustId ();
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
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())                                           // waits for a connection to be established
      { try
        { Thread.currentThread ().sleep ((long) (10));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.RECPAY, ((Barber) Thread.currentThread ()).getBarberId (),
                                ((Barber) Thread.currentThread ()).getBarberState (), customerId);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType () != MessageType.RPAYDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if (inMessage.getBarbId () != ((Barber) Thread.currentThread ()).getBarberId ())
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid barber id!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if ((inMessage.getBarbState () < BarberStates.SLEEPING) || (inMessage.getBarbState () > BarberStates.INACTIVITY))
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid barber state!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      ((Barber) Thread.currentThread ()).setBarberState (inMessage.getBarbState ());
      com.close ();
   }

  /**
   *  Operation end of work.
   *
   *   New operation.
   *
   *      @param barbId barber id
   */

   public void endOperation (int barberId)
   {
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())
      { try
        { Thread.sleep ((long) (1000));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.ENDOP, barberId);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.EOPDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      if (inMessage.getBarbId () != barberId)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid barber id!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }

   /**
   *   Operation server shutdown.
   *
   *   New operation.
   */

   public void shutdown ()
   {
      ClientCom com;                                                 // communication channel
      Message outMessage,                                            // outgoing message
              inMessage;                                             // incoming message

      com = new ClientCom (serverHostName, serverPortNumb);
      while (!com.open ())
      { try
        { Thread.sleep ((long) (1000));
        }
        catch (InterruptedException e) {}
      }
      outMessage = new Message (MessageType.SHUT);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.SHUTDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }
}
