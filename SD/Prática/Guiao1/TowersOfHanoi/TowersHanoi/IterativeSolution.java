package TowersHanoi;

import genclass.GenericIO;

/**
 *    Iterative solution of the problem of Towers of Hanoi.
 */

public class IterativeSolution implements TowersOfHanoi
{
  /**
   *  Number of disks in the pile.
   */
   private int nDisks;

  /**
   *  Left pole.
   */
   private Tower<Integer> a;

   /**
   *  Center pole.
   */
   private Tower<Integer> b;

   /**
   *  Right pole.
   */
   private Tower<Integer> c;

   /**
   *  Number of iterations.
   */
   private int nIter;

  /**
   *  Solution instantiation.
   *  The required disk configuration is inserted in the left pole.
   *
   *     @param nDisks - number of disks in the pile (it should be larger than 0)
   *     @throws MemException i the number of disks is not larger than 0
   */

   public IterativeSolution (int nDisks)
   {
     try
     { if (nDisks <= 0) throw new MemException ("Illegal number of disks!");
       this.nDisks = nDisks;
       a = new Tower<> (new Integer [nDisks]);
       b = new Tower<> (new Integer [nDisks]);
       c = new Tower<> (new Integer [nDisks]);
       for (int i = nDisks-1; i >= 0; i--)                 // disks are inserted in increasing
                                                           //  diameter order
       { try
         { a.write (i);
         }
         catch (MemException e)
         { GenericIO.writelnString ("Error: ", e.getMessage (), " in iteration " + (i+1));
           e.printStackTrace ();
           System.exit (1);
         }
       }
     }
     catch (MemException e)
     { GenericIO.writelnString ("Error: ", e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
   }

  /**
   *  Problem solution.
   *  It implements the interface method problemSolving.
   */

   @Override
   public void problemSolving ()
   {
     GenericIO.writelnString ("            Listing of movements");
     GenericIO.writelnString ();
     showState ();
     moveDisks (nDisks, a, b, c);
   }

  /**
   *  Moving the specified of number disks from the source pole to the destination pole.
   *
   *    @param n number of disks to move
   *    @param a source pole
   *    @param b destination pole
   *    @param c auxiliary pole
   */

   private void moveDisks (int n, Tower<Integer> a, Tower<Integer> b, Tower<Integer> c)
   {
     Tower<Integer> sx = a,                                // source pole during the iteration process
                    dx = ((n % 2) == 0) ? c : b,           // destination pole during the iteration process
                    xx = ((n % 2) == 0) ? b : c;           // auxiliary pole during the iteration process
     int nMov;                                             // total number of movements
     int dim = (n + (n % 2)) / 2 - 1;                      // number of points of rotation
     int [] turn = new int [dim];                          // reference values for rotation
     boolean rot;                                          // signaling a rotation should take place

     nMov = (1 << n) - 1;
     for (int j = 0; j < dim; j++)
       if (j == 0)
          turn[0] = 8;
          else turn[j] = 4 * turn[j-1];

     Tower<Integer> tx;                                         // temporary pole

     for (int i = 1; i <= nMov; i++)
     { /* base movement */
       moveOneDisk (sx, dx);
       /* pole permutation */
       rot = false;
       for (int j = dim-1; j >= 0; j--)
       { rot = (((i + 1) % turn[j]) == (turn[j] / 2) || (i % turn[j]) == (turn[j] / 2));
         if (rot) break;
       }
       if (!rot && ((i % 2) == 0) || ((i % 4) == 2))            // sx <-> xx
          { tx = sx;
            sx = xx;
            xx = tx;
          }
          else if (!rot && ((i % 2) == 1) || ((i % 4) == 1))    // dx <-> xx
                  { tx = dx;
                    dx = xx;
                    xx = tx;
                  }
                  else { tx = sx;                               // sx -> dx -> xx -> sx
                         sx = xx;
                         xx = dx;
                         dx = tx;
                    }
     }
   }

  /**
   *  Moving one disk from the source pole to the destination pole.
   *
   *    @param a source pole
   *    @param b destination pole
   */

   private void moveOneDisk (Tower<Integer> a, Tower<Integer> b)
   {
     try
     { b.write (a.read ());
     }
     catch (MemException e)
     { GenericIO.writelnString ("Error: ", e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     showState ();
   }

  /**
   *  Description of the present state of the problem.
   */

   private void showState ()
   {
     Integer [] val = new Integer[3];                      // array of stored values
     boolean isValue;                                      // signaling the line has values
     Tower<Integer> w;                                     // pole whose contents is being examined

     GenericIO.writelnString ("Iteration " + nIter);
     GenericIO.writelnString ();
     for (int i = nDisks-1; i >= 0; i--)
     { isValue = false;
       for (int j = 0; j < 3; j++)
       { try
         { w = (j == 0) ? a : (j == 1) ? b : c;
           val[j] = w.peek (i);
           isValue = isValue || (val[j] != null);
         }
         catch (MemException e)
         { GenericIO.writelnString ("Error: ", e.getMessage (), " in iteration ", "(" + i + "," + j + ")" );
           e.printStackTrace ();
           System.exit (1);
         }
       }
       if (isValue)
          { for (int j = 0; j < 3; j++)
              if (val[j] != null)
                 GenericIO.writeFormInt (6, val[j]);
                 else GenericIO.writeFormChar (6, ' ');
            GenericIO.writelnString ();
          }
     }
     GenericIO.writelnString ("\n     A     B     C\n");
     nIter += 1;
   }
}
