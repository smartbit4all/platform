package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * The invocation channel is responsible for executing the {@link InvocationRequest} with a given
 * security and thread options. It can be configured as a normal bean and collected by the
 * {@link InvocationApiImpl} to implement
 * {@link InvocationApi#invokeAsync(InvocationRequest, String)} functionality.
 * 
 * @author Peter Boros
 */
public interface AsyncInvocationChannel {

  /**
   * This invoke method is called when the execution of the request is ready to start. This function
   * will use separated thread to run the invocation request.
   * 
   * @param request
   */
  void invoke(AsyncInvocationRequest request);

  /**
   * @return The name of the channel to identify when calling the
   *         {@link InvocationApi#invokeAsync(InvocationRequest, String)}.
   */
  String getName();

}
