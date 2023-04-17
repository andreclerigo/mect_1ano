package entities;

/**
 *    Definition of the internal states of the master thief during his life cycle.
 */
public final class MasterThiefStates
{
    /**
     *   Initial state, the master thief is planning the heist and can only be changed to the next state by starting the life cycle by startOperations()
     */
    public static final int PLANNING_HEIST = 0;

    /**
     *   The master thief is deciding what to do next.
     */
    public static final int DECIDING = 1;

    /**
     *   The master thief is assembling an assault party.
     */
    public static final int ASSEMBLING = 2;

    /**
     *   The master thief is waiting for the assault party to return.
     */
    public static final int WAITING = 3;

    /**
     *   Final state, the master thief is presenting the report to the ordinary thieves by sumUpResults()
     */
    public static final int PRESENTING_REPORT = 4;

    /**
     *   
     */
    private MasterThiefStates ()
    { }
}
