package serverSide;

import genclass.GenericIO;
import commInfra.*;
import java.net.*;
import java.io.*;

/**
 *    Server side of the Riddles Play.
 *
 *    Imperfect implementation of the request serialization variant.
 *    It waits for the start of the message exchange.
 */

public class ServerKnockKnock
{
  /**
   *    Main method.
   *
   *    @param args runtime arguments
   */

    public static void main (String[] args)
    {
      /* service is established */

       DatagramSocket commSocket = null;                             // communication socket
       int portNumb = 4000;                                          // port nunber for receiving service request
       KnockKnockProtocol kkp = null;                                // provided service

       try
       { commSocket = new DatagramSocket (portNumb);                 // connection is instantiated
       }
       catch (SocketException e)                                     // fatal error
       { GenericIO.writelnString ("It was not possible to connect to the port: " + portNumb + "!");
         e.printStackTrace ();
         System.exit (1);
       }
       kkp = new KnockKnockProtocol();                               // service is instantiated
       GenericIO.writelnString ("Service is established!");
       GenericIO.writelnString ("Server is listening for service requests.");

      /* service request processing */

       while (true)
       {
         byte [] buff = new byte [100];                                          // reception buffer
         DatagramPacket inputDatagram = new DatagramPacket (buff, buff.length),  // received datagram
                        outputDatagram = null;                                   // sent datagram

         try
         { commSocket.receive (inputDatagram);                       // wait for a client request
         }
         catch (IOException e)                                       // fatal error
         { GenericIO.writelnString ("The message could not be received!");
           e.printStackTrace ();
           System.exit (1);
         }

         KnockKnockMessage msg;                                      // exchanged message
         String inputLine,                                           // input sentence
                outputLine;                                          // output sentence
         boolean endOfCommunication = false;                         // flag signaling end of service

         /* service rendering */

         do
         { try
           { msg = new KnockKnockMessage (inputDatagram.getData ());           // extraction of the client message from the received datagram
             inputLine = msg.getString ();                                     // convert it to a sentence
             outputLine = kkp.processInput (inputLine);                        // generation of the reply
             msg = new KnockKnockMessage (outputLine);                         // instantiation of the reply message
             outputDatagram = new DatagramPacket (msg.getByteArray (), msg.getByteArray ().length,           // instantiation of the sent datagram
                                                  inputDatagram.getAddress (), inputDatagram.getPort ());
             commSocket.send (outputDatagram);                                 // send reply to the client
             commSocket.receive (inputDatagram);                               // wait for the next request from the client
           }
           catch (EndOfTransactionException e)                                 // the service has come to an end
           { outputLine = (String) e.getLastAnswer ();                         // get last message
             msg = new KnockKnockMessage (outputLine);                         // generation of the reply
             outputDatagram = new DatagramPacket (msg.getByteArray (), msg.getByteArray ().length,
                                                  inputDatagram.getAddress (), inputDatagram.getPort ());
             try
             { commSocket.send (outputDatagram);                               // send reply to the client
             }
             catch (IOException ell)                                           // fatal error
             { GenericIO.writelnString ("Last message could not be sent!");
               ell.printStackTrace ();
               System.exit (1);
             }
             endOfCommunication = true;                                        // signaling end of service
           }
           catch (IOException e)                                               // fatal error
           { GenericIO.writelnString ("A message could not be sent / received!");
             e.printStackTrace ();
             System.exit (1);
           }
         } while (!endOfCommunication);
       }
    }
}
