package serverSide.entities;

import clientSide.entities.*;
import commInfra.Message;
import commInfra.MessageException;
import commInfra.ServerCom;
import serverSide.sharedRegions.ConcetSiteInterface;

/**
 *   Service provider agent for access to the Concet Site.
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */
public class ConcetSiteClientProxy extends Thread implements MasterThiefCloning, OrdinaryThiefCloning
{
    /**
     *   Number of instantiated threads.
     */
    private static int nProxy = 0;

    /**
     *   Communication channel.
     */
    private ServerCom sconi;

    /**
     *   Interface to the Concentration Site.
     */
    private ConcetSiteInterface concetSiteInter;

    /**
     *   Master Thief State
     */
    private int masterThiefState;

    /**
     *   Thief identification.
     */
    private int ordinaryThiefId;

    /**
     *   OrdinaryThief state.
     */
    private int ordinaryThiefState;

    /**
     *   The Thief's agility (2 to 6).
     */
    private int ordinaryThiefAgility;

    /**
     *  Instantiation of a client proxy.
     *
     *     @param sconi communication channel
     *     @param concetSite interface to the control site
     */
    public ConcetSiteClientProxy (ServerCom sconi, ConcetSiteInterface concetSite)
    {
        super ("ConcetSiteProxy_" + ConcetSiteClientProxy.getProxyId());
        this.sconi = sconi;
        this.concetSiteInter = concetSite;
    }

    /**
     *  Generation of the instantiation identifier.
     *
     *     @return instantiation identifier
     */
    private static int getProxyId ()
    {
        Class<?> cl = null;			// representation of the BarClientProxy object in JVM
        int proxyId;				// instantiation identifier

        try {
            cl = Class.forName("serverSide.entities.ConcetSiteClientProxy");
        } catch (ClassNotFoundException e) {
            System.out.println("Data type ConcetSiteClientProxy was not found!");
            e.printStackTrace ();
            System.exit(1);
        }

        synchronized (cl) {
            proxyId = nProxy;
            nProxy += 1;
        }
        return proxyId;
    }

    /**
     *   Set master thief state.
     *
     *     @param state new master thief state
     */
    public void setMasterThiefState (int state)
    {
        masterThiefState = state;
    }

    /**
     *   Get master thief state.
     *
     *     @return master thief state
     */
    public int getMasterThiefState ()
    {
        return masterThiefState;
    }


    /**
     *   Get Ordinary Thief state.
     *
     *     @return Ordinary Thief state
     */
    public int getOrdinaryThiefState ()
    {
        return ordinaryThiefState;
    }

    /**
     *   Set Ordinary Thief state.
     *
     *     @param state new Ordinary Thief state
     */
    public void setOrdinaryThiefState (int state)
    {
        ordinaryThiefState = state;
    }

    /**
     *   Get Ordinary Thief id.
     *
     *     @return Ordinary Thief id
     */
    public int getOrdinaryThiefId ()
    {
        return ordinaryThiefId;
    }

    /**
     *   Set Ordinary Thief id.
     *
     *     @param id new Ordinary Thief id
     */
    public void setOrdinaryThiefId (int id)
    {
        ordinaryThiefId = id;
    }

    /**
     *   Get Ordinary Thief agility.
     *
     *     @return Ordinary Thief agility
     */
    public int getOrdinaryThiefAgility ()
    {
        return ordinaryThiefAgility;
    }

    /**
     *   Set Ordinary Thief agility.
     *
     *     @param agility new Ordinary Thief agility
     */
    public void setOrdinaryThiefAgility (int agility)
    {
        ordinaryThiefAgility = agility;
    }


    /**
     *  Life cycle of the service provider agent.
     */
    @Override
    public void run ()
    {
        Message inMessage = null,                                       // service request
                outMessage = null;                                      // service reply

        /* service providing */
        inMessage = (Message) sconi.readObject();                       // get service request
        try {
            outMessage = concetSiteInter.processAndReply(inMessage);   // process it
        } catch (MessageException e) {
            System.out.println("Thread " + getName () + ": " + e.getMessage () + "!");
            System.out.println(e.getMessageVal ().toString ());
            System.exit(1);
        }
        sconi.writeObject(outMessage);                                  // send service reply
        sconi.close();                                                  // close the communication channel
    }
}
