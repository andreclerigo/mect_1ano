package clientSide.stubs;

import clientSide.entities.*;
import commInfra.ClientCom;
import commInfra.Message;
import commInfra.MessageType;

/**
 *   Stub to the Museum.
 *   It instantiates a remote reference to the museum.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class MuseumStub
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
    public MuseumStub(String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }

    /**
     *   Operation reverse direction, the state of the ordinary thief is changed to crawling outwards.
     */
    public void reverseDirection ()
    {
        ClientCom com;					//Client communication
        Message outMessage, inMessage; 	//outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while(!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        int ordinaryThiefState = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefState();
        int ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefId();
        outMessage = new Message(MessageType.REQ_REV_DIR, ordinaryThiefId, ordinaryThiefState);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_REV_DIR) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief state!");
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
     *   Get the distance of the room from the concentration site.
     *
     *     @param roomId the id of the room that we are in
     *     @return the distance of the room from the concentration site
     */
    public int getRoomDistance (int roomId)
    {
        ClientCom com;					//Client communication
        Message outMessage, inMessage; 	//outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while(!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        outMessage = new Message(MessageType.REQ_ROOM_DIST, roomId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_ROOM_DIST) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        if (inMessage.getRoomDistance() == -1) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Room distance!");
            System.out.println(inMessage.toString ());
            System.exit(1);
        }

        // Close communication channel
        com.close();

        return inMessage.getRoomDistance();
    }

    /**
     *   Operation roll a canvas, check if there are any canvas in the room and if there are, take one.
     *
     *     @param roomId the id of the room that we are in
     *     @param assaultPartyId the id of the assault party that we are in
     */
    public void rollACanvas (int roomId, int assaultPartyId)
    {
        ClientCom com;					//Client communication
        Message outMessage, inMessage; 	//outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while(!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {}
        }

        int ordinaryThiefState = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefState();
        int ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefId();

        outMessage = new Message(MessageType.REQ_ROLL_CANVAS, ordinaryThiefId, ordinaryThiefState, assaultPartyId, roomId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_ROLL_CANVAS) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString ());
            System.exit (1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid Ordinary Thief state!");
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
        outMessage = new Message(MessageType.REQ_MUSEUM_SHUT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_MUSEUM_SHUT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }
}
