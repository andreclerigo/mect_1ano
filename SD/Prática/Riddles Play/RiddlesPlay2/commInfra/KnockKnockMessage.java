package commInfra;

import java.io.*;
import genclass.GenericIO;

/**
 *    Exchanged message.
 */

public class KnockKnockMessage
{
   /**
    *  Message contents as a string.
    */

    private String msg = null;

   /**
    *  Message contents as an array of bytes.
    */

    private byte [] msgByte = null;

   /**
    *  Message instantiation (form 1).
    */

    public KnockKnockMessage (String msg)
    {
       this.msg = new String (msg);
       try
       { ByteArrayOutputStream bAux = new ByteArrayOutputStream (4096);
         ObjectOutputStream oAux = new ObjectOutputStream (bAux);
         oAux.writeObject (this.msg);
         oAux.flush ();
         msgByte = bAux.toByteArray ();
       }
       catch (IOException e)
       { GenericIO.writelnString ("Message could not be converted to an array of bytes!");
         e.printStackTrace ();
         System.exit (1);
       }
    }

   /**
    *  Message instantiation (form 2).
    */

    public KnockKnockMessage (byte [] msgByte)
    {
       this.msgByte = (byte []) msgByte.clone ();
       try
       { ObjectInputStream oAux = new ObjectInputStream (new ByteArrayInputStream (this.msgByte));
         msg = (String) oAux.readObject ();
       }
       catch (IOException e)
       { GenericIO.writelnString ("Message could not be converted to a string!");
         e.printStackTrace ();
         System.exit (1);
       }
       catch (ClassNotFoundException e)
       { GenericIO.writelnString ("Message type is unknown!");
         e.printStackTrace ();
         System.exit (1);
       }
    }

   /**
    *  Get message as a string.
    */

    public String getString ()
    {
       return (msg);
    }

   /**
    *  Get message as an array of bytes.
    */

    public byte [] getByteArray ()
    {
       return (msgByte);
    }
}
