package clientSide.stubs;

import clientSide.entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *  Stub to the general repository.
 *
 *    It instantiates a remote reference to the general repository.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class GeneralReposStub
{
  /**
   *  Name of the platform where is located the general repository server.
   */

   private String serverHostName;

  /**
   *  Port number for listening to service requests.
   */

   private int serverPortNumb;

  /**
   *   Instantiation of a stub to the general repository.
   *
   *     @param serverHostName name of the platform where is located the barber shop server
   *     @param serverPortNumb port number for listening to service requests
   */

   public GeneralReposStub (String serverHostName, int serverPortNumb)
   {
      this.serverHostName = serverHostName;
      this.serverPortNumb = serverPortNumb;
   }

  /**
   *   Operation initialization of the simulation.
   *
   *     @param fileName logging file name
   *     @param nIter number of iterations of the customer life cycle
   */

   public void initSimul (String fileName, int nIter)
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
      outMessage = new Message (MessageType.SETNFIC, fileName, nIter);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.NFICDONE)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }

  /**
   *   Set barber state.
   *
   *     @param id barber id
   *     @param state barber state
   */

   public void setBarberState (int id, int state)
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
      outMessage = new Message (MessageType.STBST, id, state);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.SACK)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }

  /**
   *   Set customer state.
   *
   *     @param id customer id
   *     @param state customer state
   */

   public void setCustomerState (int id, int state)
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
      outMessage = new Message (MessageType.STCST, id, state);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.SACK)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
           GenericIO.writelnString (inMessage.toString ());
           System.exit (1);
         }
      com.close ();
   }

  /**
   *   Set barber and customer state.
   *
   *     @param barberId barber id
   *     @param barberState barber state
   *     @param customerId customer id
   *     @param customerState customer state
   */

   public void setBarberCustomerState (int barberId, int barberState, int customerId, int customerState)
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
      outMessage = new Message (MessageType.STBCST, barberId, barberState, customerId, customerState);
      com.writeObject (outMessage);
      inMessage = (Message) com.readObject ();
      if (inMessage.getMsgType() != MessageType.SACK)
         { GenericIO.writelnString ("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
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
