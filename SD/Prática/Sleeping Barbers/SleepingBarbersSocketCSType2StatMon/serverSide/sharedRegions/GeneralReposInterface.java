package serverSide.sharedRegions;

import serverSide.main.*;
import serverSide.entities.*;
import clientSide.entities.*;
import commInfra.*;

/**
 *  Interface to the General Repository of Information.
 *
 *    It is responsible to validate and process the incoming message, execute the corresponding method on the
 *    General Repository and generate the outgoing message.
 *    Implementation of a client-server model of type 2 (server replication).
 *    Communication is based on a communication channel under the TCP protocol.
 */

public class GeneralReposInterface
{
  /**
   *  Reference to the general repository.
   */

   private final GeneralRepos repos;

  /**
   *  Instantiation of an interface to the general repository.
   *
   *    @param repos reference to the general repository
   */

   public GeneralReposInterface (GeneralRepos repos)
   {
      this.repos = repos;
   }

  /**
   *  Processing of the incoming messages.
   *
   *  Validation, execution of the corresponding method and generation of the outgoing message.
   *
   *    @param inMessage service request
   *    @return service reply
   *    @throws MessageException if the incoming message is not valid
   */

   public Message processAndReply (Message inMessage) throws MessageException
   {
      Message outMessage = null;                                     // mensagem de resposta

     /* validation of the incoming message */

      switch (inMessage.getMsgType ())
      { case MessageType.SETNFIC:  if (inMessage.getLogFName () == null)
                                      throw new MessageException ("Name of the logging file is not present!", inMessage);
                                      else if (inMessage.getNIter () <= 0)
                                              throw new MessageException ("Wrong number of iterations!", inMessage);
                                   break;
        case MessageType.STBST:    if ((inMessage.getBarbId () < 0) || (inMessage.getBarbId () >= SimulPar.M))
                                      throw new MessageException ("Invalid barber id!", inMessage);
                                      else if ((inMessage.getBarbState () != BarberStates.SLEEPING) && (inMessage.getBarbState () != BarberStates.INACTIVITY))
                                              throw new MessageException ("Invalid barber state!", inMessage);
                                   break;
        case MessageType.STCST:    if ((inMessage.getCustId () < 0) || (inMessage.getCustId () >= SimulPar.N))
                                      throw new MessageException ("Invalid customer id!", inMessage);
                                      else if ((inMessage.getCustState () < CustomerStates.DAYBYDAYLIFE) || (inMessage.getCustState () > CustomerStates.CUTTHEHAIR))
                                              throw new MessageException ("Invalid customer state!", inMessage);
                                   break;
        case MessageType.STBCST:   if ((inMessage.getBarbId () < 0) || (inMessage.getBarbId () >= SimulPar.M))
                                      throw new MessageException ("Invalid barber id!", inMessage);
                                      else if ((inMessage.getBarbState () != BarberStates.SLEEPING) && (inMessage.getBarbState () != BarberStates.INACTIVITY))
                                              throw new MessageException ("Invalid barber state!", inMessage);
                                   if ((inMessage.getCustId () < 0) || (inMessage.getCustId () >= SimulPar.N))
                                      throw new MessageException ("Invalid customer id!", inMessage);
                                      else if ((inMessage.getCustState () < CustomerStates.DAYBYDAYLIFE) || (inMessage.getCustState () > CustomerStates.CUTTHEHAIR))
                                              throw new MessageException ("Invalid customer state!", inMessage);
                                   break;
        case MessageType.SHUT:     // check nothing
                                   break;
        default:                   throw new MessageException ("Invalid message type!", inMessage);
      }

     /* processing */

      switch (inMessage.getMsgType ())

      { case MessageType.SETNFIC:  repos.initSimul (inMessage.getLogFName (), inMessage.getNIter ());
                                   outMessage = new Message (MessageType.NFICDONE);
                                   break;
        case MessageType.STBST:    repos.setBarberState (inMessage.getBarbId (), inMessage.getBarbState ());
                                   outMessage = new Message (MessageType.SACK);
                                   break;
        case MessageType.STCST:    repos.setCustomerState (inMessage.getCustId (), inMessage.getCustState ());
                                   outMessage = new Message (MessageType.SACK);
                                   break;
        case MessageType.STBCST:   repos.setBarberCustomerState (inMessage.getBarbId (), inMessage.getBarbState (),
                                                                 inMessage.getCustId (), inMessage.getCustState ());
                                   outMessage = new Message (MessageType.SACK);
                                   break;
        case MessageType.SHUT:     repos.shutdown ();
                                   outMessage = new Message (MessageType.SHUTDONE);
                                   break;
      }

     return (outMessage);
   }
}
