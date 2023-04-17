package entities;

import sharedRegions.AssaultParty;
import sharedRegions.ConcetSite;
import sharedRegions.ControlSite;

/**
 *   Master thief thread.
 *   It simulates the master thief life cycle.
 *   Static solution.
 */
public class MasterThief extends Thread
{
    /**
     *   Master thief state.
     */
    private int masterThiefState;

    /**
     *   Reference to the assault parties.
     */
    private final AssaultParty [] assaultParties;

    /**
     *   Reference to the control site.
     */
    private final ControlSite controlSite;

    /**
     *   Reference to the concentration site.
     */
    private final ConcetSite concetSite;

    /**
     *   Instantiation of a master thief thread.
     *
     *     @param name thread name
     *     @param controlSite reference to the control site
     *     @param concetSite reference to the concentration site
     *     @param assaultParties reference to the assault parties
     */
    public MasterThief (String name, ControlSite controlSite, ConcetSite concetSite, AssaultParty [] assaultParties)
    {
        super (name);
        masterThiefState = MasterThiefStates.PLANNING_HEIST;
        this.controlSite = controlSite;
        this.concetSite = concetSite;
        this.assaultParties = assaultParties;
    }

    /**
     *   Set master thief state.
     *
     *     @param state new master thief state
     */
    public void setMasterThiefState (int state)
    {
        masterThiefState = state;
    }

    /**
     *   Get master thief state.
     *
     *     @return master thief state
     */
    public int getMasterThiefState ()
    {
        return masterThiefState;
    }

    /**
     *   Life cycle of the master thief.
     */
    @Override
    public void run ()
    {
        int assaultPartyId;
        char oper;
        // Master Thief starts the heist in the control and collection site
        controlSite.startOperations();

        /* while master thief state isn't PRESENTING_REPORT run the life cycle */
        while ((oper = controlSite.appraiseSit()) != 'E') {
            switch (oper) {
                case ('P'):
                    assaultPartyId = controlSite.getAssaultId();
                    concetSite.prepareAssaultParty(assaultPartyId, controlSite.getRoomId());
                    assaultParties[assaultPartyId].sendAssaultParty();
                    break;
                case ('R'):
                    controlSite.takeARest();
                    controlSite.collectACanvas();
                    break;
            }
        }
        // The heist ended and the Master Thief will sum up the results to the Thieves
        concetSite.sumUpResults();
    }
}
