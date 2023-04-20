package clientSide.entities;

/**
 *    Customer cloning.
 *
 *      It specifies his own attributes.
 *      Implementation of a client-server model of type 2 (server replication).
 *      Communication is based on a communication channel under the TCP protocol.
 */

public interface CustomerCloning
{
  /**
   *   Set customer id.
   *
   *     @param id customer id
   */

   public void setCustomerId (int id);

  /**
   *   Get customer id.
   *
   *     @return customer id
   */

   public int getCustomerId ();

  /**
   *   Set customer state.
   *
   *     @param state new customer state
   */

   public void setCustomerState (int state);

  /**
   *   Get customer state.
   *
   *     @return customer state
   */

   public int getCustomerState ();
}
