import java.util.LinkedList;

/**
 * General description:
 * the class will create a Stack with a LinkedList.
 *
 * @author André Clérigo
 * @version 1.0 - 16/2/2023
 */
public class Stack {
    // Create a Stack with a LinkedList
    private LinkedList<Character> stack = new LinkedList<Character>();

    /**
     * General description:
     * the method will add a character to the end of the Stack.
     * @param ch
     */
    // Push a character onto the stack
    public void pushCharacter(char ch) {
        stack.push(ch);
    }

    /**
     * General description:
     * the method will remove a character from the beginning of the Stack.
     * @return
     */
    // Pop a character off the stack
    public char popCharacter() {
        return stack.pop();
    }

    /**
     * General description:
     * the method will return if the Stack is empty.
     * @return
     */
    // Return if the stack is empty
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     *  General description:
     *  the method will empty the Stack.
     */
    // Empty the stack
    public void empty() {
        stack.clear();
    }
}
