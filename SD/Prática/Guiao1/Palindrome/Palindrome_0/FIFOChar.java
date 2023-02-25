/**
 *    Character FIFO.
 */

public class FIFOChar
{
  /**
   *   Internal storage area.
   */

   private char [] fifo;

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

   public FIFOChar (int nElem)
   {
     if (nElem > 0)
        { fifo = new char [nElem];
          inPnt = outPnt = 0;
          empty = true;
        }
   }

  /**
   *   FIFO insertion.
   *   A character is written into it.
   *   If the FIFO is full, nothing happens. No error is reported.
   *
   *    @param val character to be written
   */

   public void in (char val)
   {
     if ((inPnt != outPnt) || empty)
        { fifo[inPnt] = val;
          inPnt = (inPnt + 1) % fifo.length;
          empty = false;
        }
   }

  /**
   *   FIFO retrieval.
   *   A character is read from it.
   *   If the FIFO is empty, the <code>null</code> character is returned. No error is reported.
   *
   *    @return first character that was written
   */

   public char out ()
   {
     char val = '\0';                                      // default returned character

     if (!empty)
        { val = fifo[outPnt];
          outPnt = (outPnt + 1) % fifo.length;
          empty = (inPnt == outPnt);
        }
     return val;
   }
}
