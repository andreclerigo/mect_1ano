package interfaces;

import java.io.Serializable;

/**
 *  Data type to return both a boolean and an integer state value.
 *
 *  Used in calls on remote objects.
 */

public class ReturnBoolean implements Serializable
{
  /**
   *  Serialization key.
   */

   public static final long serialVersionUID = 2021L;

  /**
   *  Boolean value.
   */

   private boolean val;

  /**
   *  Integer state value.
   */

   private int state;

  /**
   *  ReturnBoolean instantiation.
   *
   *     @param val boolean value
   *     @param state integer state value
   */

   public ReturnBoolean (boolean val, int state)
   {
      this.val = val;
      this.state = state;
   }

  /**
   *  Getting boolean value.
   *
   *     @return boolean value
   */

   public boolean getBooleanVal ()
   {
      return (val);
   }

  /**
   *  Getting integer state value.
   *
   *     @return integer state value
   */

   public int getIntStateVal ()
   {
      return (state);
   }
}
