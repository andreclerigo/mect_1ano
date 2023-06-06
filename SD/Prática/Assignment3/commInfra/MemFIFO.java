package commInfra;

/**
 *    Parametric FIFO derived from a parametric memory.
 *    Errors are reported.
 *
 *    @param <R> data type of stored objects
 */
public class MemFIFO<R> extends MemObject<R>
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
	 *
	 */
	private int nElem;

	/**
	 *   FIFO instantiation.
	 *   The instantiation only takes place if the memory exists.
	 *   Otherwise, an error is reported.
	 *
	 *     @param storage memory to be used
	 *     @throws MemException when the memory does not exist
	 */
	public MemFIFO (R [] storage) throws MemException
	{
		super (storage);
		inPnt = outPnt = 0;
		empty = true;
	}

	/**
	 *   FIFO insertion.
	 *   A parametric object is written into it.
	 *   If the FIFO is full, an error is reported.
	 *
	 *    @param val parametric object to be written
	 *    @throws MemException when the FIFO is full
	 */
	@Override
	public void write (R val) throws MemException
	{
		if ((inPnt != outPnt) || empty) {
			mem[inPnt] = val;
		  	inPnt = (inPnt + 1) % mem.length;
		  	empty = false;
			nElem++;
		} else {
			throw new MemException ("Fifo full!");
		}
	}

	/**
	 *   FIFO retrieval.
	 *   A parametric object is read from it.
	 *   If the FIFO is empty, an error is reported.
	 *
	 *    @return first parametric object that was written
	 *    @throws MemException when the FIFO is empty
	 */
	@Override
	public R read () throws MemException
	{
		R val;

		if (!empty) { 
			 val = mem[outPnt];
			 outPnt = (outPnt + 1) % mem.length;
			 empty = (inPnt == outPnt);
			 nElem--;
		} else {
			throw new MemException ("Fifo empty!");
		}
		return val;
	}

	/**
	 *   Test FIFO current full status.
	 *
	 *    @return true, if FIFO is full -
	 *            false, otherwise
	 */
	public boolean full ()
	{
		return !((inPnt != outPnt) || empty);
	}

	/**
	 *   Get the number of elements in the FIFO
	 *
	 *    @return number of elements
	 */
	public int getNumOfElements() {
		return nElem;
	}

	/**
	 *    Check if the element exists on the FIFO
	 *
	 *     @param element the element we are looking for
	 *     @return Either true if the element is present or false otherwise
	 */
	public boolean elementExists (R element)
	{
		int i = outPnt;
		while (i != inPnt) {
			if (mem[i].equals(element)) return true;
			i = (i + 1) % mem.length;
		}
		return false;
	}

	// to string function
	@Override
	public String toString() {
		String str = "";
		int i = outPnt;
		while (i != inPnt) {
			str += mem[i].toString() + " ";
			i = (i + 1) % mem.length;
		}
		return str;
	}
}
