import genclass.GenericIO;
import TowersHanoi.*;

/**
 *    Problem of Towers Of Hanoi solved through two different approaches.
 *    Both a recursive and an iterative solution are implemented.
 */

public class ProbTowersOfHanoi
{
  /**
   *  Enumerated data type associated with menu.
   */

   private static enum MenuOpt {RECUR, REP, END};

  /**
   *    Main program, implemented by the <code>main</code> method of the data type.
   *
   *      @param args runtime parameter list
   */

   public static void main (String [] args)
   {
     GenericIO.writelnString ();
     GenericIO.writelnString ("            Problem of Towers of Hanoi");
     GenericIO.writelnString ();

     String [] menu = {"1 - Recursive solution",            // menu
                       "2 - Iterative solution",
                       "0 - Program exit"
                      };
     int opt;                                              // selected option
     MenuOpt [] choice = {MenuOpt.END,                     // listing of the options
                          MenuOpt.RECUR,
                          MenuOpt.REP
                         };
     boolean end = false;                                  // signaling end of operations

     do
     {
       do
       { GenericIO.writelnString ();
         for (int i = 0; i < menu.length; i++)             // menu display
         { GenericIO.writeFormChar (10, ' ');
           GenericIO.writelnString (menu[i]);
         }
         GenericIO.writelnString ();
         GenericIO.writeString ("What is your choice? ");
         opt = GenericIO.readlnInt ();
       } while ((opt < 0) || (opt >= menu.length));

       switch (choice [opt])
       { case RECUR:
         case REP:   int val;                              // number of disks in the pile
                     TowersOfHanoi tH;                     // solution configuration

                     GenericIO.writelnString ();
                     do
                     { GenericIO.writeString ("Number of disks in the pile? ");
                       val = GenericIO.readlnInt ();
                     } while ((val <= 0) || (val > 20));   // keeping number of disks within
                                                           // reasonable limits
                     switch (choice [opt])
                     { case RECUR: tH = new RecursiveSolution (val);
                                   break;
                       case REP:   tH = new IterativeSolution (val);
                                   break;
                       default:    tH = null;
                     }
                     GenericIO.writelnString ();
                     tH.problemSolving ();
                     break;
         case END:   end = true;
       }
     } while (!end);
  }
}
