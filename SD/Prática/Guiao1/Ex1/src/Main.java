public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Array of strings to test if they are palindromes
        String[] testStrings = {"hello", "racecar", "lollipop", "Madam", "abba", "abab", "acdc", "ab"};

        // Create a PalindromeChecker
        PalindromeChecker palindromeChecker = new PalindromeChecker();

        // Loop through the test strings
        for (String testString : testStrings) {
            // Check if the string is a palindrome
            boolean isPalindrome = palindromeChecker.isPalindrome(testString);

            // Print the result
            System.out.println(testString + " is a palindrome: " + isPalindrome);
        }
    }
}
