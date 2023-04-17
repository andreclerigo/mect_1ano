package sharedRegions;

import commInfra.*;
import entities.*;
import main.SimulPar;

/**
 *   Concentration site.
 *   In this shared region, the ordinary thieves wait for the master thief to prepare an assault party and send them to the museum.
 */
public class ConcetSite
{
    /**
     *   The id of assault party to be sent.
     */
    private int assaultPartyId;

    /**
     *   Reference to the assault parties array.
     */
    private AssaultParty [] assaultParties;

    /**
     *   Ordinary thieves waiting to go on an excursion.
     */
    private MemFIFO<Integer> waitingThieves;

    /**
     *   Global variable that indicates if the heist is over.
     */
    private boolean endOfHeist;

    /**
     *  Reference to the museum.
     */
    private final Museum museum;

    /**
     *  Reference to the general repository of information.
     */
    private final GeneralRepos repos;

    /**
     *   Instantiation of a concentration site object.
     *
     *     @param assaultParties array of assault parties
     *     @param museum reference to the museum
     *     @param repos reference to the general repository of information
     */
    public ConcetSite (AssaultParty [] assaultParties, Museum museum, GeneralRepos repos)
    {
        try {
            waitingThieves = new MemFIFO<>(new Integer [SimulPar.ORDINARY_THIEVES + 1]);
        } catch (MemException e) {
            e.printStackTrace();
        }
        assaultPartyId = -1;
        endOfHeist = false;
        this.assaultParties = assaultParties;
        this.museum = museum;
        this.repos = repos;
    }

    /**
     *   Set the ID of the assault party
     *
     *     @param assaultPartyId
     */
    public void setAssaultPartyId (int assaultPartyId) {
        this.assaultPartyId = assaultPartyId;
    }

    /**
     *   Get the ID of the assault party
     *
     *     @return assaultPartyId
     */
    public int getAssaultPartyId ()
    {
        return assaultPartyId;
    }

    /**
     *   Operations has enough thieves, the master thief checks if the waiting queue has enough thieves to assemble an assault party.
     *
     *     @return true if the waiting queue is greater than or equal to the maximum number of thieves
     */
    public boolean hasEnoughThieves ()
    {
        return waitingThieves.getNumOfElements() >= SimulPar.MAX_PARTY_THIEVES;
    }

    /**
     *   Operation prepare assault party, the thief checks if the waiting queue has enough thieves to assemble an assault party.
     *
     *     @param assaultPartyId id of the assault party to be prepared
     *     @param roomId id of the exhibition room assigned to the assault party
     */
    public void prepareAssaultParty (int assaultPartyId, int roomId)
    {
        synchronized (this) {
            ((MasterThief) Thread.currentThread()).setMasterThiefState(MasterThiefStates.ASSEMBLING);
            repos.setMasterThiefState(MasterThiefStates.ASSEMBLING);
            repos.setAssaultPartyTargetRoomId(assaultPartyId, roomId);

            while (!hasEnoughThieves()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.assaultPartyId = assaultPartyId;
            assaultParties[this.assaultPartyId].setTargetRoomId(roomId, museum.getRoomDistance(roomId));

            // Pop 3 thieves from the queue and add them to the assault party
            try {
                for (int _i = 0; _i < SimulPar.MAX_PARTY_THIEVES; _i++) {
                    int tId = waitingThieves.read();
                }
            } catch (MemException e) {
                e.printStackTrace();
            }

            // Notify thieves popped from the queue
            notifyAll();
        }

        assaultParties[this.assaultPartyId].waitForThievesBeReady();
        // Assault party is assembled, the auxiliary variable is not needed anymore and must be reset for the next prepare assault party
        this.assaultPartyId = -1;
    }

    /**
     *   Operation prepare excursion, the thief will join the party.
     *
     *     @return the id of the assault party id.
     */
    public synchronized int prepareExcursion ()
    {
        int localAssaultPartyId = this.assaultPartyId;
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        assaultParties[localAssaultPartyId].addThief(thief.getOrdinaryThiefId(), thief.getDisplacement());
        //fornecer que nao tem canvas
        repos.setAssaultPartyThievesId(thief.getOrdinaryThiefId(), localAssaultPartyId);
        thief.setOrdinaryThiefState(OrdinaryThiefStates.CRAWLING_INWARDS);
        repos.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CRAWLING_INWARDS);
        // a invocao ao repos tem que ser aqui tanto no addThief como no setOrdinaryThief
        return localAssaultPartyId;
    }

    /**
     *   Operation that decides if the ordinary thief is needed. It will register the thief in the waiting queue and wait for the master thief to pop him from the queue.
     *
     *     @return either if the heist is over or not
     */
    public synchronized boolean amINeeded ()
    {
        if (endOfHeist) return false;
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();

        // Add the thief to the queue of thieves waiting to be sent to an excursion
        try {
            waitingThieves.write(thief.getOrdinaryThiefId());
            // they start in concentration site
            if (thief.getOrdinaryThiefState() != OrdinaryThiefStates.CONCENTRATION_SITE) {
                thief.setOrdinaryThiefState(OrdinaryThiefStates.CONCENTRATION_SITE);
                repos.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CONCENTRATION_SITE);
            }
        } catch (MemException e) {
            e.printStackTrace();
        }

        if (hasEnoughThieves()) notifyAll();

        // Waiting for the master thief to popped him from the waiting queue
        while (waitingThieves.elementExists(thief.getOrdinaryThiefId())) {
            try {
                wait();
                if (endOfHeist) return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     *   Operation sum up results.
     */
    public synchronized void sumUpResults ()
    {
        endOfHeist = true;
        repos.setMasterThiefState(MasterThiefStates.PRESENTING_REPORT);
        notifyAll();
        repos.printTail();
    }
}
