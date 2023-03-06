package FixPointOperations;

/**
 *    Single digit basic arithmetic operations.
 *    Addition and multiplication.
 *    Operands are contained in a variable of data type <code>Operand</code>
 *    and the result in a variable of data type <code>Result</code>.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class DigitOper
{
  /**
   *  Prevent instantiation.
   */

   private DigitOper ()
   {
   }

  /**
   *   Addition of two decimal digits.
   *
   *   @param op operands
   *
   *          op.op1 - digit <em>operand 1</em>
   *          op.op2 - digit <em>operand 2</em>
   *          op.pvi - digit <em>carry in</em>
   *
   *   @return result

   *           result.res - digit <em>result</em> -
   *           result.pvo - digit <em>carry out</em>)
   */

   public static Result add (Operand op)
   {
      int whole;                       // result (in a non normalized format)

      whole = op.iop1 + op.iop2 + op.ipvi;
      return (new Result ((whole >= 10) ? whole - 10 : whole, (whole >= 10) ? 1 : 0));
   }

  /**
   *   Multiplication of two decimal digits.
   *
   *   @param op operands
   *
   *          op.op1 - digit <em>operand 1</em>
   *          op.op2 - digit <em>operand 2</em>
   *          op.pvi - digit <em>carry in</em>
   *
   *   @return result

   *           result.res - digit <em>result</em> -
   *           result.pvo - digit <em>carry out</em>)
   */

   public static Result mult (Operand op)
   {
      int whole;                       // result (in a non normalized format)
      int wholeDiv10;                  // carry out

      whole = op.iop1 * op.iop2 + op.ipvi;
      wholeDiv10 = (whole - (((whole >> 3) - ((whole >= 40) ? ((whole >= 80) ? 2 : 1) : 0)) << 1)) >> 3;
      return (new Result (whole - ((wholeDiv10 << 3) + (wholeDiv10 << 1)), wholeDiv10));
   }
}
