package interfaces;

import java.io.Serializable;

/**
 *  Operational interface of objects passed as a parameter to the method executeTask of a
 *  remote object of type ComputeEngine.
 *
 *  Example of a simple handler for code migration.
 */

public interface Task extends Serializable
{
  /**
   *  Serialization key.
   */

   public static final long serialVersionUID = 2021L;

  /**
   *  Code execution.
   *
   *    @return whatever it is be returned
   */

   Object execute();
}
