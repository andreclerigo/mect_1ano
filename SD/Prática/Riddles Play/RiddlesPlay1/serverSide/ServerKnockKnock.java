package serverSide;

import genclass.GenericIO;

/**
 *    Server side of the Riddles Play.
 *
 *    Request serialization variant.
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

       ServerCom scon, sconi;                                        // communication channels
       int portNumb = 4000;                                          // port nunber for listening to service requests
       KnockKnockProtocol kkp;                                       // provided service

       scon = new ServerCom (portNumb);                              // listening channel at the public port is established
       scon.start ();
       kkp = new KnockKnockProtocol();                               // service is instantiated
       GenericIO.writelnString ("Service is established!");
       GenericIO.writelnString ("Server is listening for service requests.");

      /* service request processing */

       KnockKnockThread kkt;                                         // service provider agent
       while (true)
       { sconi = scon.accept ();                                     // enter listening procedure
         kkt = new KnockKnockThread (sconi, kkp);                    // start a service provider agent to address
         kkt.start ();                                               //   the request of service
         try
         { kkt.join ();                                              // wait for service provider agent to terminate
         }
         catch (InterruptedException e) {}
       }
    }
}
