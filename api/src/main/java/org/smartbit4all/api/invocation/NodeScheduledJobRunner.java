package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.api.invocation.bean.RuntimeSchedule;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ScheduledJobState;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.transaction.PlatformTransactionManager;
import com.google.common.base.Objects;


/**
 * Node-scoped scheduled job runner. Each node manages its own execution independently.
 */
class NodeScheduledJobRunner extends ScheduledJobRunner {

  public NodeScheduledJobRunner(ScheduledJobDefinition def, URI runtimeUri, ObjectApi objectApi,
      InvocationApi invocationApi, ApplicationRuntimeApi applicationRuntimeApi,
      PlatformTransactionManager transactionManager) {
    super(def, runtimeUri, objectApi, invocationApi, applicationRuntimeApi, transactionManager);
  }

  @Override
  protected ReservationResult tryReserveExecutionInTransaction() {
    Lock lock = getLock();
    lock.lock();
    try {
      return reserveExecution();
    } finally {
      lock.unlock();
    }
  }

  @Override
  protected OffsetDateTime refreshNextScheduledAt(ScheduledJobState state) {
    // For node: ensure runtimeSchedules for this node is updated.
    OffsetDateTime now = OffsetDateTime.now();
    RuntimeSchedule mySchedule = state.getRuntimeSchedules().stream()
        .filter(rs -> Objects.equal(runtimeUri, rs.getRuntime()))
        .findFirst()
        .orElseGet(() -> {
          RuntimeSchedule rs = new RuntimeSchedule();
          rs.setRuntime(runtimeUri);
          state.getRuntimeSchedules().add(rs);
          return rs;
        });
    mySchedule.setNextScheduledAt(nextExecutionTime(now));
    return mySchedule.getNextScheduledAt();
  }

  @Override
  protected void cleanupSchedules(ScheduledJobState state) {
    state.getRuntimeSchedules().removeIf(rs -> Objects.equal(runtimeUri, rs.getRuntime()));
  }
}
