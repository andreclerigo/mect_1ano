package interfaces;

import java.rmi.*;


/**
 *   Operational interface of a remote object of type Concentration Site.
 *     It provides the functionality to access the Concentration Site.
 */
public interface ConcetSiteInterface extends Remote
{
    /**
     *   Set the ID of the assault party
     *
     *     @param assaultPartyId id of the assault party
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void setAssaultPartyId (int assaultPartyId) throws RemoteException;

    /**
     *   Get the ID of the assault party
     *
     *     @return assaultPartyId
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int getAssaultPartyId () throws RemoteException;

    /**
     *   Operation prepare assault party, the thief checks if the waiting queue has enough thieves to assemble an assault party.
     *
     *     @param assaultPartyId id of the assault party to be prepared
     *     @param roomId id of the exhibition room assigned to the assault party
     *     @return return the state of the master thief
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     *
     */
    public int prepareAssaultParty (int assaultPartyId, int roomId) throws RemoteException;

    /**
     *   Operation prepare excursion, the thief will join the party.
     *
     *     @param ordinaryThiefId id of the ordinary thief
     *     @param ordinaryThiefAgility agility of the ordinary thief
     *     @return the id of the assault party and the ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public ReturnInt prepareExcursion (int ordinaryThiefId, int ordinaryThiefAgility) throws RemoteException;

    /**
     *   Operation that decides if the ordinary thief is needed. It will register the thief in the waiting queue and wait for the master thief to pop him from the queue.
     *
     *     @param ordinaryThiefId id of the ordinary thief
     *     @param ordinaryThiefState state of the ordinary thief
     *     @return either if the heist is over or not and the ordinary thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     *
     */
    public ReturnBoolean amINeeded (int ordinaryThiefId, int ordinaryThiefState) throws RemoteException;

    /**
     *   Operation sum up results.
     *
     *     @return the master thief state
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public int sumUpResults () throws RemoteException;

    /**
     *   Operation Concentration Site server shutdown
     *
     *     @throws RemoteException if either the invocation of the remote method, or the communication with the registry
     *                             service fails
     */
    public void shutdown () throws RemoteException;
}
