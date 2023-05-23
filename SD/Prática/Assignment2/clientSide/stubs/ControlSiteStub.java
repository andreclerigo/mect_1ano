package clientSide.stubs;

import clientSide.entities.*;
import commInfra.ClientCom;
import commInfra.Message;
import commInfra.MessageType;

/**
 *   Stub to the Control Site.
 *   It instantiates a remote reference to the control site.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class ControlSiteStub
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
    public ControlSiteStub(String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }

    /**
     *   Start the heist to the museum operation.
     */
    public void startOperations ()
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

        outMessage = new Message(MessageType.REQ_START_OP, ((MasterThief) Thread.currentThread()).getMasterThiefState());
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_START_OP) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getMasterThiefState() != MasterThiefStates.DECIDING) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Master Thief state!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((MasterThief) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
    }

    /**
     *   Operation appraise sit. If there is not any room left and all thieves arrived then the assault has ended.
     *   Otherwise, we see if there is any assault party available, if there is the Master Thief assembles a new excursion.
     *
     *     @return the int correspondent of the operation.
     */
    public char appraiseSit ()
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

        outMessage = new Message(MessageType.REQ_APPRAISE_SIT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_APPRAISE_SIT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getOperation() == '?') {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid operation!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getOperation();
    }

    /**
     *   Get the id of the next assault party to be used (should never return -1 because this function is only called when there is at least 1 assault party available).
     *
     *     @return id of assault party
     */
    public int getAssaultId ()
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

        outMessage = new Message(MessageType.REQ_GET_ASSAULT_ID);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_GET_ASSAULT_ID) {
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
     *   Get the id of an exhibition room that is not empty and is not assigned to an assault party (should never return -1 because this function is only called when at least 1 room is available)..
     *
     *     @return id of exhibition room
     */
    public int getRoomId ()
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

        outMessage = new Message(MessageType.REQ_GET_ROOM_ID);
        com.writeObject (outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_GET_ROOM_ID) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getRoomId() == -1) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid exhibition room id!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        return inMessage.getRoomId();
    }

    /**
     *   Operation collect a canvas, the master thief retrieves the information from the FIFO and updates the empty rooms and the number of canvas.
     *   The thief "leaves" the assault party and if there are no more thieves in the assault party, the assault party leaves the operation.
     */
    public void collectACanvas ()
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

        outMessage = new Message(MessageType.REQ_COLLECT_CANVAS, ((MasterThief) Thread.currentThread()).getMasterThiefState());
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_COLLECT_CANVAS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getMasterThiefState() != MasterThiefStates.DECIDING) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Master Thief state!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((MasterThief) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
    }

    /**
     *   Operation hand a canvas, add the thief id, assault party id and bringsCanvas flag to the waiting thieves queue.
     *
     *     @param assaultPartyId id of the assault party
     */
    public void handACanvas (int assaultPartyId)
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

        outMessage = new Message(MessageType.REQ_HAND_CANVAS, ordinaryThiefId, ordinaryThiefState, assaultPartyId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_HAND_CANVAS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief Id!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CONTROL_SITE) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief state!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((OrdinaryThief) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
    }

    /**
     *   Operation take a rest, change the master thief state and wait for thieves to go arrive to the waiting queue.
     */
    public void takeARest ()
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

        outMessage = new Message(MessageType.REQ_TAKE_REST, ((MasterThief) Thread.currentThread()).getMasterThiefState());
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_TAKE_REST) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getMasterThiefState() != MasterThiefStates.WAITING) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((MasterThief) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
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
        outMessage = new Message(MessageType.REQ_CONTROL_SHUT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_CONTROL_SHUT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }
}
