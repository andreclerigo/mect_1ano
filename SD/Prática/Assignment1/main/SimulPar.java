package main;

/**
 *    Definition of the simulation parameters.
 */
public final class SimulPar
{
    /**
     *   Number of exhibition rooms in the museum.
     */
    public static final int ROOMS_NUMBER = 5;

    /**
     *   Number of maximum paintings in each room.
     */
    public static final int MAX_PAINTINGS = 16;

    /**
     *   Number of minimum paintings in each room.
     */
    public static final int MIN_PAINTINGS = 8;

    /**
     *   Number of maximum displacement of the ordinary thieves.
     */
    public static final int MAX_ROOM_DISTANCE = 30;

    /**
     *   Number of minimum displacement of the ordinary thieves.
     */
    public static final int MIN_ROOM_DISTANCE = 15;

    /**
     *   Number of maximum displacement between ordinary thieves.
     */
    public static final int MAX_THIEVES_DISTANCE = 3;

    /**
     *   Number of maximum displacement of an ordinary thief
     */
    public static final int MAX_THIEVES_DISPLACEMENT = 6;


    /**
     *   Number of minimum displacement of an ordinary thief
     */
    public static final int MIN_THIEVES_DISPLACEMENT = 2;

    /**
     *   Number of maximum thieves in each assault party.
     */
    public static final int MAX_PARTY_THIEVES = 3;

    /**
     *   Number of ordinary thieves to robe the museum.
     */
    public static final int ORDINARY_THIEVES = 6;

    /**
     *   Number of assault parties to rob the museum.
     */
    public static final int ASSAULT_PARTIES_NUMBER = ORDINARY_THIEVES / MAX_PARTY_THIEVES;

    /**
     *   It can not be instantiated.
     */
    private SimulPar ()
    { }
}
