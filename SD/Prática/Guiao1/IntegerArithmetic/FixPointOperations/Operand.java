package FixPointOperations;

/**
 *    Operand representation for single digit basic arithmetic operations.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class Operand
{
  /**
   *   Binary representation of <em>operand 1</em>.
   */

   protected int iop1;

  /**
   *   Binary representation of <em>operand 2</em>.
   */

   protected int iop2;

  /**
   *   Binary representation of <em>carry in</em>.
   */

   protected int ipvi;

  /**
   *   Character representation of <em>operand 1</em>.
   */

   protected char op1;

  /**
   *   Character representation of <em>operand 2</em>.
   */

   protected char op2;

  /**
   *   Character representation of <em>carry in</em>.
   */

   protected char pvi;

  /**
   *   Initialization (this constructor should always be used).
   *
   *      @param op1 operand 1
   *      @param op2 operand 2
   *      @param pvi carry in
   */

   public Operand (char op1, char op2, char pvi)
   {
      this.op1 = op1;
      this.op2 = op2;
      this.pvi = pvi;
      iop1 = Character.digit (op1, 10);
      iop2 = Character.digit (op2, 10);
      ipvi = Character.digit (pvi, 10);
   }
}
