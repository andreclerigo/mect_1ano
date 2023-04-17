package sharedRegions;

import entities.*;
import main.SimulPar;

/**
 *   Museum.
 *   In this shared region the Ordinary Thieves will be crawling inwards to a room, perform roolACanvas at a give room and crawl outwards.
 */
public class Museum
{
    /**
     *   Room class.
     *   It represents a room in the museum with the id of the room, the distance from the room to the concentration site and the number of canvas present in the room at a give time.
     */
    private class Room {
        private final int roomId;

        private final int distance;

        private int numCanvas;

        private int thievesAtRoom;

        public int getDistance ()
        {
            return distance;
        }

        public int getNumCanvas ()
        {
            return numCanvas;
        }

        public synchronized void setNumCanvas (int numCanvas)
        {
            this.numCanvas = numCanvas;
        }

        Room (int roomId, int numCanvas, int distance)
        {
            this.roomId = roomId;
            this.numCanvas = numCanvas;
            this.distance = distance;
            //System.out.println("Room " + roomId + " has " + numCanvas + " canvas and the distance " + distance);
        }
    }

    /**
     *   Reference to the Rooms array.
     */
    private final Room [] rooms;

    /**
     *   Reference to the general repository of information.
     */
    private final GeneralRepos repos;

    private final AssaultParty [] assaultParties;

    /**
     *   Museum instantiation.
     *
     *     @param repos reference to the general repository
     */
    public Museum (int [] paintingsPerRoom, int [] distancePerRoom, AssaultParty [] assaultParties, GeneralRepos repos)
    {
        this.repos = repos;
        this.rooms = new Room[SimulPar.ROOMS_NUMBER];
        this.assaultParties = assaultParties;

        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = new Room(i, paintingsPerRoom[i], distancePerRoom[i]);
        }
    }

    /**
     *   Operation reverse direction, the state of the ordinary thief is changed to crawling outwards.
     */
    public synchronized void reverseDirection ()
    {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setOrdinaryThiefState(OrdinaryThiefStates.CRAWLING_OUTWARDS);
        repos.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CRAWLING_OUTWARDS);
    }

    /**
     *   Get the distance of the room from the concentration site.
     *
     *     @param roomId the id of the room that we are in
     *     @return the distance of the room from the concentration site
     */
    public int getRoomDistance (int roomId)
    {
        return rooms[roomId].getDistance();
    }

    /**
     *   Operation roll a canvas, check if there are any canvas in the room and if there are, take one.
     *
     *     @param roomId the id of the room that we are in
     */
    public synchronized void rollACanvas (int roomId, int assaultPartyId)
    {
        OrdinaryThief thief = (OrdinaryThief) Thread.currentThread();
        thief.setOrdinaryThiefState(OrdinaryThiefStates.AT_A_ROOM);
        repos.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.AT_A_ROOM);

        if (rooms[roomId].getNumCanvas() > 0) {
            assaultParties[assaultPartyId].setBringsCanvas(thief.getOrdinaryThiefId(), true);
            // update repository
            repos.setBringsCanvas(thief.getOrdinaryThiefId(), true);
            repos.setUpdateInRoom(roomId, rooms[roomId].getNumCanvas() - 1);
            rooms[roomId].setNumCanvas(rooms[roomId].getNumCanvas() - 1);
        } else {
            assaultParties[assaultPartyId].setBringsCanvas(thief.getOrdinaryThiefId(), false);
        }
        //System.out.println("Thief " + thief.getOrdinaryThiefId() + " rolled canvas " + thief.getBringsCanvas());
    }
}
