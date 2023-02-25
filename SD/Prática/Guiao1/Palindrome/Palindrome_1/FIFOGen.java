/**
 *    Generic FIFO.
 */

public class FIFOGen
{
  /**
   *   Internal storage area.
   */

   private Object [] fifo;

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

   public FIFOGen (int nElem)
   {
     if (nElem > 0)
        { fifo = new Object [nElem];
          inPnt = outPnt = 0;
          empty = true;
        }
   }

  /**
   *   FIFO insertion.
   *   A generic object is written into it.
   *   If the FIFO is full, nothing happens. No error is reported.
   *
   *    @param val generic object to be written
   */

   public void in (Object val)
   {
     if ((inPnt != outPnt) || empty)
        { fifo[inPnt] = val;
          inPnt = (inPnt + 1) % fifo.length;
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

   public Object out ()
   {
     Object val = null;                                    // default returned object

     if (!empty)
        { val = fifo[outPnt];
          outPnt = (outPnt + 1) % fifo.length;
          empty = (inPnt == outPnt);
        }
     return val;
   }
}
