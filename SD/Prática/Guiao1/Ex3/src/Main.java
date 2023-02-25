import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read the first number
        System.out.print("Enter the first number: ");
        String numStr1 = scanner.nextLine();
        BigInt num1 = new BigInt(numStr1);

        // Read the second number
        System.out.print("Enter the second number: ");
        String numStr2 = scanner.nextLine();
        BigInt num2 = new BigInt(numStr2);

        // Perform addition and print the result
        BigInt sum = num1.add(num2);
        System.out.println("Sum: " +  sum.toString());

        // Perform multiplication and print the result
        BigInt product = num1.multiply(num2);
        System.out.println("Product: " + product.toString());
    }
}