/**
 * General description:
 * the program will create a PalindromeChecker with a Fifo and a Stack.
 *
 * @author André Clérigo
 * @version 1.0 - 16/2/2023
 */
public class PalindromeChecker {
    private Fifo fifo = new Fifo();
    private Stack stack = new Stack();

    /**
     * General description:
     * the method will return either <b>True</b> or <b>False</b> if the input string is a palindrome or not.
     * @param input
     * @return
     */
    public boolean isPalindrome(String input) {
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            // Convert the char to lowercase and add it to the Fifo and Stack
            ch = Character.toLowerCase(ch);
            fifo.enqueueCharacter(ch);
            stack.pushCharacter(ch);
        }

        while (!fifo.isEmpty() && !stack.isEmpty()) {
            // Compare characters from the beginning and end of the string
            boolean equal = fifo.dequeueCharacter() == stack.popCharacter();

            if (!equal) {
                fifo.empty();
                stack.empty();
                return false;
            }
        }

        fifo.empty();
        stack.empty();
        return true;
    }
}
