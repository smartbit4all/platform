package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationCallLog;

public class InvocationCallResult<T> {

  private final InvocationCallLog callLog;

  private final T callResult;

  public InvocationCallResult(InvocationCallLog callLog, T callResult) {
    super();
    this.callLog = callLog;
    this.callResult = callResult;
  }

  public InvocationCallLog getCallLog() {
    return callLog;
  }

  public T getCallResult() {
    return callResult;
  }

}
