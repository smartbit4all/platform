package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.JobDefinition;
import org.smartbit4all.api.invocation.bean.JobInstance;
import org.smartbit4all.api.invocation.bean.JobParameter;
import org.smartbit4all.api.invocation.bean.JobParameter.TypeEnum;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ScheduledJobState;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.core.object.ContextObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import com.google.common.base.Objects;
import jakarta.validation.Valid;

/**
 * Abstract base class for executing a scheduled job. Handles common logic such as reserving
 * execution, execution lifecycle, and managing job state and instances.
 */
public abstract class ScheduledJobRunner {
  private static final Logger log = LoggerFactory.getLogger(ScheduledJobRunner.class);

  // JobDefinition
  protected final ScheduledJobDefinition scheduledJobDef;
  protected final JobDefinition jobDefinition;
  protected final URI runtimeUri;
  protected URI ownerRuntimeUri;

  // Apis
  protected final ObjectApi objectApi;
  protected final ApplicationRuntimeApi applicationRuntimeApi;
  protected final InvocationApi invocationApi;
  protected final PlatformTransactionManager transactionManager;
  protected final MasterDataManagementApi mdmApi;
  protected final SessionManagementApi sessionManagementApi;

  // Schedule
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  protected volatile ScheduledFuture<?> future;

  public ScheduledJobRunner(ScheduledJobDefinition def,
      URI runtimeUri,
      ObjectApi objectApi,
      InvocationApi invocationApi,
      MasterDataManagementApi mdmApi,
      ApplicationRuntimeApi applicationRuntimeApi,
      SessionManagementApi sessionManagementApi,
      PlatformTransactionManager transactionManager) {
    this.scheduledJobDef = def;
    this.runtimeUri = runtimeUri;
    this.objectApi = objectApi;
    this.invocationApi = invocationApi;
    this.mdmApi = mdmApi;
    this.applicationRuntimeApi = applicationRuntimeApi;
    this.sessionManagementApi = sessionManagementApi;
    this.transactionManager = transactionManager;

    String jobDefinitionCode = scheduledJobDef.getJobDefinitionCode();
    this.jobDefinition = mdmApi
        .getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            InvocationApiMdmConfig.MDM_ENTRY_JOBDEFINITION)
        .getList().nodesFromCache()
        .filter(n -> Objects.equal(jobDefinitionCode, n.getValueAsString(JobDefinition.CODE)))
        .findFirst()
        .map(n -> n.getObject(JobDefinition.class))
        .get();
  }

  /**
   * Entrypoint: Triggers scheduling depending on the execution scope.
   */
  public void scheduleIfNeeded() {
    if (scheduledJobDef.getState() == null) {
      initStateIfMissing();
    }
    ReservationResult result = tryReserveExecution();
    if (result != null) {
      scheduleJob(result.instanceUri, result.nextScheduledAt);
    }
  }

  private ReservationResult tryReserveExecution() {
    if (transactionManager == null) {
      // no transactionManager or already in transaction
      return tryReserveExecutionInTransaction();
    }
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    return transaction.execute(status -> tryReserveExecutionInTransaction());
  }

  /**
   * Attempts to reserve a job execution for this node.
   * 
   * @return ReservationResult if reserved, null otherwise.
   */
  protected abstract ReservationResult tryReserveExecutionInTransaction();

  /**
   * Initializes the job state if it has not been created yet. Ensures only one node creates it
   * using a lock on the job definition.
   */
  private void initStateIfMissing() {
    if (transactionManager == null) {
      // no transactionManager or already in transaction
      initStateIfMissingInTransaction();;
      return;
    }
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    transaction.executeWithoutResult(status -> initStateIfMissingInTransaction());
  }

  /**
   * Initializes the job state if it has not been created yet. Ensures only one node creates it
   * using a lock on the job definition.
   */
  private void initStateIfMissingInTransaction() {
    Lock definitionLock = objectApi.getLock(objectApi.getLatestUri(scheduledJobDef.getUri()));
    definitionLock.lock();
    try {
      // Re-fetch job definition to avoid stale in-memory state
      ObjectNode jobDefNode = objectApi.loadLatest(scheduledJobDef.getUri());
      ScheduledJobDefinition freshJobDef = jobDefNode.getObject(ScheduledJobDefinition.class);
      URI stateUri;

      if (freshJobDef.getState() != null) {
        stateUri = freshJobDef.getState();
      } else {
        // Create new state and save it
        ScheduledJobState newState = new ScheduledJobState();
        stateUri = objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, newState);

        // Link the state to the job definition
        jobDefNode.modify(ScheduledJobDefinition.class, d -> d.state(stateUri));
        objectApi.save(jobDefNode);
      }

      // Update local in-memory definition as well
      scheduledJobDef.state(stateUri);
    } finally {
      definitionLock.unlock();
    }
  }

  /**
   * Reserves execution by creating a new instance and marking it as running in job state.
   */
  protected ReservationResult reserveExecution() {
    ownerRuntimeUri = runtimeUri;
    ObjectNode stateNode = objectApi.loadLatest(scheduledJobDef.getState());
    ScheduledJobState state = stateNode.getObject(ScheduledJobState.class);

    return reserveExecutionForState(stateNode, state);
  }

  /**
   * Reserves execution by creating a new instance and marking it as running in job state.
   */
  protected ReservationResult reserveExecutionForState(ObjectNode stateNode,
      ScheduledJobState state) {
    URI instanceUri = createInstance();
    state.getInstances().add(instanceUri);
    final OffsetDateTime nextAt = refreshNextScheduledAt(state);

    stateNode.modify(ScheduledJobState.class, s -> state);
    objectApi.save(stateNode);
    return new ReservationResult(instanceUri, nextAt);
  }

  /**
   * Update scheduling state with nextExecutionTime
   */
  protected abstract OffsetDateTime refreshNextScheduledAt(ScheduledJobState state);

  /**
   * Creates and saves a new job instance for this runtime.
   */
  protected URI createInstance() {
    OffsetDateTime now = OffsetDateTime.now();
    JobInstance instance = new JobInstance()
        .createdAt(now)
        .runtime(runtimeUri)
        .reservedAt(now)
        .threadName(Thread.currentThread().getName());
    return objectApi.saveAsNew(Invocations.INVOCATION_SCHEME, instance);
  }

  /**
   * Retrieves the lock for this job state.
   */
  protected Lock getLock() {
    return objectApi.getLock(objectApi.getLatestUri(scheduledJobDef.getState()));
  }

  /**
   * Schedules the job to run at its scheduled time (possibly now if missed).
   */
  protected void scheduleJob(URI instanceUri, OffsetDateTime scheduledAt) {
    if (future != null && !future.isDone()) {
      return;
    }

    OffsetDateTime now = OffsetDateTime.now(scheduledAt.getOffset());
    Duration duration = Duration.between(now, scheduledAt);
    long delay = Math.max(0, duration.toMillis());

    future = scheduler.schedule(() -> {
      String error = null;
      OffsetDateTime startedAt = OffsetDateTime.now();
      try {
        startTechnicalSessionIfNeeded();
        // Start execution
        startExecution(instanceUri, startedAt);

        // call InvocationRun
        runInvocation();

      } catch (Exception ex) {
        error = ex.getMessage();
        log.error("Cannot execute schedule job " + scheduledJobDef.getCode(), ex);
      } finally {
        // Always cleanup, remove instance entry, update runtimeSchedules (for node), etc.
        OffsetDateTime finishedAt = OffsetDateTime.now();
        finishExecution(instanceUri, error, finishedAt);
        // Reschedule (re-evaluate if needed)
        future = null;
        if (canSchedule()) {
          scheduleIfNeeded();
        }
      }
    }, delay, TimeUnit.MILLISECONDS);
  }

  private void startTechnicalSessionIfNeeded() {
    if (sessionManagementApi != null && scheduledJobDef.getUserName() != null) {
      sessionManagementApi.startTechnicalSessionWithUser(scheduledJobDef.getUserName());
    }
  }

  private void finishExecution(URI instanceUri, String error, OffsetDateTime finishedAt) {
    if (transactionManager == null) {
      // no transactionManager or already in transaction
      finishExecutionInTransaction(instanceUri, error, finishedAt);
      return;
    }
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    transaction.executeWithoutResult(
        status -> finishExecutionInTransaction(instanceUri, error, finishedAt));
  }

  protected void finishExecutionInTransaction(URI instanceUri, String error,
      OffsetDateTime finishedAt) {
    // Mark instance as finished
    finishInstance(instanceUri, finishedAt, error);
    cleanupAfterExecution(instanceUri, error);
  }

  private void startExecution(URI instanceUri, OffsetDateTime startedAt) {
    if (transactionManager == null) {
      // no transactionManager or already in transaction
      startExecutionInTransaction(instanceUri, startedAt);
      return;
    }
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    transaction.executeWithoutResult(status -> startExecutionInTransaction(instanceUri, startedAt));
  }

  protected void startExecutionInTransaction(URI instanceUri, OffsetDateTime startedAt) {
    ObjectNode instanceNode = objectApi.loadLatest(instanceUri);
    instanceNode.modify(JobInstance.class, instance -> instance.startedAt(startedAt));
    objectApi.save(instanceNode);
  }

  private boolean canSchedule() {
    return scheduledJobDef.getCronExpression() != null;
  }

  protected void runInvocation() {
    invocationApi.run(createContextObject(), jobDefinition.getTask());
  }

  protected ContextObject createContextObject() {
    List<@Valid JobParameter> parameters = getParameters();
    ContextObject contextObject = objectApi.contextObject();

    List<JobParameter> valueParams = parameters.stream()
        .filter(param -> param.getType() == TypeEnum.VALUE)
        .toList();

    // TODO handle collections parameters
    valueParams.forEach(param -> contextObject.set(param.getKey(), param.getValue()));

    return contextObject;
  }

  protected List<JobParameter> getParameters() {
    return !CollectionUtils.isEmpty(scheduledJobDef.getParameters())
        ? scheduledJobDef.getParameters()
        : jobDefinition.getParameters();
  }

  /**
   * Mark the instance as finished, and set error if any.
   */
  private void finishInstance(URI instanceUri, OffsetDateTime finishedAt, String error) {
    ObjectNode instanceNode = objectApi.loadLatest(instanceUri);
    instanceNode.modify(JobInstance.class, i -> {
      i.finishedAt(finishedAt);
      if (error != null)
        i.errorMessage(error);
      return i;
    });
    objectApi.save(instanceNode);
  }

  /**
   * Cleanup state after execution: removes the finished instance entry from state.
   */
  private void cleanupAfterExecution(URI instanceUri, String error) {
    Lock lock = getLock();
    lock.lock();
    try {
      ObjectNode stateNode = objectApi.loadLatest(scheduledJobDef.getState());
      stateNode.modify(ScheduledJobState.class, state -> {
        // Remove the finished instance
        state.getInstances().removeIf(i -> Objects.equal(instanceUri, i));
        // For node jobs, remove the runtimeSchedule for this runtime
        cleanupSchedules(state);
        return state;
      });
      objectApi.save(stateNode);
    } finally {
      lock.unlock();
    }
  }

  protected abstract void cleanupSchedules(ScheduledJobState state);

  /**
   * Calculates the next execution time using the job's cron expression, given a reference time.
   */
  protected OffsetDateTime nextExecutionTime(OffsetDateTime reference) {
    CronExpression expression = CronExpression.parse(scheduledJobDef.getCronExpression());
    return expression.next(reference);
  }

  /**
   * Gets the runtime URI for this runner.
   */
  public URI getOwnerRuntimeUri() {
    return ownerRuntimeUri;
  }


  public void stop() {
    future.cancel(true);
  }

  public void shutDown() {
    scheduler.shutdown();
  }

  /**
   * Encapsulates the reservation result for scheduling.
   */
  record ReservationResult(URI instanceUri, OffsetDateTime nextScheduledAt) {
  }
}
