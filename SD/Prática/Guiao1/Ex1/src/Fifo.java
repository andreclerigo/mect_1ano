import java.util.LinkedList;

/**
 * General description:
 * the class will create a Fifo with a LinkedList.
 *
 * @author André Clérigo
 * @version 1.0 - 16/2/2023
 */
public class Fifo {
    // Create a Fifo with a LinkedList
    private LinkedList<Character> fifo = new LinkedList<Character>();

    // create documentation

    /**
     * General description:
     * the method will add a character to the end of the Fifo.
     * @param ch
     */
    // Add a character to the end of the Fifo
    public void enqueueCharacter(char ch) {
        fifo.addLast(ch);
    }

    /**
     * General description:
     * the method will remove a character from the beginning of the Fifo.
     * @return
     */
    // Remove a character from the beginning of the Fifo
    public char dequeueCharacter() {
        return fifo.removeFirst();
    }

    /**
     * General description:
     * the method will return if the Fifo is empty.
     * @return
     */
    // Return if the fifo is empty
    public boolean isEmpty() {
        return fifo.isEmpty();
    }

    /**
     * General description:
     * the method will empty the Fifo.
     */
    // Empty the fifo
    public void empty() {
        fifo.clear();
    }
}
