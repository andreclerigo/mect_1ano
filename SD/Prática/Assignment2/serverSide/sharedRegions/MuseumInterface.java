package serverSide.sharedRegions;

import clientSide.entities.OrdinaryThiefStates;
import commInfra.Message;
import commInfra.MessageException;
import commInfra.MessageType;
import serverSide.entities.MuseumClientProxy;

/**
 *   Interface to the Museum
 *   It is responsible to validate and process the incoming message, execute the corresponding method on the
 *   Control Site and generate the outgoing message.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class MuseumInterface
{
    /**
     *   Reference to the Museum.
     */
    private final Museum museum;

    /**
     *   Instantiation of an interface to the Museum.
     *
     *     @param museum reference to the museum
     */
    public MuseumInterface (Museum museum)
    {
        this.museum = museum;
    }

    /**
     *   Processing of the incoming messages
     *   Validation, execution of the corresponding method and generation of the outgoing message.
     *
     * 	   @param inMessage service request
     * 	   @return service reply
     * 	   @throws MessageException if incoming message was not valid
     */
    public Message processAndReply (Message inMessage) throws MessageException
    {
        //outGoing message
        Message outMessage = null;

        /* Validation of the incoming message */

        switch (inMessage.getMsgType()) {
            // Ordinary Thief Messages that require type and room id verification
            case MessageType.REQ_ROOM_DIST:
            case MessageType.REQ_GET_ROOM_ID: 		// Get Room Id Request
                if (inMessage.getRoomId() == -1)
                    throw new MessageException ("Invalid room id!", inMessage);
                break;
            // Messages that only require type verification
            case MessageType.REQ_MUSEUM_SHUT: 	    // Museum Shutdown Request
                break;
            // Ordinary Thief Messages that require type and Thief id and Thief state verification
            case MessageType.REQ_REV_DIR: 		    // Reverse Direction Request
                if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.AT_A_ROOM || inMessage.getOrdinaryThiefId() == -1){
                    throw new MessageException("Invalid Ordinary Thief parameters!", inMessage);
                }
                break;
            // Ordinary Thief Messages that require type and Thief id, Thief state, room id and assaultPartyId verification
            case MessageType.REQ_ROLL_CANVAS:		// Roll Canvas Request
                if (inMessage.getOrdinaryThiefState() < OrdinaryThiefStates.CRAWLING_INWARDS || inMessage.getOrdinaryThiefId() == -1 || inMessage.getRoomId() == -1 || inMessage.getAssaultPartyId() == -1)
                    throw new MessageException("Invalid parameters!", inMessage);
                break;
            default:
                throw new MessageException ("Invalid message type!", inMessage);
        }

        /* Processing of the incoming message */
        switch (inMessage.getMsgType()) {
            case MessageType.REQ_ROOM_DIST:
                int roomDistance = museum.getRoomDistance(inMessage.getRoomId());
                outMessage = new Message(MessageType.REP_ROOM_DIST, roomDistance);
                break;
            case MessageType.REQ_REV_DIR:
                ((MuseumClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((MuseumClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                museum.reverseDirection();
                outMessage = new Message(MessageType.REP_REV_DIR, inMessage.getOrdinaryThiefId(), ((MuseumClientProxy) Thread.currentThread()).getOrdinaryThiefState());
                break;
            case MessageType.REQ_ROLL_CANVAS:
                ((MuseumClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((MuseumClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                museum.rollACanvas(inMessage.getRoomId(), inMessage.getAssaultPartyId());
                outMessage = new Message(MessageType.REP_ROLL_CANVAS, inMessage.getOrdinaryThiefId(), ((MuseumClientProxy) Thread.currentThread()).getOrdinaryThiefState());
                break;
            case MessageType.REQ_MUSEUM_SHUT:
                museum.shutdown();
                outMessage = new Message(MessageType.REP_MUSEUM_SHUT);
                break;
        }

        return (outMessage);
    }
}