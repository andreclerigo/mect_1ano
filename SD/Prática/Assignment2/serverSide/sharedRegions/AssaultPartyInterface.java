package serverSide.sharedRegions;

import clientSide.entities.MasterThiefStates;
import clientSide.entities.OrdinaryThiefStates;
import commInfra.Message;
import commInfra.MessageException;
import commInfra.MessageType;
import serverSide.entities.AssaultPartyClientProxy;

/**
 *   Interface to the Assault Party
 *   It is responsible to validate and process the incoming message, execute the corresponding method on the
 *   Control Site and generate the outgoing message.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class AssaultPartyInterface
{
    /**
     *   Reference to the Assault Party.
     */
    private final AssaultParty assaultParty;

    /**
     *   Instantiation of an interface to the Assault Party.
     *
     *     @param assaultParty reference to the assault party
     */
    public AssaultPartyInterface (AssaultParty assaultParty)
    {
        this.assaultParty = assaultParty;
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
            case MessageType.REQ_CRAWL_IN:
                if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CRAWLING_INWARDS || inMessage.getOrdinaryThiefId() == -1)
                    throw new MessageException("Invalid Ordinary Thief parameters for Crawl In!", inMessage);
                break;
            case MessageType.REQ_CRAWL_OUT:
                if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CRAWLING_OUTWARDS || inMessage.getOrdinaryThiefId() == -1)
                    throw new MessageException("Invalid Ordinary Thief parameters for Crawl Out!", inMessage);
                break;
            case MessageType.REQ_SEND_PARTY:
                if (inMessage.getMasterThiefState() != MasterThiefStates.ASSEMBLING)
                    throw new MessageException("Invalid parameters to Send Assault Party!", inMessage);
                break;
            case MessageType.REQ_ADD_THIEF:
                if (inMessage.getOrdinaryThiefId() == -1 || inMessage.getOrdinaryThiefAgility() == -1)
                    throw new MessageException("Invalid parameters to Add Thief!", inMessage);
                break;

            case MessageType.REQ_SET_TARGET_ROOM:
                if (inMessage.getTargetRoomId() == -1 || inMessage.getRoomDistance() == -1)
                    throw new MessageException("Invalid parameters to set target room!", inMessage);
                break;
            case MessageType.REQ_LEAVE_PARTY:
                if (inMessage.getOrdinaryThiefId() == -1)
                    throw new MessageException("Invalid parameters to leave the assault party", inMessage);
                break;
            case MessageType.REQ_GET_BRINGS_CANVAS:
            case MessageType.REQ_SET_BRINGS_CANVAS:
                if (inMessage.getOrdinaryThiefId() == -1)
                    throw new MessageException("Invalid parameters to brings canvas", inMessage);
                break;
            // Assault Party Messages that require only type verification
            case MessageType.REQ_NUM_THIEVES:
            case MessageType.REQ_PARTY_SHUT:
            case MessageType.REQ_GET_TARGET_ROOM:
            case MessageType.REQ_GET_IN_OP:
            case MessageType.REQ_SET_IN_OP:
            case MessageType.REQ_GET_PARTY_ID:
            case MessageType.REQ_WAIT_FOR_THIEVES:
                break;
            default:
                throw new MessageException ("Invalid message type!", inMessage);
        }

        /* Processing of the incoming message */
        switch (inMessage.getMsgType()) {
            case MessageType.REQ_CRAWL_IN:
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                assaultParty.crawlIn();
                outMessage = new Message(MessageType.REP_CRAWL_IN, ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefId(), ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefState());
                break;

            case MessageType.REQ_CRAWL_OUT:
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                assaultParty.crawlOut();
                outMessage = new Message(MessageType.REP_CRAWL_OUT, ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefId(), ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefState());
                break;

            case MessageType.REQ_SEND_PARTY:
                ((AssaultPartyClientProxy) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
                assaultParty.sendAssaultParty();
                outMessage = new Message(MessageType.REP_SEND_PARTY, ((AssaultPartyClientProxy) Thread.currentThread()).getMasterThiefState());
                break;

            case MessageType.REQ_ADD_THIEF:
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefAgility(inMessage.getOrdinaryThiefAgility());
                assaultParty.addThief(inMessage.getOrdinaryThiefId(), inMessage.getOrdinaryThiefAgility());
                outMessage = new Message(MessageType.REP_ADD_THIEF, ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefId(), ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefAgility());
                break;

            case MessageType.REQ_GET_PARTY_ID:
                int assPartyId = assaultParty.getAssaultPartyId();
                outMessage = new Message(MessageType.REP_GET_PARTY_ID, assPartyId);
                break;

            case MessageType.REQ_WAIT_FOR_THIEVES:
                assaultParty.waitForThievesBeReady();
                outMessage = new Message(MessageType.REP_WAIT_FOR_THIEVES);
                break;

            case MessageType.REQ_GET_IN_OP:
                boolean inOp = assaultParty.getInOp();
                outMessage = new Message(MessageType.REP_GET_IN_OP, inOp);
                break;

            case MessageType.REQ_SET_IN_OP:
                ((AssaultPartyClientProxy) Thread.currentThread()).setInOp(inMessage.getInOp());
                assaultParty.setInOp(inMessage.getInOp());
                outMessage = new Message(MessageType.REP_SET_IN_OP, ((AssaultPartyClientProxy) Thread.currentThread()).getInOp());
                break;

            case MessageType.REQ_GET_TARGET_ROOM:
                int targetRoomId = assaultParty.getTargetRoomId();
                outMessage = new Message(MessageType.REP_GET_TARGET_ROOM, targetRoomId);
                break;

            case MessageType.REQ_SET_TARGET_ROOM:
                assaultParty.setTargetRoomId(inMessage.getTargetRoomId(), inMessage.getRoomDistance());
                outMessage = new Message(MessageType.REP_SET_TARGET_ROOM);
                break;

            case MessageType.REQ_NUM_THIEVES:
                int numOfThieves = assaultParty.numOfThieves();
                outMessage = new Message(MessageType.REP_NUM_THIEVES, numOfThieves);
                break;

            case MessageType.REQ_LEAVE_PARTY:
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                assaultParty.leaveTheAssaultParty(inMessage.getOrdinaryThiefId());
                outMessage = new Message(MessageType.REP_LEAVE_PARTY, ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefId());
                break;

            case MessageType.REQ_GET_BRINGS_CANVAS:
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                boolean bringsCanvas = assaultParty.getBringsCanvas(inMessage.getOrdinaryThiefId());
                outMessage = new Message(MessageType.REP_GET_BRINGS_CANVAS, ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefId(), bringsCanvas);
                break;

            case MessageType.REQ_SET_BRINGS_CANVAS:
                ((AssaultPartyClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                assaultParty.setBringsCanvas(inMessage.getOrdinaryThiefId(), inMessage.getBringsCanvas());
                outMessage = new Message(MessageType.REP_SET_BRINGS_CANVAS, ((AssaultPartyClientProxy) Thread.currentThread()).getOrdinaryThiefId(),
                        ((AssaultPartyClientProxy) Thread.currentThread()).getBringsCanvas());
                break;

            case MessageType.REQ_PARTY_SHUT:
                assaultParty.shutdown();
                outMessage = new Message(MessageType.REP_PARTY_SHUT);
                break;
        }

        return (outMessage);
    }
}
