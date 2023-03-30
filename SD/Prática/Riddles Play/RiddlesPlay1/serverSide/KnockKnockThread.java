package serverSide;

import genclass.GenericIO;

/**
 *    Service provider agent.
 */

public class KnockKnockThread extends Thread
{
   /**
    *  Communication channel.
    */

    private ServerCom com;

   /**
    *  Service to be provided.
    */

    private KnockKnockProtocol kkp;

   /**
    *  Instantiation.
    *
    *     @param com communication channel
    *     @param kkp reference to provided service
    */

    public KnockKnockThread (ServerCom com, KnockKnockProtocol kkp)
    {
       this.com = com;
       this.kkp = kkp;
    }

   /**
    *  Life cycle of the service provider agent.
    */

    @Override
    public void run ()
    {
       String inputLine,                                   // input sentence
              outputLine;                                  // output sentence

       /* service providing */

       outputLine = kkp.processInput (null);               // generation of first sentence
       com.writeObject (outputLine);                       // its sending to the client
       while ((inputLine = (String) com.readObject ()) != null)     // chech if thereis an answer from the client
       { outputLine = kkp.processInput (inputLine);        // generation of next sentence
         com.writeObject (outputLine);                     // its sending to the client
         if (outputLine.equals ("Bye."))  break;           // check for end of exchange
       }
    }
}
