import genclass.GenericIO;

/**
 *    Palindrome detection using a character stack and a character FIFO for temporary storage.
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

     GenericIO.writeString ("What is the word? ");
     word = GenericIO.readlnString ();
     if (word == null) word = "";

    /* its parallel storage (character by character) both in the stack and the FIFO */

     StackChar stack = new StackChar (word.length ());     // stack instantiation
     FIFOChar fifo = new FIFOChar (word.length ());        // FIFO instantiation

     for (int i = 0; i < word.length (); i++)
     { stack.push (word.charAt (i));
       fifo.in (word.charAt (i));
     }

    /* successive parallel reading (character by character) of the stored values both in the stack and the FIFO
       and their comparision */

     for (int i = 0; i < word.length (); i++)
       if (stack.pop () != fifo.out ())
          { GenericIO.writelnString ("It is not a palindrome!");
            return;
          }
     GenericIO.writelnString ("It is a palindrome!");
   }
}
