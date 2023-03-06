import genclass.GenericIO;
import FloatingPointOperations.*;

/**
 *    Testbench for the basic arithmetic operations having as operands real / complex double precision
 *    floating point numbers.
 *
 *    The following operations are tested
 *     - addition
 *     - subtraction
 *     - multiplication
 *     - division.
 */

public class TestElemOper

{
  /**
   *    Main program, implemented by the <code>main</code> method of the data type.
   *
   *      @param args runtime parameter list
   */

   public static void main (String [] args)
   {
     Value op1,                                        // operand 1
           op2,                                        // operand 2
           res;                                        // result
     Real rop1,                                        // operand 1 as a real number
          rop2;                                        // operand 2 as a real number
     Complex cop1,                                     // operand 1 as a complex number
             cop2;                                     // operand 2 as a complex number
     char toper;                                       // operation type
     boolean done;                                     // signaling a choice was made
     double [] v;                                      // temporary variable used for value input
     String str;                                       // operation designation

     /* reading the operands */

     done = false;
     do
     { GenericIO.writelnString ();
       GenericIO.writelnString ("Type of operand 1");
       GenericIO.writelnString ("  1 - Real\n  2 - Complex");
       GenericIO.writeString ("What is your choice? ");
       toper = GenericIO.readlnChar ();
       if ((toper == '1') || (toper == '2')) done = true;
     } while (!done);
     if (toper == '1')
        { GenericIO.writeString ("Value? ");
          rop1 = new Real (GenericIO.readlnDouble ());
          op1 = rop1;
        }
        else { GenericIO.writeString ("Real part value / Imaginary part value? ");
               v = GenericIO.readlnNDouble (2);
               cop1 = new Complex (v[0], v[1]);
               op1 = cop1;
             }

     done = false;
     do
     { GenericIO.writelnString ();
       GenericIO.writelnString ("Type of operand 2");
       GenericIO.writelnString ("  1 - Real\n  2 - Complex");
       GenericIO.writeString ("What is your choice? ");
       toper = GenericIO.readlnChar ();
       if ((toper == '1') || (toper == '2')) done = true;
     } while (!done);
     if (toper == '1')
        { GenericIO.writeString ("Value? ");
          rop2 = new Real (GenericIO.readlnDouble ());
          op2 = rop2;
        }
        else { GenericIO.writeString ("Real part value / Imaginary part value? ");
               v = GenericIO.readlnNDouble (2);
               cop2 = new Complex (v[0], v[1]);
               op2 = cop2;
             }

     /* selecting the operation */

     done = false;
     do
     { GenericIO.writelnString ();
       GenericIO.writelnString ("Type of operation");
       GenericIO.writelnString ("  1 - Addition\n  2 - Subtraction\n  3 - Multiplication\n  4 - Division");
       GenericIO.writeString ("Qual é a sua escolha? ");
       toper = GenericIO.readlnChar ();
       done = (toper == '1') || (toper == '2') || (toper == '3') || (toper == '4');
     } while (!done);

     /* computation and printing the result */

     switch (toper)
     { case '1': str = "The sum is ";
                 res = ElemOper.add (op1, op2);
                 break;
       case '2': str = "The difference is ";
                 res = ElemOper.sub (op1, op2);
                 break;
       case '3': str = "The product is ";
                 res = ElemOper.mult (op1, op2);
                 break;
       case '4': str = "The quotient is ";
                 res = ElemOper.div (op1, op2);
                 break;
       default:  str = "";
                 res = null;
     }
     GenericIO.writelnString ();
     GenericIO.writelnString (str + res.printVal () + ".");
     GenericIO.writelnString ();
  }
}
