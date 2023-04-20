package commInfra;

/**
 *   Type of the exchanged messages.
 *the
 *   Implementation of a client-server model of type 2 (server replication).
 *   Communication is based on a communication channel under the TCP protocol.
 */

public class MessageType
{
  /**
   *  Initialization of the logging file name and the number of iterations (service request).
   */

   public static final int SETNFIC = 1;

  /**
   *  Logging file was initialized (reply).
   */

   public static final int NFICDONE = 2;

  /**
   *  Request hair cut (service request).
   */

   public static final int REQCUTH = 3;

  /**
   *  Hair was cut (reply).
   */

   public static final int CUTHDONE = 4;

  /**
   *  Barber shop is full (reply).
   */

   public static final int BSHOPF = 5;

  /**
   *  Barber goes to sleep (service request).
   */

   public static final int SLEEP = 6;

  /**
   *  Barber is asleep (reply).
   */

   public static final int SLEEPDONE = 7;

  /**
   *  Call a customer (service request).
   */

   public static final int CALLCUST = 8;

  /**
   *  Customer was called (reply).
   */

   public static final int CCUSTDONE = 9;

  /**
   *  Barber receives payment (service request).
   */

   public static final int RECPAY = 10;

  /**
   *  Payment was received (reply).
   */

   public static final int RPAYDONE = 11;

  /**
   *  End of work - barber (service request).
   */

   public static final int ENDOP = 12;

  /**
   *  Barber goes home (reply).
   */

   public static final int EOPDONE = 13;

  /**
   *  Server shutdown (service request).
   */

   public static final int SHUT = 14;

  /**
   *  Server was shutdown (reply).
   */

   public static final int SHUTDONE = 15;

  /**
   *  Set barber state (service request).
   */

   public static final int STBST = 16;

  /**
   *  Set customer state (service request).
   */

   public static final int STCST = 17;

  /**
   *  Set barber and customer states (service request).
   */

   public static final int STBCST = 18;

  /**
   *  Setting acknowledged (reply).
   */

   public static final int SACK = 19;
}
