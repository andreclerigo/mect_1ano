package interfaces;

import java.rmi.*;


/**
 *   Operational interface of a remote object of type Assault Party.
 *     It provides the functionality to access the Assault Party.
 *     Communication is based in Java RMI.
 */
public interface AssaultPartyInterface extends Remote
{
    /**
     *   Operation crawl in.
     *
     *     @param ordinaryThiefId the id of the ordinary thief
     *     @return the ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int crawlIn (int ordinaryThiefId) throws RemoteException;

    /**
     *   Operation crawl out.
     *
     *     @param ordinaryThiefId the id of the ordinary thief
     *     @return the ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int crawlOut (int ordinaryThiefId) throws RemoteException;

    /**
     *  Operation send assault party, we set the next thief to be the first one and give him position 0
     *
     *     @return the master thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int sendAssaultParty () throws RemoteException;

    /**
     *  Add a thief to the assault party to a position that has the id -1
     *
     *    @param ordinaryThiefId the id of the thief to add
     *    @param ordinaryThiefDis the displacement of the thief to add
     *    @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                            service fails
     */
    public void addThief (int ordinaryThiefId, int ordinaryThiefDis) throws RemoteException;

    /**
     *   Master Thief waits for the thieves to fill the assault party
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void waitForThievesBeReady () throws RemoteException;

    /**
     *   Auxiliary function to get the index of the thief in the arrays used by giving the thief id
     *
     *     @param ordinaryThiefId the id of the thief
     *     @return the index of the thief in the arrays used
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int ordinaryThiefIndex (int ordinaryThiefId) throws RemoteException;

    /**
     *   Get the id of the assault party
     *
     *     @return the id of the assault party
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getAssaultPartyId () throws RemoteException;

    /**
     *   See if the assault party is in operation
     *
     *     @return true if the assault party is in operation
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public boolean getInOp () throws RemoteException;

    /**
     *   Set the assault party to be in operation
     *
     *     @param inOp true if the assault party is in operation
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setInOp (boolean inOp) throws RemoteException;

    /**
     *   Get the id of the room assigned to this assault party
     *
     *     @return the id of the room assigned to this assault party
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getTargetRoomId () throws RemoteException;

    /**
     *   Set the distance from the concentration site to the room
     *
     *     @param targetRoomId the id of the room assigned to this assault party
     *     @param roomDistance the distance from the concentration site to the room
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                            service fails
     */
    public void setTargetRoomId (int targetRoomId, int roomDistance) throws RemoteException;

    /**
     *   Get the number of thieves in the assault party
     *
     *     @return the number of thieves in the assault party
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int numOfThieves () throws RemoteException;

    /**
     *   Leve the assault party by setting the id of thief in array as -1.
     *
     *     @param thiefId the id of the thief that leaves the assault party
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void leaveTheAssaultParty (int thiefId) throws RemoteException;

    /**
     *   See if the thief has the canvas
     *
     *     @param thiefId the id of the thief
     *     @return true if the thief has the canvas
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public boolean getBringsCanvas (int thiefId) throws RemoteException;

    /**
     *   Set if the thief has the canvas
     *
     *     @param thiefId the id of the thief
     *     @param canvas true if the thief has the canvas
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setBringsCanvas (int thiefId, boolean canvas) throws RemoteException;

    /**
     *   Operation Concentration Site server shutdown
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void shutdown () throws RemoteException;
}
