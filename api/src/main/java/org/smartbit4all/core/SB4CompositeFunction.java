/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core;

import java.util.Iterator;

/**
 * The platform supports the {@link SB4Function} like a piece of code with identified instance. So
 * if would like to "call" a function then it's an object with properties as input parameters and
 * after the execution we find the result data in the output of the same object. This pattern has
 * many advantages like separating the preparation phase of the call from the execution. The
 * execution can run in different machines because we can send this node to another application to
 * execute.
 * 
 * In this concept we need the ability to collect a bunch of executable service to execute in one
 * "transaction". Although we know nothing about the transactions in this level. Let's assume that
 * we are in a "client" and we would like to run a few query and an insert against our database. We
 * can setup an {@link SB4CompositeFunction} with these and start the execution by calling the
 * execute function of the composite service. If we have a composite service implementation in our
 * hand then all these services are going to be sent to a "server" where the communication layer
 * will create the same structure but know with the server side implementations. We will have a
 * composite service on the server side but now with the SQL implementations.
 * 
 * When executing this composites service over there it will be executed in one database
 * transaction. All the results will appear in the server side service objects and will be
 * transfered back to the client. At the end of the client execution we will see the outputs of the
 * services we executed together. But we now nothing about the complexity of the execution itself.
 * 
 * This interface is the builder API for composite function executions.
 * 
 * @author Peter Boros
 */
public interface SB4CompositeFunction<I, O> extends SB4Function<I, O>, SB4Execution {

  /**
   * The execution mode.
   * 
   * @author Peter Boros
   */
  public static enum ExecutionMode {
    SEQUENTIAL, PARELLEl;
  }

  /**
   * Constructs an execution node with sequential content. It will be the next node in this
   * execution node.
   * 
   * @return The newly created node for fluid API.
   */
  SB4ExecutionNode sequenceOf();

  /**
   * Constructs a parallel execution context. All the
   * 
   * @return The newly created node for fluid API.
   */
  SB4ExecutionNode parallelOf();

  /**
   * For the sake of simplicity we can add a service directly to the root node. The node is
   * sequential by default.
   * 
   * @param function The function to add to the call.
   * @return For the fluid API we get back the root node in this case to continue adding.
   */
  SB4ExecutionNode call(SB4Function<?, ?> function);

  /**
   * The root node of the composite function.
   * 
   * @return
   */
  SB4ExecutionNode calls();

  /**
   * Check the emptiness.
   * 
   * @return Return true when there is nothing to execute.
   */
  boolean isEmpty();

  SB4ExecutionNode preCalls();

  SB4ExecutionNode postCalls();

  /**
   * The service executions of the composite service. The order of the iteration is the order of the
   * executions.
   * 
   * @return The executed {@link SB4Function}.
   */
  Iterator<SB4Function<?, ?>> functionsIter();

  /**
   * The service executions of the composite service. The order of the iteration is the order of the
   * executions.
   * 
   * @return The executed {@link SB4Function}.
   */
  Iterable<SB4Function<?, ?>> functions();

}
