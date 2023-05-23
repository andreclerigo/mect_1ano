package clientSide;

import java.math.BigDecimal;
import interfaces.Task;

/**
 *  This data type defines a method for the computation of PI with a variable number of didgits which is prepared to be
 *  moved to a remote host and be executed there.
 */

public class Pi implements Task
{
  /**
   *  Constant ZERO.
   */

   private static final BigDecimal ZERO = BigDecimal.valueOf (0);

  /**
   *  Constant ONE.
   */

   private static final BigDecimal  ONE = BigDecimal.valueOf (1);

  /**
   *  Constant FOUR.
   */

   private static final BigDecimal FOUR = BigDecimal.valueOf (4);

  /**
   *  Rounding mode to use during pi computation.
   */

   private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

  /**
   *  Digits of precision after the decimal point.
   */

   private int digits;

  /**
   *  Instantiation of a PI computation object.
   *
   *     @param digits digits of precision after the decimal point
   */

   public Pi(int digits)
   {
     this.digits = digits;
   }

  /**
   *  PI computation.
   *
   *     @return value of PI with the required number of digits of precision
   */

   @Override
   public Object execute()
   {
     return computePi (digits);
   }

  /**
   *  Compute the value of pi to the specified number of digits after the decimal point.
   *
   *  The value is computed using Machin's formula:
   *
   *          pi/4 = 4*arctan(1/5) - arctan(1/239)
   *
   *  and a power series expansion of arctan(x) to sufficient precision.
   *
   *     @param digits digits of precision after the decimal point
   */

   public static BigDecimal computePi (int digits)
   {
     int scale = digits + 5;
     BigDecimal arctan1_5 = arctan (5, scale);
     BigDecimal arctan1_239 = arctan (239, scale);

     BigDecimal pi = arctan1_5.multiply (FOUR).subtract (arctan1_239).multiply (FOUR);
     return pi.setScale(digits, BigDecimal.ROUND_HALF_UP);
   }

  /**
   *  Compute the value, in radians, of the arctangent of the inverse of the supplied integer to the speficied
   *  number of digits after the decimal point.
   *  The value is computed using the power series expansion for the arc tangent:
   *
   *  arctan(x) = x - (x^3)/3 + (x^5)/5 - (x^7)/7 + (x^9)/9 ...
   */

   private static BigDecimal arctan (int inverseX, int scale)
   {
     BigDecimal result, numer, term;
     BigDecimal invX = BigDecimal.valueOf (inverseX);
     BigDecimal invX2 = BigDecimal.valueOf (inverseX * inverseX);

     numer = ONE.divide(invX, scale, roundingMode);
     result = numer;

     int i = 1;

     do
     { numer = numer.divide (invX2, scale, roundingMode);
       int denom = 2 * i + 1;
       term = numer.divide(BigDecimal.valueOf(denom), scale, roundingMode);
       if ((i % 2) != 0)
          result = result.subtract(term);
          else result = result.add(term);
       i++;
     } while (term.compareTo (ZERO) != 0);

     return result;
    }
}
