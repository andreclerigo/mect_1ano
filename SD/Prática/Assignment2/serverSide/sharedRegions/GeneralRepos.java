package serverSide.sharedRegions;

import clientSide.entities.OrdinaryThiefStates;
import clientSide.entities.MasterThiefStates;
import genclass.GenericIO;
import genclass.TextFile;
import serverSide.main.ServerHeistToMuseumGeneralRepos;
import serverSide.main.SimulPar;

import java.util.Arrays;
import java.util.Objects;

/**
 *   General Repository.
 *   It is responsible to keep the visible internal state of the problem and to provide means for it to be printed in the logging file.
 *   It is implemented as an implicit monitor.
 *   All public methods are executed in mutual exclusion.
 *   There are no internal synchronization points.
 */
public class GeneralRepos
{
    /**
     *   Name of the logging file.
     */
    private final String logFileName;

    /**
     *   State of the master thief.
     */
    private int masterThiefState;

    /**
     *   State of each ordinary thief.
     */
    private final int [] ordinaryThiefState;

    /**
     *   Maximum displacement of each ordinary thieves.
     */
    private int [] ordinaryThiefAgility;

    /**
     *   Position of each ordinary thieves.
     */
    private int [] ordinaryThiefPos;

    /**
     *   Distance to each room.
     */
    private int [] roomDistance;

    /**
     *   Number of paintings in each room.
     */
    private int [] nCanvasInRoom;

    /**
     *   Flag that indicates if each thief has a canvas in his possession or not.
     */
    private int [] bringsCanvas;

    /**
     *   Array of thief Ids that from position 0 to 2 is the assault party 1 and 3 to 6 is the assault party 2.
     */
    private int [] assaultPartyThievesId;

    /**
     *   ID of assigned room to each assault party.
     */
    private int [] assaultPartyTargetRoomId;

    /**
     *  All canvas that were stolen.
     */
    private int canvasResult;

    /**
     *   Number of entities that requested shutdown.
     */
    private int nEntities;

    /**
     *   Instantiation of a general repository object.
     *
     *     @param logFileName name of the logging file
     */
    public GeneralRepos (String logFileName)
    {
        if ((logFileName == null) || Objects.equals (logFileName, ""))
            this.logFileName = "logger";
        else
            this.logFileName = logFileName;

        this.nEntities = 0;

        // thief characteristics
        ordinaryThiefState = new int [SimulPar.ORDINARY_THIEVES];

        // other thief related characteristics
        ordinaryThiefPos = new int [SimulPar.ORDINARY_THIEVES];
        bringsCanvas = new int [SimulPar.ORDINARY_THIEVES];

        // assault party characteristics
        assaultPartyThievesId = new int [SimulPar.ORDINARY_THIEVES];
        Arrays.fill(assaultPartyThievesId, -1);

        assaultPartyTargetRoomId = new int [SimulPar.ORDINARY_THIEVES];
        canvasResult = 0;
        
        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++)
            ordinaryThiefState[i] = OrdinaryThiefStates.CONCENTRATION_SITE;

        masterThiefState = MasterThiefStates.PLANNING_HEIST;
        printHead();
    }

    /**
     *   Set ordinary thief state.
     *
     *     @param id ordinary thief id
     *     @param state ordinary thief state
     */
    public synchronized void setOrdinaryThiefState (int id, int state)
    {
        ordinaryThiefState[id] = state;
        printState();
    }

    /**
     *   Set master thief state.
     *
     *     @param state master thief state
     */
    public synchronized void setMasterThiefState (int state)
    {
        masterThiefState = state;
        printState();
    }

    /**
     *   Set ordinary thief position while crawling to the museum
     *
     *     @param id ordinary thief id
     *     @param pos position of the thief (0 is the concentration/control site, to Distance to the room assigned for its assault party)
     */
    public synchronized void setOrdinaryThiefPos (int id, int pos)
    {
        ordinaryThiefPos[id] = pos;
        printState();
    }

    /**
     *   Update number of paintings in a room of the museum.
     *
     *     @param roomId room id
     *     @param nCanvas number of paintings in the room
     */
    public synchronized void setUpdateInRoom (int roomId, int nCanvas)
    {
        nCanvasInRoom[roomId] = nCanvas;
        printState();
    }

    /**
     *   Set flag that indicates if the thief has a canvas in his possession or not.
     *
     *     @param id ordinary thief id
     *     @param canvas boolean that indicates if the thief has a canvas in his possession or not
     */
    public synchronized void setBringsCanvas (int id, boolean canvas)
    {
        if (canvas){
            bringsCanvas[id] = 1;
        } else {
            bringsCanvas[id] = 0;
        }
    }

    /**
     *   Action of the ordinary thief to give a canvas to the master thief.
     *
     *     @param id ordinary thief id
     *     @param canvas boolean that indicates if the thief has a canvas in his possession or not
     */
    public void giveCanvas (int id, boolean canvas)
    {
        setBringsCanvas(id, canvas);
        printState();
    }

    /**
     *   Set assault party id.
     *
     *     @param thiefId thief party id
     *     @param partyId assault party id
     */
    public synchronized void setAssaultPartyThievesId (int thiefId, int partyId)
    {
        if (partyId == 0) {
            for (int i = 0; i < 3; i++) {
                if (assaultPartyThievesId[i] == -1) {
                    assaultPartyThievesId[i] = thiefId;
                    break;
                }
            }
        } else if (partyId == 1) {
            for (int i = 3; i < 6; i++) {
                if (assaultPartyThievesId[i] == -1) {
                    assaultPartyThievesId[i] = thiefId;
                    break;
                }
            }
        } else {
            for (int i = 0; i < 6; i++) {
                if (assaultPartyThievesId[i] == thiefId) {
                    assaultPartyThievesId[i] = -1;
                    break;
                }
            }
        }
    }

    /**
     *   Set paintings per room.
     *
     *   @param paintingsPerRoom number of paintings per room
     */
    public synchronized void setPaintingsPerRoom(int[] paintingsPerRoom){
        nCanvasInRoom = paintingsPerRoom;
    }

    /**
     *   Set distance to each room.
     *
     *   @param distancePerRoom distance to each room
     */
    public synchronized void setDistancePerRoom(int[] distancePerRoom){
        roomDistance = distancePerRoom;
    }

    /**
     *   Set Agility of each ordinary thief.
     *
     *   @param agility agility of each ordinary thief
     */
    public synchronized void setOrdinaryThievesAgility(int[] agility)
    {
        ordinaryThiefAgility = agility;
    }

    /**
     *   Set ID of assigned room to each assault party.
     *
     *     @param id assault party id
     *     @param roomId room id
     */
    public synchronized void setAssaultPartyTargetRoomId (int id, int roomId)
    {
        assaultPartyTargetRoomId[id] = roomId;
    }

    /**
     *   Set all canvas that were stolen.
     *
     *     @param canvas number of canvas that were stolen
     */
    public synchronized void setCanvasResult (int canvas)
    {
        canvasResult = canvas;
    }

    /**
     *   Write the header to the logging file.
     *   The ordinary thieves are sleeping and the master thief is planning the heist.
     */
    private void printHead ()
    {
        TextFile log = new TextFile ();                     // instantiation of a text file handler
        if (!log.openForWriting(".", logFileName)) {
            GenericIO.writelnString("The process of creating the file " +  logFileName + " failed!");
            System.exit(1);
        }

        log.writelnString("                            Heist to the Museum - Description of the internal state\n");
        log.writelnString("MstT   Thief 1      Thief 2      Thief 3      Thief 4      Thief 5      Thief 6");
        log.writelnString("Stat  Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD    Stat S MD");
        log.writelnString("                   Assault party 1                       Assault party 2                       Museum");
        log.writelnString("           Elem 1     Elem 2     Elem 3          Elem 1     Elem 2     Elem 3   Room 1  Room 2  Room 3  Room 4  Room 5");
        log.writelnString("    RId  Id Pos Cv  Id Pos Cv  Id Pos Cv  RId  Id Pos Cv  Id Pos Cv  Id Pos Cv   NP DT   NP DT   NP DT   NP DT   NP DT");

        if (!log.close()) {
            GenericIO.writelnString("While trying to close the file " + logFileName + " the process failed.");
        }
    }

    /**
     *   Write a state line at the end of the logging file.
     *   The current state of the ordinary thieves and master thief is organized in a line to be printed.
     *   Internal operation.
     */
    private void printState ()
    {
        // instantiation of a text file handler
        TextFile log = new TextFile ();

        if (!log.openForAppending(".", logFileName)) {
            GenericIO.writelnString("The process of appending in file " + logFileName + " could not be started!");
            System.exit(1);
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2]; // Index 0 is getStackTrace, 1 is methodA
        String callerMethodName = caller.getMethodName();
        //log.writelnString(callerMethodName);

        // state lines to be printed
        String upperLineStatus = "";
        String lowerLineStatus = "";

        // upper line
        switch (masterThiefState) {
            case MasterThiefStates.PLANNING_HEIST: upperLineStatus += "Plan" + "  "; break;
            case MasterThiefStates.DECIDING: upperLineStatus += "Deci" + "  "; break;
            case MasterThiefStates.ASSEMBLING: upperLineStatus += "Asmb" + "  "; break;
            case MasterThiefStates.WAITING: upperLineStatus += "Wait" + "  "; break;
            case MasterThiefStates.PRESENTING_REPORT: upperLineStatus += "Pres" + "  "; break;
        }

        for (int i = 0; i < SimulPar.ORDINARY_THIEVES; i++) {
            switch (ordinaryThiefState[i]) {
                case OrdinaryThiefStates.CONCENTRATION_SITE:
                    upperLineStatus += "Conc " + "W";
                    break;
                case OrdinaryThiefStates.ON_EXCURSION:
                    upperLineStatus += "Excu " + "P";
                    break;
                case OrdinaryThiefStates.CRAWLING_INWARDS:
                    upperLineStatus += "CrIn " + "P";
                    break;
                case OrdinaryThiefStates.AT_A_ROOM:
                    upperLineStatus += "AtRo " + "P";
                    break;
                case OrdinaryThiefStates.CRAWLING_OUTWARDS:
                    upperLineStatus += "CrOu " + "P";
                    break;
                case OrdinaryThiefStates.CONTROL_SITE:
                    upperLineStatus += "Coll " + "W";
                    break;
            }
            upperLineStatus += "  " + ordinaryThiefAgility[i] + "    ";
        }


        // lower line - assault party 0 info
        if (assaultPartyThievesId[0] == -1) {
            lowerLineStatus += "     " + "-" + "    ";
        } else {
            lowerLineStatus += "     " + (assaultPartyTargetRoomId[0]+1) + "    ";
        }

        for (int i = 0; i < (assaultPartyThievesId.length/2); i++){
            if (assaultPartyThievesId[i] == -1) {
                lowerLineStatus += "-  --  -   ";
            } else {
                if (ordinaryThiefPos[assaultPartyThievesId[i]] < 10){
                    lowerLineStatus += (assaultPartyThievesId[i]+1) + "  0" + ordinaryThiefPos[assaultPartyThievesId[i]] + "  " + bringsCanvas[assaultPartyThievesId[i]] + "   ";
                }else{
                    lowerLineStatus += (assaultPartyThievesId[i]+1) + "  " + ordinaryThiefPos[assaultPartyThievesId[i]] + "  " + bringsCanvas[assaultPartyThievesId[i]] + "   ";
                }
            }
        }

        // lower line -- assault party 1 info
        if (assaultPartyThievesId[3] == -1){
            lowerLineStatus += "-" + "    ";
        }else{
            lowerLineStatus += (assaultPartyTargetRoomId[1]+1) + "    ";
        }

        for (int i = 3; i < assaultPartyThievesId.length; i++) {
            if (assaultPartyThievesId[i] == -1) {
                lowerLineStatus += "-  --  -   ";
            } else {
                if (ordinaryThiefPos[assaultPartyThievesId[i]] < 10)
                    lowerLineStatus += (assaultPartyThievesId[i]+1) + "  0" + ordinaryThiefPos[assaultPartyThievesId[i]] + "  " + bringsCanvas[assaultPartyThievesId[i]] + "   ";
                else
                    lowerLineStatus += (assaultPartyThievesId[i]+1) + "  " + ordinaryThiefPos[assaultPartyThievesId[i]] + "  " + bringsCanvas[assaultPartyThievesId[i]] + "   ";
            }
        }

        for (int i = 0; i < SimulPar.ROOMS_NUMBER; i++) {
            if (nCanvasInRoom[i] < 10)
                lowerLineStatus += "0" + nCanvasInRoom[i] + " " + roomDistance[i] + "   ";
            else
                lowerLineStatus += nCanvasInRoom[i] + " " + roomDistance[i] + "   ";
        }
        /*
        log.writelnString("####  #### #  #   #### #  #    #### #  #    #### #  #    #### #  #    #### #  #");
        log.writelnString("     #    #  ##  #  #  ##  #   #  ##  #   #    #  ##  #   #  ##  #   #  ##  #   ## ##   ## ##   ## ##   ## ##   ## ##");
        */
        log.writelnString(upperLineStatus);
        log.writelnString(lowerLineStatus);

        if (!log.close()){
            GenericIO.writelnString("While trying to close the file " + logFileName + " the process failed.");
        }
    }

    /**
     *  Write the final state of the simulation at the end of the logging file.
     */
    public synchronized void printTail ()
    {
        TextFile log = new TextFile ();                     // instantiation of a text file handler
        String lineStatus;

        if (!log.openForAppending(".", logFileName)) {
            GenericIO.writelnString("The process of appending in file " + logFileName + " could not be started!");
            System.exit(1);
        }

        lineStatus = "My friends, tonight's effort produced " + canvasResult + " priceless paintings!";

        log.writelnString(lineStatus);

        if (!log.close()) {
            GenericIO.writelnString("While trying to close the file " + logFileName + " the process failed.");
        }
    }

    /**
     *   Operation General Repository server shutdown
     */
    public synchronized void shutdown()
    {
        nEntities += 1;
        if (nEntities >= 1){
            ServerHeistToMuseumGeneralRepos.waitConnection = false;
        }
        
        notifyAll();
    }

}
