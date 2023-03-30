package clientSide;

import genclass.GenericIO;

/**
 *   Reference to a remote object.
 *
 *   It provides means to the setup of a communication channel and the message exchange.
 */

public class KnockKnockStub
{
  /**
   *  Name of the computational system where the server is located.
   */

   private String serverHostName;

  /**
   *  Number of the listening port at the computational system where the server is located.
   */

   private int serverPortNumb;

  /**
   *  Instantiation of a remote reference
   *
   *    @param hostName name of the computational system where the server is located
   *    @param portNumb number of the listening port at the computational system where the server is located
  */

   public KnockKnockStub (String hostName, int port)
   {
      serverHostName = hostName;
      serverPortNumb = port;
   }

  /**
   *  Message exchange with the remote object.
   */

   public void exchange ()
   {
     ClientCom com = new ClientCom (serverHostName, serverPortNumb);           // communication channel
     String fromServer,                                                        // input sentence
            fromUser;                                                          // output sentence

     while (!com.open ())                                                      // open the connection
     { try
       { Thread.currentThread ().sleep ((long) (10));
       }
       catch (InterruptedException e) {}
     }
     while ((fromServer = (String) com.readObject ()) != null)                 // check receiving message
     { GenericIO.writelnString ("Server: " + fromServer);                      // print receiving message
       if (fromServer.equals ("Bye.")) break;                                  // check for continuation
       GenericIO.writeString ("Client: ");                                     // read user reply
       do
       { fromUser = GenericIO.readlnString ();
       } while (fromUser == null);
       com.writeObject (fromUser);                                             // send reply
     }
     com.close ();                                                             // close the connection
   }
}
