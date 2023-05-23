package serverSide.sharedRegions;

import serverSide.main.ServerHeistToMuseumAssaultParty;
import serverSide.main.SimulPar;
import clientSide.stubs.GeneralReposStub;
import clientSide.entities.OrdinaryThiefStates;
import clientSide.entities.MasterThiefStates;
import serverSide.entities.AssaultPartyClientProxy;

import java.util.Arrays;

/**
 *   Assault party.
 *   In this shared region, the assault party crawls to the room and collects the canvas if there is one.
 */
public class AssaultParty
{
    /**
     *   The assault party id.
     */
    private final int assaultPartyId;

    /**
     *   The id of the next thief that will try to crawl.
     */
    private int nextThiefId;

    /**
     *   The room assigned to this assault party.
     */
    private int targetRoomId;

    /**
     *   The distance from the concentration site to the room.
     */
    private int roomDistance;

    /**
     *   The assault party is in operation.
     */
    private boolean inOp;

    /**
     *   The number of thieves at the room.
     */
    private int thievesAtRoom;

    /**
     *   Ids of the ordinary thieves in the assault party (initially -1).
     */
    private int [] ordinaryThievesId;

    /**
     *   Position of the ordinary thieves in the assault party.
     */
    private int [] ordinaryThievesPos;

    /**
     *   Displacement of the ordinary thieves in the assault party.
     */
    private int [] ordinaryThievesDis;

    /**
     *   If the ordinary thief has a canvas or not.
     */
    private boolean [] ordinaryThievesCanvas;

    /**
     *   Reference to the general repository of information.
     */
    private final GeneralReposStub reposStub;

    /**
     *   Number of entities that requested shutdown.
     */
    private int nEntities = 0;

    /**
     *   Assault party instantiation.
     *
     *     @param assaultPartyId assault party id
     *     @param targetRoomId room assigned to this assault party
     *     @param reposStub reference to the general repository of information
     */
    public AssaultParty (int assaultPartyId, int targetRoomId, GeneralReposStub reposStub)
    {
        this.reposStub = reposStub;
        this.assaultPartyId = assaultPartyId;
        this.targetRoomId = targetRoomId;
        ordinaryThievesId = new int [SimulPar.MAX_PARTY_THIEVES];
        ordinaryThievesPos = new int [SimulPar.MAX_PARTY_THIEVES];
        ordinaryThievesDis = new int [SimulPar.MAX_PARTY_THIEVES];
        ordinaryThievesCanvas = new boolean [SimulPar.MAX_PARTY_THIEVES];
        Arrays.fill(ordinaryThievesId, -1);
        Arrays.fill(ordinaryThievesPos, -1);
        Arrays.fill(ordinaryThievesCanvas, false);
        thievesAtRoom = 0;
        nextThiefId = -1;
        inOp = false;
    }

    /**
     *  Operation crawl in.
     */
    public synchronized void crawlIn ()
    {
        AssaultPartyClientProxy thief = (AssaultPartyClientProxy) Thread.currentThread();
        thief.setOrdinaryThiefState(OrdinaryThiefStates.CRAWLING_INWARDS);
        int displacement;
        int thiefIndex = ordinaryThiefIndex(thief.getOrdinaryThiefId());

        while (true) {
            while (thief.getOrdinaryThiefId() != nextThiefId) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while ((displacement = canICrawl(thiefIndex)) != -1) {
                if (ordinaryThievesPos[thiefIndex] == -1) ordinaryThievesPos[thiefIndex] = 0;
                ordinaryThievesPos[thiefIndex] += displacement;
                reposStub.setOrdinaryThiefPos(thief.getOrdinaryThiefId(), ordinaryThievesPos[thiefIndex]);
            }

            // Call the next thief
            nextThiefId = ordinaryThievesId[(thiefIndex + 1) % 3];
            notifyAll();

            if (ordinaryThievesPos[thiefIndex] == roomDistance) {
                thievesAtRoom++;
                break;
            }
        }

        thief.setOrdinaryThiefState(OrdinaryThiefStates.AT_A_ROOM);
    }

    /**
     *   Can I Crawl operation, it will return the displacement if the thief can crawl, or -1 if it can't
     *
     *     @param thiefIndex the thief index on the arrays
     *     @return the displacement value
     */
    private int canICrawl (int thiefIndex)
    {
        for (int dis = ordinaryThievesDis[thiefIndex]; dis > 0; dis--) {
            if (checkValidPos(thiefIndex, dis)) return dis;
        }
        return -1;
    }

    /**
     *   Check valid position will check if the new position follows the following rules:
     *   - The thieves can't be in the same position (unless it is the room or the beginning)
     *   - The Thieves can't be apart more than 3 positions
     *   - The thieves can't go beyond the room distance
     *
     *     @param thiefIndex the thief index on the arrays
     *     @param displacement the displacement value to test (lesser or equal to the thief's displacement)
     *     @return true if the position is valid
     */
    private boolean checkValidPos (int thiefIndex, int displacement)
    {
        int max, mid, min;
        int [] auxArrayPos = Arrays.copyOf(ordinaryThievesPos, ordinaryThievesPos.length);
        for (int i = 0; i < auxArrayPos.length; i++)  if (auxArrayPos[i] == -1) auxArrayPos[i] = 0;
        auxArrayPos[thiefIndex] += displacement;

        int [] auxArrayPosSorted = Arrays.copyOf(auxArrayPos, auxArrayPos.length);
        Arrays.sort(auxArrayPosSorted);
        min = auxArrayPosSorted[0];
        mid = auxArrayPosSorted[1];
        max = auxArrayPosSorted[2];

        if ((max == mid) && (max != roomDistance) && (max != 0)) return false;
        if ((min == mid) && (min != roomDistance) && (min != 0)) return false;

        // If the pos goes beyond the room distance, return false
        if (auxArrayPos[thiefIndex] > roomDistance) return false;

        // If the distance between the thieves is greater than the max distance, return false
        if ((max - mid > SimulPar.MAX_THIEVES_DISTANCE) || (mid - min > SimulPar.MAX_THIEVES_DISTANCE))
            return false;
        else
            return true;
    }

    /**
     *   Operation crawl out.
     *
     *     @param thiefIndex the thief index on the arrays
     *     @return the displacement value
     */
    private int canICrawlOut (int thiefIndex)
    {
        for (int dis = ordinaryThievesDis[thiefIndex]; dis > 0; dis--) {
            if (checkValidPosOut(thiefIndex, dis)) return dis;
        }
        return -1;
    }

    /**
     *   Check valid position will check if the new position follows the following rules:
     *   - The thieves can't be in the same position (unless it is the room or the beginning)
     *   - The Thieves can't be apart more than 3 positions
     *   - The thieves can't go beyond the control site
     *
     *     @param thiefIndex the thief index on the arrays
     *     @param displacement the displacement value to test (lesser or equal to the thief's displacement)
     *     @return true if the position is valid
     */
    private boolean checkValidPosOut (int thiefIndex, int displacement)
    {
        int max, mid, min;
        int [] auxArrayPos = Arrays.copyOf(ordinaryThievesPos, ordinaryThievesPos.length);
        auxArrayPos[thiefIndex] -= displacement;

        int [] auxArrayPosSorted = Arrays.copyOf(auxArrayPos, auxArrayPos.length);
        Arrays.sort(auxArrayPosSorted);
        min = auxArrayPosSorted[0];
        mid = auxArrayPosSorted[1];
        max = auxArrayPosSorted[2];

        if ((max == mid) && (max != roomDistance) && (max != 0)) return false;
        if ((min == mid) && (min != roomDistance) && (min != 0)) return false;

        // If the pos goes beyond the control site, return false
        if (auxArrayPos[thiefIndex] < 0) return false;

        // If the distance between the thieves is greater than the max distance, return false
        if ((max - mid > SimulPar.MAX_THIEVES_DISTANCE) || (mid - min > SimulPar.MAX_THIEVES_DISTANCE))
            return false;
        else
            return true;
    }

    /**
     *  Operation crawl out.
     *
     */
    public synchronized void crawlOut ()
    {
        AssaultPartyClientProxy thief = (AssaultPartyClientProxy) Thread.currentThread();
        thief.setOrdinaryThiefState(OrdinaryThiefStates.CRAWLING_OUTWARDS);
        reposStub.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CRAWLING_OUTWARDS);
        int displacement;
        int thiefIndex = ordinaryThiefIndex(thief.getOrdinaryThiefId());


        // Wait for all thieves to leave
        thievesAtRoom--;
        notifyAll();
        while (thievesAtRoom > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            while (thief.getOrdinaryThiefId() != nextThiefId) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while ((displacement = canICrawlOut(thiefIndex)) != -1) {
                ordinaryThievesPos[thiefIndex] -= displacement;
                reposStub.setOrdinaryThiefPos(thief.getOrdinaryThiefId(), ordinaryThievesPos[thiefIndex]);
            }

            // Call the next thief
            nextThiefId = ordinaryThievesId[(thiefIndex + 1) % 3];
            notifyAll();

            if (ordinaryThievesPos[thiefIndex] == 0) {
                break;
            }
        }
    }

    /**
     *  Operation send assault party, we set the next thief to be the first one and give him position 0
     */
    public synchronized void sendAssaultParty ()
    {
        inOp = true;
        AssaultPartyClientProxy masterThief = (AssaultPartyClientProxy) Thread.currentThread();
        masterThief.setMasterThiefState(MasterThiefStates.DECIDING);
        reposStub.setMasterThiefState(masterThief.getMasterThiefState());

        // Choose the first as the leader and give it position 0 (which makes him ahead of the other two)
        nextThiefId = ordinaryThievesId[0];
        ordinaryThievesPos[0] = 0;
        notifyAll();
    }

    /**
     *  Add a thief to the assault party to a position that has the id -1
     *
     *   @param ordinaryThiefId the id of the thief to add
     *   @param ordinaryThiefDis the displacement of the thief to add
     */
    public synchronized void addThief (int ordinaryThiefId, int ordinaryThiefDis)
    {
        for (int i = 0; i < ordinaryThievesId.length; i++) {
            if (ordinaryThievesId[i] == -1) {
                ordinaryThievesId[i] = ordinaryThiefId;
                ordinaryThievesDis[i] = ordinaryThiefDis;
                break;
            }
        }

        notifyAll();
    }

    /**
     *   Master Thief waits for the thieves to fill the assault party
     */
    public synchronized void waitForThievesBeReady ()
    {
        nextThiefId = -1;
        while (numOfThieves() != SimulPar.MAX_PARTY_THIEVES) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   Auxiliary function to get the index of the thief in the arrays used by giving the thief id
     *    @param ordinaryThiefId the id of the thief
     *
     *    @return the index of the thief in the arrays used
     */
    public int ordinaryThiefIndex (int ordinaryThiefId)
    {
        for (int i = 0; i < ordinaryThievesId.length; i++) {
            if (ordinaryThievesId[i] == ordinaryThiefId) return i;
        }
        return -1;
    }

    /**
     *   Get the id of the assault party
     *
     *    @return the id of the assault party
     */
    public int getAssaultPartyId ()
    {
        return assaultPartyId;
    }

    /**
     *   See if the assault party is in operation
     *
     *     @return true if the assault party is in operation
     */
    public boolean getInOp ()
    {
        return inOp;
    }

    /**
     *   Set the assault party to be in operation
     *     @param inOp true if the assault party is in operation
     */
    public void setInOp (boolean inOp)
    {
        this.inOp = inOp;
    }

    /**
     *   Get the id of the room assigned to this assault party
     *
     *    @return the id of the room assigned to this assault party
     */
    public int getTargetRoomId ()
    {
        return targetRoomId;
    }

    /**
     *  Set the distance from the concentration site to the room
     *
     *   @param targetRoomId the id of the room assigned to this assault party
     *   @param roomDistance the distance from the concentration site to the room
     */
    public void setTargetRoomId (int targetRoomId, int roomDistance)
    {
        this.targetRoomId = targetRoomId;
        this.roomDistance = roomDistance;
    }

    /**
     *   Get the number of thieves in the assault party
     *
     *     @return the number of thieves in the assault party
     */
    public int numOfThieves()
    {
        int counter = 0;
        for (int thiefId : ordinaryThievesId) {
            if (thiefId != -1) counter++;
        }
        return counter;
    }

    /**
     *   Leve the assault party by setting the id of thief in array as -1.
     *
     *     @param thiefId the id of the thief that leaves the assault party
     */
    public void leaveTheAssaultParty (int thiefId)
    {
        int thiefIndex = ordinaryThiefIndex(thiefId);
        ordinaryThievesId[thiefIndex] = -1;
        reposStub.setAssaultPartyThievesId(thiefId, -1);
    }

    /**
     *   See if the thief has the canvas
     *
     *     @param thiefId the id of the thief
     *     @return true if the thief has the canvas
     */
    public boolean getBringsCanvas (int thiefId)
    {
        int thiefIndex = ordinaryThiefIndex(thiefId);
        return ordinaryThievesCanvas[thiefIndex];
    }

    /**
     *   Set if the thief has the canvas
     *
     *     @param thiefId the id of the thief
     *     @param canvas true if the thief has the canvas
     */
    public synchronized void setBringsCanvas (int thiefId, boolean canvas)
    {
        int thiefIndex = ordinaryThiefIndex(thiefId);
        ordinaryThievesCanvas[thiefIndex] = canvas;
    }

    /**
     *   Operation Concentration Site server shutdown
     */
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1) {
            ServerHeistToMuseumAssaultParty.waitConnection = false;
        }

        notifyAll();
    }

}
