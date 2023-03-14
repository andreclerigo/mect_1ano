package entities;

/**
 *    Definition of the internal states of the customer during his life cycle.
 */

public final class CustomerStates
{
  /**
   *   The customer takes the bus to go to the departure airport.
   */

   public static final int DAYBYDAYLIFE = 0;

  /**
   *   The customer queue at the boarding gate waiting for the flight to be announced.
   */

   public static final int WANTTOCUTHAIR = 1;

  /**
   *   The customer flies to the destination airport.
   */

   public static final int WAITTURN = 2;

  /**
   *   The customer arrives at the destination airport, disembarks and leaves the airport.
   */

   public static final int CUTTHEHAIR = 3;

  /**
   *   It can not be instantiated.
   */

   private CustomerStates ()
   { }
}
