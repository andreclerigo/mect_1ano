package serverSide.sharedRegions;

import clientSide.stubs.GeneralReposStub;
import clientSide.stubs.AssaultPartyStub;
import clientSide.entities.OrdinaryThiefStates;
import serverSide.main.ServerHeistToMuseumMuseum;
import serverSide.main.SimulPar;
import serverSide.entities.MuseumClientProxy;

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
        /**
         *   Room id.
         */
        private final int roomId;

        /**
         *   Distance from the room to the concentration site.
         */
        private final int distance;

        /**
         *   Number of canvas in the room.
         */
        private int numCanvas;

        /**
         *   Get the room id.
         *
         *     @return the room id
         */
        public int getDistance ()
        {
            return distance;
        }

        /**
         *   Get the number of canvas in the room.
         *
         *   @return the number of canvas in the room
         */
        public int getNumCanvas ()
        {
            return numCanvas;
        }

        /**
         *   Set the number of canvas in the room.
         *
         *     @param numCanvas the number of canvas in the room
         */
        public synchronized void setNumCanvas (int numCanvas)
        {
            this.numCanvas = numCanvas;
        }

        /**
         *   Get the room id.
         *
         *     @return the room id
         */
        Room (int roomId, int numCanvas, int distance)
        {
            this.roomId = roomId;
            this.numCanvas = numCanvas;
            this.distance = distance;
        }
    }

    /**
     *   Reference to the Rooms array.
     */
    private final Room [] rooms;

    /**
     *   Reference to the general repository of information.
     */
    private final GeneralReposStub reposStub;

    /**
     *   Reference to the assault parties.
     */
    private final AssaultPartyStub [] assaultPartiesStubs;

    /**
     *   Number of entities that requested shutdown.
     */
    private int nEntities;

    /**
     *   Museum instantiation.
     *
     *     @param reposStub reference to the general repository
     *     @param assaultPartiesStubs array of assault parties
     *     @param paintingsPerRoom array with the number of paintings in each room
     *     @param distancePerRoom array with the distance of each room to the concentration site
     */
    public Museum (int [] paintingsPerRoom, int [] distancePerRoom, AssaultPartyStub [] assaultPartiesStubs, GeneralReposStub reposStub)
    {
        this.reposStub = reposStub;
        this.rooms = new Room[SimulPar.ROOMS_NUMBER];
        this.assaultPartiesStubs = assaultPartiesStubs;
        this.nEntities = 0;
        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = new Room(i, paintingsPerRoom[i], distancePerRoom[i]);
        }
    }

    /**
     *   Operation reverse direction, the state of the ordinary thief is changed to crawling outwards.
     */
    public synchronized void reverseDirection ()
    {
        MuseumClientProxy thief = (MuseumClientProxy) Thread.currentThread();
        thief.setOrdinaryThiefState(OrdinaryThiefStates.CRAWLING_OUTWARDS);
        reposStub.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.CRAWLING_OUTWARDS);
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
     *     @param assaultPartyId the id of the assault party that we are in
     */
    public synchronized void rollACanvas (int roomId, int assaultPartyId)
    {
        MuseumClientProxy thief = (MuseumClientProxy) Thread.currentThread();
        thief.setOrdinaryThiefState(OrdinaryThiefStates.AT_A_ROOM);
        reposStub.setOrdinaryThiefState(thief.getOrdinaryThiefId(), OrdinaryThiefStates.AT_A_ROOM);

        if (rooms[roomId].getNumCanvas() > 0) {
            assaultPartiesStubs[assaultPartyId].setBringsCanvas(thief.getOrdinaryThiefId(), true);
            // update repository
            reposStub.setBringsCanvas(thief.getOrdinaryThiefId(), true);
            reposStub.setUpdateInRoom(roomId, rooms[roomId].getNumCanvas() - 1);
            rooms[roomId].setNumCanvas(rooms[roomId].getNumCanvas() - 1);
        } else {
            assaultPartiesStubs[assaultPartyId].setBringsCanvas(thief.getOrdinaryThiefId(), false);
        }
    }

    /**
     *   Operation Museum server shutdown
     */
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1){
            ServerHeistToMuseumMuseum.waitConnection = false;
        }

        notifyAll();
    }
}
