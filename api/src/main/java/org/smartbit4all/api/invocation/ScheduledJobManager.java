package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition.ExecutionScopeEnum;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Manages and tracks all scheduled jobs in the current runtime. Delegates execution to appropriate
 * runners based on job scope.
 */
public class ScheduledJobManager {

  private final Map<String, ScheduledJobRunner> jobControls = new ConcurrentHashMap<>();

  @Autowired
  private ApplicationRuntimeApi applicationRuntimeApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired(required = false)
  private PlatformTransactionManager transactionManager;

  private URI runtimeUri;

  public ScheduledJobManager() {}

  public void scheduleJobs(List<ScheduledJobDefinition> jobDefinitions) {
    jobDefinitions.stream().forEach(this::startScheduledJob);
  }

  /**
   * Factory for creating correct runner based on execution scope.
   */
  protected ScheduledJobRunner createScheduledJobRunner(ScheduledJobDefinition jobDefinition,
      URI runtimeUri) {
    ExecutionScopeEnum scope = jobDefinition.getExecutionScope();

    if (scope == null) {
      throw new IllegalArgumentException(
          "Execution scope cannot be null for job: " + jobDefinition.getId());
    }

    switch (scope) {
      case CLUSTER:
        return new ClusterScheduledJobRunner(jobDefinition,
            runtimeUri,
            objectApi,
            invocationApi,
            applicationRuntimeApi,
            transactionManager);

      case NODE:
        return new NodeScheduledJobRunner(jobDefinition,
            runtimeUri,
            objectApi,
            invocationApi,
            applicationRuntimeApi,
            transactionManager);

      default:
        throw new IllegalArgumentException("Unsupported execution scope: " + scope);
    }
  }

  public void stopScheduledJob(String jobId) {
    ScheduledJobRunner runner = jobControls.get(jobId);
    if (runner != null) {
      runner.stop();
      jobControls.remove(jobId);
    }
  }

  public void startScheduledJob(ScheduledJobDefinition def) {
    ScheduledJobRunner runner = createScheduledJobRunner(def, runtimeUri);
    runner.scheduleIfNeeded();
    jobControls.put(def.getId(), runner);
  }

  public List<ScheduledJobRunner> getAllJobControls() {
    return new ArrayList<>(jobControls.values());
  }

  @Scheduled(initialDelayString = "${scheduledjobmanager.maintain.initialDelay:30000}",
      fixedDelayString = "${scheduledjobmanager.maintain.fixeddelay:30000}")
  public void maintain() throws InterruptedException, ExecutionException {
    Set<URI> activeRuntimes = applicationRuntimeApi.getActiveRuntimes().stream()
        .map(ApplicationRuntime::getUri)
        .collect(Collectors.toSet());

    // reschedule jobs with inactive runtimes
    jobControls.values().stream()
        .filter(runner -> !activeRuntimes.contains(runner.getOwnerRuntimeUri()))
        .forEach(runner -> reschedule(runner));
  }

  protected void reschedule(ScheduledJobRunner runner) {
    runner.stop();
    runner.scheduleIfNeeded();
  }

  public void shutdown() {
    jobControls.values().stream().forEach(runner -> runner.shutDown());
  }

}
