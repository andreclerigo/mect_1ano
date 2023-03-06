package FixPointOperations;

/**
 *    Generic functionality for the basic arithmetic operations having as operands decimal integers with
 *    a variable number of digits.
 *    The following operations are implemented
 *     - addition
 *     - multiplication.
 *    Operands and the result are expressed as <code>String</code> type quantities.
 *    Internal processing is carried out as a <code> character arrays</code>.
 *
 *    @author António Rui Borges
 *    @version 1.1
 */

public class BasicOper
{
  /**
   *  Prevent instantiation.
   */

   private BasicOper ()
   {
   }

  /**
   *   Addition of two decimal numbers.
   *
   *   @param op1 operand 1
   *   @param op2 operand 2
   *
   *   @return sum
   *
   *   @throws NumberFormatException if the operands do not represent decimal numbers
   */

   public static String add (String op1, String op2) throws NumberFormatException
   {
      char [] oop1,                               // operand 1 as a character array
              oop2,                               // operand 2 as a character array
              osum;                               // sum as a character array

      /* parameter validation */

      if (op1.length () == 0)
         throw new NumberFormatException ("Operand 1 is a null string!");
      if (op2.length () == 0)
         throw new NumberFormatException ("Operand 2 is a null string!");

      oop1 = normNum (1, op1);
      oop2 = normNum (2, op2);

      /* computation */

      osum = internalAdd (oop1, oop2);

      return (new String (osum));
  }

  /**
   *   Multiplication of two decimal numbers.
   *
   *   @param op1 operand 1
   *   @param op2 operand 2
   *
   *   @return product
   *
   *   @throws NumberFormatException if the operands do not represent decimal numbers
   */

   public static String mult (String op1, String op2) throws NumberFormatException
   {
      char [] oop1,                               // operand 1 as a character array
              oop2,                               // operand 2 as a character array
              oprod;                              // product as a character array

      /* parameter validation */

      if (op1.length () == 0)
         throw new NumberFormatException ("Operand 1 is a null string!");
      if (op2.length () == 0)
         throw new NumberFormatException ("Operand 2 is a null string!");

      oop1 = normNum (1, op1);
      oop2 = normNum (2, op2);

      /* computation */

      oprod = internalMult (oop1, oop2);

      return (new String (oprod));
  }

  /**
   *   Operand validation and normalization (removal of left zeros).
   *   An operand should only consist of decimal numbers. Left zeros are removed because they do not affect the result of
   *   the operation.
   *
   *   @param opN operand order (1 or 2)
   *   @param op operand
   *
   *   @return operand as a normalized number
   */

   private static char [] normNum (int opN, String op) throws NumberFormatException
   {
      char [] numNorm = null;                    // normalized operand
      int nCar;                                  // n. de caracteres do operando
      int nZeroPos;                              // position of the first left digit different from zero
                                                 // number of zeros to the left
      char alg;                                  // decimal digit

      /* assert the position of the first left digit different from zero */

      nCar = op.length ();
      nZeroPos = 0;
      for (int i = 0; i < nCar; i++)
      { alg = op.charAt (i);
        if (!Character.isDigit (alg))
           throw new NumberFormatException ("Operand " + opN + " is not a decimal number!");
           else if (Character.digit (alg, 10) == 0)
                   nZeroPos += 1;
                   else { numNorm = new char [nCar - nZeroPos];
                          numNorm[i - nZeroPos] = alg;
                          break;
                        }
      }

      /* storage of the remaining decimal digits of the operand into an array of characters */

      if (numNorm == null)
         { numNorm = new char [1];
           numNorm[0] = '0';
         }
         else for (int i = nZeroPos + 1, ii = 1; i < nCar; i++, ii++)
              { alg = op.charAt (i);
                if (!Character.isDigit (alg))
                   throw new NumberFormatException ("Operand " + opN + " is not a decimal number!");
                   else numNorm[ii] = alg;
              }

      return (numNorm);
   }

  /**
   *   Addition of two decimal numbers (expressed as an array of characters).
   *
   *   @param op1 operand 1
   *   @param op2 operand 2
   *
   *   @return sum
   */

   private static char [] internalAdd (char [] op1, char [] op2)
   {
      /* assert the number of decimal digits of the sum */

      int sumSize = (op1.length >= op2.length) ? op1.length + 1 : op2.length + 1;

      /* operand and result normalization */

      char [] top1, top2,                        // operands
              sum = new char[sumSize];           // sum

      if (op1.length >= op2.length)
         { top1 = op1;
           top2 = op2;
         }
         else { top1 = op2;
                top2 = op1;
              }

      /* computation */

      Operand top;                               // single digit representation of the operands
      Result tresu = null;                       // single digit representation of the result
      char pv;                                   // carry out

      for (int i = 0, i1 = top1.length-i-1, i2 = top2.length-i-1, is = sum.length-i-1;
           i < top2.length; i++, i1--, i2--, is--)
      { pv = (i == 0) ? '0' : tresu.pvo;
        top = new Operand (top1[i1], top2[i2], pv);
        tresu = DigitOper.add (top);
        sum[is] = tresu.res;
      }
      for (int i = top2.length, i1 = top1.length-i-1, is = sum.length-i-1;
           i < top1.length; i++, i1--, is--)
      { pv = tresu.pvo;
        top = new Operand (top1[i1], '0', pv);
        tresu = DigitOper.add (top);
        sum[is] = tresu.res;
      }
      sum[0] = (tresu.ipvo == 0) ? ' ' : tresu.pvo;

      return (sum);
   }

  /**
   *   Addition of two decimal numbers (expressed as an array of characters) with sum accumulation in operand 1.
   *
   *   @param n1 number of digits of operand 1
   *   @param op1 operand 1 / sum
   *   @param n2 number of digits of operand 2
   *   @param op2 operand 2
   */

   private static void internalAddSp (int n1, char [] op1, int n2, char [] op2)
   {
      /* computation */

      Operand top;                               // single digit representation of the operands
      Result tresu = null;                       // single digit representation of the result
      char pv;                                   // carry out

      for (int i = 0, i1 = n1-i-1, i2 = n2-i-1;
           i < n2; i++, i1--, i2--)
      { pv = (i == 0) ? '0' : tresu.pvo;
        top = new Operand (op1[i1], op2[i2], pv);
        tresu = DigitOper.add (top);
        op1[i1] = tresu.res;
      }
   }

  /**
   *   Multiplication of two decimal numbers (expressed as an array of characters).
   *
   *   @param op1 operand 1
   *   @param op2 operand 2
   *
   *   @return product
   */

   private static char [] internalMult (char [] op1, char [] op2)
   {
      /* assert the number of decimal digits of the product */

      int prodSize = op1.length + op2.length;

      /* operand and result normalization */

      char [] top1, top2,                        // operands
              prod = new char[prodSize];         // product

      if (op1.length >= op2.length)
         { top1 = op1;
           top2 = op2;
         }
         else { top1 = op2;
                top2 = op1;
              }
      for (int i = 0; i < prod.length; i++)
        prod[i] = '0';

      /* computation */

      for (int i = 0, i2 = top2.length-i-1, ip = prod.length-i-1;
           i < top2.length; i++, i2--, ip--)
        internalAddSp (prodSize-i, prod, top1.length+1, internalMultSp (top1, top2[i2]));

      for (int i = 0; i < prodSize-1; i++)
        if (prod[i] == '0')
           prod[i] = ' ';
           else break;

      return (prod);
   }

  /**
   *   Multiplication of a decimal number by a digit (expressed, respectively, as an array of characters and a digit).
   *
   *   @param op1 operand 1 (decimal number)
   *   @param op2 operand 2 (digit)
   *
   *   @return product
   */

   private static char [] internalMultSp (char [] op1, char op2)
   {
      /* assert the number of decimal digits of the product */

      int prodSize = op1.length + 1;
      char [] prod = new char[prodSize];         // product

      /* realização da operação */

      Operand top;                               // single digit representation of the operand
      Result tresu = null;                       // single digit representation of the result
      char pv;                                   // carry out

      for (int i = 0, i1 = op1.length-i-1, ip = prod.length-i-1;
           i < op1.length; i++, i1--, ip--)
      { pv = (i == 0) ? '0' : tresu.pvo;
        top = new Operand (op1[i1], op2, pv);
        tresu = DigitOper.mult (top);
        prod[ip] = tresu.res;
      }
      prod[0] = tresu.pvo;

      return (prod);
   }
}
