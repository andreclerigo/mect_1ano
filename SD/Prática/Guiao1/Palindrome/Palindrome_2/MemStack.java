/**
 *    Generic stack derived from a generic memory.
 */

public class MemStack extends MemObject
{
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

   public MemStack (int nElem)
   {
     super (nElem);
     stackPnt = 0;
   }

  /**
   *   Stack push.
   *   A generic object is written into it.
   *   If the stack is full, nothing happens. No error is reported.
   *
   *    @param val generic object to be written
   */

   @Override
   public void write (Object val)
   {
     if (stackPnt < mem.length)
        { mem[stackPnt] = val;
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

   @Override
   public Object read ()
   {
     Object val = null;                                    // default returned object

     if (stackPnt > 0)
        { stackPnt -= 1;
          val = mem[stackPnt];
        }
     return val;
   }
}
