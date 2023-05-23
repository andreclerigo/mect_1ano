package serverSide.sharedRegions;

import clientSide.entities.MasterThiefStates;
import clientSide.entities.OrdinaryThiefStates;
import commInfra.Message;
import commInfra.MessageException;
import commInfra.MessageType;
import serverSide.entities.ControlSiteClientProxy;

/**
 *   Interface to the Control Site
 *   It is responsible to validate and process the incoming message, execute the corresponding method on the
 *   Control Site and generate the outgoing message.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class ControlSiteInterface
{
    /**
     *   Reference to the Control Site.
     */
    private final ControlSite controlSite;

    /**
     *   Instantiation of an interface to the Control Site.
     *
     *     @param controlSite reference to the control site
     */
    public ControlSiteInterface (ControlSite controlSite)
    {
        this.controlSite = controlSite;
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
            // Master Thief Messages that require type and state verification
            case MessageType.REQ_START_OP: 		    // Start operations Request
                if (inMessage.getMasterThiefState() != MasterThiefStates.PLANNING_HEIST)
                    throw new MessageException("Invalid Master Thief state!", inMessage);
                break;
            case MessageType.REQ_COLLECT_CANVAS:    // Collect a Canvas Request
                if (inMessage.getMasterThiefState() != MasterThiefStates.WAITING)
                    throw new MessageException("Invalid Master Thief state!", inMessage);
                break;
            case MessageType.REQ_TAKE_REST:         // Take a Rest Request
                if (inMessage.getMasterThiefState() != MasterThiefStates.DECIDING)
                    throw new MessageException("Invalid Master Thief state!", inMessage);
                break;
            // Control Site Messages that require only type verification
            case MessageType.REQ_APPRAISE_SIT: 		// Appraise Sit Request
            case MessageType.REQ_GET_ASSAULT_ID:    // Get Assault id Request
            case MessageType.REQ_GET_ROOM_ID:       // Get Room id Request
            case MessageType.REQ_CONTROL_SHUT:      // Control Site Shutdown
                break;
            // Ordinary Thief Messages that require type, id verification, state and assaultParty
            case MessageType.REQ_HAND_CANVAS:	    // Hand a Canvas Request
                if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CRAWLING_OUTWARDS || inMessage.getOrdinaryThiefId() == -1 || inMessage.getAssaultPartyId() == -1){
                    throw new MessageException("Invalid Ordinary Thief parameters!", inMessage);
                }
                break;
            default:
                throw new MessageException ("Invalid message type!", inMessage);
        }

        /* Processing of the incoming message */
        switch (inMessage.getMsgType()) {
            case MessageType.REQ_START_OP:
                ((ControlSiteClientProxy) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
                controlSite.startOperations();
                outMessage = new Message(MessageType.REP_START_OP, ((ControlSiteClientProxy) Thread.currentThread()).getMasterThiefState());
                break;
            case MessageType.REQ_COLLECT_CANVAS:
                ((ControlSiteClientProxy) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
                controlSite.collectACanvas();
                outMessage = new Message(MessageType.REP_COLLECT_CANVAS, ((ControlSiteClientProxy) Thread.currentThread()).getMasterThiefState());
                break;
            case MessageType.REQ_TAKE_REST:
                ((ControlSiteClientProxy) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
                controlSite.takeARest();
                outMessage = new Message(MessageType.REP_TAKE_REST, ((ControlSiteClientProxy) Thread.currentThread()).getMasterThiefState());
                break;
            case MessageType.REQ_APPRAISE_SIT:
                char oper = controlSite.appraiseSit();
                outMessage = new Message(MessageType.REP_APPRAISE_SIT, oper);
                break;
            case MessageType.REQ_GET_ASSAULT_ID:
                int assaultPartyId = controlSite.getAssaultId();
                outMessage = new Message(MessageType.REP_GET_ASSAULT_ID, assaultPartyId);
                break;
            case MessageType.REQ_GET_ROOM_ID:
                int roomId = controlSite.getRoomId();
                outMessage = new Message(MessageType.REP_GET_ROOM_ID, roomId);
                break;
            case MessageType.REQ_CONTROL_SHUT:
                controlSite.shutdown();
                outMessage = new Message(MessageType.REP_CONTROL_SHUT);
                break;
            case MessageType.REQ_HAND_CANVAS:
                ((ControlSiteClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((ControlSiteClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                controlSite.handACanvas(inMessage.getOrdinaryThiefId(), inMessage.getAssaultPartyId());
                outMessage = new Message(MessageType.REP_HAND_CANVAS, inMessage.getOrdinaryThiefId(), ((ControlSiteClientProxy) Thread.currentThread()).getOrdinaryThiefState());
                break;
        }

        return (outMessage);
    }
}
