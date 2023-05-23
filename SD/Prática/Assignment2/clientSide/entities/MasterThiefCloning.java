package clientSide.entities;

/**
 *   Master thief interface.
 *   It specifies his own attributes.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public interface MasterThiefCloning {
    /**
     *   Set master thief state.
     *
     *     @param state new master thief state
     */
    public void setMasterThiefState (int state);

    /**
     *   Get master thief state.
     *
     *     @return master thief state
     */
    public int getMasterThiefState ();
}
