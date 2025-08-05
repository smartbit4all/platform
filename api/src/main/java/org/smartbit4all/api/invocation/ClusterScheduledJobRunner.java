package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import org.smartbit4all.api.invocation.bean.JobInstance;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ScheduledJobState;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Cluster-scoped scheduled job runner. Ensures a single runtime owns/reserves a job execution at a
 * time.
 */
class ClusterScheduledJobRunner extends ScheduledJobRunner {

  public ClusterScheduledJobRunner(ScheduledJobDefinition def, URI runtimeUri, ObjectApi objectApi,
      InvocationApi invocationApi, ApplicationRuntimeApi applicationRuntimeApi,
      PlatformTransactionManager transactionManager) {
    super(def, runtimeUri, objectApi, invocationApi, applicationRuntimeApi, transactionManager);
  }

  @Override
  protected ReservationResult tryReserveExecutionInTransaction() {
    if (!shouldRunClusterJob()) {
      return null;
    }
    Lock lock = getLock();
    lock.lock();
    try {
      if (!shouldRunClusterJob()) {
        return null;
      }
      return reserveExecution();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Determines if the current runtime should run the cluster-scoped job.
   * 
   * @return true if this node should execute; false otherwise.
   */
  private boolean shouldRunClusterJob() {
    ScheduledJobState state = objectApi.loadLatest(scheduledJobDef.getState())
        .getObject(ScheduledJobState.class);
    if (!state.getInstances().isEmpty()) {
      Set<URI> activeRuntimes = applicationRuntimeApi.getActiveRuntimes().stream()
          .map(ApplicationRuntime::getUri)
          .collect(Collectors.toSet());

      URI instance = state.getInstances().get(0);
      URI runtimeUri =
          objectApi.loadLatest(instance).getValue(URI.class, JobInstance.RUNTIME);
      boolean takeOver = !activeRuntimes.contains(runtimeUri);
      // if runtime for instance is inactive, then take over
      if (!takeOver) {
        ownerRuntimeUri = runtimeUri;
      }
      return takeOver;
    }

    return true;
  }

  @Override
  protected void cleanupSchedules(ScheduledJobState state) {
    // Do nothing for cluster
  }

  @Override
  protected ReservationResult reserveExecutionForState(ObjectNode stateNode,
      ScheduledJobState state) {

    // Need to take over missed execution from another runtime
    if (!state.getInstances().isEmpty()) {
      OffsetDateTime now = OffsetDateTime.now();

      // update entry with self
      URI instanceUri = state.getInstances().get(0);

      // modify instance with self
      modifyInstanceWithSelf(instanceUri, now);

      // update next scheduled time
      refreshExistingNextSceduledAt(state, now);

      stateNode.modify(ScheduledJobState.class, s -> state);
      objectApi.save(stateNode);

      return new ReservationResult(instanceUri, state.getNextScheduledAt());
    }

    return super.reserveExecutionForState(stateNode, state);
  }

  private void refreshExistingNextSceduledAt(ScheduledJobState state, OffsetDateTime now) {
    OffsetDateTime scheduledAt = state.getNextScheduledAt();

    // If the instance that was taken over from another node has
    // - past scheduled at, then run job now
    // - future scheduled at, then run the job with the original schedule time
    // - anything else, then calculate the next scheduled at normally
    if (scheduledAt == null || scheduledAt.isBefore(now)) {
      state.setNextScheduledAt(now);
      return;
    }
    if (scheduledAt == null || !scheduledAt.isBefore(now)) {
      return;
    }

    refreshNextScheduledAt(state);
  }

  @Override
  protected OffsetDateTime refreshNextScheduledAt(ScheduledJobState state) {
    // For cluster: set nextScheduledAt to the next cron time after now, or keep if future. Also:
    // handles "missed" executions (fires immediately if needed).

    OffsetDateTime now = OffsetDateTime.now();
    OffsetDateTime scheduledAt;
    // Fire immediately if missed
    scheduledAt = state.getNextScheduledAt();
    if (scheduledAt == null || scheduledAt.isBefore(now)) {
      state.setNextScheduledAt(nextExecutionTime(now)); // Set the next after now
    }
    return state.getNextScheduledAt();
  }

  /** Updates the job instance entry with this runtime and reservation time. */
  private void modifyInstanceWithSelf(URI instanceUri, OffsetDateTime now) {
    ObjectNode instanceNode = objectApi.loadLatest(instanceUri);
    instanceNode.modify(JobInstance.class,
        instance -> instance.runtime(runtimeUri).reservedAt(now));
    objectApi.save(instanceNode);
  }
}
