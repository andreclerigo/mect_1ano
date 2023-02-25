package TowersHanoi;

/**
 *    Parametric stack derived from a parametric memory.
 *    Errors are reported.
 *
 *    @param <R> data type of stored objects
 */

public class MemStack<R> extends MemObject<R>
{
  /**
   *   Pointer to the first empty location.
   */

   protected int stackPnt;

  /**
   *   Stack instantiation.
   *   The instantiation only takes place if the memory exists.
   *   Otherwise, an error is reported.
   *
   *     @param storage memory to be used
   *     @throws MemException when the memory does not exist
   */

   public MemStack (R [] storage) throws MemException
   {
     super (storage);
     stackPnt = 0;
   }

  /**
   *   Stack push.
   *   A parametric object is written into it.
   *   If the stack is full, an error is reported.
   *
   *    @param val parametric object to be written
   *    @throws MemException when the stack is full
   */

   @Override
   public void write (R val) throws MemException
   {
     if (stackPnt < mem.length)
        { mem[stackPnt] = val;
          stackPnt += 1;
        }
        else throw new MemException ("Stack full!");
   }

  /**
   *   Stack pop.
   *   A parametric object is read from it.
   *   If the stack is empty, an error is reported.
   *
   *    @return last parametric object that was written
   *    @throws MemException when the stack is empty
   */

   @Override
   public R read () throws MemException
   {
     if (stackPnt != 0)
        { stackPnt -= 1;
          return mem[stackPnt];
        }
        else throw new MemException ("Stack empty!");
   }
}
