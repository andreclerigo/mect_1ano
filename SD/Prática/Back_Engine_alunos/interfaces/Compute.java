package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *   Operational interface of a remote object of type ComputeEngine.
 *
 *   Objects of type Task which are passed as a parameter of the method executeTask, are run locally.
 */

public interface Compute extends Remote
{
  /**
   *  Execution of remote code.
   *
   *     @param t code to be executed remotely
   *     @return return value of the invocation of method execute of t
   *     @throws RemoteException if the invocation of the remote method fails
   */

   Object executeTask (Task t) throws RemoteException;
}
