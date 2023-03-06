package FloatingPointOperations;

/**
 *    Representation of a real number as a double precision floating point quantity.
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

public class Real implements Value
{
  /**
   *   Binary representation of a real number.
   */

   private double real;

  /**
   *   Initialization.
   *
   *      @param real real valued quantity
   */

   public Real (double real)
   {
      this.real = real;
   }

  /**
   *   Getting the value of the quantity in textual format.
   *
   *      @return textual representation of the quantity
   */

   public String printVal ()
   {
      return new Double (real).toString ();
   }

  /**
   *   Getting the value of the quantity in binary.
   *
   *      @return binary representation of the quantity
   */

   public double getVal ()
   {
      return real;
   }

  /**
   *   Addition of two real numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return sum
   */

   protected static Real add (Real op1, Real op2)
   {
      return new Real (op1.getVal () + op2.getVal ());
   }

  /**  Subtraction of two real numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return difference
   */

   protected static Real sub (Real op1, Real op2)
   {
      return new Real (op1.getVal () - op2.getVal ());
   }

  /**
   *   Multiplication of two real numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return product
   */

   protected static Real mult (Real op1, Real op2)
   {
      return new Real (op1.getVal () * op2.getVal ());
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

   protected static Real div (Real op1, Real op2)
   {
      return new Real (op1.getVal () / op2.getVal ());
   }
}
