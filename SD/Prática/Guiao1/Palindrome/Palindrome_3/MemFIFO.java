/**
 *    Generic FIFO derived from a generic memory.
 *    Errors are reported.
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
   *   Otherwise, an error is reported.
   *
   *     @param nElem FIFO size
   *     @throws MemException when an illegal size is passed
   */

   public MemFIFO (int nElem) throws MemException
   {
     super (nElem);
     inPnt = outPnt = 0;
     empty = true;
   }

  /**
   *   FIFO insertion.
   *   A generic object is written into it.
   *   If the FIFO is full, an error is reported.
   *
   *    @param val generic object to be written
   *    @throws MemException when the FIFO is full
   */

   @Override
   public void write (Object val) throws MemException
   {
     if ((inPnt != outPnt) || empty)
        { mem[inPnt] = val;
          inPnt = (inPnt + 1) % mem.length;
          empty = false;
        }
        else throw new MemException ("Fifo full!");
   }

  /**
   *   FIFO retrieval.
   *   A generic object is read from it.
   *   If the FIFO is empty, an error is reported.
   *
   *    @return first generic object that was written
   *    @throws MemException when the FIFO is empty
   */

   @Override
   public Object read () throws MemException
   {
     Object val;                                           // temporary value

     if (!empty)
        { val = mem[outPnt];
          outPnt = (outPnt + 1) % mem.length;
          empty = (inPnt == outPnt);
        }
        else throw new MemException ("Fifo empty!");
     return val;
   }
}
