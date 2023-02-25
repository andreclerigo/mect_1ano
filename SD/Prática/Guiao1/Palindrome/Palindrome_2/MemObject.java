/**
 *    Generic memory.
 *    Non-instantiatable data type. It must be derived.
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
   *   No error is reported.
   *
   *     @param nElem memory size
   */

   protected MemObject (int nElem)
   {
     if (nElem > 0) mem = new Object [nElem];
   }

  /**
   *   Memory write.
   *   A generic object is written into it.
   *   Virtual method, it has to be overridden in a derived data type.
   *
   *    @param val generic object to be written
   */

   protected abstract void write (Object val);

  /**
   *   Memory read.
   *   A generic object is read from it.
   *   Virtual method, it has to be overridden in a derived data type.
   *
   *    @return last generic object that was written
   */

   protected abstract Object read ();
}
