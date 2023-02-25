/**
 *    Generic memory.
 *    Non-instantiatable data type. It must be derived.
 *    Errors are reported.
 */

public abstract class MemObject

{
  /**
   *   Internal storage area.
   */

   protected Object [] mem;

  /**
   *   Memory instantiation.
   *   The instantiation only takes place if the memory size is meaningful (greater than zero).
   *
   *     @param nElem memory size
   *     @throws MemException when an illegal size is passed
   */

   protected MemObject (int nElem) throws MemException
   {
     if (nElem > 0)
        mem = new Object [nElem];
        else throw new MemException ("Illegal storage size!");
   }

  /**
   *   Memory write.
   *   A generic object is written into it.
   *   Virtual method, it has to be overridden in a derived data type.
   *
   *    @param val generic object to be written
   *    @throws MemException when the memory is full
   */

   protected abstract void write (Object val) throws MemException;

  /**
   *   Memory read.
   *   A generic object is read from it.
   *   Virtual method, it has to be overridden in a derived data type.
   *
   *    @return last generic object that was written
   *    @throws MemException when the memory is empty
   */

   protected abstract Object read () throws MemException;
}
