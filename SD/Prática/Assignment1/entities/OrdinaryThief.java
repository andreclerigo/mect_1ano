package entities;

import main.SimulPar;
import sharedRegions.*;
import sharedRegions.ConcetSite;

/**
 *   OrdinaryThief thread.
 *   It simulates the OrdinaryThief life cycle.
 *   Static solution.
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
    private final AssaultParty [] assaultParties;

    /**
     *   The Thief's agility (2 to 6).
     */
    private final int ordinaryThiefAgility;

    /**
     *   Reference to the museum.
     */
    private final Museum museum;

    /**
     *   Reference to the control site.
     */
    private final ControlSite controlSite;

    /**
     *   Reference to the concentration site.
     */
    private final ConcetSite concetSite;

    /**
     *   Instantiation of a OrdinaryThief thread.
     *
     *     @param name thread name
     *     @param ordinaryThiefId Ordinary Thief id
     *     @param agility Ordinary Thief agility
     *     @param museum reference to the museum
     *     @param controlSite reference to the control site
     *     @param concetSite reference to the concentration site
     *     @param assaultParties reference to the assault parties
     */
    public OrdinaryThief (String name, int ordinaryThiefId, int agility, Museum museum, ControlSite controlSite, ConcetSite concetSite, AssaultParty [] assaultParties)
    {
        super (name);
        this.ordinaryThiefId = ordinaryThiefId;
        this.museum = museum;
        this.controlSite = controlSite;
        this.concetSite = concetSite;
        this.assaultParties = assaultParties;
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

        while (concetSite.amINeeded()) {
            assaultPartyId = concetSite.prepareExcursion();
            assaultParties[assaultPartyId].crawlIn();
            museum.rollACanvas(assaultParties[assaultPartyId].getTargetRoomId(), assaultPartyId);
            museum.reverseDirection();
            assaultParties[assaultPartyId].crawlOut();
            controlSite.handACanvas(assaultPartyId);
        }
    }
}
