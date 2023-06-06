package serverSide.objects;

import interfaces.*;
import serverSide.main.*;
import clientSide.entities.*;
import commInfra.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 *   Control/Collection site.
 *   In this shared region, the master thief waits for the ordinary thieves to arrive at their excursion and collects canvases if they bring one.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on Java RMI.
 */
public class ControlSite implements ControlSiteInterface
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
     *   Remote reference to the assault parties array.
     */
    private final AssaultPartyInterface [] assaultPartiesStubs;

    /**
     *   Number of canvases collected.
     */
    private int numOfCanvas;

    /**
     *  Remote reference to the general repository of information.
     */
    private final GeneralReposInterface reposStub;

    /**
     *   Number of entities that requested shutdown.
     */
    private int nEntities;

    /**
     *   Instantiation of a control site object.
     *
     *     @param assaultPartiesStubs array of assault parties
     *     @param reposStub remote reference to the general repository of information
     */
    public ControlSite (AssaultPartyInterface [] assaultPartiesStubs, GeneralReposInterface reposStub)
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
    @Override
    public synchronized int startOperations () throws RemoteException
    {
        int masterThiefState;

        masterThiefState = MasterThiefStates.PLANNING_HEIST;
        try{
            reposStub.setMasterThiefState(masterThiefState);
        } catch (RemoteException e) {
            System.out.println("Remote exception in (1) startOperations  while setting master thief state: " + e.getMessage());
            System.exit (1);
        }

        masterThiefState = MasterThiefStates.DECIDING;
        try{
            reposStub.setMasterThiefState(masterThiefState);
        } catch (RemoteException e) {
            System.out.println("Remote exception in (2) startOperations while setting master thief state: " + e.getMessage());
            System.exit (1);
        }

        return masterThiefState;
    }

    /**
     *   Operation appraise sit. If there is not any room left and all thieves arrived then the assault has ended.
     *   Otherwise, we see if there is any assault party available, if there is the Master Thief assembles a new excursion.
     *
     *     @return the char correspondent of the operation.
     */
    @Override
    public synchronized char appraiseSit ()
    {
        int targetRoom = -1;   // target room of the assault party

        if ((numPartiesInOp() == 0) && (numOfEmptyRooms() == SimulPar.ROOMS_NUMBER)){
            return 'E';
        }
        else {
            try {
                if (numPartiesInOp() != 0) {
                    targetRoom = assaultPartiesStubs[partyInOpId()].getTargetRoomId();
                }
            } catch (RemoteException e) {
                System.out.println("Remote exception in appraiseSit while getting target room id: " + e.getMessage());
                System.exit (1);
            }

            if ((numPartiesInOp() == SimulPar.ASSAULT_PARTIES_NUMBER) ||
                    (numPartiesInOp() == 1 && numOfEmptyRooms() == SimulPar.ROOMS_NUMBER) ||
                    (numPartiesInOp() == 1 &&
                            numOfEmptyRooms() == SimulPar.ROOMS_NUMBER - 1 &&
                            targetRoom == idOfRoomNotEmpty())
            ) {
                return 'R';
            } else {
                return 'P';
            }
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
        boolean inOp;

        for (AssaultPartyInterface assaultPartyStub : assaultPartiesStubs) {
            try {
                inOp = assaultPartyStub.getInOp();
            } catch (RemoteException e) {
                System.out.println("Remote exception in numPartiesInOp while getting in operation: " + e.getMessage());
                System.exit (1);
                return -1;      // Should never get here
            }

            if (inOp) counter++;
        }
        return counter;
    }

    /**
     *  Get the id of the assault party that is in operation (should never return -1 because this function is only called when only 1 party is active).
     *
     *     @return id of assault party
     */
    private int partyInOpId ()
    {
        boolean inOp;
        int assPartyId;

        // Iterate through assaultParties array and get the on that is in operation
        for (AssaultPartyInterface assaultPartyStub : assaultPartiesStubs) {
            try{
                inOp = assaultPartyStub.getInOp();
            }  catch (RemoteException e) {
                System.out.println("Remote exception in partyInOpId while getting if assParty is in operation: " + e.getMessage());
                System.exit (1);
                return -1;      // Should never get here
            }

            if (inOp) {
                try{
                    assPartyId = assaultPartyStub.getAssaultPartyId();
                } catch (RemoteException e) {
                    System.out.println("Remote exception in partyInOpId while getting assault party id that's in operation: " + e.getMessage());
                    System.exit (1);
                    return -1;      // Should never get here
                }
                return assPartyId;
            }
        }
        // Should never get here if appraiseSit is working properly
        return -1;
    }

    /**
     *   Get the id of the next assault party to be used (should never return -1 because this function is only called when there is at least 1 assault party available).
     *
     *     @return id of assault party
     */
    @Override
    public int getAssaultId()
    {
        boolean inOp;
        // Iterate through assaultParties array and get the first one that isn't in operation
        for (AssaultPartyInterface assaultPartyStub : assaultPartiesStubs) {
            try{
                inOp = assaultPartyStub.getInOp();
            }  catch (RemoteException e) {
                System.out.println("Remote exception in getAssaultId while getting in operation: " + e.getMessage());
                System.exit (1);
                return -1;      // Should never get here
            }

            if (!inOp) {
                int assParty;
                try{
                    assParty = assaultPartyStub.getAssaultPartyId();
                } catch (RemoteException e) {
                    System.out.println("Remote exception in getAssaultId while getting assault party id: " + e.getMessage());
                    System.exit (1);
                    return -1;      // Should never get here
                }

                return assParty;
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
    @Override
    public void setNumOfCanvas (int numOfCanvas)
    {
        this.numOfCanvas = numOfCanvas;
    }

    /**
     *   Get the number of canvas collected
     *    @return numOfCanvas
     */
    @Override
    public int getNumOfCanvas ()
    {
        return numOfCanvas;
    }

	/**
     *   Get the id of an exhibition room that is not empty and is not assigned to an assault party (should never return -1 because this function is only called when at least 1 room is available)..
     *
     *     @return id of exhibition room
     */
    @Override
    public int getRoomId ()
    {
        boolean flag;
        // Iterate through emptyRooms array and get the first one that is not empty and is not the targeted room of an active assault party
        for (int i = 0; i < emptyRooms.length; i++) {
            if (!emptyRooms[i]) {
                flag = true;
                for (AssaultPartyInterface assaultPartyStub : assaultPartiesStubs) {
                    boolean inOp;
                    int assParty;

                    try{
                        inOp = assaultPartyStub.getInOp();
                    } catch (RemoteException e) {
                        System.out.println("Remote exception in getRoomId while getting in operation: " + e.getMessage());
                        System.exit (1);
                        return -1;      // Should never get here
                    }

                    try{
                        assParty = assaultPartyStub.getTargetRoomId();
                    } catch (RemoteException e) {
                        System.out.println("Remote exception in getRoomId while getting assault party id: " + e.getMessage());
                        System.exit (1);
                        return -1;      // Should never get here
                    }

                    if (inOp && assParty == i) {
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
    @Override
    public synchronized int collectACanvas ()
    {
        int masterThiefState;
        try {
            ExcursionInfo excursionInfo = thievesToHandCanvas.read();

            if (excursionInfo.bringsCanvas) {
                numOfCanvas++;
                try {
                    reposStub.giveCanvas(excursionInfo.thiefId, false);
                } catch (RemoteException e) {
                    System.out.println("Remote exception in collectACanvas while giving canvas: " + e.getMessage());
                    System.exit (1);
                }

                try {
                    reposStub.setCanvasResult(numOfCanvas);
                } catch (RemoteException e) {
                    System.out.println("Remote exception in collectACanvas while setting canvas result: " + e.getMessage());
                    System.exit (1);
                }

            } else {
                int targetRoomId;
                try {
                    targetRoomId = assaultPartiesStubs[excursionInfo.assaultPartyId].getTargetRoomId();
                } catch (RemoteException e) {
                    System.out.println("Remote exception in collectACanvas while getting target room id: " + e.getMessage());
                    System.exit (1);
                    return -1;      // Should never get here
                }

                emptyRooms[targetRoomId] = true;
            }
            try {
                assaultPartiesStubs[excursionInfo.assaultPartyId].leaveTheAssaultParty(excursionInfo.thiefId);
            } catch (RemoteException e) {
                System.out.println("Remote exception in collectACanvas while leaving the assault party: " + e.getMessage());
                System.exit (1);
                return -1;      // Should never get here
            }

            int numThieves;
            try {
                numThieves = assaultPartiesStubs[excursionInfo.assaultPartyId].numOfThieves();
            } catch (RemoteException e) {
                System.out.println("Remote exception in collectACanvas while getting number of thieves: " + e.getMessage());
                System.exit(1);
                return -1;      // Should never get here
            }

            if (numThieves == 0) {
                try{
                    assaultPartiesStubs[excursionInfo.assaultPartyId].setInOp(false);
                } catch (RemoteException e) {
                    System.out.println("Remote exception in collectACanvas while setting in operation: " + e.getMessage());
                    System.exit (1);
                }
            }

            // Wakeup the thief served
            notifyAll();
        } catch (MemException e) {
            e.printStackTrace();
        }

        masterThiefState = MasterThiefStates.DECIDING;
        try {
            reposStub.setMasterThiefState(MasterThiefStates.DECIDING);
        } catch (RemoteException e) {
            System.out.println("Remote exception in collectACanvas while setting master thief state: " + e.getMessage());
            System.exit (1);
        }

        return masterThiefState;
    }

	/**
     *   Operation hand a canvas, add the thief id, assault party id and bringsCanvas flag to the waiting thieves queue.
     *
     *     @param assaultPartyId id of the assault party
     *     @param ordinaryThiefId id of the ordinary thief
     */
    @Override
    public synchronized void handACanvas (int ordinaryThiefId, int assaultPartyId)
    {
        ExcursionInfo info = null;

        try {
             info = new ExcursionInfo(ordinaryThiefId, assaultPartiesStubs[assaultPartyId].getBringsCanvas(ordinaryThiefId), assaultPartyId);
        } catch (RemoteException e) {
            System.out.println("Remote exception in handACanvas while getting brings canvas (in Excursion Info instantiation): " + e.getMessage());
            System.exit (1);
        }

        try {
            thievesToHandCanvas.write(info);
            try {
                reposStub.setOrdinaryThiefState(ordinaryThiefId, OrdinaryThiefStates.CONTROL_SITE);
            } catch (RemoteException e) {
                System.out.println("Remote exception in handACanvas while setting ordinary thief state: " + e.getMessage());
                System.exit (1);
            }

        } catch (MemException e) {
            e.printStackTrace();
        }
        try{
            assaultPartiesStubs[assaultPartyId].setBringsCanvas(ordinaryThiefId, false);
        } catch (RemoteException e) {
            System.out.println("Remote exception in handACanvas while setting brings canvas: " + e.getMessage());
            System.exit (1);
        }

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
    @Override
    public synchronized int takeARest ()
    {
        int masterThiefState;
        masterThiefState = MasterThiefStates.WAITING;
        try {
            reposStub.setMasterThiefState(MasterThiefStates.WAITING);
        } catch (RemoteException e) {
            System.out.println("Remote exception in takeARest while setting master thief state: " + e.getMessage());
            System.exit (1);
        }

        while (thievesToHandCanvas.getNumOfElements() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return masterThiefState;
    }

    /**
     *   Operation Control Site server shutdown
     */
    @Override
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1) {
            ServerHeistToMuseumControlSite.shutdown();
        }

        notifyAll();
    }
}
