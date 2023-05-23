package commInfra;

import clientSide.main.SimulPar;

import java.util.Arrays;
import java.io.*;

/**
 *   Internal structure of the exchanged messages.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class Message implements Serializable
{
   /**
    *   Serialization key.
    */
   private static final long serialVersionUID = 2021L;

   /**
    *   Message type.
    */
   private int msgType = -1;

   /**
    *   Master thief state.
    */
   private int masterThiefState = -1;

   /**
    *   Ordinary Thief identification.
    */
   private int ordinaryThiefId = -1;

   /**
    *   Ordinary Thief state.
    */
   private int ordinaryThiefState = -1;

   /**
    *   Ordinary Thief position.
    */
   private int ordinaryThiefPos = -1;

    /**
     *   Assault Party identification.
     */
   private int assaultPartyId = -1;

    /**
     *   Room distance.
     */
   private int roomDistance = -1;

    /**
     *   Room identification.
     */
   private int roomId = -1;

    /**
     *   Ordinary Thief agility.
     */
   private int ordinaryThiefAgility = -1;

    /**
     *   The state of operation of the assault party.
     */
   private boolean inOp = false;

    /**
     *   Control variable to see if the thief is needed.
     */
   private boolean amINeeded = false;

    /**
     *   Target room identification.
     */
   private int targetRoomId = -1;

    /**
     *   Number of thieves in the assault party.
     */
   private int numOfThieves = -1;

    /**
     *   Thief carrying a canvas.
     */
   private boolean bringsCanvas = false;

    /**
     *   Next operation identifier.
     */
   private char oper = '?';

  /**
   *   Name of the logging file.
   */
   private String fName = null;

  /**
   *   Number of iterations of the customer life cycle.
   */
   private int nIter = -1;

  /**
   *   Number of canvas in each room of the museum.
   */
   private int[] canvas_per_room = new int[SimulPar.ROOMS_NUMBER];

  /**
   *   Distance to each room of the museum.
   */
   private int[] distance_per_room = new int[SimulPar.ROOMS_NUMBER];

  /**
   *   Agility of the ordinary thieves.
   */
   private int[] ordinaryThievesAgility = new int[SimulPar.ORDINARY_THIEVES];

  /**
   *   Number of stolen canvas.
   */
   private int canvasStolen = -1;

    /**
     *   Message instantiation (form 1).
     *     - ControlSiteStub - appraiseSit()
     *                       - getAssaultId()
     *                       - getRoomId()
     *     - ConcetSiteStub - sumUpResults()
     *     - AssaultPartyStub - waitForThievesToBeReady()
     *                        - getAssaultPartyId()
     *                        - getInOp()
     *                        - getNumOfThieves()
     *
     *     @param type message type
     */
    public Message (int type)
   {
      msgType = type;
   }

    /**
     *   Message instantiation (form 2).
     *     - ControlSiteStub - startOperations()
     *                       - collectCanvas()
     *                       - takeARest()
     *                       - getRoomId()            - REP_GET_ROOM_ID
     *     - MuseumStub      - getRoomDistance()      - REP_ROOM_DIST
     *     - AssaultPartyStub - sendAssaultParty()
     *                        - getTargetRoomId()
     *                        - leaveTheAssaultParty()
     *                        - getBringsCanvas()
     *                        - getAssaultPartyId()   - REP_GET_ASSAULT_ID / REP_GET_PARTY_ID
     *     - GeneralReposStub - setOrdinaryThiefPos() - REP_SET_ORDINARY_THIEF_POS
     *                        - sumUpResults()        - REP_SET_CANVAS_RESULT
     *     @param type message type
     *     @param val1 value 1
     */
    public Message (int type, int val1)
    {
        msgType = type;

        if (getEntitiesFromMessageType(type) == 1) {
            if (msgType == MessageType.REQ_GET_BRINGS_CANVAS){
                ordinaryThiefId = val1;
            } else {
                masterThiefState = val1;
            }

        } else if (getEntitiesFromMessageType(type) == 2) {
            ordinaryThiefId = val1;

        } else if (getEntitiesFromMessageType(type) == 3){
            if (msgType == MessageType.REP_GET_ASSAULT_ID || msgType == MessageType.REP_GET_PARTY_ID){
                assaultPartyId = val1;
            } else if (msgType == MessageType.REP_GET_ROOM_ID || msgType == MessageType.REQ_ROOM_DIST){
                roomId = val1;
            } else if (msgType == MessageType.REP_GET_TARGET_ROOM){
                targetRoomId = val1;
            } else if (msgType == MessageType.REP_NUM_THIEVES) {
                numOfThieves = val1;
            } else if (msgType == MessageType.REP_ROOM_DIST) {
                roomDistance = val1;
            } else if (msgType == MessageType.REQ_LEAVE_PARTY || msgType == MessageType.REP_LEAVE_PARTY || msgType == MessageType.REQ_GET_BRINGS_CANVAS){
                ordinaryThiefId = val1;
            } else {
                masterThiefState = val1;
            }

        } else if (getEntitiesFromMessageType(type) == 4) {
            if (msgType == MessageType.REP_SET_ORDINARY_THIEF_POS){
                ordinaryThiefPos = val1;
            } else if (msgType == MessageType.REQ_SET_CANVAS_RESULT){
                canvasStolen = val1;
            } else if (msgType == MessageType.REQ_SET_MASTER_THIEF_STATE){
                masterThiefState = val1;
            }
        }
    }

    /**
     *   Message instantiation (form 3).
     *     - AssaultPartyStub - setInOp()
     *     - ConcetSiteStub - REP_NEEDED
     *
     *     @param type message type
     *     @param val1 value 1
     */
    public Message (int type, boolean val1)
    {
        msgType = type;

        if (getEntitiesFromMessageType(type) == 3) {
            if (msgType == MessageType.REP_NEEDED){
                amINeeded = val1;
            } else {
                inOp = val1;
            }
        }
    }

   /**
    *   Message instantiation (form 4).
    *     - MuseumStub - reverseDirection()
    *     - ConcetSiteStub - amINeeded()
    *     - AssaultPartyStub - crawlIn()
    *                        - crawlOut()
    *                        - addThief()
    *                        - setTargetRoomId()
    *
    *     @param type message type
    *     @param val1 value 1
    *     @param val2 value 2
    */
    public Message (int type, int val1, int val2)
    {
        msgType = type;

        if (getEntitiesFromMessageType(type) == 1) {
            targetRoomId = val1;
            roomDistance = val2;

        } else if (getEntitiesFromMessageType(type) == 2) {
            ordinaryThiefId = val1;
            ordinaryThiefState = val2;

        } else if (getEntitiesFromMessageType(type) == 3){

            if (msgType == MessageType.REQ_ADD_THIEF || msgType == MessageType.REP_ADD_THIEF) {
                ordinaryThiefId = val1;
                ordinaryThiefAgility = val2;
            } else if (msgType == MessageType.REQ_SET_TARGET_ROOM){
                targetRoomId = val1;
                roomDistance = val2;
            } else {
                ordinaryThiefId = val1;
                ordinaryThiefState = val2;
            }

        } else {
            if (msgType == MessageType.REQ_SET_ASSAULT_PARTY_TARGET_ROOM_ID){
                assaultPartyId = val1;
                targetRoomId = val2;
            } else if (msgType == MessageType.REQ_SET_ORDINARY_THIEF_STATE){
                ordinaryThiefId = val1;
                ordinaryThiefState = val2;
            } else if (msgType == MessageType.REQ_SET_ORDINARY_THIEF_POS){
                ordinaryThiefId = val1;
                ordinaryThiefPos = val2;
            } else if (msgType == MessageType.REQ_SET_UPDATE_CANVAS_IN_ROOM){
                roomId = val1;
                canvas_per_room[roomId] = val2;
            } else {
                ordinaryThiefId = val1;
                assaultPartyId = val2;
            }
        }
    }

    /**
     *   Message instantiation (form 5).
     *     - AssaultPartyStub - setBringsCanvas()
     *
     *     @param type message type
     *     @param val1 value 1
     *     @param val2 value 2
     */
    public Message (int type, int val1, boolean val2)
    {
        msgType = type;

        if (getEntitiesFromMessageType(type) == 1) {
            ordinaryThiefId = val1;
            bringsCanvas = val2;
        } else if (getEntitiesFromMessageType(type) == 3){
            if (msgType == MessageType.REP_GET_BRINGS_CANVAS || msgType == MessageType.REQ_SET_BRINGS_CANVAS || msgType == MessageType.REP_SET_BRINGS_CANVAS){
                ordinaryThiefId = val1;
                bringsCanvas = val2;
            }
        } else if (getEntitiesFromMessageType(type) == 4){
            if (msgType == MessageType.REQ_GIVE_CANVAS || msgType == MessageType.REQ_SET_BRINGS){
                ordinaryThiefId = val1;
                bringsCanvas = val2;
            }
        }
    }

    /**
     *   Message instantiation (form 6).
     *     - ControlSiteStub - handACanvas()
     *     - ConcetSiteStub - prepareAssaultParty()
     *                      - prepareExcursion()
     *
     *     @param type message type
     *     @param val1 value 1
     *     @param val2 value 2
     *     @param val3 value 3
     */
    public Message (int type, int val1, int val2, int val3)
    {
        msgType = type;

        if (getEntitiesFromMessageType(type) == 1) {
            masterThiefState = val1;
            assaultPartyId = val2;
            roomId = val3;

        } else if (getEntitiesFromMessageType(type) == 2) {

                if (msgType == MessageType.REQ_HAND_CANVAS || msgType == MessageType.REP_PREP_EXCUR){
                    ordinaryThiefId = val1;
                    ordinaryThiefState = val2;
                    assaultPartyId = val3;
                } else if (msgType == MessageType.REQ_PREP_EXCUR){
                    ordinaryThiefId = val1;
                    ordinaryThiefState = val2;
                    ordinaryThiefAgility = val3;
                }

        } else if (getEntitiesFromMessageType(type) == 3){
            if (msgType == MessageType.REQ_PREP_EXCUR || msgType == MessageType.REP_PREP_EXCUR){
                ordinaryThiefId = val1;
                ordinaryThiefState = val2;
                ordinaryThiefAgility = val3;
            } else if (msgType == MessageType.REQ_PREP_PARTY){
                masterThiefState = val1;
                assaultPartyId = val2;
                roomId = val3;
            } else if (msgType == MessageType.REQ_HAND_CANVAS){
                ordinaryThiefId = val1;
                ordinaryThiefState = val2;
                assaultPartyId = val3;
            }

        }
    }

   /**
    *   Message instantiation (form 7).
    *     - MuseumStub - rollACanvas()
    *
    *     @param type message type
    *     @param val1 value 1
    *     @param val2 value 2
    *     @param val3 value 3
    *     @param val4 value 4
    */
   public Message (int type, int val1, int val2, int val3, int val4)
   {
      msgType = type;

      if (getEntitiesFromMessageType(type) == 2) {
          ordinaryThiefId = val1;
          ordinaryThiefState = val2;
          assaultPartyId = val3;
          roomId = val4;

      } else if (getEntitiesFromMessageType(type) == 3) {
          if (msgType == MessageType.REQ_ROLL_CANVAS){
              ordinaryThiefId = val1;
              ordinaryThiefState = val2;
              assaultPartyId = val3;
              roomId = val4;
          }
      }
   }

    /**
     *  Message instantiation (form 8).
     *    - GeneralReposStub - setOrdinaryThievesAgility()
     *    - GeneralReposStub - setPaintingsPerRoom()
     *    - GeneralReposStub - setRoomDistance()
     *
     *    @param type message type
     *    @param val1 value 1
     */
   public Message (int type, int[] val1){
         msgType = type;

         if (getEntitiesFromMessageType(type) == 4){
            if(msgType == MessageType.REQ_SET_ORDINARY_THIEVES_AGILITY){
                ordinaryThievesAgility = val1;
            } else if (msgType == MessageType.REQ_SET_DISTANCE_PER_ROOM){
                distance_per_room = val1;
            } else if (msgType == MessageType.REQ_SET_PAINTINGS_PER_ROOM){
                canvas_per_room = val1;
            }
         }
   }

    /**
     *  Message instantiation (form 9).
     *      - ControlSiteStub - appraiseSit() - REPLY
     *   @param type message type
     *   @param val1 value 1
     */
    public Message (int type, char val1){
        msgType = type;

        if (getEntitiesFromMessageType(type) == 3){
            if (msgType == MessageType.REP_APPRAISE_SIT){
                oper = val1;
            }
        }
    }

    /**
     *  Message instantiation (form 10).
     *      - ControlSiteStub - appraiseSit() - REQUEST
     *   @param type message type
     *   @param val1 value 1
     *   @param val2 value 2
     *   @param val3 value 3
     */
    public Message (int type, int val1, int val2, boolean val3)
    {
        msgType = type;

        if (getEntitiesFromMessageType(type) == 2){
            ordinaryThiefId = val1;
            ordinaryThiefState = val2;
            amINeeded = val3;

        } else if (getEntitiesFromMessageType(type) == 3){
            ordinaryThiefId = val1;
            ordinaryThiefState = val2;
            amINeeded = val3;
        }

    }

   /**
    *  Getting message type.
    *
    *     @return message type
    */
    public int getMsgType ()
    {
      return (msgType);
    }

    /**
    *   Getting ordinary thief identification.
    *
    *     @return ordinary thief identification
    */
    public int getOrdinaryThiefId ()
    {
      return ordinaryThiefId;
   }

    /**
     *   Getting ordinary thief state.
     *
     *     @return ordinary thief state
     */
    public int getOrdinaryThiefState ()
    {
        return ordinaryThiefState;
    }

    /**
     *   Getting ordinary thief position.
     *
     *     @return ordinary thief position
     */
    public int getOrdinaryThiefPos (){return ordinaryThiefPos;}

    /**
     *   Getting ordinary thief agility.
     *
     *     @return ordinary thief agility
     */
    public int getOrdinaryThiefAgility ()
    {
        return ordinaryThiefAgility;
    }

    /**
    *   Getting master thief state.
    *
    *     @return master thief state
    */
    public int getMasterThiefState ()
    {
      return masterThiefState;
   }

    /**
     *   Getting assault party identification.
     *
     *     @return ordinary thief identification
     */
    public int getAssaultPartyId ()
    {
        return assaultPartyId;
    }

    /**
     *   Getting room distance.
     *
     *     @return room distance
     */
    public int getRoomDistance ()
    {
        return roomDistance;
    }

    /**
     *    Getting the number of canvas in a given room.
     *      @param roomId room identification
     *      @return number of canvas in the room
     */
    public int getUpdateInRoom(int roomId){
        return canvas_per_room[roomId];
    }

    /**
     *   Getting room identification.
     *
     *     @return room identification
     */
    public int getRoomId ()
    {
        return roomId;
    }

    /**
     *   Getting next operation identifier.
     *
     *     @return next operation identifier
     */
    public char getOperation ()
    {
        return oper;
    }

    /**
     *   Getting target room identification.
     *
     *     @return target room id
     */
    public int getTargetRoomId ()
    {
        return targetRoomId;
    }

    /**
     *   Getting number of thieves in the assault party.
     *
     *     @return number of thieves
     */
    public int getNumOfThieves ()
    {
        return numOfThieves;
    }

    /**
     *   Getting the state of operation of the assault party
     *
     *     @return true if the assault party is in operation, false otherwise
     */
    public boolean getInOp ()
    {
        return inOp;
    }

    /**
     *   Getting the canvas of the ordinary thief.
     *
     *     @return true if the ordinary thief has a canvas, false otherwise
     */
    public boolean getBringsCanvas ()
    {
        return bringsCanvas;
    }

    /**
     *   Getting if the ordinary thief is needed.
     *
     *     @return true if the ordinary thief is needed, false otherwise
     */
    public boolean getAmINeeded ()
    {
        return amINeeded;
    }

    /**
     *   Getting the number of canvas that have already been stolen.
     *
     *      @return number of canvas stolen
     */
    public int[] getOrdinaryThievesAgility(){
        return ordinaryThievesAgility;
    }

    /**
     *   Getting the number of canvas that have already been stolen.
     *
     *      @return number of canvas stolen
     */
    public int[] getPaintingsPerRoom(){
        return canvas_per_room;
    }

    /**
     *   Getting the number of canvas that have already been stolen.
     *
     *      @return number of canvas stolen
     */
    public int[] getDistancePerRoom(){
        return distance_per_room;
    }

    /**
     *   Getting the number of canvas that have already been stolen.
     *
     *      @return number of canvas stolen
     */
    public int getCanvasResult(){
        return canvasStolen;
    }

    /**
    *   Getting name of logging file.
    *
    *     @return name of the logging file
    */
    public String getLogFName ()
    {
      return (fName);
    }

    /**
     *   For a given message type, get the entity that called it ()
     *    @param messageType type of the message
     *    @return entity that called the message
     */
    public int getEntitiesFromMessageType(int messageType)
    {
        switch(messageType)
        {
            // Master Thief messages
            case MessageType.REQ_START_OP: 		    case MessageType.REP_START_OP:
            case MessageType.REQ_COLLECT_CANVAS:    case MessageType.REP_COLLECT_CANVAS:
            case MessageType.REQ_TAKE_REST: 		case MessageType.REP_TAKE_REST:
            case MessageType.REQ_PREP_PARTY:        case MessageType.REP_PREP_PARTY:
            case MessageType.REQ_SEND_PARTY:        case MessageType.REP_SEND_PARTY:
            case MessageType.REQ_SET_TARGET_ROOM:   case MessageType.REP_SET_TARGET_ROOM:
            case MessageType.REQ_GET_BRINGS_CANVAS: case MessageType.REP_GET_BRINGS_CANVAS:
            case MessageType.REQ_SET_BRINGS_CANVAS: case MessageType.REP_SET_BRINGS_CANVAS:
            return 1;
            // Ordinary Thief messages
            case MessageType.REQ_HAND_CANVAS:		case MessageType.REP_HAND_CANVAS:
            case MessageType.REQ_REV_DIR: 	        case MessageType.REP_REV_DIR:
            case MessageType.REQ_ROLL_CANVAS:       case MessageType.REP_ROLL_CANVAS:
            case MessageType.REQ_PREP_EXCUR:        case MessageType.REP_PREP_EXCUR:
            case MessageType.REQ_NEEDED:            case MessageType.REP_NEEDED:
            case MessageType.REQ_CRAWL_IN:          case MessageType.REP_CRAWL_IN:
            case MessageType.REQ_CRAWL_OUT:         case MessageType.REP_CRAWL_OUT:
            case MessageType.REQ_LEAVE_PARTY:       case MessageType.REP_LEAVE_PARTY:
            return 2;
            // Additional Messages
            case MessageType.REQ_ROOM_DIST:	        case MessageType.REP_ROOM_DIST:
            case MessageType.REQ_APPRAISE_SIT:      case MessageType.REP_APPRAISE_SIT:
            case MessageType.REQ_GET_ASSAULT_ID:    case MessageType.REP_GET_ASSAULT_ID:
            case MessageType.REQ_GET_ROOM_ID:       case MessageType.REP_GET_ROOM_ID:       //
            case MessageType.REQ_SUM_UP:            case MessageType.REP_SUM_UP:            //
            case MessageType.REQ_ADD_THIEF:         case MessageType.REP_ADD_THIEF:
            case MessageType.REQ_WAIT_FOR_THIEVES:  case MessageType.REP_WAIT_FOR_THIEVES:  //
            case MessageType.REQ_GET_PARTY_ID:      case MessageType.REP_GET_PARTY_ID:      //
            case MessageType.REQ_GET_IN_OP:         case MessageType.REP_GET_IN_OP:         //
            case MessageType.REQ_SET_IN_OP:         case MessageType.REP_SET_IN_OP:         //
            case MessageType.REQ_GET_TARGET_ROOM:   case MessageType.REP_GET_TARGET_ROOM:
            case MessageType.REQ_NUM_THIEVES:       case MessageType.REP_NUM_THIEVES:
            case MessageType.REQ_CONTROL_SHUT:      case MessageType.REP_CONTROL_SHUT:
            case MessageType.REQ_MUSEUM_SHUT:       case MessageType.REP_MUSEUM_SHUT:
            case MessageType.REQ_CONCET_SHUT:       case MessageType.REP_CONCET_SHUT:
            case MessageType.REQ_PARTY_SHUT:        case MessageType.REP_PARTY_SHUT:
            return 3;
            //GeneralRepo Message
            case MessageType.REQ_SET_PAINTINGS_PER_ROOM:            case MessageType.REP_SET_PAINTINGS_PER_ROOM:
            case MessageType.REQ_SET_DISTANCE_PER_ROOM:             case MessageType.REP_SET_DISTANCE_PER_ROOM:
            case MessageType.REQ_SET_MASTER_THIEF_STATE:            case MessageType.REP_SET_MASTER_THIEF_STATE:
            case MessageType.REQ_SET_ORDINARY_THIEF_STATE:          case MessageType.REP_SET_ORDINARY_THIEF_STATE:
            case MessageType.REQ_SET_ORDINARY_THIEVES_AGILITY:      case MessageType.REP_SET_ORDINARY_THIEVES_AGILITY:
            case MessageType.REQ_SET_ORDINARY_THIEF_POS:            case MessageType.REP_SET_ORDINARY_THIEF_POS:
            case MessageType.REQ_SET_UPDATE_CANVAS_IN_ROOM:         case MessageType.REP_SET_UPDATE_CANVAS_IN_ROOM:
            case MessageType.REQ_GIVE_CANVAS:                       case MessageType.REP_GIVE_CANVAS:
            case MessageType.REQ_SET_BRINGS:                        case MessageType.REP_SET_BRINGS:
            case MessageType.REQ_SET_ASSAULT_PARTY_THIEVES_ID:      case MessageType.REP_SET_ASSAULT_PARTY_THIEVES_ID:
            case MessageType.REQ_SET_ASSAULT_PARTY_TARGET_ROOM_ID:  case MessageType.REP_SET_ASSAULT_PARTY_TARGET_ROOM_ID:
            case MessageType.REQ_SET_CANVAS_RESULT:                 case MessageType.REP_SET_CANVAS_RESULT:
            case MessageType.REQ_PRINT_TAIL:                        case MessageType.REP_PRINT_TAIL:
            case MessageType.REQ_GENERAL_REPO_SHUT:                 case MessageType.REP_GENERAL_REPO_SHUT:
            return 4;
            default:
                return -1;
        }
    }

   /**
    *   Printing the values of the internal fields.
    *   It is used for debugging purposes.
    *
    *     @return string containing, in separate lines, the pair field name - field value
    */
   @Override
   public String toString() {
       return "Message type = " + msgType +
               "\nMaster Thief state = " + masterThiefState +
               "\nOrdinary Thief identification = " + ordinaryThiefId +
               "\nOrdinary Thief state = " + ordinaryThiefState +
               "\nOrdinary Thief position = " + ordinaryThiefPos +
               "\nAssault Party identification = " + assaultPartyId +
               "\nRoom distance = " + roomDistance +
               "\nRoom identification = " + roomId +
               "\nOrdinary Thief agility = " + ordinaryThiefAgility +
               "\nIn operation = " + inOp +
               "\nAm I needed = " + amINeeded +
               "\nTarget room identification = " + targetRoomId +
               "\nNumber of thieves in the assault party = " + numOfThieves +
               "\nBrings canvas = " + bringsCanvas +
               "\nNext operation identifier = " + oper +
               "\nName of logging file = " + fName +
               "\nNumber of iterations = " + nIter +
               "\nCanvas per room = " + Arrays.toString(canvas_per_room) +
               "\nDistance per room = " + Arrays.toString(distance_per_room) +
               "\nOrdinary thieves agility = " + Arrays.toString(ordinaryThievesAgility) +
               "\nCanvas stolen = " + canvasStolen;
   }
}
