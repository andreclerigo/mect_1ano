package TowersHanoi;

/**
 *    Tower is defined as specialization of the stack.
 *    An aditional property, reading the value stored at each position, has been introduced.
 */

public class Tower<R> extends MemStack<R>
{
  /**
   *  Tower instantiation.
   *
   *    @param storage storage area
   *    @throws MemException if no storage are is provided
   */

   public Tower (R [] storage) throws MemException
   {
     super (storage);
   }

  /**
   *  Reading the value stored at a given position.
   *
   *    @param pos memory position (index of an element of the storage array)
   *    @return stored value, or null if the position is not being used
   *    @throws MemException if the access is outside the storage area
   */

   public R peek (int pos) throws MemException
   {
     if ((pos < 0) || (pos >= mem.length))
        throw new MemException ("Illegal position!");
        else if (pos < stackPnt)
                return (mem[pos]);
                else return (null);
   }
}
