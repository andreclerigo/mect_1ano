/**
 *    Generic FIFO derived from a generic memory.
 */

public class MemFIFO extends MemObject
{
  /**
   *   Pointer to the first empty location.
   */

   private int inPnt;

  /**
   *   Pointer to the first occupied location.
   */

   private int outPnt;

  /**
   *   Signaling FIFO empty state.
   */

   private boolean empty;

  /**
   *   FIFO instantiation.
   *   The instantiation only takes place if the FIFO size is meaningful (greater than zero).
   *   No error is reported.
   *
   *     @param nElem FIFO size
   */

   public MemFIFO (int nElem)
   {
     super (nElem);
     inPnt = outPnt = 0;
     empty = true;
   }

  /**
   *   FIFO insertion.
   *   A generic object is written into it.
   *   If the FIFO is full, nothing happens. No error is reported.
   *
   *    @param val generic object to be written
   */

   @Override
   public void write (Object val)
   {
     if ((inPnt != outPnt) || empty)
        { mem[inPnt] = val;
          inPnt = (inPnt + 1) % mem.length;
          empty = false;
        }
   }

  /**
   *   FIFO retrieval.
   *   A generic object is read from it.
   *   If the FIFO is empty, <code>null</code> is returned. No error is reported.
   *
   *    @return first generic object that was written
   */

   @Override
   public Object read ()
   {
     Object val = null;                                    // default returned object

     if (!empty)
        { val = mem[outPnt];
          outPnt = (outPnt + 1) % mem.length;
          empty = (inPnt == outPnt);
        }
     return val;
   }
}
