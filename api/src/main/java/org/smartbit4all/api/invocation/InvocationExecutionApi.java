package org.smartbit4all.api.invocation;

/**
 * The execution api for the invocations. The {@link InvocationExecutionApiLocal} is a basic
 * implementation to call the apis in the local JVM.
 * 
 * @author Peter Boros
 */
public interface InvocationExecutionApi {

  String getName();

  /**
   * The generic api call.
   * 
   * @param request
   * @throws ClassNotFoundException
   */
  InvocationParameter invoke(InvocationRequest request) throws ClassNotFoundException;

}
