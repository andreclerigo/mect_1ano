package clientSide.entities;

import clientSide.stubs.*;


/**
 *   Master thief thread.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
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
    private final AssaultPartyStub [] assaultPartiesStubs;

    /**
     *   Reference to the stub control site.
     */
    private final ControlSiteStub controlSiteStub;

    /**
     *   Reference to the stub of concentration site.
     */
    private final ConcetSiteStub concetSiteStub;

    /**
     *   Instantiation of a master thief thread.
     *
     *     @param name thread name
     *     @param controlSiteStub reference to the control site
     *     @param concetSiteStub reference to the concentration site
     *     @param assaultPartiesStubs reference to the assault parties
     */
    public MasterThief (String name, ControlSiteStub controlSiteStub, ConcetSiteStub concetSiteStub, AssaultPartyStub [] assaultPartiesStubs)
    {
        super (name);
        masterThiefState = MasterThiefStates.PLANNING_HEIST;
        this.controlSiteStub = controlSiteStub;
        this.concetSiteStub = concetSiteStub;
        this.assaultPartiesStubs = assaultPartiesStubs;
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
        controlSiteStub.startOperations();

        /* while master thief state isn't PRESENTING_REPORT run the life cycle */
        while ((oper = controlSiteStub.appraiseSit()) != 'E') {
            switch (oper) {
                case ('P'):
                    assaultPartyId = controlSiteStub.getAssaultId();
                    concetSiteStub.prepareAssaultParty(assaultPartyId, controlSiteStub.getRoomId());
                    assaultPartiesStubs[assaultPartyId].sendAssaultParty();
                    break;
                case ('R'):
                    controlSiteStub.takeARest();
                    controlSiteStub.collectACanvas();
                    break;
            }
        }
        // The heist ended and the Master Thief will sum up the results to the Thieves
        concetSiteStub.sumUpResults();
    }
}
