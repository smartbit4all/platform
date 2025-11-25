package org.smartbit4all.api.invocation;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.invocation.bean.InvocationCall;
import org.smartbit4all.api.invocation.bean.InvocationCallLog;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/**
 * If an {@link InvocationCallLog} is started on a theread then this object encapsulate the stack of
 * call logs and the parameteres of the log management.
 */
public class InvocationCallStack {

  /**
   * The first invocation call log that started this call log. It will be the point where this log
   * will be used.
   */
  private final InvocationCallLog startCallLog;

  /**
   * The invocation call log that finished for the last time. If it the last in the sequence then it
   * is the same as {@link #startCallLog}.
   */
  private InvocationCallLog lastFinishedCallLog;

  private final List<InvocationCallLog> callStack = new ArrayList<>();

  private boolean autoAddInvocations = false;

  public InvocationCallStack(InvocationCallLog startCallLog) {
    super();
    this.startCallLog = startCallLog;
  }

  public boolean isAutoAddInvocations() {
    return autoAddInvocations;
  }

  public InvocationCallStack autoAddInvocations(boolean autoAddInvocations) {
    this.autoAddInvocations = autoAddInvocations;
    return this;
  }

  public List<InvocationCallLog> getStack() {
    return callStack;
  }

  public InvocationCallLog getStartCallLog() {
    return startCallLog;
  }

  public InvocationCallLog getLastFinishedCallLog() {
    return lastFinishedCallLog;
  }

  public InvocationCallLog getCurrentCallLog() {
    return callStack.getLast();
  }

  void setLastFinishedCallLog(InvocationCallLog lastFinishedCallLog) {
    this.lastFinishedCallLog = lastFinishedCallLog;
  }

  /**
   * Two-pass pretty printer: 1) Compute total/self times for every node (postorder). 2) Print
   * call-first tree with indentation (preorder).
   *
   * Header selection: - call.invocationDescription if present/non-blank - otherwise
   * interfaceClass.methodName from call.request
   *
   * Each line prints: [total=XXms, self=YYms]
   *
   * @param root root log
   * @return formatted text
   */
  public static String print(InvocationCallLog root) {
    Map<InvocationCallLog, Times> timesByNode = new IdentityHashMap<>();
    computeTimes(root, timesByNode);

    StringBuilder sb = new StringBuilder();
    printPreorder(root, 0, timesByNode, sb);
    return sb.toString();
  }

  // ---------- PASS 1: compute times (postorder) ----------

  private static Times computeTimes(InvocationCallLog log,
      Map<InvocationCallLog, Times> timesByNode) {
    if (log == null) {
      return Times.unknown();
    }

    long childrenTotalMs = 0L;
    boolean allChildrenKnown = true;

    List<InvocationCallLog> subs = log.getSubCalls();
    if (subs != null) {
      for (InvocationCallLog child : subs) {
        Times childTimes = computeTimes(child, timesByNode);
        if (childTimes.totalMs >= 0) {
          childrenTotalMs += childTimes.totalMs;
        } else {
          allChildrenKnown = false;
        }
      }
    }

    long totalMs = durationMs(log.getStartTime(), log.getFinishTime());
    long selfMs;

    if (totalMs >= 0) {
      selfMs = totalMs - childrenTotalMs;
      if (selfMs < 0)
        selfMs = 0; // guard against overlap/clock skew
    } else {
      selfMs = -1L;
    }

    Times t = new Times(totalMs, selfMs, childrenTotalMs, allChildrenKnown);
    timesByNode.put(log, t);
    return t;
  }

  private static long durationMs(OffsetDateTime start, OffsetDateTime finish) {
    if (start == null || finish == null) {
      return -1L;
    }
    try {
      return Duration.between(start, finish).toMillis();
    } catch (Exception e) {
      return -1L;
    }
  }

  // ---------- PASS 2: print call-first tree (preorder) ----------

  private static void printPreorder(InvocationCallLog log,
      int depth,
      Map<InvocationCallLog, Times> timesByNode,
      StringBuilder sb) {
    if (log == null) {
      return;
    }

    Times t = timesByNode.getOrDefault(log, Times.unknown());

    String indent = "  ".repeat(depth);
    String header = headerText(log);

    sb.append(indent)
        .append("- ")
        .append(header)
        .append(" [total=")
        .append(formatMs(t.totalMs))
        .append(", self=")
        .append(formatMs(t.selfMs))
        .append("]")
        .append("\n");

    List<InvocationCallLog> subs = log.getSubCalls();
    if (subs != null) {
      for (InvocationCallLog child : subs) {
        printPreorder(child, depth + 1, timesByNode, sb);
      }
    }
  }

  private static String formatMs(long ms) {
    return ms >= 0 ? (ms + "ms") : "??ms";
  }

  private static String headerText(InvocationCallLog log) {
    if (log.getCall() == null) {
      return "(empty call)";
    }

    InvocationCall call = log.getCall();

    String desc = call.getInvocationDescription();
    if (desc != null && !desc.isBlank()) {
      return desc.trim();
    }

    InvocationRequest req = call.getInvocationRequest();
    if (req == null) {
      return "(unknown invocation)";
    }

    String iface = req.getInterfaceClass() != null ? req.getInterfaceClass() : "UnknownInterface";
    String method = req.getMethodName() != null ? req.getMethodName() : "unknownMethod";
    return iface + "." + method;
  }

  // ---------- helper record ----------

  private record Times(long totalMs, long selfMs, long childrenTotalMs, boolean allChildrenKnown) {
    static Times unknown() {
      return new Times(-1L, -1L, 0L, true);
    }
  }
}
