package clientSide;

import genclass.GenericIO;
import commInfra.*;
import java.io.*;
import java.net.*;

/**
 *    Client side of Riddles Play.
 *
 *    Imperfect implementation of the request serialization variant.
 *    It requests the start of the message exchange.
 */

public class ClientKnockKnock
{
  /**
   *    Main method.
   *
   *    @param args runtime arguments
   */

    public static void main (String[] args)
    {

      /* connection is established */

       DatagramSocket kkSocket = null;                     // communication socket
       String hostName;                                    // name of the computational system where the server is located
       int portNumb = 4000;                                // port nunber for sending service request

       GenericIO.writeString ("Name of the computational system where the server is located? ");
       hostName = GenericIO.readlnString ();
       try
       { kkSocket = new DatagramSocket ();                 // local connection is instantiated
       }
       catch (SocketException e)                           // fatal error
       { GenericIO.writelnString ("It was not possible to connect to a local port!");
         e.printStackTrace ();
         System.exit (1);
       }

      /* message exhange takes place */

       byte [] buff = new byte [100];                                               // reception buffer
       DatagramPacket inputDatagram = new DatagramPacket (buff, buff.length),       // receiving datagram
                      outputDatagram = null;                                        // sending datagram
       KnockKnockMessage msg;                                                       // exchanged message
       String fromServer,                                                           // input sentence
              fromUser = "";                                                        // output sentence

       while (true)
         try
         { msg = new KnockKnockMessage (fromUser);                                  // instantiation of the request message
           outputDatagram = new DatagramPacket (msg.getByteArray (), msg.getByteArray ().length,
                                                InetAddress.getByName (hostName), portNumb);
           kkSocket.send (outputDatagram);                                          // request of service is sent
           kkSocket.receive (inputDatagram);                                        // wait for the reply response
           msg = new KnockKnockMessage (inputDatagram.getData ());                  // instantiation of the reply message
           fromServer = msg.getString ();                                           // get the reply sentence
           GenericIO.writelnString ("Server: " + fromServer);                       // print the reply sentence
           if (fromServer.equals ("Bye.")) break;                                   // check for continuation
           GenericIO.writeString ("Client: ");                                      // read user reply
           do
           { fromUser = GenericIO.readlnString ();
           } while (fromUser == null);
         }
         catch (UnknownHostException e)                                             // fatal error
         { GenericIO.writelnString ("Unknown host!");
           e.printStackTrace ();
           System.exit (1);
         }
         catch (IOException e)                                                      // fatal error
         { GenericIO.writelnString ("A message could not be sent / received!");
           e.printStackTrace ();
           System.exit (1);
         }
    }
}
