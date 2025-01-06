package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * This api is reponsible for the access and modification of the ProcessInstance. It can manage the
 * hierarchical structure of the process.
 */
public interface InvocationStackApi {

  /**
   * @return The {@link InvocationStack} assiciated with the current thread.
   */
  InvocationStack get();

  /**
   * Initiate an active stack for the current thread.
   * 
   * @param code The code of the root stack
   * @param name The name of the root stack
   * @param inputParameters The input parameteres for the stack.
   * @param defaultVariables The default variable for the process stack.
   * @return The URI of the stack.
   */
  URI initiateStack(String code, String name, Map<String, Object> inputParameters,
      Map<String, Object> defaultVariables);

  /**
   * Initiate an active stack for the current thread.
   * 
   * @param processUri
   * @param path
   * @return
   */
  InvocationStack loadStack(URI stackUri, List<String> path);

  /**
   * Finalize the {@link InvocationStack} associated with the current thread.
   */
  void commit();

  /**
   * Rollback the {@link InvocationStack} associated with the current thread without saving its
   * state.
   */
  void rollback();

}
