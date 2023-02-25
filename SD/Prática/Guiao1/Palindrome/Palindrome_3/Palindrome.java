import genclass.GenericIO;
import java.util.Objects;

/**
 *    Palindrome detection using a stack and a FIFO derived both from a generic memory for temporary storage.
 *    Errors are reported.
 */

public class Palindrome
{
  /**
   *    Main program, implemented by the <code>main</code> method of the data type.
   *
   *      @param args runtime parameter list
   */

   public static void main (String [] args)
   {
     String word;                                          // read word

    /* reading the word from the keyboard */

     GenericIO.writeString ("Qual é a palavra? ");
     word = GenericIO.readlnString ();
     if (word == null) word = "";

     try
     {
       int i = 0;                                          // counting variable
                                                           //   declaration and initialization are required by its use
                                                           //   in situations where exceptions may be raised
       MemStack stack = new MemStack (word.length ());     // stack instantiation
       MemFIFO fifo = new MemFIFO (word.length ());        // FIFO instantiation

       /* its parallel storage (character by character) both in the stack and the FIFO */

       try
       { for (i = 0; i < word.length (); i++)
         { stack.write (word.charAt (i));
           fifo.write (word.charAt (i));
         }
       }
       catch (MemException e)
       { GenericIO.writelnString ("Error: ", e.getMessage (), " in iteration " + (i+1));
         GenericIO.writelnString ("Error: ", e.toString ());
         e.printStackTrace ();
         System.exit (1);
       }

       /* successive parallel reading (character by character) of the stored values both in the stack and the FIFO
          and their comparision */

       try
       { for (i = 0; i < word.length (); i++)
           if (!Objects.equals ((Character) stack.read (), (Character) fifo.read ()))
              { GenericIO.writelnString ("It is not a palindrome!");
                return;
              }
       }
       catch (MemException e)
       { GenericIO.writelnString ("Error: ", e.getMessage (), " in iteration " + (i+1));
         GenericIO.writelnString ("Error: ", e.toString ());
         e.printStackTrace ();
         System.exit (1);
       }
       GenericIO.writelnString ("It is a palindrome!");
     }
     catch (MemException e)
     { GenericIO.writelnString ("Error: ", e.getMessage ());
       GenericIO.writelnString ("Error: ", e.toString ());
       e.printStackTrace ();
       System.exit (1);
     }
  }
}
