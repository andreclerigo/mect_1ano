package interfaces;

import java.rmi.*;

/**
 *   General Repository.
 *   It is responsible to keep the visible internal state of the problem and to provide means for it to be printed in the logging file.
 *   It is implemented as an implicit monitor.
 *   All public methods are executed in mutual exclusion.
 *   There are no internal synchronization points.
 */
public interface GeneralReposInterface extends Remote
{
    /**
     *   Set ordinary thief state.
     *
     *     @param id ordinary thief id
     *     @param state ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setOrdinaryThiefState (int id, int state) throws RemoteException;

    /**
     *   Set master thief state.
     *
     *     @param state master thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public  void setMasterThiefState (int state) throws RemoteException;

    /**
     *   Set ordinary thief position while crawling to the museum
     *
     *     @param id ordinary thief id
     *     @param pos position of the thief (0 is the concentration/control site, to Distance to the room assigned for its assault party)
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setOrdinaryThiefPos (int id, int pos) throws RemoteException;

    /**
     *   Update number of paintings in a room of the museum.
     *
     *     @param roomId room id
     *     @param nCanvas number of paintings in the room
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setUpdateInRoom (int roomId, int nCanvas) throws RemoteException;

    /**
     *   Set flag that indicates if the thief has a canvas in his possession or not.
     *
     *     @param id ordinary thief id
     *     @param canvas boolean that indicates if the thief has a canvas in his possession or not
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setBringsCanvas (int id, boolean canvas) throws RemoteException;

    /**
     *   Action of the ordinary thief to give a canvas to the master thief.
     *
     *     @param id ordinary thief id
     *     @param canvas boolean that indicates if the thief has a canvas in his possession or not
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void giveCanvas (int id, boolean canvas) throws RemoteException;

    /**
     *   Set assault party id.
     *
     *     @param thiefId thief party id
     *     @param partyId assault party id
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setAssaultPartyThievesId (int thiefId, int partyId) throws RemoteException;

    /**
     *   Set paintings per room.
     *
     *   @param paintingsPerRoom number of paintings per room
     *   @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setPaintingsPerRoom(int[] paintingsPerRoom) throws RemoteException;

    /**
     *   Set distance to each room.
     *
     *   @param distancePerRoom distance to each room
     *   @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setDistancePerRoom(int[] distancePerRoom) throws RemoteException;

    /**
     *   Set Agility of each ordinary thief.
     *
     *   @param agility agility of each ordinary thief
     *   @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setOrdinaryThievesAgility(int[] agility) throws RemoteException;

    /**
     *   Set ID of assigned room to each assault party.
     *
     *     @param id assault party id
     *     @param roomId room id
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setAssaultPartyTargetRoomId (int id, int roomId) throws RemoteException;

    /**
     *   Set all canvas that were stolen.
     *
     *     @param canvas number of canvas that were stolen
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setCanvasResult (int canvas) throws RemoteException;

    /**
     *  Write the final state of the simulation at the end of the logging file.
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void printTail () throws RemoteException;

    /**
     *   Operation General Repository server shutdown
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void shutdown() throws RemoteException;

}




