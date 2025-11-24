package org.smartbit4all.api.invocation;

import org.smartbit4all.api.invocation.bean.InvocationCall;
import org.smartbit4all.api.invocation.bean.InvocationCallLog;

/**
 * This exception is used when the {@link InvocationLogContext} is executing an invocation and
 * exception occurred. The exception and the log itself is wrapped into this exception.
 */
public class InvocationFailedException extends RuntimeException {

  private final InvocationCall call;
  private final InvocationCallLog log;

  public InvocationFailedException(InvocationCall call, InvocationCallLog log, Throwable cause) {
    super(cause);
    this.call = call;
    this.log = log;
  }

  public InvocationCall getCall() {
    return call;
  }

  public InvocationCallLog getLog() {
    return log;
  }

}
