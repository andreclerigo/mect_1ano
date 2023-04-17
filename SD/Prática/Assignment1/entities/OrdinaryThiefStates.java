package entities;

/**
 *    Definition of the internal states of the ordinary thief during his life cycle.
 */
public final class OrdinaryThiefStates
{
    /**
     *   The ordinary thief is waiting for assault group to be prepared (on collection/concentration site).
     */
    public static final int CONCENTRATION_SITE = 0;

    /**
     *   The ordinary thief is at the museum.
     */
    public static final int ON_EXCURSION = 1;

    /**
     *  The ordinary thief is crawling inwards.
     */
    public static final int CRAWLING_INWARDS = 2;

    /**
     *   The ordinary thief is at a room.
     */
    public static final int AT_A_ROOM = 3;

    /**
     *   The ordinary thief is crawling outwards.
     */
    public static final int CRAWLING_OUTWARDS = 4;

    /**
     *   The ordinary thief is at the control site.
     */
    public static final int CONTROL_SITE = 5;

    /**
     *   It can not be instantiated.
     */
    private OrdinaryThiefStates ()
    { }
}
