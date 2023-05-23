package clientSide.stubs;

import clientSide.entities.*;
import commInfra.ClientCom;
import commInfra.Message;
import commInfra.MessageType;

/**
 *   Stub to the Assault Party.
 *   It instantiates a remote reference to the assault party.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class AssaultPartyStub
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
    public AssaultPartyStub(String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }

    /**
     *  Operation crawl in.
     */
    public void crawlIn ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        int ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefId();
        int ordinaryThiefState = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefState();

        outMessage = new Message(MessageType.REQ_CRAWL_IN, ordinaryThiefId, ordinaryThiefState);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_CRAWL_IN) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief Id!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.AT_A_ROOM) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief state!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((OrdinaryThief) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
    }

    /**
     *  Operation crawl out.
     */
    public void crawlOut ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        int ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefId();
        int ordinaryThiefState = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefState();

        outMessage = new Message(MessageType.REQ_CRAWL_OUT, ordinaryThiefId, ordinaryThiefState);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_CRAWL_OUT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief Id!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CRAWLING_OUTWARDS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief state!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((OrdinaryThief) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
    }

    /**
     *  Operation send assault party, we set the next thief to be the first one and give him position 0
     */
    public void sendAssaultParty ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SEND_PARTY, ((MasterThief) Thread.currentThread()).getMasterThiefState());
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_SEND_PARTY) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getMasterThiefState() != MasterThiefStates.DECIDING) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief state!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((MasterThief) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
    }

    /**
     *  Operation send assault party, we set the next thief to be the first one and give him position 0
     *    @param ordinaryThiefId thief id
     *    @param ordinaryThiefDis thief distance to room
     */
    public void addThief (int ordinaryThiefId, int ordinaryThiefDis)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_ADD_THIEF, ordinaryThiefId, ordinaryThiefDis);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_ADD_THIEF) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief Id!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefAgility() != ordinaryThiefDis) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief Displacement!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
    }

    /**
     *   Master Thief waits for the thieves to fill the assault party
     */
    public void waitForThievesBeReady ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_WAIT_FOR_THIEVES);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_WAIT_FOR_THIEVES) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
    }

    /**
     *   Get the id of the assault party
     *
     *    @return the id of the assault party
     */
    public int getAssaultPartyId ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_GET_PARTY_ID);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_GET_PARTY_ID) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getAssaultPartyId() == -1) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid assault party id!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getAssaultPartyId();
    }

    /**
     *   See if the assault party is in operation
     *
     *     @return true if the assault party is in operation
     */
    public boolean getInOp ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_GET_IN_OP);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_GET_IN_OP) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getInOp();
    }

    /**
     *   Set the assault party to be in operation
     *     @param inOp true if the assault party is in operation
     */
    public void setInOp (boolean inOp)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SET_IN_OP, inOp);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_SET_IN_OP) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
    }

    /**
     *   Get the id of the room assigned to this assault party
     *
     *    @return the id of the room assigned to this assault party
     */
    public int getTargetRoomId ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_GET_TARGET_ROOM);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_GET_TARGET_ROOM) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getTargetRoomId() == -1) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid target room id!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getTargetRoomId();
    }

    /**
     *  Set the distance from the concentration site to the room
     *
     *   @param targetRoomId the id of the room assigned to this assault party
     *   @param roomDistance the distance from the concentration site to the room
     */
    public void setTargetRoomId (int targetRoomId, int roomDistance)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SET_TARGET_ROOM, targetRoomId, roomDistance);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_SET_TARGET_ROOM) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
    }

    /**
     *   Get the number of thieves in the assault party
     *
     *     @return the number of thieves in the assault party
     */
    public int numOfThieves ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_NUM_THIEVES);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_NUM_THIEVES) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getNumOfThieves() == -1) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid number of thieves!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getNumOfThieves();
    }

    /**
     *   Leve the assault party by setting the id of thief in array as -1.
     *
     *     @param thiefId the id of the thief that leaves the assault party
     */
    public void leaveTheAssaultParty (int thiefId)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_LEAVE_PARTY, thiefId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_LEAVE_PARTY) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
    }

    /**
     *   Check if the thief brings a canvas.
     *
     *     @param thiefId the id of the thief
     *     @return true if the thief brings a canvas, false otherwise
     */
    public boolean getBringsCanvas (int thiefId)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_GET_BRINGS_CANVAS, thiefId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_GET_BRINGS_CANVAS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getBringsCanvas();
    }

    /**
     *   Set if the thief brings a canvas.
     *
     *     @param thiefId the id of the thief
     *     @param canvas true if the thief brings a canvas, false otherwise
     */
    public void setBringsCanvas (int thiefId, boolean canvas)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;     //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_SET_BRINGS_CANVAS, thiefId, canvas);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_SET_BRINGS_CANVAS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
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
        outMessage = new Message(MessageType.REQ_PARTY_SHUT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_PARTY_SHUT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }
}
