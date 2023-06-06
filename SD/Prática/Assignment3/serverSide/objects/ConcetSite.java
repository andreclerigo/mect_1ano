package serverSide.objects;

import java.rmi.registry.*;
import java.rmi.*;
import interfaces.*;
import serverSide.main.*;
import clientSide.entities.*;
import commInfra.*;

/**
 *   Concentration site.
 *   In this shared region, the ordinary thieves wait for the master thief to prepare an assault party and send them to the museum.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on Java RMI.
 */
public class ConcetSite implements ConcetSiteInterface
{
    /**
     *   The id of assault party to be sent.
     */
    private int assaultPartyId;

    /**
     *   Remote reference to the assault parties array.
     */
    private AssaultPartyInterface [] assaultPartiesStubs;

    /**
     *   Ordinary thieves waiting to go on an excursion.
     */
    private MemFIFO<Integer> waitingThieves;

    /**
     *   Global variable that indicates if the heist is over.
     */
    private boolean endOfHeist;

    /**
     *  Remote reference to the museum.
     */
    private final MuseumInterface museumStub;

    /**
     *  Remote reference to the general repository of information.
     */
    private final GeneralReposInterface reposStub;

    /**
     *   Number of entities that requested shutdown
     */
    private int nEntities;

    /**
     *   Instantiation of a concentration site object.
     *
     *     @param assaultPartyStubs array of assault parties
     *     @param museumStub remote reference to the museumStub
     *     @param reposStub remote reference to the general repository of information
     */
    public ConcetSite (AssaultPartyInterface [] assaultPartyStubs, MuseumInterface museumStub, GeneralReposInterface reposStub)
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
    @Override
    public void setAssaultPartyId (int assaultPartyId) {
        this.assaultPartyId = assaultPartyId;
    }

    /**
     *   Get the ID of the assault party
     *
     *     @return assaultPartyId
     */
    @Override
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
    @Override
    public int prepareAssaultParty (int assaultPartyId, int roomId)
    {
        int masterThiefState;
        masterThiefState = MasterThiefStates.ASSEMBLING;
        synchronized (this) {
            try {
                reposStub.setMasterThiefState(MasterThiefStates.ASSEMBLING);
            } catch (RemoteException e) {
                System.out.println("Remote exception in prepareAssaultParty while setting master thief's state: " + e.getMessage());
                System.exit (1);
            }

            try {
                reposStub.setAssaultPartyTargetRoomId(assaultPartyId, roomId);
            } catch (RemoteException e) {
                System.out.println("Remote exception in prepareAssaultParty while setting the target room id: " + e.getMessage());
                System.exit (1);
            }

            while (!hasEnoughThieves()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.assaultPartyId = assaultPartyId;
            try {
                assaultPartiesStubs[this.assaultPartyId].setTargetRoomId(roomId, museumStub.getRoomDistance(roomId));
            } catch (RemoteException e) {
                System.out.println("Remote exception in prepareAssaultParty while setting the target room id: " + e.getMessage());
                System.exit (1);
            }

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

        try {
            assaultPartiesStubs[this.assaultPartyId].waitForThievesBeReady();
        } catch (RemoteException e) {
            System.out.println("Remote exception in prepareAssaultParty while waiting for thieves to be ready: " + e.getMessage());
            System.exit (1);
        }

        // Assault party is assembled, the auxiliary variable is not needed anymore and must be reset for the next prepare assault party
        this.assaultPartyId = -1;
        return masterThiefState;
    }

    /**
     *   Operation prepare excursion, the thief will join the party.
     *
     *     @param ordinaryThiefId id of the ordinary thief
     *     @param ordinaryThiefAgility agility of the ordinary thief
     *     @return the id of the assault party id.
     */
    @Override
    public synchronized ReturnInt prepareExcursion (int ordinaryThiefId, int ordinaryThiefAgility)
    {
        int localAssaultPartyId = this.assaultPartyId;

        try {
            assaultPartiesStubs[localAssaultPartyId].addThief(ordinaryThiefId, ordinaryThiefAgility);
        } catch (RemoteException e) {
            System.out.println("Remote exception in prepareExcursion while adding a thief: " + e.getMessage());
            System.exit (1);
        }

        try {
            reposStub.setAssaultPartyThievesId(ordinaryThiefId, localAssaultPartyId);
        } catch (RemoteException e) {
            System.out.println("Remote exception in prepareExcursion while setting the id of the thieves in the assault party: " + e.getMessage());
            System.exit (1);
        }

        try {
            reposStub.setOrdinaryThiefState(ordinaryThiefId, OrdinaryThiefStates.CRAWLING_INWARDS);
        } catch (RemoteException e) {
            System.out.println("Remote exception in prepareExcursion while setting the state of the thieves in the assault party: " + e.getMessage());
            System.exit (1);
        }

        return new ReturnInt(localAssaultPartyId, OrdinaryThiefStates.CRAWLING_INWARDS);
    }

    /**
     *   Operation that decides if the ordinary thief is needed. It will register the thief in the waiting queue and wait for the master thief to pop him from the queue.
     *
     *     @param ordinaryThiefId id of the ordinary thief
     *     @param ordinaryThiefState state of the ordinary thief
     *     @return either if the heist is over or not
     */
    @Override
    public synchronized ReturnBoolean amINeeded (int ordinaryThiefId, int ordinaryThiefState)
    {
        if (endOfHeist) return new ReturnBoolean(false, OrdinaryThiefStates.CONCENTRATION_SITE);

        // Add the thief to the queue of thieves waiting to be sent to an excursion
        try {
            waitingThieves.write(ordinaryThiefId);
            // they start in concentration site
            // pass as parameter the ordinary thief's state and its ID
            if (ordinaryThiefState != OrdinaryThiefStates.CONCENTRATION_SITE) {
                try{
                    reposStub.setOrdinaryThiefState(ordinaryThiefId, OrdinaryThiefStates.CONCENTRATION_SITE);
                } catch (RemoteException e){
                    System.out.println("Remote exception in amINeeded while setting the state of the thieves in the assault party: " + e.getMessage());
                    System.exit (1);
                }
            }
        } catch (MemException e) {
            e.printStackTrace();
        }

        if (hasEnoughThieves()) notifyAll();

        // Waiting for the master thief to popped him from the waiting queue
        while (waitingThieves.elementExists(ordinaryThiefId)) {
            try {
                wait();
                if (endOfHeist) return new ReturnBoolean(false, OrdinaryThiefStates.CONCENTRATION_SITE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // return boolean and state
        return new ReturnBoolean(true, OrdinaryThiefStates.CONCENTRATION_SITE);
    }

    /**
     *   Operation sum up results.
     *
     *   @return the state of the master thief
     */
    @Override
    public synchronized int sumUpResults ()
    {
        int masterThiefState;
        endOfHeist = true;
        try {
            masterThiefState = MasterThiefStates.PRESENTING_REPORT;
            reposStub.setMasterThiefState(MasterThiefStates.PRESENTING_REPORT);
        } catch (RemoteException e) {
            System.out.println("Remote exception in sumUpResults while setting master thief state: " + e.getMessage());
            System.exit (1);
            return -1;
        }

        try {
            reposStub.printTail();
        } catch (RemoteException e) {
            System.out.println("Remote exception in sumUpResults while printing tail: " + e.getMessage());
            System.exit (1);
            return -1;
        }

        notifyAll();

        return masterThiefState;
    }

    /**
     *   Operation Concentration Site server shutdown
     */
    @Override
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1){
            ServerHeistToMuseumConcetSite.shutdown();
        }

        notifyAll();
    }
}
