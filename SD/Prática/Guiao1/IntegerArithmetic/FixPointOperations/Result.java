package FixPointOperations;

/**
 *    Result representation for single digit basic arithmetic operations.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class Result
{
  /**
   *   Binary representation of <em>result</em>.
   */

   protected int ires;

  /**
   *   Binary representation of <em>carry out</em>.
   */

   protected int ipvo;

  /**
   *   Character representation of <em>result</em>.
   */

   protected char res;

  /**
   *   Character representation of <em>carry out</em>.
   */

   protected char pvo;

  /**
   *   Initialization (this constructor should always be used).
   *
   *      @param ires resultado
   *      @param ipvo propagação de saída
   */

   public Result (int ires, int ipvo)
   {
      this.ires = ires;
      this.ipvo = ipvo;
      res = Character.forDigit (ires, 10);
      pvo = Character.forDigit (ipvo, 10);
   }
}
