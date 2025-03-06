package org.smartbit4all.api.invocation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;

/**
 * A special future to enable waiting for the {@link InvocationApi} asynchronous execution. This
 * future can be passed to the
 * {@link InvocationApi#invokeAsyncAndWait(InvocationRequest, String, AsyncCompletableFuture)}
 * function. Use this future to wait for the execution result when you are sure that the execution
 * is already started.
 */
public final class AsyncCompletableFuture extends CompletableFuture<InvocationResult> {

  /**
   * A special flag that indicates if the given futire is ready to wait.
   */
  private boolean readyToWait = false;

  private InvocationRequest invocationRequest;

  @Override
  public InvocationResult get() throws InterruptedException, ExecutionException {
    check();
    return super.get();
  }

  private final void check() {
    if (!readyToWait) {
      throw new IllegalStateException(
          "Unable to start waiting for the result of the invocation request, the execution hasn't been started yet. Start waiting after the completeion of the current transaction. ("
              +
              invocationRequest + ")");
    }
  }

  @Override
  public InvocationResult get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    check();
    return super.get(timeout, unit);
  }

  public final boolean isReadyToWait() {
    return readyToWait;
  }

  final void setReadyToWait(boolean readyToWait) {
    this.readyToWait = readyToWait;
  }

  public final InvocationRequest getInvocationRequest() {
    return invocationRequest;
  }

  final void setInvocationRequest(InvocationRequest invocationRequest) {
    this.invocationRequest = invocationRequest;
  }

}
