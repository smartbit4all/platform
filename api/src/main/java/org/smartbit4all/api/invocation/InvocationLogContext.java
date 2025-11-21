package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;
import org.smartbit4all.api.invocation.bean.InvocationCall;
import org.smartbit4all.api.invocation.bean.InvocationCallLog;

/**
 * This object contains an {@link InvocationCallLog} object and register the start of invocation
 * calls and it sub call recursively. It always know the current call and if start a sub call then
 * the sub call will be the current. The current depens on the thread we are working on. So it can
 * track the execution of a complex call hierarchy.
 */
public class InvocationLogContext {

  /**
   * The current call is always set on the current thread into this thread local. It is necessary
   * because if some of the calls are executed asynchronously via another thread then the current
   * could be different.
   */
  private final ThreadLocal<InvocationCallStack> currentCallStack = new ThreadLocal<>();

  InvocationLogContext() {
    super();
  }

  public InvocationCallStack startCall(InvocationCall call) {
    InvocationCallStack currentLogStack = currentCallStack.get();
    InvocationCallLog subCall = new InvocationCallLog()
        .startTime(OffsetDateTime.now()).call(call);
    if (currentLogStack == null) {
      // It is an enry point to start from as a root.
      currentLogStack = new InvocationCallStack();
      currentCallStack.set(currentLogStack);
    } else {
      InvocationCallLog currentLog;
      currentLog = currentLogStack.getStack().getLast();
      currentLog.addSubCallsItem(subCall);
    }
    currentLogStack.getStack().add(subCall);
    return currentLogStack;
  }

  public InvocationCallStack finishCall() {
    InvocationCallStack currentLogStack = currentCallStack.get();
    if (currentLogStack == null) {
      return null;
    }
    List<InvocationCallLog> stack = currentLogStack.getStack();
    InvocationCallLog currentLog = stack.getLast();
    currentLog.finishTime(OffsetDateTime.now());
    if (stack.size() > 1) {
      stack.removeLast();
    }
    return currentLogStack;
  }

  public InvocationCallLog cancelCall() {
    InvocationCallStack currentLogStack = currentCallStack.get();
    if (currentLogStack != null) {
      // Call all the finish of the executing calls.
      int runningCalls = currentLogStack.getStack().size();
      for (int i = 0; i < runningCalls; i++) {
        finishCall();
      }
      currentCallStack.remove();
      return currentLogStack.getStack().getFirst();
    }
    return null;
  }

  public final <T> InvocationCallResult<T> execute(Supplier<T> action, InvocationCall call) {
    startCall(call);
    T result;
    InvocationCallLog callLog = null;
    try {
      result = action.get();
    } finally {
      callLog = cancelCall();
    }
    return new InvocationCallResult<T>(callLog, result);
  }

}
