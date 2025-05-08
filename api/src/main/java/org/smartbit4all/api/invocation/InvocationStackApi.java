package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.invocation.bean.InvocationStackItem;

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
   * Set a new unsaved, not persisted stack with the values passed in stack item.
   * 
   * @param stackItem
   * @return The newly initiated stack itself.
   */
  InvocationStack set(InvocationStackItem stackItem);

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
   * Run the functionality passed to this method on the stack saved into a {@link StoredReference}.
   * 
   * @param schema The schema of the stack.
   * @param stackName The name of the stack
   * @param func The functionality to run on the stack. This must return a boolean to decide if we
   *        have to save the result ir not.
   * @return The URI of the stack. The stack is a reference in this case. So we can load it and read
   *         the values if necessary.
   */
  URI runOnNamedStack(String schema, String stackName, Consumer<InvocationStack> func);

  /**
   * Initiate an active stack for the current thread.
   * 
   * @param stackUri
   * @param path
   * @return The loaded stack.
   */
  InvocationStack loadStack(URI stackUri, List<String> path);

  /**
   * Finalize the {@link InvocationStack} associated with the current thread.
   */
  void commit();

  /**
   * Remove the invocation stack and restore the previous one if any.
   * 
   * @return The removed stack or null if there was no stack.
   */
  InvocationStack remove();

  /**
   * Rollback the {@link InvocationStack} associated with the current thread without saving its
   * state.
   */
  void rollback();

}
