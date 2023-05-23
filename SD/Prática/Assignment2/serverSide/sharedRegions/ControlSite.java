package serverSide.sharedRegions;

import clientSide.stubs.AssaultPartyStub;
import clientSide.stubs.GeneralReposStub;
import clientSide.entities.MasterThiefStates;
import clientSide.entities.OrdinaryThiefStates;
import commInfra.MemException;
import commInfra.MemFIFO;
import serverSide.main.SimulPar;
import serverSide.entities.ControlSiteClientProxy;
import serverSide.main.ServerHeistToMuseumControlSite;

import java.util.Arrays;

/**
 *   Control/Collection site.
 *   In this shared region, the master thief waits for the ordinary thieves to arrive at their excursion and collects canvases if they bring one.
 */
public class ControlSite
{
    /**
     *   Excursion info class.
     *   Class that represents the information of a thief that has arrived at the control site.
     */
    private static class ExcursionInfo {
        private final int thiefId;
        private final int assaultPartyId;
        private final boolean bringsCanvas;

        public ExcursionInfo (int thiefId, boolean bringsCanvas, int assaultPartyId) {
            this.thiefId = thiefId;
            this.bringsCanvas = bringsCanvas;
            this.assaultPartyId = assaultPartyId;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExcursionInfo that = (ExcursionInfo) o;
            return thiefId == that.thiefId &&
                    assaultPartyId == that.assaultPartyId &&
                    bringsCanvas == that.bringsCanvas;
        }

        @Override
        public String toString () {
            return "ExcursionInfo{" +
                    "thiefId=" + thiefId +
                    ", assaultPartyId=" + assaultPartyId +
                    ", bringsCanvas=" + bringsCanvas +
                    '}';
        }
    }

    /**
     *   Fifo of the thieves that have arrived at the control site
     */
    private MemFIFO<ExcursionInfo> thievesToHandCanvas;

    /**
     *   Array of booleans informing if a room is empty or not
     */
    private boolean [] emptyRooms;

    /**
     *   Reference to the assault parties array.
     */
    private final AssaultPartyStub [] assaultPartiesStubs;

    /**
     *   Number of canvases collected.
     */
    private int numOfCanvas;

    /**
     *  Reference to the general repository of information.
     */
    private final GeneralReposStub reposStub;

    /**
     *   Number of entities that requested shutdown.
     */
    private int nEntities;

    /**
     *   Instantiation of a control site object.
     *
     *     @param assaultPartiesStubs array of assault parties
     *     @param reposStub reference to the general repository of information
     */
    public ControlSite (AssaultPartyStub [] assaultPartiesStubs, GeneralReposStub reposStub)
    {
        this.assaultPartiesStubs = assaultPartiesStubs;
        this.reposStub = reposStub;
        this.nEntities = 0;
        emptyRooms = new boolean[SimulPar.ROOMS_NUMBER];
        Arrays.fill(emptyRooms, false);

        try {
            this.thievesToHandCanvas = new MemFIFO<>(new ExcursionInfo[SimulPar.ORDINARY_THIEVES + 1]);
        } catch (MemException e) {
            e.printStackTrace();
        }
    }

    /**
     *   Start the heist to the museum operation.
     */
    public synchronized void startOperations ()
    {
        reposStub.setMasterThiefState(MasterThiefStates.PLANNING_HEIST);
        ((ControlSiteClientProxy) Thread.currentThread()).setMasterThiefState(MasterThiefStates.DECIDING);
        reposStub.setMasterThiefState(MasterThiefStates.DECIDING);
    }

    /**
     *   Operation appraise sit. If there is not any room left and all thieves arrived then the assault has ended.
     *   Otherwise, we see if there is any assault party available, if there is the Master Thief assembles a new excursion.
     *
     *     @return the int correspondent of the operation.
     */
    public synchronized char appraiseSit ()
    {
        if ((numPartiesInOp() == 0) && (numOfEmptyRooms() == SimulPar.ROOMS_NUMBER)){
            return 'E';
        }

        else if( (numPartiesInOp() == SimulPar.ASSAULT_PARTIES_NUMBER) ||
                 (numPartiesInOp() == 1 && numOfEmptyRooms() == SimulPar.ROOMS_NUMBER) ||
                 (numPartiesInOp() == 1 &&
                  numOfEmptyRooms() == SimulPar.ROOMS_NUMBER - 1 &&
                  assaultPartiesStubs[partyInOpId()].getTargetRoomId() == idOfRoomNotEmpty())
        ) {
            return 'R';
        } else {
            return 'P';
        }
    }

    /**
     *   Check how many assault parties are in operation.
     *
     *     @return the number of assault parties in operation
     */
    private int numPartiesInOp()
    {
        int counter = 0;
        for (AssaultPartyStub assaultPartyStub : assaultPartiesStubs) {
            if (assaultPartyStub.getInOp()) counter++;
        }
        return counter;
    }

    /**
     *  Get the id of the assault party that is in operation (should never return -1 because this function is only called when only 1 party is active).
     *
     *     @return id of assault party
     */
    private int partyInOpId()
    {
        // Iterate through assaultParties array and get the on that is in operation
        for (AssaultPartyStub assaultPartyStub : assaultPartiesStubs) {
            if (assaultPartyStub.getInOp()) return assaultPartyStub.getAssaultPartyId();
        }
        // Should never get here if appraiseSit is working properly
        return -1;
    }

    /**
     *   Get the id of the next assault party to be used (should never return -1 because this function is only called when there is at least 1 assault party available).
     *
     *     @return id of assault party
     */
    public int getAssaultId()
    {
        // Iterate through assaultParties array and get the first one that isn't in operation
        for (AssaultPartyStub assaultPartyStub : assaultPartiesStubs) {
            if (!assaultPartyStub.getInOp()) {
                return assaultPartyStub.getAssaultPartyId();
            }
        }
        // Should never get here if appraiseSit is working properly
        return -1;
    }

    /**
     *   Set the number of canvas collected
     *
     *     @param numOfCanvas number of canvas collected
     */
    public void setNumOfCanvas(int numOfCanvas) {
        this.numOfCanvas = numOfCanvas;
    }

    /**
     *   Get the number of canvas collected
     *    @return numOfCanvas
     */
    public int getNumOfCanvas() {
        return numOfCanvas;
    }

	/**
     *   Get the id of an exhibition room that is not empty and is not assigned to an assault party (should never return -1 because this function is only called when at least 1 room is available)..
     *
     *     @return id of exhibition room
     */
    public int getRoomId ()
    {
        boolean flag;
        // Iterate through emptyRooms array and get the first one that is not empty and is not the targeted room of an active assault party
        for (int i = 0; i < emptyRooms.length; i++) {
            if (!emptyRooms[i]) {
                flag = true;
                for (AssaultPartyStub assaultPartyStub : assaultPartiesStubs) {
                    if (assaultPartyStub.getInOp() && assaultPartyStub.getTargetRoomId() == i) {
                        flag = false;
                        break;
                    }
                }
                if (flag) return i;
            }
        }
        // Should never get here if appraiseSit is working properly
        return -1;
    }

    /**
     *   Get the number of empty rooms.
     *
     *     @return number of empty rooms
     */
    private int numOfEmptyRooms ()
    {
        int counter = 0;
        for (boolean emptyRoom : emptyRooms) {
            if (emptyRoom) counter++;
        }
        return counter;
    }

    /**
     *   Get the id of an exhibition room that is not empty (should never return -1 because this function is only called when only 1 room is empty).
     *
     *     @return id of exhibition room
     */
    private int idOfRoomNotEmpty ()
    {
        for (int i = 0; i < emptyRooms.length; i++) {
            if(!emptyRooms[i]) return i;
        }
        // Should never get here if appraiseSit is working properly
        return -1;
    }

    /**
     *   Operation collect a canvas, the master thief retrieves the information from the FIFO and updates the empty rooms and the number of canvas.
     *   The thief "leaves" the assault party and if there are no more thieves in the assault party, the assault party leaves the operation.
     */
    public synchronized void collectACanvas ()
    {
        ControlSiteClientProxy masterThief = (ControlSiteClientProxy) Thread.currentThread();
        try {
            ExcursionInfo excursionInfo = thievesToHandCanvas.read();

            if (excursionInfo.bringsCanvas) {
                numOfCanvas++;
                reposStub.giveCanvas(excursionInfo.thiefId, false);
                reposStub.setCanvasResult(numOfCanvas);
            } else {
                int targetRoomId = assaultPartiesStubs[excursionInfo.assaultPartyId].getTargetRoomId();
                emptyRooms[targetRoomId] = true;
            }
            assaultPartiesStubs[excursionInfo.assaultPartyId].leaveTheAssaultParty(excursionInfo.thiefId);

            if (assaultPartiesStubs[excursionInfo.assaultPartyId].numOfThieves() == 0) {
                assaultPartiesStubs[excursionInfo.assaultPartyId].setInOp(false);
            }

            // Wakeup the thief served
            notifyAll();
        } catch (MemException e)    {
            e.printStackTrace();
        }

        masterThief.setMasterThiefState(MasterThiefStates.DECIDING);
        reposStub.setMasterThiefState(MasterThiefStates.DECIDING);
    }

	/**
     *   Operation hand a canvas, add the thief id, assault party id and bringsCanvas flag to the waiting thieves queue.
     *
     *     @param assaultPartyId id of the assault party
     *     @param ordinaryThiefId id of the ordinary thief
     */
    public synchronized void handACanvas (int ordinaryThiefId, int assaultPartyId)
    {
        ControlSiteClientProxy ordinaryThief = (ControlSiteClientProxy) Thread.currentThread();
        ExcursionInfo info = new ExcursionInfo(ordinaryThiefId, assaultPartiesStubs[assaultPartyId].getBringsCanvas(ordinaryThiefId), assaultPartyId);

        try {
            thievesToHandCanvas.write(info);
            ordinaryThief.setOrdinaryThiefState(OrdinaryThiefStates.CONTROL_SITE);
            reposStub.setOrdinaryThiefState(ordinaryThiefId, OrdinaryThiefStates.CONTROL_SITE);
        } catch (MemException e) {
            e.printStackTrace();
        }
        assaultPartiesStubs[assaultPartyId].setBringsCanvas(ordinaryThiefId, false);

        // Notify the master thief that the queue has changed
        notifyAll();
        // Wait for the master thief to collect the canvas
        while (thievesToHandCanvas.elementExists(info)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	/**
     *   Operation take a rest, change the master thief state and wait for thieves to go arrive to the waiting queue.
     */
    public synchronized void takeARest ()
    {
        ((ControlSiteClientProxy) Thread.currentThread()).setMasterThiefState(MasterThiefStates.WAITING);
        reposStub.setMasterThiefState(MasterThiefStates.WAITING);

        while (thievesToHandCanvas.getNumOfElements() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   Operation Control Site server shutdown
     */
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1) {
            ServerHeistToMuseumControlSite.waitConnection = false;
        }

        notifyAll();
    }

}
