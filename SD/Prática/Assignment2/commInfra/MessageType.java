package commInfra;

/**
 *   Type of the exchanged messages.
 *
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class MessageType {
    /**
     *  Control Site
     */
    public static final int REQ_START_OP = 1;
    public static final int REP_START_OP = 2;

    public static final int REQ_APPRAISE_SIT = 3;
    public static final int REP_APPRAISE_SIT = 4;

    public static final int REQ_GET_ASSAULT_ID = 5;
    public static final int REP_GET_ASSAULT_ID = 6;

    public static final int REQ_GET_ROOM_ID = 7;
    public static final int REP_GET_ROOM_ID = 8;

    public static final int REQ_COLLECT_CANVAS = 9;
    public static final int REP_COLLECT_CANVAS = 10;

    public static final int REQ_HAND_CANVAS = 11;
    public static final int REP_HAND_CANVAS = 12;

    public static final int REQ_TAKE_REST = 13;
    public static final int REP_TAKE_REST = 14;

    public static final int REQ_CONTROL_SHUT = 15;
    public static final int REP_CONTROL_SHUT = 16;


    /**
     *   Museum
     */
    public static final int REQ_REV_DIR = 17;
    public static final int REP_REV_DIR = 18;

    public static final int REQ_ROOM_DIST = 19;
    public static final int REP_ROOM_DIST = 20;

    public static final int REQ_ROLL_CANVAS = 21;
    public static final int REP_ROLL_CANVAS = 22;

    public static final int REQ_MUSEUM_SHUT = 23;
    public static final int REP_MUSEUM_SHUT = 24;

    /**
     *   Concentration Site
     */
    public static final int REQ_PREP_PARTY = 25;
    public static final int REP_PREP_PARTY = 26;

    public static final int REQ_PREP_EXCUR = 27;
    public static final int REP_PREP_EXCUR = 28;

    public static final int REQ_NEEDED = 29;
    public static final int REP_NEEDED = 30;

    public static final int REQ_SUM_UP = 31;
    public static final int REP_SUM_UP = 32;

    public static final int REQ_CONCET_SHUT = 33;
    public static final int REP_CONCET_SHUT = 34;

    /**
     *  Assault Party
     */
    public static final int REQ_CRAWL_IN = 35;
    public static final int REP_CRAWL_IN = 36;

    public static final int REQ_CRAWL_OUT = 37;
    public static final int REP_CRAWL_OUT = 38;

    public static final int REQ_SEND_PARTY = 39;
    public static final int REP_SEND_PARTY = 40;

    public static final int REQ_ADD_THIEF = 41;
    public static final int REP_ADD_THIEF = 42;

    public static final int REQ_WAIT_FOR_THIEVES = 43;
    public static final int REP_WAIT_FOR_THIEVES = 44;

    public static final int REQ_GET_PARTY_ID = 45;
    public static final int REP_GET_PARTY_ID = 46;

    public static final int REQ_GET_IN_OP = 47;
    public static final int REP_GET_IN_OP = 48;

    public static final int REQ_SET_IN_OP = 49;
    public static final int REP_SET_IN_OP = 50;

    public static final int REQ_GET_TARGET_ROOM = 51;
    public static final int REP_GET_TARGET_ROOM = 52;

    public static final int REQ_SET_TARGET_ROOM = 53;
    public static final int REP_SET_TARGET_ROOM = 54;

    public static final int REQ_NUM_THIEVES = 55;
    public static final int REP_NUM_THIEVES = 56;

    public static final int REQ_LEAVE_PARTY = 57;
    public static final int REP_LEAVE_PARTY = 58;

    public static final int REQ_GET_BRINGS_CANVAS = 59;
    public static final int REP_GET_BRINGS_CANVAS = 60;

    public static final int REQ_SET_BRINGS_CANVAS = 61;
    public static final int REP_SET_BRINGS_CANVAS = 62;

    public static final int REQ_PARTY_SHUT = 63;
    public static final int REP_PARTY_SHUT = 64;

    /**
     *  General Repository
     */
    public static final int REQ_SET_PAINTINGS_PER_ROOM = 65;
    public static final int REP_SET_PAINTINGS_PER_ROOM = 66;

    public static final int REQ_SET_DISTANCE_PER_ROOM = 67;
    public static final int REP_SET_DISTANCE_PER_ROOM = 68;

    public static final int REQ_SET_ORDINARY_THIEVES_AGILITY = 69;
    public static final int REP_SET_ORDINARY_THIEVES_AGILITY = 70;

    public static final int REQ_SET_MASTER_THIEF_STATE = 71;
    public static final int REP_SET_MASTER_THIEF_STATE = 72;

    public static final int REQ_SET_ORDINARY_THIEF_STATE = 73;
    public static final int REP_SET_ORDINARY_THIEF_STATE = 74;

    public static final int REQ_SET_ORDINARY_THIEF_POS = 75;
    public static final int REP_SET_ORDINARY_THIEF_POS = 76;

    public static final int REQ_SET_UPDATE_CANVAS_IN_ROOM = 77;
    public static final int REP_SET_UPDATE_CANVAS_IN_ROOM = 78;

    public static final int REQ_GIVE_CANVAS = 79;
    public static final int REP_GIVE_CANVAS = 80;

    public static final int REQ_SET_BRINGS = 81;
    public static final int REP_SET_BRINGS = 82;

    public static final int REQ_SET_ASSAULT_PARTY_THIEVES_ID = 83;
    public static final int REP_SET_ASSAULT_PARTY_THIEVES_ID = 84;

    public static final int REQ_SET_ASSAULT_PARTY_TARGET_ROOM_ID = 85;
    public static final int REP_SET_ASSAULT_PARTY_TARGET_ROOM_ID = 86;

    public static final int REQ_SET_CANVAS_RESULT = 87;
    public static final int REP_SET_CANVAS_RESULT = 88;

    public static final int REQ_PRINT_TAIL = 89;
    public static final int REP_PRINT_TAIL = 90;

    public static final int REQ_GENERAL_REPO_SHUT = 91;
    public static final int REP_GENERAL_REPO_SHUT = 92;
}

