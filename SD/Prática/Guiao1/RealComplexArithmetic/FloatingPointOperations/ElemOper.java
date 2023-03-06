package FloatingPointOperations;

/**
 *    Generic functionality for the basic arithmetic operations having as operands real / complex double precision
 *    floating point numbers.
 *    The following operations are implemented
 *     - addition
 *     - subtraction
 *     - multiplication
 *     - division.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class ElemOper
{
  /**
   *  Prevent instantiation.
   */

   private ElemOper ()
   {
   }

  /**
   *   Addition of two real / complex numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return sum
   */

   public static Value add (Value op1, Value op2)
   {
      if ((op1 instanceof Real) && (op2 instanceof Real))
         return Real.add ((Real) op1, (Real) op2);
         else { Complex op1x, op2x;
                if (op1 instanceof Real)
                   op1x = new Complex (((Real) op1).getVal (), 0.0);
                   else op1x = (Complex) op1;
                if (op2 instanceof Real)
                   op2x = new Complex (((Real) op2).getVal (), 0.0);
                   else op2x =  (Complex) op2;
                return Complex.add (op1x, op2x);
              }
   }

  /**
   *   Subtraction of two real / complex numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return difference
   */

   public static Value sub (Value op1, Value op2)
   {
      if ((op1 instanceof Real) && (op2 instanceof Real))
         return Real.sub ((Real) op1, (Real) op2);
         else { Complex op1x, op2x;
                if (op1 instanceof Real)
                   op1x = new Complex (((Real) op1).getVal (), 0.0);
                   else op1x = (Complex) op1;
                if (op2 instanceof Real)
                   op2x = new Complex (((Real) op2).getVal (), 0.0);
                   else op2x =  (Complex) op2;
                return Complex.sub (op1x, op2x);
              }
   }

  /**
   *   Multiplication of two real / complex numbers.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return product
   */

   public static Value mult (Value op1, Value op2)
   {
      if ((op1 instanceof Real) && (op2 instanceof Real))
         return Real.mult ((Real) op1, (Real) op2);
         else { Complex op1x, op2x;
                if (op1 instanceof Real)
                   op1x = new Complex (((Real) op1).getVal (), 0.0);
                   else op1x = (Complex) op1;
                if (op2 instanceof Real)
                   op2x = new Complex (((Real) op2).getVal (), 0.0);
                   else op2x =  (Complex) op2;
                return Complex.mult (op1x, op2x);
              }
   }

  /**
   *   Division of two real / complex numbers.
   *   Bear in mind that, as Java represents double precision quantities using the standard IEEE 754,
   *   division by zero does not produce an exception.
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *
   *      @return quotient
   */

   public static Value div (Value op1, Value op2)
   {
      if ((op1 instanceof Real) && (op2 instanceof Real))
         return Real.div ((Real) op1, (Real) op2);
         else { Complex op1x, op2x;
                if (op1 instanceof Real)
                   op1x = new Complex (((Real) op1).getVal (), 0.0);
                   else op1x = (Complex) op1;
                if (op2 instanceof Real)
                   op2x = new Complex (((Real) op2).getVal (), 0.0);
                   else op2x =  (Complex) op2;
                return Complex.div (op1x, op2x);
              }
   }
}
