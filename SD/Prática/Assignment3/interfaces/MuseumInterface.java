package interfaces;

import java.rmi.*;


/**
 *   Operational interface of a remote object of type Museum.
 *     It provides the functionality to access the Museum.
 */
public interface MuseumInterface extends Remote
{
    /**
     *   Operation reverse direction, the state of the ordinary thief is changed to crawling outwards.
     *
     *     @param ordinaryThiefId the id of the ordinary thief that we are in
     *     @return the ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int reverseDirection (int ordinaryThiefId) throws RemoteException;

    /**
     *   Get the distance of the room from the concentration site.
     *
     *     @param roomId the id of the room that we are in
     *     @return the distance of the room from the concentration site
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getRoomDistance (int roomId) throws RemoteException;

    /**
     *   Operation roll a canvas, check if there are any canvas in the room and if there are, take one.
     *
     *     @param roomId the id of the room that we are in
     *     @param assaultPartyId the id of the assault party that we are in
     *     @param ordinaryThiefId the id of the ordinary thief
     *     @return the ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public  int rollACanvas (int roomId, int assaultPartyId, int ordinaryThiefId) throws RemoteException;

    /**
     *   Operation Museum server shutdown
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void shutdown () throws RemoteException;
}
