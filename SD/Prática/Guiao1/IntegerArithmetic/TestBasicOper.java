import genclass.GenericIO;
import FixPointOperations.BasicOper;

/**
 *    Testbench for the basic arithmetic operations having as operands decimal integers with a variable number of digits.
 *
 *    The following operations are tested
 *     - addition
 *     - multiplication.
 */

public class TestBasicOper

{
  /**
   *    Main program, implemented by the <code>main</code> method of the data type.
   *
   *      @param args runtime parameter list
   */

   public static void main (String [] args)
   {
     String op1,                                       // operand 1 as a stringipo
            op2,                                       // operand 2 as a string
            res;                                       // result as a string
     char toper;                                       // type of operation

     GenericIO.writelnString ();
     GenericIO.writeString ("Operand 1? ");
     op1 = GenericIO.readlnString ();
     if (op1 == null) op1 = "";
     GenericIO.writeString ("Operand 2? ");
     op2 = GenericIO.readlnString ();
     if (op2 == null) op2 = "";
     GenericIO.writelnString ("\n     1 - Addition\n     2 - Multiplication");
     GenericIO.writeString ("\n     What is your choice? ");
     toper = GenericIO.readlnChar ();
     GenericIO.writelnString ();
     try
     { if (toper == '1')
          { res = BasicOper.add (op1, op2);
            GenericIO.writelnString ("The sum is " + res + ".");
          }
          else { res = BasicOper.mult (op1, op2);
                 GenericIO.writelnString ("The product is " + res + ".");
               }
     }
     catch (NumberFormatException e)
     { e.printStackTrace ();
       GenericIO.writelnString ();
       System.exit (1);
     }
   }
}
