import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import genclass.GenericIO;

/**
 *  Visualization of the serialization format.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class ShowMain
{
   /**
    *   Main program, method main of the data type.
    */

    public static void main (String [] args)
    {
       ByteArrayOutputStream bOut;                         // byte array based output stream
       ObjectOutputStream oOut = null;                     // object based output stream
       Record rec;                                         // record
       byte [] bData;                                      // byte array for conversion data

       rec = new Record (105, "Ana Francisca");
       bOut = new ByteArrayOutputStream (4096);
       try
       { oOut = new ObjectOutputStream (bOut);
       }
       catch (IOException e)
       { GenericIO.writelnString ("I/O error occurs while writing stream header!");
         e.printStackTrace ();
         System.exit (1);
       }
       try
       { oOut.writeObject (rec);
         oOut.flush ();
       }
       catch (IOException e)
       { GenericIO.writelnString ("I/O error occurs while writing stream header!");
         e.printStackTrace ();
         System.exit (1);
       }
       bData = bOut.toByteArray ();

       System.out.println ("Serialization length = " + bData.length);
       for (int i = 0; i < bData.length; i++)
       { if (((i % 40) == 0) && (i != 0)) System.out.println ();
         GenericIO.writeString (convByteHexa (bData[i]));
       }
       System.out.println ();
       System.out.println ();
       System.out.println ("Serialization header");
       System.out.println ("  Magic number = " + convByteHexa (bData[0]) + convByteHexa (bData[1]));
       System.out.println ("  Version number = " + convByteHexa (bData[2]) + convByteHexa (bData[3]));
       System.out.println ("Full description of the object = " + convByteHexa (bData[4]));
       System.out.println ("  Object's data type descripton start marker = " + convByteHexa (bData[5]));
       System.out.println ("    Number of characters of the data type name = " + convBytesShort (bData[6], bData[7]));
       try
       { System.out.println ("    Data type name = " + new String (bData, 8, convBytesShort (bData[6], bData[7]),
                                                                   "UTF-8"));
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
       System.out.println ("    Data type serial version identification = " + convBytesLong (bData[14], bData[15],
                                                   bData[16], bData[17], bData[18], bData[19], bData[20], bData[21]));
       System.out.println ("    Data type encoding = " + convByteHexa (bData[22]) +
                           " (implements Serializable interface)");
       System.out.println ("    Number of instance fields = " + convBytesShort (bData[23], bData[24]));
       try
       { System.out.println ("    Instance field 1 type = " + new String (bData, 25, 1, "UTF-8"));
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
       System.out.println ("      Number of characters of the field 1 name = " + convBytesShort (bData[26], bData[27]));
       try
       { System.out.println ("      Instance field 1 name = " + new String (bData, 28, convBytesShort (bData[26],
                                                                            bData[27]), "UTF-8") + " (int)");
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
       try
       { System.out.println ("    Instance field 2 type = " + new String (bData, 32, 1, "UTF-8") + " (object)");
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
       System.out.println ("      Number of characters of the field 2 name = " + convBytesShort (bData[33], bData[34]));
       try
       { System.out.println ("      Instance field 2 name = " + new String (bData, 35, convBytesShort (bData[33],
                             bData[34]), "UTF-8"));
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
       System.out.println ("      Object = " + convByteHexa (bData[39]));
       System.out.println ("      Number of characters of the data type name = " + convBytesShort (bData[40], bData[41]));
       try
       { System.out.println ("      Data type name = " + new String (bData, 42, convBytesShort (bData[40], bData[41]),
                                                                     "UTF-8"));
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
       System.out.println ("  Object's data type descripton end marker = " + convByteHexa (bData[60]));
       System.out.println ("  No superclass marker = " + convByteHexa (bData[61]));
       System.out.println ("Object's contents");
       System.out.println ("  Filed 1 value = " + convBytesInt (bData[62], bData[63], bData[64], bData[65]));
       System.out.println ("  Field 2 object marker = " + convByteHexa (bData[66]));
       System.out.println ("    Number of characters of the field 2 value = " + convBytesShort (bData[67], bData[68]));
       try
       { System.out.println ("    Field 2 value = " + new String (bData, 69, convBytesShort (bData[67], bData[68]),
                                                               "UTF-8"));
       }
       catch (UnsupportedEncodingException e)
       { GenericIO.writelnString ("Encoding character set is not supported!");
         e.printStackTrace ();
         System.exit (1);
       }
    }

   /**
    *   Convert byte to hexadecimal.
    */

    private static String convByteHexa (byte b)
    {
       byte mask = (byte) 0x0F;
       byte [] hexa = new byte[2];
       String s;

       hexa[0] = (((b >> 4) & mask) > 0x9) ? (byte) (0x37 + ((b >> 4) & mask)) : (byte) (0x30 + ((b >> 4) & mask));
       hexa[1] = ((b & mask) > 0x9) ? (byte) (0x37 + (b & mask)) : (byte) (0x30 + (b & mask));
       s = new String (hexa);

       return s;
    }

   /**
    *   Convert bytes to short.
    */

    private static short convBytesShort (byte b1, byte b0)
    {
       return (short) (b1 * 256 + b0);
    }

   /**
    *   Convert bytes to int.
    */

    private static int convBytesInt (byte b3, byte b2, byte b1, byte b0)
    {
       return (((b3* 256 + b2) * 256 + b1) * 256 + b0);
    }

   /**
    *   Convert bytes to long.
    */

    private static long convBytesLong (byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0)
    {
       return (long) (((((((b7 * 256 + b6) * 256 + b5) * 256 + b4) * 256 + b3)* 256 + b2) * 256 + b1) * 256 + b0);
    }
 }
