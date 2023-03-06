package FloatingPointOperations;

/**
 *    Representation of a complex number as a pair of double precision floating point quantities.
 *
 *    The following operations are implemented
 *     - addition
 *     - subtraction
 *     - multiplication
 *     - division.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class Complex implements Value
{
  /**
   *   Binary representation of the real part of a complex number.
   */

   private double realPart;

  /**
   *   Binary representation of the imaginary part of a complex number.
   */

   private double imagPart;

  /**
   *   Initialization.
   *
   *      @param realPart real part of a complex quantity
   *      @param imagPart imaginary part of a complex quantity
   */

   public Complex (double realPart, double imagPart)
   {
      this.realPart = realPart;
      this.imagPart = imagPart;
   }

  /**
   *   Getting the value of the quantity in textual format.
   *
   *      @return textual representation of the quantity
   */

   public String printVal ()
   {
      if (imagPart >= 0.0)
         return new Double (realPart).toString () + " + i" + new Double (imagPart).toString ();
         else return new Double (realPart).toString () + " - i" + new Double (-imagPart).toString ();
   }

  /**
   *   Getting the value of the real part of the quantity in binary.
   *
   *      @return binary representation of the real part of the quantity
   */

   public double getRealPart ()
   {
      return realPart;
   }

  /**
   *   Getting the value of the imaginary part of the quantity in binary.
   *
   *      @return binary representation of the imaginary part of the quantity
   */

   public double getImagPart ()
   {
      return imagPart;
   }

  /**
   *   Addition of two complex numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return sum
   */

   protected static Complex add (Complex op1, Complex op2)
   {
      return new Complex (op1.getRealPart () + op2.getRealPart (), op1.getImagPart () + op2.getImagPart ());
   }

  /**  Subtraction of two complex numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return difference
   */

   protected static Complex sub (Complex op1, Complex op2)
   {
      return new Complex (op1.getRealPart () - op2.getRealPart (), op1.getImagPart () - op2.getImagPart ());
   }

  /**
   *   Multiplication of two real numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return product
   */

   protected static Complex mult (Complex op1, Complex op2)
   {
      return new Complex (op1.getRealPart () * op2.getRealPart () - op1.getImagPart () * op2.getImagPart (),
                          op1.getImagPart () * op2.getRealPart () + op1.getRealPart () * op2.getImagPart ());
   }

  /**
   *   Division of two real numbers.
   *   Bear in mind that, as Java represents double precision quantities using the standard IEEE 754,
   *   division by zero does not produce an exception.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return quotient
   */

   protected static Complex div (Complex op1, Complex op2)
   {
      double denom = op2.getRealPart () * op2.getRealPart () + op2.getImagPart () * op2.getImagPart ();

      return new Complex ((op1.getRealPart () * op2.getRealPart () + op1.getImagPart () * op2.getImagPart ()) / denom,
                          (op1.getImagPart () * op2.getRealPart () - op1.getRealPart () * op2.getImagPart ()) / denom);
   }
}
