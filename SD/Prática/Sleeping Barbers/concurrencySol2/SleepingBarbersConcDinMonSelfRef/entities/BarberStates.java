package entities;

/**
 *    Definition of the internal states of the barber during his life cycle.
 */

public final class BarberStates
{
  /**
   *   The barber is resting while waiting for a customer.
   */

   public static final int SLEEPING = 0;

  /**
   *   The barber is cutting some customer hair.
   */

   public static final int INACTIVITY = 1;

  /**
   *   It can not be instantiated.
   */

   private BarberStates ()
   { }
}
