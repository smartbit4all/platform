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
      if (log.isDebugEnabled()) {
        log.debug("Invoking: {}", toLog());
      }
      channel.invoke(this);
    } else {
      log.error("Unable to execute async call.");
    }
  }

  public String toLog() {
    StringBuilder sb = new StringBuilder();
    sb.append(request.getRequest() != null ? request.getRequest().getInterfaceClass() : "??");
    sb.append(".");
    sb.append(request.getRequest() != null ? request.getRequest().getMethodName() : "??");
    sb.append(" in ");
    sb.append(request.getChannel() != null ? request.getChannel() : "<n/a>");
    return sb.toString();
  }
}
