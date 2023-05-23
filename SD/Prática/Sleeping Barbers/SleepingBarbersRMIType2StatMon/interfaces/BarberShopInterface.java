package interfaces;

import java.rmi.*;

/**
 *   Operational interface of a remote object of type BarberShop.
 *
 *     It provides the functionality to access the Barber Shop.
 */

public interface BarberShopInterface extends Remote
{

  /**
   *  Operation go cut the hair.
   *
   *  It is called by a customer when he goes to the barber shop to try and cut his hair.
   *
   *     @param customerId identification of the customer
   *     @return true, if he did manage to cut his hair -
   *             false, otherwise
   *             and his state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public ReturnBoolean goCutHair (int customerId) throws RemoteException;

  /**
   *  Operation go to sleep.
   *
   *  It is called by a barber while waiting for customers to be serviced.
   *
   *     @param barberId identification of the barber
   *     @return true, if his life cycle has come to an end -
   *             false, otherwise
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public boolean goToSleep (int barberId) throws RemoteException;

  /**
   *  Operation call a customer.
   *
   *  It is called by a barber if a customer has requested his service.
   *
   *     @param barberId identification of the barber
   *     @return customer id and the barber state
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public ReturnInt callACustomer (int barberId) throws RemoteException;

  /**
   *  Operation receive payment.
   *
   *  It is called by a barber after finishing the customer hair cut.
   *
   *     @param barberId identification of the barber
   *     @param customerId identification of the customer
   *     @return state of the barber
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public int receivePayment (int barberId, int customerId) throws RemoteException;

  /**
   *  Operation end of work.
   *
   *   New operation.
   *
   *      @param barbId barber id
   *      @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                              service fails
   */

   public void endOperation (int barberId) throws RemoteException;

  /**
   *   Operation server shutdown.
   *
   *   New operation.
   *
   *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
   *                             service fails
   */

   public void shutdown () throws RemoteException;
}
