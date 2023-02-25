import java.util.ArrayList;
import java.util.List;

/**
 * General description:
 * the class will create a BigInt with a list of digits.
 *
 * @author André Clérigo
 * @version 1.0 - 17/2/2023
 */
public class BigInt {
    private List<Integer> digits;

    /**
     * General description:
     * the method is a constructor for BigInt with a list of digits.
     */
    public BigInt() {
        digits = new ArrayList<>();
    }

    /**
     * General description:
     * the method is a constructor for BigInt with a string.
     * @param numStr
     */
    public BigInt(String numStr) {
        digits = new ArrayList<>();
        // Iterate through the string in reverse order
        for (int i = numStr.length() - 1; i >= 0; i--) {
            char c = numStr.charAt(i);
            if (Character.isDigit(c)) {
                digits.add(c - '0');
            } else {
                throw new IllegalArgumentException("Invalid digit: " + c);
            }
        }
        if (digits.isEmpty()) {
            digits.add(0);
        }

        removeLeadingZeros();
    }

    /**
     * General description:
     * the method will remove the leading zeros from the BigInt.
     */
    private void removeLeadingZeros() {
        // Iterate through the digits in reverse order
        while (digits.size() > 1 && digits.get(digits.size() - 1) == 0) {
            digits.remove(digits.size() - 1);
        }
    }

    /**
     * General description:
     * the method will return the result of the addition of two BigInts.
     * @param other
     * @return
     */
    public BigInt add(BigInt other) {
        BigInt result = new BigInt();
        int carry = 0;
        // Calculate the size of the result
        int size = Math.max(digits.size(), other.digits.size());
        // Iterate through the digits
        for (int i = 0; i < size; i++) {
            int sum = getDigit(i) + other.getDigit(i) + carry;
            result.digits.add(sum % 10);
            carry = sum / 10;
        }
        if (carry > 0) {
            result.digits.add(carry);
        }
        return result;
    }

    /**
     * General description:
     * the method will return the result of the multiplication of two BigInts.
     * @param other
     * @return
     */
    public BigInt multiply(BigInt other) {
        BigInt result = new BigInt();
        for (int i = 0; i < digits.size(); i++) {
            int carry = 0;
            BigInt temp = new BigInt();
            for (int j = 0; j < i; j++) {
                temp.digits.add(0);
            }
            for (int j = 0; j < other.digits.size(); j++) {
                int product = getDigit(i) * other.getDigit(j) + carry;
                temp.digits.add(product % 10);
                carry = product / 10;
            }
            if (carry > 0) {
                temp.digits.add(carry);
            }
            result = result.add(temp);
        }
        return result;
    }

    /**
     * General description:
     * the method will represent the BigInt as a string.
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = digits.size() - 1; i >= 0; i--) {
            // Add the digit to the string builder
            sb.append(digits.get(i));
        }

        return sb.toString();
    }

    /**
     * General description:
     * the method will return the digit at the given index.
     * @param index
     * @return
     */
    private int getDigit(int index) {
        return index < digits.size() ? digits.get(index) : 0;
    }
}
