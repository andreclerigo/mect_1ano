package clientSide.entities;

import clientSide.stubs.*;

/**
 *   OrdinaryThief thread.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
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
     *   Reference to the assault parties.
     */
    private final AssaultPartyStub [] assaultPartiesStubs;

    /**
     *   The Thief's agility (2 to 6).
     */
    private final int ordinaryThiefAgility;

    /**
     *   Reference to the museum.
     */
    private final MuseumStub museumStub;

    /**
     *   Reference to the control site.
     */
    private final ControlSiteStub controlSiteStub;

    /**
     *   Reference to the concentration site.
     */
    private final ConcetSiteStub concetSiteStub;

    /**
     *   Instantiation of a OrdinaryThief thread.
     *
     *     @param name thread name
     *     @param ordinaryThiefId Ordinary Thief id
     *     @param agility Ordinary Thief agility
     *     @param museumStub reference to the museum
     *     @param controlSiteStub reference to the control site
     *     @param concetSiteStub reference to the concentration site
     *     @param assaultPartiesStubs reference to the assault parties
     */
    public OrdinaryThief (String name, int ordinaryThiefId, int agility, MuseumStub museumStub, ControlSiteStub controlSiteStub, ConcetSiteStub concetSiteStub,
                          AssaultPartyStub [] assaultPartiesStubs)
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

        while (concetSiteStub.amINeeded()) {
            assaultPartyId = concetSiteStub.prepareExcursion();
            assaultPartiesStubs[assaultPartyId].crawlIn();
            museumStub.rollACanvas(assaultPartiesStubs[assaultPartyId].getTargetRoomId(), assaultPartyId);
            museumStub.reverseDirection();
            assaultPartiesStubs[assaultPartyId].crawlOut();
            controlSiteStub.handACanvas(assaultPartyId);
        }
    }
}
