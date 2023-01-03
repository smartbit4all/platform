package org.smartbit4all.api.invocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;

/**
 * This entry encapsulate the {@link AsyncInvocationRequest} and the {@link AsyncInvocationChannel}
 * that is responsible for the execution of the invocation.
 * 
 * @author Peter Boros
 */
public class AsyncInvocationRequestEntry {

  private static final Logger log = LoggerFactory.getLogger(AsyncInvocationRequestEntry.class);

  AsyncInvocationChannel channel;
  AsyncInvocationRequest request;

  public AsyncInvocationRequestEntry(AsyncInvocationChannel channel,
      AsyncInvocationRequest request) {
    super();
    this.channel = channel;
    this.request = request;
  }

  public void invoke() {
    if (channel != null && request != null) {
      channel.invoke(this);
    } else {
      log.error("Unable to execute async call.");
    }
  }

}
