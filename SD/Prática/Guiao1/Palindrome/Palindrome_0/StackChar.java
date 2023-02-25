/**
 *    Character stack.
 */

public class StackChar
{
  /**
   *   Internal storage area.
   */

   private char [] stack;

  /**
   *   Pointer to the first empty location.
   */

   private int stackPnt;

  /**
   *   Stack instantiation.
   *   The instantiation only takes place if the stack size is meaningful (greater than zero).
   *   No error is reported.
   *
   *     @param nElem stack size
   */

   public StackChar (int nElem)
   {
     if (nElem > 0)
        { stack = new char [nElem];
          stackPnt = 0;
        }
   }

  /**
   *   Stack push.
   *   A character is written into it.
   *   If the stack is full, nothing happens. No error is reported.
   *
   *    @param val character to be written
   */

   public void push (char val)
   {
     if (stackPnt < stack.length)
        { stack[stackPnt] = val;
          stackPnt += 1;
        }
   }

  /**
   *   Stack pop.
   *   A character is read from it.
   *   If the stack is empty, the <code>null</code> character is returned. No error is reported.
   *
   *    @return last character that was written
   */

   public char pop ()
   {
     char val = '\0';                                      // default returned character

     if (stackPnt > 0)
        { stackPnt -= 1;
          val = stack[stackPnt];
        }
     return val;
   }
}
