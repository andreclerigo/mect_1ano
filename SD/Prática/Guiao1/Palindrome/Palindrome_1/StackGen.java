/**
 *    Generic stack.
 */

public class StackGen
{
  /**
   *   Internal storage area.
   */

   private Object [] stack;

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

   public StackGen (int nElem)
   {
     if (nElem > 0)
        { stack = new Object [nElem];
          stackPnt = 0;
        }
   }

  /**
   *   Stack push.
   *   A generic object is written into it.
   *   If the stack is full, nothing happens. No error is reported.
   *
   *    @param val generic object to be written
   */

   public void push (Object val)
   {
     if (stackPnt < stack.length)
        { stack[stackPnt] = val;
          stackPnt += 1;
        }
   }

  /**
   *   Stack pop.
   *   A generic object is read from it.
   *   If the stack is empty, <code>null</code> is returned. No error is reported.
   *
   *    @return last generic object that was written
   */

   public Object pop ()
   {
     Object val = null;                                    // default value

     if (stackPnt > 0)
        { stackPnt -= 1;
          val = stack[stackPnt];
        }
     return val;
   }
}
