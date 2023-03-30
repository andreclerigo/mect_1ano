package commInfra;

/**
 *    Exception signaling end of transaction.
 */

public class EndOfTransactionException extends Exception

{

  /**
   *  Last answer.
   */

   private Object lastAnswer = null;

  /**
   *  Exception instantiation.
   */

   public EndOfTransactionException (Object lastAnswer)
   {
      super ();
      this.lastAnswer = lastAnswer;
   }

  /**
   *  Getting last answer.
   */

   public Object getLastAnswer ()
   {
      return (lastAnswer);
   }
}
