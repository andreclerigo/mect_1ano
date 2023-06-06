package clientSide.entities;

import java.rmi.*;
import interfaces.*;


/**
 *    Master Thief thread.
 *      It simulates the master thief cycle.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on remote calls under Java RMI.
 */
public class MasterThief extends Thread
{
    /**
     *   Master thief state.
     */
    private int masterThiefState;

    /**
     *   Remote reference to the assault parties.
     */
    private final AssaultPartyInterface [] assaultPartiesStubs;

    /**
     *   Remote reference to the stub control site.
     */
    private final ControlSiteInterface controlSiteStub;

    /**
     *   Remote reference to the stub of concentration site.
     */
    private final ConcetSiteInterface concetSiteStub;

    /**
     *   Instantiation of a master thief thread.
     *
     *     @param name thread name
     *     @param controlSiteStub remote reference to the control site
     *     @param concetSiteStub remote reference to the concentration site
     *     @param assaultPartiesStubs remote reference to the assault parties
     */
    public MasterThief (String name, ControlSiteInterface controlSiteStub, ConcetSiteInterface concetSiteStub, AssaultPartyInterface [] assaultPartiesStubs)
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
        startOperations();

        /* while master thief state isn't PRESENTING_REPORT run the life cycle */
        while ((oper = appraiseSit()) != 'E') {
            switch (oper) {
                case ('P'):
                    assaultPartyId = getAssaultId();
                    prepareAssaultParty(assaultPartyId, getRoomId());
                    sendAssaultParty(assaultPartyId);
                    break;
                case ('R'):
                    takeARest();
                    collectACanvas();
                    break;
            }
        }
        // The heist ended and the Master Thief will sum up the results to the Thieves
        sumUpResults();
    }

    /**
     *    Operation send assault party, we set the next thief to be the first one and give him position 0.
     *    Remote Operation.
     *
     *      @param assaultPartyId the identification of the assault party
     */
    private void sendAssaultParty (int assaultPartyId)
    {
        try {
            masterThiefState = assaultPartiesStubs[assaultPartyId].sendAssaultParty();
        } catch (RemoteException e) {
            System.out.println("Master Thief remote exception on sendAssaultParty: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Start the heist to the museum operation.
     *   Remote Operation.
     */
    private void startOperations ()
    {
        try {
            masterThiefState = controlSiteStub.startOperations();
        } catch (RemoteException e) {
            System.out.println("Master Thief remote exception on startOperations: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Operation appraise sit. If there is not any room left and all thieves arrived then the assault has ended.
     *   Otherwise, we see if there is any assault party available, if there is the Master Thief assembles a new excursion.
     *   Remote Operation.
     *
     *     @return the int correspondent of the operation.
     */
    private char appraiseSit ()
    {
        char oper = '?';
        try {
            oper = controlSiteStub.appraiseSit();
        } catch (RemoteException e) {
            System.out.println("Master Thief remote exception on appraiseSit: " + e.getMessage ());
            System.exit (1);
        }

        return oper;
    }

    /**
     *   Operation prepare assault party, the thief checks if the waiting queue has enough thieves to assemble an assault party.
     *   Remote Operation.
     *
     *     @param assaultPartyId id of the assault party to be prepared
     *     @param roomId id of the exhibition room assigned to the assault party
     */
    private void prepareAssaultParty (int assaultPartyId, int roomId)
    {
        try {
            masterThiefState = concetSiteStub.prepareAssaultParty(assaultPartyId, roomId);
        } catch (RemoteException e) {
            System.out.println("Master Thief remote exception on prepareAssaultParty: " + e.getMessage ());
            System.exit (1);
        }
    }

    /**
     *   Get the id of the next assault party to be used (should never return -1 because this function is only called when there is at least 1 assault party available).
     *   Remote Operation.
     *
     *     @return id of assault party
     */
    private int getAssaultId ()
    {
        int assaultId = -1;
        try {
            assaultId = controlSiteStub.getAssaultId();
        } catch (RemoteException e) {
            System.out.println("Master Thief remote exception on getAssaultId: " + e.getMessage ());
            System.exit (1);
        }

        return assaultId;
    }

    /**
     *   Get the id of an exhibition room that is not empty and is not assigned to an assault party (should never return -1 because this function is only called when at least 1 room is available)..
     *   Remote Operation.
     *
     *     @return id of exhibition room
     */
    private int getRoomId ()
    {
        int roomId = -1;
        try {
            roomId = controlSiteStub.getRoomId();
        } catch (RemoteException e) {
            System.out.println("Master Thief remote exception on getRoomId: " + e.getMessage ());
            System.exit (1);
        }

        return roomId;
    }

    /**
     *   Operation take a rest, change the master thief state and wait for thieves to go arrive to the waiting queue.
     *   Remote Operation.
     */
    private void takeARest ()
    {
        try{
            masterThiefState = controlSiteStub.takeARest();
        } catch (RemoteException e){
            System.out.println("Master Thief remote exception on takeARest: " + e.getMessage());
        }

    }

    /**
     *   Operation collect a canvas, the master thief retrieves the information from the FIFO and updates the empty rooms and the number of canvas.
     *   The thief "leaves" the assault party and if there are no more thieves in the assault party, the assault party leaves the operation.
     *   Remote Operation.
     */
    private void collectACanvas ()
    {
        try{
            masterThiefState = controlSiteStub.collectACanvas();
        } catch (RemoteException e){
            System.out.println("Master Thief remote exception on collectACanvas: " + e.getMessage());
        }

    }

    /**
     *   Operation sum up results.
     *   Remote Operation.
     */
    private void sumUpResults ()
    {
        try{
            masterThiefState = concetSiteStub.sumUpResults();
        } catch (RemoteException e){
            System.out.println("Master Thief remote exception on sumUpResults: " + e.getMessage());
        }
    }
}
