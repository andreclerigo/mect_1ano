package serverSide.sharedRegions;

import commInfra.MemException;
import commInfra.MemFIFO;
import serverSide.main.ServerHeistToMuseumConcetSite;
import serverSide.main.SimulPar;
import serverSide.entities.ConcetSiteClientProxy;
import clientSide.stubs.AssaultPartyStub;
import clientSide.stubs.GeneralReposStub;
import clientSide.stubs.MuseumStub;
import clientSide.entities.OrdinaryThiefStates;
import clientSide.entities.MasterThiefStates;

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
    private AssaultPartyStub [] assaultPartiesStubs;

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
    private final MuseumStub museumStub;

    /**
     *  Reference to the general repository of information.
     */
    private final GeneralReposStub reposStub;

    /**
     *   Number of entities that requested shutdown
     */
    private int nEntities;

    /**
     *   Instantiation of a concentration site object.
     *
     *     @param assaultPartyStubs array of assault parties
     *     @param museumStub reference to the museumStub
     *     @param reposStub reference to the general repository of information
     */
    public ConcetSite (AssaultPartyStub [] assaultPartyStubs, MuseumStub museumStub, GeneralReposStub reposStub)
    {
        try {
            waitingThieves = new MemFIFO<>(new Integer [SimulPar.ORDINARY_THIEVES + 1]);
        } catch (MemException e) {
            e.printStackTrace();
        }
        assaultPartyId = -1;
        endOfHeist = false;
        this.assaultPartiesStubs = assaultPartyStubs;
        this.museumStub = museumStub;
        this.reposStub = reposStub;
        this.nEntities = 0;
    }

    /**
     *   Set the ID of the assault party
     *
     *     @param assaultPartyId id of the assault party
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
    private boolean hasEnoughThieves ()
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
            ((ConcetSiteClientProxy) Thread.currentThread()).setMasterThiefState(MasterThiefStates.ASSEMBLING);
            reposStub.setMasterThiefState(MasterThiefStates.ASSEMBLING);
            reposStub.setAssaultPartyTargetRoomId(assaultPartyId, roomId);

            while (!hasEnoughThieves()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.assaultPartyId = assaultPartyId;
            assaultPartiesStubs[this.assaultPartyId].setTargetRoomId(roomId, museumStub.getRoomDistance(roomId));

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

        assaultPartiesStubs[this.assaultPartyId].waitForThievesBeReady();
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
        ConcetSiteClientProxy thief = (ConcetSiteClientProxy) Thread.currentThread();
        assaultPartiesStubs[localAssaultPartyId].addThief(thief.getOrdinaryThiefId(), thief.getOrdinaryThiefAgility());
        //fornecer que nao tem canvas
        reposStub.setAssaultPartyThievesId(thief.getOrdinaryThiefId(), localAssaultPartyId);
        thief.setOrdinaryThiefState(OrdinaryThiefStates.CRAWLING_INWARDS);
        reposStub.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CRAWLING_INWARDS);
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
        ConcetSiteClientProxy thief = (ConcetSiteClientProxy) Thread.currentThread();

        // Add the thief to the queue of thieves waiting to be sent to an excursion
        try {
            waitingThieves.write(thief.getOrdinaryThiefId());
            // they start in concentration site
            if (thief.getOrdinaryThiefState() != OrdinaryThiefStates.CONCENTRATION_SITE) {
                thief.setOrdinaryThiefState(OrdinaryThiefStates.CONCENTRATION_SITE);
                reposStub.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CONCENTRATION_SITE);
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
        reposStub.setMasterThiefState(MasterThiefStates.PRESENTING_REPORT);
        reposStub.printTail();
        notifyAll();
    }

    /**
     *   Operation Concentration Site server shutdown
     */
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1){
            ServerHeistToMuseumConcetSite.waitConnection = false;
        }

        notifyAll();
    }

}
