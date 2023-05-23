package clientSide.stubs;

import commInfra.ClientCom;
import commInfra.Message;
import commInfra.MessageType;

/**
 *   Stub to the General Repository.
 *   It instantiates a remote reference to the control site.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class GeneralReposStub
{
    /**
     *   Name of the platform where is located the bar server
     */
    private String serverHostName;

    /**
     *   Port number for listening to service requests
     */
    private int serverPortNumb;

    /**
     *   Instantiation of a stub to the Bar.
     *
     *   @param serverHostName name of the platform where is located the bar server
     *   @param serverPortNumb port number for listening to service requests
     */
    public GeneralReposStub(String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }

    /**
     *    Set the Master Thief state.
     *
     *    @param state master thief state
     */
    public void setMasterThiefState (int state)
    {
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message

        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SET_MASTER_THIEF_STATE, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_MASTER_THIEF_STATE) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Set the number of paintings in each room.
     *
     *   @param paintingsPerRoom number of paintings in each room
     */
    public void setPaintingsPerRoom(int[] paintingsPerRoom)
    {
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message

        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SET_PAINTINGS_PER_ROOM, paintingsPerRoom);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_PAINTINGS_PER_ROOM) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Set the distances to each room.
     *
     *   @param distancePerRoom distances to each room
     */
    public void setDistancePerRoom(int[] distancePerRoom){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message

        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SET_DISTANCE_PER_ROOM, distancePerRoom);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_DISTANCE_PER_ROOM) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }


    /**
     *   Set the Ordinary Thief state.
     *
     *   @param id thief id
     *   @param state thief state
     */
    public void setOrdinaryThiefState (int id, int state)
    {
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_ORDINARY_THIEF_STATE, id, state);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_ORDINARY_THIEF_STATE) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Set the Ordinary Thief position.
     *
     *   @param id thief id
     *   @param pos thief position
     */
    public void setOrdinaryThiefPos(int id, int pos)
    {
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_ORDINARY_THIEF_POS, id, pos);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_ORDINARY_THIEF_POS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Set the ordinary thieves agility in the repository.
     *
     *     @param agility ordinary thieves agility
     */
    public void setOrdinaryThievesAgility(int[] agility){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_ORDINARY_THIEVES_AGILITY, agility);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_ORDINARY_THIEVES_AGILITY) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
    }

    /**
     *   Set the updated number of canvas in each room.
     *
     *   @param roomId room id
     *   @param nCanvas number of canvas in the room
     */
    public void setUpdateInRoom(int roomId, int nCanvas){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_UPDATE_CANVAS_IN_ROOM, roomId, nCanvas);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_UPDATE_CANVAS_IN_ROOM) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Set if the ordinary thief brings a canvas.
     *
     *   @param id id of the ordinary thief
     *   @param hasCanvas true if the ordinary thief brings a canvas
     */
    public void setBringsCanvas(int id, boolean hasCanvas){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_BRINGS, id, hasCanvas);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_BRINGS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   The ordinary thief gives the canvas to the master thief (she checks if he brings one or not).
     *
     *   @param id ID of the ordinary thief
     *   @param hasCanvas true if the ordinary thief brings a canvas, false otherwise
     */
    public void giveCanvas(int id, boolean hasCanvas){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_GIVE_CANVAS, id, hasCanvas);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_GIVE_CANVAS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Associate the thieves IDs to the assault party they belong to.
     *
     *   @param thiefId ID of the thief
     *   @param partyId ID of the assault party
     */
    public void setAssaultPartyThievesId(int thiefId, int partyId){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_ASSAULT_PARTY_THIEVES_ID, thiefId, partyId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_ASSAULT_PARTY_THIEVES_ID) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Set the assault party target room ID.
     *
     *   @param id id of the assault party
     *   @param roomId target room ID
     */
    public void setAssaultPartyTargetRoomId(int id, int roomId){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_ASSAULT_PARTY_TARGET_ROOM_ID, id, roomId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_ASSAULT_PARTY_TARGET_ROOM_ID) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *    Set the number of total canvas stolen.
     *
     *    @param canvas number of total canvas stolen
     */
    public void setCanvasResult(int canvas){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_SET_CANVAS_RESULT, canvas);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_SET_CANVAS_RESULT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Print the final report.
     */
    public void printTail(){
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message
        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {
            }
        }
        outMessage = new Message(MessageType.REQ_PRINT_TAIL);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();
        if (inMessage.getMsgType() != MessageType.REP_PRINT_TAIL) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }

    /**
     *   Operation server shutdown.
     */
    public void shutdown ()
    {
        ClientCom com;                                                 // communication channel
        Message outMessage,                                            // outgoing message
                inMessage;                                             // incoming message

        com = new ClientCom(serverHostName, serverPortNumb);
        while (!com.open()) {
            try {
                Thread.sleep ((long) (1000));
            } catch (InterruptedException e) {}
        }
        outMessage = new Message(MessageType.REQ_GENERAL_REPO_SHUT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_GENERAL_REPO_SHUT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }
}
