package interfaces;

import java.rmi.*;


/**
 *   Operational interface of a remote object of type Control Site.
 *     It provides the functionality to access the Control Site.
 */
public interface ControlSiteInterface extends Remote
{
    /**
     *   Start the heist to the museum operation.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     *     @return the master thief state
     */
    public int startOperations () throws RemoteException;

    /**
     *   Operation appraise sit. If there is not any room left and all thieves arrived then the assault has ended.
     *   Otherwise, we see if there is any assault party available, if there is the Master Thief assembles a new excursion.
     *
     *     @return the char correspondent of the operation
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public  char appraiseSit () throws RemoteException;

    /**
     *   Get the id of the next assault party to be used (should never return -1 because this function is only called when there is at least 1 assault party available).
     *
     *     @return id of assault party
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getAssaultId () throws RemoteException;

    /**
     *   Set the number of canvas collected
     *
     *     @param numOfCanvas number of canvas collected
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setNumOfCanvas(int numOfCanvas) throws RemoteException;

    /**
     *   Get the number of canvas collected
     *
     *     @return numOfCanvas
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getNumOfCanvas() throws RemoteException;

    /**
     *   Get the id of an exhibition room that is not empty and is not assigned to an assault party (should never return -1 because this function is only called when at least 1 room is available)..
     *
     *     @return id of exhibition room
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getRoomId () throws RemoteException;

    /**
     *   Operation collect a canvas, the master thief retrieves the information from the FIFO and updates the empty rooms and the number of canvas.
     *   The thief "leaves" the assault party and if there are no more thieves in the assault party, the assault party leaves the operation.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     *     @return the master thief state
     */
    public int collectACanvas () throws RemoteException;

    /**
     *   Operation hand a canvas, add the thief id, assault party id and bringsCanvas flag to the waiting thieves queue.
     *
     *     @param assaultPartyId id of the assault party
     *     @param ordinaryThiefId id of the ordinary thief
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void handACanvas (int ordinaryThiefId, int assaultPartyId) throws RemoteException;

    /**
     *   Operation take a rest, change the master thief state and wait for thieves to go arrive to the waiting queue.
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     *     @return the master thief state
     */
    public int takeARest () throws RemoteException;

    /**
     *   Operation Control Site server shutdown
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void shutdown () throws RemoteException;
}
