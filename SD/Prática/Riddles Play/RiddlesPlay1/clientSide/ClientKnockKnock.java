package clientSide;

import genclass.GenericIO;

/**
 *    Client side of the Riddles Play.
 *
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

     /* getting information about the server location */

      String hostName;                                              // name of the computational system where the server is located
      int portNumb = 4000;                                          // number of the listening port at the computational system where the server is located

      GenericIO.writeString ("Name of the computational system where the server is located? ");
      hostName = GenericIO.readlnString ();

     /* message exchange */

      KnockKnockStub kkStub;                                        // reference to remote object

      kkStub = new KnockKnockStub (hostName, portNumb);
      kkStub.exchange ();
   }
}
