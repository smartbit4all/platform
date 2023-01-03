package org.smartbit4all.api.invocation;

import java.net.URI;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannel;
import org.smartbit4all.api.session.SessionManagementApi;

/**
 * The setup interface of the {@link AsyncInvocationChannel}.
 * 
 * @author Peter Boros
 *
 */
interface AsyncInvocationChannelSetup {

  /**
   * Start the executor threads and initialize the given channel.
   */
  void start();

  /**
   * Stop the executor threads and release the resources.
   */
  void stop();

  void setInvocationApi(InvocationApi invocationApi);

  void setInvocationRegisterApi(InvocationRegisterApi invocationRegisterApi);

  void setSessionManagementApi(SessionManagementApi sessionManagementApi);

  /**
   * The URI of the {@link RuntimeAsyncChannel} that contains the actively managed
   * {@link AsyncInvocationRequest}s of the given runtime.
   * 
   * @param uri
   */
  void setUri(URI uri);
}
