package serverSide.sharedRegions;

import clientSide.entities.MasterThiefStates;
import clientSide.entities.OrdinaryThiefStates;
import commInfra.Message;
import commInfra.MessageException;
import commInfra.MessageType;
import serverSide.entities.ConcetSiteClientProxy;

/**
 *   Interface to the Concentration Site
 *   It is responsible to validate and process the incoming message, execute the corresponding method on the
 *   Control Site and generate the outgoing message.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class ConcetSiteInterface
{
    /**
     *   Reference to the Concentration Site.
     */
    private final ConcetSite concetSite;

    /**
     *   Instantiation of an interface to the Concentration Site.
     *
     *     @param concetSite reference to the control site
     */
    public ConcetSiteInterface (ConcetSite concetSite)
    {
        this.concetSite = concetSite;
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
            // Master Thief Messages that require type, state, assault id and room id verification
            case MessageType.REQ_PREP_PARTY: 		// Prepare Assault Party Request
                if (inMessage.getMasterThiefState() != MasterThiefStates.DECIDING || inMessage.getAssaultPartyId() == -1 || inMessage.getRoomId() == -1)
                    throw new MessageException("Invalid parameters!", inMessage);
                break;
            // Ordinary Thief Messages that require type, id, state and agility verification
            case MessageType.REQ_PREP_EXCUR:        // Prepare excursion Request
                if (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CONCENTRATION_SITE || inMessage.getOrdinaryThiefId() == -1 || inMessage.getOrdinaryThiefAgility() == -1)
                    throw new MessageException("Invalid Ordinary Thief parameters!", inMessage);
                break;
            // Ordinary Thief Messages that require type and state verification
            case MessageType.REQ_NEEDED:            // Am I needed Request
                // TODO - Check if the state is correct
                if ((inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CONCENTRATION_SITE || inMessage.getOrdinaryThiefId() == -1) && (inMessage.getOrdinaryThiefState() != OrdinaryThiefStates.CONTROL_SITE || inMessage.getOrdinaryThiefId() == -1))
                    throw new MessageException("Invalid Ordinary Thief parameters!", inMessage);
                break;
            // Concentration Site Messages that require only type verification
            case MessageType.REQ_SUM_UP:     		// Sum up results Request
            case MessageType.REQ_CONCET_SHUT:       // Concentration Site shutdown Request
                break;
            default:
                throw new MessageException ("Invalid message type!", inMessage);
        }

        /* Processing of the incoming message */
        switch (inMessage.getMsgType()) {
            case MessageType.REQ_PREP_PARTY:
                ((ConcetSiteClientProxy) Thread.currentThread()).setMasterThiefState(inMessage.getMasterThiefState());
                concetSite.prepareAssaultParty(inMessage.getAssaultPartyId(), inMessage.getRoomId());
                outMessage = new Message(MessageType.REP_PREP_PARTY, ((ConcetSiteClientProxy) Thread.currentThread()).getMasterThiefState());
                break;
            case MessageType.REQ_PREP_EXCUR:
                ((ConcetSiteClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((ConcetSiteClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                ((ConcetSiteClientProxy) Thread.currentThread()).setOrdinaryThiefAgility(inMessage.getOrdinaryThiefAgility());
                int assaultPartyId = concetSite.prepareExcursion();
                outMessage = new Message(MessageType.REP_PREP_EXCUR, inMessage.getOrdinaryThiefId(), ((ConcetSiteClientProxy) Thread.currentThread()).getOrdinaryThiefState(), assaultPartyId);
                break;
            case MessageType.REQ_NEEDED:
                ((ConcetSiteClientProxy) Thread.currentThread()).setOrdinaryThiefState(inMessage.getOrdinaryThiefState());
                ((ConcetSiteClientProxy) Thread.currentThread()).setOrdinaryThiefId(inMessage.getOrdinaryThiefId());
                boolean amINeeded = concetSite.amINeeded();
                outMessage = new Message(MessageType.REP_NEEDED, inMessage.getOrdinaryThiefId(), ((ConcetSiteClientProxy) Thread.currentThread()).getOrdinaryThiefState(), amINeeded);
                break;
            case MessageType.REQ_SUM_UP:
                concetSite.sumUpResults();
                outMessage = new Message(MessageType.REP_SUM_UP);
                break;
            case MessageType.REQ_CONCET_SHUT:
                concetSite.shutdown();
                outMessage = new Message(MessageType.REP_CONCET_SHUT);
                break;
        }

        return (outMessage);
    }
}
