package commInfra;

import java.io.*;
import genclass.GenericIO;

/**
 *   Internal structure of the exchanged messages.
 *
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */

public class Message implements Serializable
{
  /**
   *  Serialization key.
   */

   private static final long serialVersionUID = 2021L;

  /**
   *  Message type.
   */

   private int msgType = -1;

  /**
   *  Barber identification.
   */

   private int barbId = -1;

  /**
   *  Barber state.
   */

   private int barbState = -1;

  /**
   *  Customer identification.
   */

   private int custId = -1;

  /**
   *  Customer state.
   */

   private int custState = -1;

  /**
   *  End of operations (barber).
   */

   private boolean endOp = false;

  /**
   *  Name of the logging file.
   */

   private String fName = null;

  /**
   *  Number of iterations of the customer life cycle.
   */

   private int nIter = -1;

  /**
   *  Message instantiation (form 1).
   *
   *     @param type message type
   */

   public Message (int type)
   {
      msgType = type;
   }

  /**
   *  Message instantiation (form 2).
   *
   *     @param type message type
   *     @param id barber / customer identification
   *     @param state barber / customer state
   */

   public Message (int type, int id, int state)
   {
      msgType = type;
      if ((msgType == MessageType.STBST) || (msgType == MessageType.CALLCUST) || (msgType == MessageType.RPAYDONE))
         { barbId= id;
           barbState = state;
         }
         else if ((msgType == MessageType.STCST) || (msgType == MessageType.REQCUTH) || (msgType == MessageType.CUTHDONE) ||
                  (msgType == MessageType.BSHOPF))
                 { custId= id;
                   custState = state;
                 }
                 else { GenericIO.writelnString ("Message type = " + msgType + ": non-implemented instantiation!");
                        System.exit (1);
                      }
   }

  /**
   *  Message instantiation (form 3).
   *
   *     @param type message type
   *     @param id barber identification
   */

   public Message (int type, int id)
   {
      msgType = type;
      barbId= id;
   }

  /**
   *  Message instantiation (form 4).
   *
   *     @param type message type
   *     @param id barber identification
   *     @param endOP end of operations flag
   */

   public Message (int type, int id, boolean endOp)
   {
      msgType = type;
      barbId= id;
      this.endOp = endOp;
   }

  /**
   *  Message instantiation (form 5).
   *
   *     @param type message type
   *     @param barbId barber identification
   *     @param barbState barber state
   *     @param custId customer identification
   */

   public Message (int type, int barbId, int barbState, int custId)
   {
      msgType = type;
      this.barbId= barbId;
      this.barbState = barbState;
      this.custId= custId;
   }

  /**
   *  Message instantiation (form 6).
   *
   *     @param type message type
   *     @param barbId barber identification
   *     @param barbState barber state
   *     @param custId customer identification
   *     @param custState customer state
   */

   public Message (int type, int barbId, int barbState, int custId, int custState)
   {
      msgType = type;
      this.barbId= barbId;
      this.barbState = barbState;
      this.custId= custId;
      this.custState = custState;
   }

  /**
   *  Message instantiation (form 7).
   *
   *     @param type message type
   *     @param name name of the logging file
   *     @param nIter number of iterations of the customer life cycle
   */

   public Message (int type, String name, int nIter)
   {
      msgType = type;
      fName= name;
      this.nIter = nIter;
   }

  /**
   *  Getting message type.
   *
   *     @return message type
   */

   public int getMsgType ()
   {
      return (msgType);
   }

  /**
   *  Getting barber identification.
   *
   *     @return barber identification
   */

   public int getBarbId ()
   {
      return (barbId);
   }

  /**
   *  Getting barber state.
   *
   *     @return barber state
   */

   public int getBarbState ()
   {
      return (barbState);
   }

  /**
   *  Getting customer identification.
   *
   *     @return customer identification
   */

   public int getCustId ()
   {
      return (custId);
   }

  /**
   *  Getting customer state.
   *
   *     @return customer state
   */

   public int getCustState ()
   {
      return (custState);
   }

  /**
   *  Getting end of operations flag (barber).
   *
   *     @return end of operations flag
   */

   public boolean getEndOp ()
   {
      return (endOp);
   }

  /**
   *  Getting name of logging file.
   *
   *     @return name of the logging file
   */

   public String getLogFName ()
   {
      return (fName);
   }

  /**
   *  Getting the number of iterations of the customer life cycle.
   *
   *     @return number of iterations of the customer life cycle
   */

   public int getNIter ()
   {
      return (nIter);
   }

  /**
   *  Printing the values of the internal fields.
   *
   *  It is used for debugging purposes.
   *
   *     @return string containing, in separate lines, the pair field name - field value
   */

   @Override
   public String toString ()
   {
      return ("Message type = " + msgType +
              "\nBarber Id = " + barbId +
              "\nBarber State = " + barbState +
              "\nCustomer Id = " + custId +
              "\nCustomer State = " + custState +
              "\nEnd of Operations (barber) = " + endOp +
              "\nName of logging file = " + fName +
              "\nNumber of iterations = " + nIter);
   }
}
