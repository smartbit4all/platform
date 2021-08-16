package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationRequest;

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
  void invoke(InvocationRequest request) throws ClassNotFoundException;

}
