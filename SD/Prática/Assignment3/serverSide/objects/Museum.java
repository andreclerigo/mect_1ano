package serverSide.objects;

import java.rmi.registry.*;
import java.rmi.*;
import interfaces.*;
import serverSide.main.*;
import clientSide.entities.*;

/**
 *   Museum.
 *   In this shared region the Ordinary Thieves will be crawling inwards to a room, perform roolACanvas at a give room and crawl outwards.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on Java RMI.
 */
public class Museum implements MuseumInterface
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
     *   Remote reference to the general repository of information.
     */
    private final GeneralReposInterface reposStub;

    /**
     *   Remote reference to the assault parties.
     */
    private final AssaultPartyInterface [] assaultPartiesStubs;

    /**
     *   Number of entities that requested shutdown.
     */
    private int nEntities;

    /**
     *   Museum instantiation.
     *
     *     @param reposStub remote reference to the general repository
     *     @param assaultPartiesStubs remote reference to array of assault parties
     *     @param paintingsPerRoom array with the number of paintings in each room
     *     @param distancePerRoom array with the distance of each room to the concentration site
     */
    public Museum (int [] paintingsPerRoom, int [] distancePerRoom, AssaultPartyInterface [] assaultPartiesStubs, GeneralReposInterface reposStub)
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
     *    @param ordinaryThiefId the id of the ordinary thief
     *    @return the state of the ordinary thief
     */
    @Override
    public synchronized int reverseDirection (int ordinaryThiefId)
    {
        int ordinaryThiefState = OrdinaryThiefStates.CRAWLING_OUTWARDS;
        try{
            reposStub.setOrdinaryThiefState(ordinaryThiefId, OrdinaryThiefStates.CRAWLING_OUTWARDS);
        } catch (RemoteException e){
            System.out.println("Remote exception in reverseDirection while setting ordinary thief's state: " + e.getMessage());
            System.exit(1);
        }

        return ordinaryThiefState;
    }

    /**
     *   Get the distance of the room from the concentration site.
     *
     *     @param roomId the id of the room that we are in
     *     @return the distance of the room from the concentration site
     */
    @Override
    public int getRoomDistance (int roomId)
    {
        return rooms[roomId].getDistance();
    }

    /**
     *   Operation roll a canvas, check if there are any canvas in the room and if there are, take one.
     *
     *     @param roomId the id of the room that we are in
     *     @param assaultPartyId the id of the assault party that we are in
     *     @param ordinaryThiefId the id of the ordinary thief
     */
    @Override
    public synchronized int rollACanvas (int roomId, int assaultPartyId, int ordinaryThiefId)
    {
        int ordinaryThiefState = OrdinaryThiefStates.AT_A_ROOM;

        try {
            reposStub.setOrdinaryThiefState(ordinaryThiefId, OrdinaryThiefStates.AT_A_ROOM);
        } catch (RemoteException e) {
            System.out.println("Remote exception in rollACanvas while setting ordinary thief's state: " + e.getMessage());
            System.exit(1);
        }

        if (rooms[roomId].getNumCanvas() > 0) {
            try {
                assaultPartiesStubs[assaultPartyId].setBringsCanvas(ordinaryThiefId, true);
            } catch (RemoteException e) {
                System.out.println("Remote exception in rollACanvas while setting ordinary thief's state: " + e.getMessage());
                System.exit(1);
            }
            // update repository
            try{
                reposStub.setBringsCanvas(ordinaryThiefId, true);
            } catch (RemoteException e){
                System.out.println("Remote exception in rollACanvas while setting ordinary thief's state: " + e.getMessage());
                System.exit(1);
            }

            try{
                reposStub.setUpdateInRoom(roomId, rooms[roomId].getNumCanvas() - 1);
            } catch (RemoteException e){
                System.out.println("Remote exception in rollACanvas while setting ordinary thief's state: " + e.getMessage());
                System.exit(1);
            }

            rooms[roomId].setNumCanvas(rooms[roomId].getNumCanvas() - 1);
        } else {
            try{
                assaultPartiesStubs[assaultPartyId].setBringsCanvas(ordinaryThiefId, false);
            } catch (RemoteException e){
                System.out.println("Remote exception in rollACanvas while setting ordinary thief's state: " + e.getMessage());
                System.exit(1);
            }
        }

        return ordinaryThiefState;
    }

    /**
     *   Operation Museum server shutdown
     */
    @Override
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1){
            ServerHeistToMuseumMuseum.shutdown();
        }

        notifyAll();
    }
}
