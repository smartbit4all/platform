package org.smartbit4all.api.invocation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
    return startCall(call, false, false);
  }

  public InvocationCallStack startCallJoinOnly(InvocationCall call) {
    return startCall(call, true, false);
  }

  InvocationCallStack startCall(InvocationCall call, boolean joinOnly, boolean startIfAutoAdd) {
    InvocationCallStack currentLogStack = currentCallStack.get();
    if ((currentLogStack == null && joinOnly)
        || (currentLogStack != null && startIfAutoAdd && !currentLogStack.isAutoAddInvocations())) {
      return currentLogStack;
    }
    InvocationCallLog subCall = new InvocationCallLog()
        .startTime(OffsetDateTime.now()).call(call);
    if (currentLogStack == null) {
      // It is an enry point to start from as a root.
      currentLogStack = new InvocationCallStack(subCall);
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
    return finishCall(null);
  }

  public InvocationCallStack finishCall(Object result) {
    InvocationCallStack currentLogStack = currentCallStack.get();
    if (currentLogStack == null) {
      return null;
    }
    List<InvocationCallLog> stack = currentLogStack.getStack();
    InvocationCallLog currentLog = stack.getLast();
    currentLog.finishTime(OffsetDateTime.now()).result(result);
    if (!stack.isEmpty()) {
      currentLogStack.setLastFinishedCallLog(stack.removeLast());
    }
    if (stack.isEmpty()) {
      // We finished the last call in the hierarchy so we can remove the thread local.
      currentCallStack.remove();
    }
    return currentLogStack;
  }

  public Optional<InvocationCallStack> getCurrent() {
    InvocationCallStack currentLogStack = currentCallStack.get();
    if (currentLogStack == null) {
      return Optional.empty();
    }
    return Optional.of(currentLogStack);
  }

  /**
   * Append the {@link InvocationCallLog} as an already finished one to the sub calls of the
   * currently running call if any. Could be used to append the log returned by an integration call.
   * 
   * @param logToAppend
   * @return The currently running call log if any.
   */
  public InvocationCallLog appendSubCall(InvocationCallLog logToAppend) {
    if (logToAppend == null) {
      return null;
    }
    InvocationCallStack currentLogStack = currentCallStack.get();
    if (currentLogStack == null) {
      return null;
    }
    InvocationCallLog currentlyRunning = currentLogStack.getStack().getLast();
    currentlyRunning.addSubCallsItem(logToAppend);
    return currentlyRunning;
  }

  public InvocationCallStack cancelCall() {
    InvocationCallStack currentLogStack = currentCallStack.get();
    if (currentLogStack != null) {
      // Call all the finish of the executing calls.
      int runningCalls = currentLogStack.getStack().size();
      for (int i = 0; i < runningCalls; i++) {
        finishCall();
      }
    }
    return currentLogStack;
  }

  public final <T> InvocationCallResult<T> execute(ThrowingSupplier<T> action,
      InvocationCall call) {
    return execute(action, call, false, false);
  }

  public final <T> InvocationCallResult<T> executeJoinOnly(ThrowingSupplier<T> action,
      InvocationCall call) {
    return execute(action, call, true, false);
  }

  @FunctionalInterface
  public interface ThrowingSupplier<T> {
    T get() throws Exception;
  }

  public final <T> InvocationCallResult<T> execute(ThrowingSupplier<T> action, InvocationCall call,
      boolean joinOnly, boolean autoAddInvocations) {
    startCall(call, joinOnly, autoAddInvocations);
    T result;
    InvocationCallLog callLog = null;
    try {
      result = action.get();
    } catch (Throwable e) {
      InvocationCallStack finishCall = finishCall();
      throw new InvocationFailedException(call,
          finishCall == null ? null : finishCall.getStartCallLog(), e);
    } finally {
      if (callLog == null) {
        InvocationCallStack finishCall = finishCall();
        callLog = finishCall == null ? null : finishCall.getStartCallLog();
      }
    }
    return new InvocationCallResult<T>(callLog, result);
  }

}
