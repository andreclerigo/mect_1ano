package serverSide;

/**
 *    Service that is provided.
 */

public class KnockKnockProtocol
{
   /**
    *  Initial state.
    */

    private static final int WAITING = 0;

   /**
    *  Initial sentence was sent.
    */

    private static final int SENTKNOCKKNOCK = 1;

   /**
    *  Wait for answer.
    */

    private static final int SENTCLUE = 2;

   /**
    *  Continuation test.
    */

    private static final int ANOTHER = 3;

   /**
    *  Number of riddles.
    */

    private static final int NUMJOKES = 5;

   /**
    *  Clues.
    */

    private String[] clues = {"Turnip", "Little Old Lady", "Atch", "Who", "Who"};

   /**
    *  Funny answers.
    */

    private String[] answers = {"Turnip the heat, it's cold in here!",
                                "I didn't know you could yodel!",
                                "Bless you!",
                                "Is there an owl in here?",
                                "Is there an echo in here?"};

   /**
    *  Current state.
    */

    private int state = WAITING;

   /**
    *  Current riddle.
    */

    private int currentJoke = 0;

   /**
    *  Processing.
    *
    *  An answer is generated which depends on the current state and the current riddle.
    *
    *    @param theInput question
    *    @return answer
    */

    public String processInput (String theInput)
    {
       String theOutput = null;                            // answer

       switch (state)
       { case WAITING:        theOutput = "Knock! Knock!";
                              state = SENTKNOCKKNOCK;
                              break;
         case SENTKNOCKKNOCK: if (theInput.equalsIgnoreCase ("Who's there?"))
                                 { theOutput = clues[currentJoke];
                                   state = SENTCLUE;
                                 }
                                 else theOutput = "You're supposed to say \"Who's there?\"! " +
                                                  "Try again. Knock! Knock!";
                              break;
         case SENTCLUE:       if (theInput.equalsIgnoreCase (clues[currentJoke] + " who?"))
                                 { theOutput = answers[currentJoke] + " Want another? (y/n)";
                                   state = ANOTHER;
                                 }
                                 else { theOutput = "You're supposed to say \"" +
                                                    clues[currentJoke] + " who?\"" +
                                                    "! Try again. Knock! Knock!";
                                        state = SENTKNOCKKNOCK;
                                      }
                              break;
         case ANOTHER:        if (currentJoke == (NUMJOKES - 1))
                                 currentJoke = 0;
                                 else currentJoke++;
                              if (theInput.equalsIgnoreCase ("y"))
                                 { theOutput = "Knock! Knock!";
                                   state = SENTKNOCKKNOCK;
                                 }
                                 else { theOutput = "Bye.";
                                        state = WAITING;
                                      }
       }
       return theOutput;
    }
}
