package clientSide.entities;

/**
 *    Barber cloning.
 *
 *      It specifies his own attributes.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public interface BarberCloning
{
  /**
   *   Set barber id.
   *
   *     @param id barber id
   */

   public void setBarberId (int id);

  /**
   *   Get barber id.
   *
   *     @return barber id
   */

   public int getBarberId ();

  /**
   *   Set barber state.
   *
   *     @param state new barber state
   */

   public void setBarberState (int state);

  /**
   *   Get barber state.
   *
   *     @return barber state
   */

   public int getBarberState ();
}
