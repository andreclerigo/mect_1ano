/**
 *    Generic stack derived from a generic memory.
 *    Errors are reported.
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
   *   Otherwise, an error is reported.
   *
   *     @param nElem stack size
   *     @throws MemException when an illegal size is passed
   */

   public MemStack (int nElem) throws MemException
   {
     super (nElem);
     stackPnt = 0;
   }

  /**
   *   Stack push.
   *   A generic object is written into it.
   *   If the stack is full, an error is reported.
   *
   *    @param val generic object to be written
   *    @throws MemException when the stack is full
   */

   @Override
   public void write (Object val) throws MemException
   {
     if (stackPnt < mem.length)
        { mem[stackPnt] = val;
          stackPnt += 1;
        }
        else throw new MemException ("Stack full!");
   }

  /**
   *   Stack pop.
   *   A generic object is read from it.
   *   If the stack is empty, an error is reported.
   *
   *    @return last generic object that was written
   *    @throws MemException when the stack is empty
   */

   @Override
   public Object read () throws MemException
   {
     if (stackPnt > 0)
        { stackPnt -= 1;
          return (mem[stackPnt]);
        }
        else throw new MemException ("Stack empty!");
   }
}
