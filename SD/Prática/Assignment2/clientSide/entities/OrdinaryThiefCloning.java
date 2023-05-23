package clientSide.entities;

/**
 *   Ordinary thief interface.
 *   It specifies his own attributes.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public interface OrdinaryThiefCloning {
    /**
     *   Get Ordinary Thief state.
     *
     *     @return Ordinary Thief state
     */
    public int getOrdinaryThiefState ();

    /**
     *   Set Ordinary Thief state.
     *
     *     @param state new Ordinary Thief state
     */
    public void setOrdinaryThiefState (int state);

    /**
     *   Get Ordinary Thief id.
     *
     *     @return Ordinary Thief id
     */
    public int getOrdinaryThiefId ();

    /**
     *   Set Ordinary Thief id.
     *
     *     @param id new Ordinary Thief id
     */
    public void setOrdinaryThiefId (int id);

    /**
     *   Get Ordinary Thief agility.
     *
     *     @return Ordinary Thief agility
     */
    public int getOrdinaryThiefAgility ();

    /**
     *   Set Ordinary Thief agility.
     *
     *     @param agility new Ordinary Thief agility
     */
    public void setOrdinaryThiefAgility (int agility);
}
