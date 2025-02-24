package org.smartbit4all.api.invocation;

import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;

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
  AsyncCompletableFuture future;


  public AsyncInvocationRequestEntry(AsyncInvocationChannel channel,
      AsyncInvocationRequest request) {
    this(channel, request, null);
  }

  public AsyncInvocationRequestEntry(AsyncInvocationChannel channel,
      AsyncInvocationRequest request, AsyncCompletableFuture future) {
    this.channel = channel;
    this.request = request;
    this.future = future;
  }

  public void invoke() {
    if (channel != null && request != null) {
      if (log.isDebugEnabled()) {
        log.debug("Invoking: {}", toLog());
      }
      if (future != null) {
        future.setReadyToWait(true);
      }
      Future<InvocationResult> invokeFuture = channel.invoke(this);
      if (future != null) {
        try {
          InvocationResult result = invokeFuture.get();
          future.complete(result);
        } catch (Exception e) {
          future.completeExceptionally(e);
        }
      }
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
