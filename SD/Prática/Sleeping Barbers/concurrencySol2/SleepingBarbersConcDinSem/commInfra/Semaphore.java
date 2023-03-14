package commInfra;

/**
 *    Dijkstra semaphore built as an implicit monitor.
 */

public class Semaphore
{
  /**
   *  Type of light indicator.
   *
   *  Red state upon instantiation.
   */

   private int val = 0;

  /**
   *  Number of threads currently blocked at the semaphore.
   *
   *  No thread is blocked upon instantiation.
   */

   private int numbBlockThreads = 0;

  /**
   *  Operation down.
   */

   public synchronized void down ()
   {
     if (val == 0)
        { numbBlockThreads += 1;
          try
          { wait ();
          }
          catch (InterruptedException e) {}
        }
        else val -= 1;
   }

  /**
   *  Operation up.
   */

   public synchronized void up ()
   {
     if (numbBlockThreads != 0)
        { numbBlockThreads -= 1;
          notify ();
        }
        else val += 1;
   }
}
