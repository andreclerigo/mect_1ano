package serverSide;

import interfaces.Compute;
import interfaces.Task;

/**
 *  Generic functionality that will run locally code transferred to it.
 *
 *  Communication is based in Java RMI.
 */

public class ComputeEngine implements Compute
{
  /**
   *  Local execution of remote code.
   *
   *     @param t code to be locally executed
   *     @return return value of the invocation of method execute of t
   *     @throws RemoteException if the invocation of the remote method fails
   */

   @Override
   public Object executeTask (Task t)
   {
     return t.execute ();
   }
}
