package clientSide.stubs;

import clientSide.entities.*;
import commInfra.ClientCom;
import commInfra.Message;
import commInfra.MessageType;

/**
 *   Stub to the Concentration Site.
 *   It instantiates a remote reference to the concetration site.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class ConcetSiteStub
{
    /**
     * Name of the platform where is located the bar server
     */
    private String serverHostName;

    /**
     * Port number for listening to service requests
     */
    private int serverPortNumb;

    /**
     * Instantiation of a stub to the Bar.
     *
     * @param serverHostName name of the platform where is located the bar server
     * @param serverPortNumb port number for listening to service requests
     */
    public ConcetSiteStub(String serverHostName, int serverPortNumb)
    {
        this.serverHostName = serverHostName;
        this.serverPortNumb = serverPortNumb;
    }

    /**
     *   Operation prepare assault party, the thief checks if the waiting queue has enough thieves to assemble an assault party.
     *
     *     @param assaultPartyId id of the assault party to be prepared
     *     @param roomId id of the exhibition room assigned to the assault party
     */
    public void prepareAssaultParty (int assaultPartyId, int roomId)
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;    //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {
            }
        }

        outMessage = new Message(MessageType.REQ_PREP_PARTY, ((MasterThief) Thread.currentThread()).getMasterThiefState(), assaultPartyId, roomId);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_PREP_PARTY) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getMasterThiefState() != MasterThiefStates.ASSEMBLING) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid Master Thief state!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        ((MasterThief) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
        // Close communication channel
        com.close();
    }

    /**
     *   Operation prepare excursion, the thief will join the party.
     *
     *     @return the id of the assault party id.
     */
    public int prepareExcursion ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;    //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {
            }
        }

        int ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefId();
        int ordinaryThiefState = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefState();
        int ordinaryThiefDisplacement = ((OrdinaryThief) Thread.currentThread()).getDisplacement();

        outMessage = new Message(MessageType.REQ_PREP_EXCUR, ordinaryThiefId, ordinaryThiefState, ordinaryThiefDisplacement);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_PREP_EXCUR) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid Ordinary Thief Id!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CRAWLING_INWARDS) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid Ordinary Thief state!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getAssaultPartyId() == -1) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid Assault Party Id!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((OrdinaryThief) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());

        return inMessage.getAssaultPartyId();
    }

    /**
     *   Operation that decides if the ordinary thief is needed. It will register the thief in the waiting queue and wait for the master thief to pop him from the queue.
     *
     *     @return either if the heist is over or not
     */
    public boolean amINeeded ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;    //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {
            }
        }

        int ordinaryThiefId = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefId();
        int ordinaryThiefState = ((OrdinaryThief) Thread.currentThread()).getOrdinaryThiefState();

        outMessage = new Message(MessageType.REQ_NEEDED, ordinaryThiefId, ordinaryThiefState);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_NEEDED) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefId() != ordinaryThiefId) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid Ordinary Thief Id!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CONCENTRATION_SITE) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid Ordinary Thief state!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }

        // Close communication channel
        com.close();
        ((OrdinaryThief) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());

        return inMessage.getAmINeeded();
    }

    /**
     *   Operation sum up results.
     */
    public void sumUpResults ()
    {
        ClientCom com;                    //Client communication
        Message outMessage, inMessage;    //outGoing and inGoing messages

        com = new ClientCom(serverHostName, serverPortNumb);

        // Wait for a connection to be established
        while (!com.open()) {
            try {
                Thread.sleep ((long) (10));
            } catch (InterruptedException e) {
            }
        }

        outMessage = new Message(MessageType.REQ_SUM_UP);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject();

        // Validate inGoing message type and arguments
        if (inMessage.getMsgType() != MessageType.REP_SUM_UP) {
            System.out.println("Thread " + Thread.currentThread().getName() + ": Invalid message type!");
            System.out.println(inMessage.toString());
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
        outMessage = new Message(MessageType.REQ_CONCET_SHUT);
        com.writeObject(outMessage);
        inMessage = (Message) com.readObject ();
        if (inMessage.getMsgType() != MessageType.REP_CONCET_SHUT) {
            System.out.println("Thread " + Thread.currentThread ().getName () + ": Invalid message type!");
            System.out.println(inMessage.toString());
            System.exit(1);
        }
        com.close();
    }
}
