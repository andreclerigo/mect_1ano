package interfaces;

import java.rmi.*;

/**
 *   Operational interface of a remote object of type GeneralRepos.
 *
 *     It provides the functionality to access the General Repository of Information.
 */

public interface GeneralReposInterface extends Remote
{
  /**
   *   Operation initialization of simulation.
   *
   *   New operation.
   *
   *     @param logFileName name of the logging file
   *     @param nIter number of iterations of the customer life cycle
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public void initSimul (String logFileName, int nIter) throws RemoteException;

  /**
   *   Operation server shutdown.
   *
   *   New operation.
   *
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public void shutdown () throws RemoteException;

  /**
   *   Set barber state.
   *
   *     @param id barber id
   *     @param state barber state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public void setBarberState (int id, int state) throws RemoteException;

  /**
   *   Set customer state.
   *
   *     @param id customer id
   *     @param state customer state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public void setCustomerState (int id, int state) throws RemoteException;

  /**
   *   Set barber and customer state.
   *
   *     @param barberId barber id
   *     @param barberState barber state
   *     @param customerId customer id
   *     @param customerState customer state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public void setBarberCustomerState (int barberId, int barberState, int customerId, int customerState) throws RemoteException;
}
