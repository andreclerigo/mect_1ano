package serverSide.sharedRegions;

import clientSide.entities.MasterThiefStates;
import clientSide.entities.OrdinaryThiefStates;
import commInfra.Message;
import commInfra.MessageException;
import commInfra.MessageType;

/**
 *    Interface to the General Repository.
 *
 *    It is responsible to validate and process the incoming message, execute the corresponding method on the
 *    General Repository and generate the outgoing message.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */
public class GeneralReposInterface {

    /**
     * Reference to the General Repository.
     */
    private final GeneralRepos repos;

    /**
     * Instantiation of an interface to the General Repository.
     * @param repos reference to the General Repository
     */
    public GeneralReposInterface(GeneralRepos repos) {
        this.repos = repos;
    }

    /**
     * Processing of messages by executing the corresponding task.
     * Generation of a reply message.
     *
     * @param inMessage incoming message with the request
     * @return reply message
     * @throws MessageException if the incoming message is not valid
     */
    public Message processAndReply(Message inMessage) throws MessageException{
        // outgoing message
        Message outMessage = null;

        /* Validation of the incoming message */
        switch(inMessage.getMsgType()){
            /* Verify only the messages type */
            case MessageType.REQ_SET_ORDINARY_THIEF_STATE:
                if (inMessage.getOrdinaryThiefState() < OrdinaryThiefStates.CONCENTRATION_SITE || inMessage.getOrdinaryThiefState() > OrdinaryThiefStates.CONTROL_SITE)
                    throw new MessageException("Invalid Ordinary Thief state!", inMessage);
                break;
            case MessageType.REQ_SET_MASTER_THIEF_STATE:
                if (inMessage.getMasterThiefState() < MasterThiefStates.PLANNING_HEIST || inMessage.getMasterThiefState() > MasterThiefStates.PRESENTING_REPORT)
                    throw new MessageException("Invalid Master Thief state!", inMessage);
                break;
            case MessageType.REQ_SET_ORDINARY_THIEF_POS:
            case MessageType.REQ_SET_UPDATE_CANVAS_IN_ROOM:
            case MessageType.REQ_SET_BRINGS:
            case MessageType.REQ_GIVE_CANVAS:
            case MessageType.REQ_SET_ASSAULT_PARTY_THIEVES_ID:
            case MessageType.REQ_SET_ASSAULT_PARTY_TARGET_ROOM_ID:
            case MessageType.REQ_SET_CANVAS_RESULT:
            case MessageType.REQ_SET_PAINTINGS_PER_ROOM:
            case MessageType.REQ_SET_DISTANCE_PER_ROOM:
            case MessageType.REQ_SET_ORDINARY_THIEVES_AGILITY:
            case MessageType.REQ_PRINT_TAIL:
            case MessageType.REQ_GENERAL_REPO_SHUT:
                break;
            default:
                throw new MessageException("Invalid message type!", inMessage);
        }

        /* Processing of the incoming message */
        switch (inMessage.getMsgType()){
            case MessageType.REQ_SET_ORDINARY_THIEVES_AGILITY:
                repos.setOrdinaryThievesAgility(inMessage.getOrdinaryThievesAgility());
                outMessage = new Message(MessageType.REP_SET_ORDINARY_THIEVES_AGILITY);
                break;
            case MessageType.REQ_SET_PAINTINGS_PER_ROOM:
                repos.setPaintingsPerRoom(inMessage.getPaintingsPerRoom());
                outMessage = new Message(MessageType.REP_SET_PAINTINGS_PER_ROOM);
                break;
            case MessageType.REQ_SET_DISTANCE_PER_ROOM:
                repos.setDistancePerRoom(inMessage.getDistancePerRoom());
                outMessage = new Message(MessageType.REP_SET_DISTANCE_PER_ROOM);
                break;
            case MessageType.REQ_SET_MASTER_THIEF_STATE:
                repos.setMasterThiefState(inMessage.getMasterThiefState());
                outMessage = new Message(MessageType.REP_SET_MASTER_THIEF_STATE);
                break;
            case MessageType.REQ_SET_ORDINARY_THIEF_STATE:
                repos.setOrdinaryThiefState(inMessage.getOrdinaryThiefId(), inMessage.getOrdinaryThiefState());
                outMessage = new Message(MessageType.REP_SET_ORDINARY_THIEF_STATE);
                break;
            case MessageType.REQ_SET_ORDINARY_THIEF_POS:
                repos.setOrdinaryThiefPos(inMessage.getOrdinaryThiefId(), inMessage.getOrdinaryThiefPos());
                outMessage = new Message(MessageType.REP_SET_ORDINARY_THIEF_POS);
                break;
            case MessageType.REQ_SET_UPDATE_CANVAS_IN_ROOM:
                repos.setUpdateInRoom(inMessage.getRoomId(), inMessage.getUpdateInRoom(inMessage.getRoomId()));
                outMessage = new Message(MessageType.REP_SET_UPDATE_CANVAS_IN_ROOM);
                break;
            case MessageType.REQ_SET_BRINGS:
                repos.setBringsCanvas(inMessage.getOrdinaryThiefId(), inMessage.getBringsCanvas());
                outMessage = new Message(MessageType.REP_SET_BRINGS);
                break;
            case MessageType.REQ_GIVE_CANVAS:
                repos.giveCanvas(inMessage.getOrdinaryThiefId(), inMessage.getBringsCanvas());
                outMessage = new Message(MessageType.REP_GIVE_CANVAS);
                break;
            case MessageType.REQ_SET_ASSAULT_PARTY_THIEVES_ID:
                repos.setAssaultPartyThievesId(inMessage.getOrdinaryThiefId(), inMessage.getAssaultPartyId());
                outMessage = new Message(MessageType.REP_SET_ASSAULT_PARTY_THIEVES_ID);
                break;
            case MessageType.REQ_SET_ASSAULT_PARTY_TARGET_ROOM_ID:
                repos.setAssaultPartyTargetRoomId(inMessage.getAssaultPartyId(), inMessage.getTargetRoomId());
                outMessage = new Message(MessageType.REP_SET_ASSAULT_PARTY_TARGET_ROOM_ID);
                break;
            case MessageType.REQ_SET_CANVAS_RESULT:
                repos.setCanvasResult(inMessage.getCanvasResult());
                outMessage = new Message(MessageType.REP_SET_CANVAS_RESULT);
                break;
            case MessageType.REQ_PRINT_TAIL:
                repos.printTail();
                outMessage = new Message(MessageType.REP_PRINT_TAIL);
                break;
            case MessageType.REQ_GENERAL_REPO_SHUT:
                repos.shutdown();
                outMessage = new Message(MessageType.REP_GENERAL_REPO_SHUT);
                break;
        }

        return outMessage;
    }
}