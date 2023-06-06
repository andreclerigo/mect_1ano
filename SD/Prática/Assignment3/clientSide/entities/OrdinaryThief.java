package clientSide.entities;

import interfaces.*;

import java.rmi.RemoteException;

/**
 *    Ordinary Thief thread.
 *      It simulates the ordinary thief cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on remote calls under Java RMI.
 */
public class OrdinaryThief extends Thread
{
    /**
     *   Thief identification.
     */
    private final int ordinaryThiefId;

    /**
     *   OrdinaryThief state.
     */
    private int ordinaryThiefState;

    /**
     *   Remote reference to the assault parties.
     */
    private final AssaultPartyInterface [] assaultPartiesStubs;

    /**
     *   The Thief's agility (2 to 6).
     */
    private final int ordinaryThiefAgility;

    /**
     *   Remote reference to the museum.
     */
    private final MuseumInterface museumStub;

    /**
     *   Remote reference to the control site.
     */
    private final ControlSiteInterface controlSiteStub;

    /**
     *   Remote reference to the concentration site.
     */
    private final ConcetSiteInterface concetSiteStub;

    /**
     *   Instantiation of a OrdinaryThief thread.
     *
     *     @param name thread name
     *     @param ordinaryThiefId Ordinary Thief id
     *     @param agility Ordinary Thief agility
     *     @param museumStub remote reference to the museum
     *     @param controlSiteStub remote reference to the control site
     *     @param concetSiteStub remote reference to the concentration site
     *     @param assaultPartiesStubs remote reference to the assault parties
     */
    public OrdinaryThief (String name, int ordinaryThiefId, int agility, MuseumInterface museumStub, ControlSiteInterface controlSiteStub,
                          ConcetSiteInterface concetSiteStub, AssaultPartyInterface[] assaultPartiesStubs)
    {
        super (name);
        this.ordinaryThiefId = ordinaryThiefId;
        this.museumStub = museumStub;
        this.controlSiteStub = controlSiteStub;
        this.concetSiteStub = concetSiteStub;
        this.assaultPartiesStubs = assaultPartiesStubs;
        ordinaryThiefState = OrdinaryThiefStates.CONCENTRATION_SITE;
        ordinaryThiefAgility = agility;
    }

    /**
     *   Get ordinary thief id.
     *
     *     @return ordinary thief id
     */
    public int getOrdinaryThiefId ()
    {
        return ordinaryThiefId;
    }

    /**
     *   Set ordinary thief state.
     *
     *     @param state new ordinary thief state
     */
    public void setOrdinaryThiefState (int state)
    {
        ordinaryThiefState = state;
    }

    /**
     *   Get ordinary thief state.
     *
     *     @return ordinary thief state
     */
    public int getOrdinaryThiefState (){
        return ordinaryThiefState;
    }

    /**
     *   Get the thief's max agility or displacement.
     *
     *     @return the thief's displacement value
     */
    public int getDisplacement ()
    {
        return ordinaryThiefAgility;
    }

    /**
     *   Life cycle of the ordinary thief.
     */
    @Override
    public synchronized void run ()
    {
        int assaultPartyId;

        while (amINeeded()) {
            assaultPartyId = prepareExcursion();
            crawlIn(assaultPartyId);
            rollACanvas(getTargetRoomId(assaultPartyId), assaultPartyId);
            reverseDirection();
            crawlOut(assaultPartyId);
            handACanvas(assaultPartyId);
        }
    }

    /**
     *   Operation that decides if the ordinary thief is needed. It will register the thief in the waiting queue and wait for the master thief to pop him from the queue.
     *   Remote Operation.
     *
     *     @return either if the heist is over or not
     */
    private boolean amINeeded ()
    {
        ReturnBoolean ret = null;
        try {
            ret = concetSiteStub.amINeeded(ordinaryThiefId, ordinaryThiefState);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on amINeeded: " + e.getMessage ());
            System.exit (1);
        }
        ordinaryThiefState = ret.getIntStateVal();

        return ret.getBooleanVal();
    }

    /**
     *   Operation prepare excursion, the thief will join the party.
     *   Remote Operation.
     *
     *     @return the id of the assault party
     */
    private int prepareExcursion ()
    {
        ReturnInt ret = null;
        try {
            ret = concetSiteStub.prepareExcursion(ordinaryThiefId, ordinaryThiefAgility);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on prepareExcursion: " + e.getMessage ());
            System.exit (1);
        }
        ordinaryThiefState = ret.getIntStateVal();

        return ret.getIntVal();
    }

    /**
     *   Operation crawl in.
     *   Remote Operation.
     *
     *     @param assaultPartyId the identification of the assault party
     */
    private void crawlIn (int assaultPartyId)
    {
        try {
            ordinaryThiefState = assaultPartiesStubs[assaultPartyId].crawlIn(ordinaryThiefId);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on crawlIn: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Get the id of the room assigned to this assault party
     *   Remote Operation.
     *
     *    @return the id of the room assigned to this assault party
     *
     */
    private int getTargetRoomId (int assaultPartyId)
    {
        int targetRoomId = -1;
        try {
            targetRoomId = assaultPartiesStubs[assaultPartyId].getTargetRoomId();
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on getTargetRoomId: " + e.getMessage ());
            System.exit (1);
        }

        return targetRoomId;
    }

    /**
     *   Operation roll a canvas, check if there are any canvas in the room and if there are, take one.
     *   Remote Operation.
     *
     *     @param roomId the id of the room that we are in
     *     @param assaultPartyId the id of the assault party that we are in
     */
    private void rollACanvas (int roomId, int assaultPartyId)
    {
        try {
            ordinaryThiefState = museumStub.rollACanvas(roomId, assaultPartyId, ordinaryThiefId);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on rollACanvas: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Operation reverse direction, the state of the ordinary thief is changed to crawling outwards.
     *   Remote Operation.
     */
    private void reverseDirection ()
    {
        try {
            ordinaryThiefState = museumStub.reverseDirection(ordinaryThiefId);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on reverseDirection: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Operation crawl out.
     *   Remote Operation.
     *
     *     @param assaultPartyId the identification of the assault party
     */
    private void crawlOut (int assaultPartyId)
    {
        try {
            ordinaryThiefState = assaultPartiesStubs[assaultPartyId].crawlOut(ordinaryThiefId);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on crawlOut: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Operation hand a canvas, add the thief id, assault party id and bringsCanvas flag to the waiting thieves queue.
     *   Remote Operation.
     *
     *     @param assaultPartyId id of the assault party
     */
    private void handACanvas (int assaultPartyId)
    {
        try {
            controlSiteStub.handACanvas(ordinaryThiefId, assaultPartyId);
        } catch (RemoteException e) {
            System.out.println("Ordinary Thief " + ordinaryThiefId + "remote exception on handACanvas: " + e.getMessage ());
            System.exit (1);
        }
    }
}
