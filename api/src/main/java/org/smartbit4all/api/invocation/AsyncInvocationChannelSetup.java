package org.smartbit4all.api.invocation;

import org.smartbit4all.api.collection.CollectionApi;
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

  AsyncInvocationChannel invocationApi(InvocationApi invocationApi);

  AsyncInvocationChannel collectionApi(CollectionApi collectionApi);

  void setSessionManagementApi(SessionManagementApi sessionManagementApi);

}
