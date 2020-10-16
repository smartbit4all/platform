package org.smartbit4all.core;

/**
 * This node is responsible for executing the instance of the given executable. Inner class for the
 * {@link SB4ExecutionNode}.
 * 
 * @author Peter Boros
 */
final class SB4ExecutionFunction implements SB4Execution {

  /**
   * The piece of code to be executed in the given code. This is always a new instance for the given
   * execution plan so it can use the fields to store information during the execution.
   */
  SB4Function<?, ?> service;

  SB4ExecutionFunction(SB4Function<?, ?> executable) {
    super();
    this.service = executable;
  }

}
