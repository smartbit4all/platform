package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionManagementApi;

/**
 * The invocation channel is responsible for executing the {@link InvocationRequest} with a given
 * security and thread options. It can be configured as a normal bean and collected by the
 * {@link InvocationApiImpl} to implement
 * {@link InvocationApi#invokeAsync(InvocationRequest, String)} functionality.
 * 
 * @author Peter Boros
 */
public interface AsyncInvocationChannel {

  void invoke(InvocationRequest request);

  String getName();

  /**
   * Start the executor threads and initialize the given channel.
   */
  void start();

  /**
   * Stop the executor threads and release the resources.
   */
  void stop();

  AsyncInvocationChannel invocationApi(InvocationApi invocationApi);

  void setSessionManagementApi(SessionManagementApi sessionManagementApi);

}
