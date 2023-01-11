package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.InvocationError;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision.DecisionEnum;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannel;
import org.smartbit4all.api.session.SessionManagementApi;
import org.springframework.beans.factory.annotation.Autowired;

public final class AsyncInvocationChannelImpl
    implements AsyncInvocationChannel, AsyncInvocationChannelSetup {


  private static final Logger log = LoggerFactory.getLogger(AsyncInvocationChannelImpl.class);


  /**
   * The thread pool executor for the given channel and provides the execution threads for the
   * channels.
   */
  private ExecutorService executorService;

  /**
   * Core thread pool size of the {@link #executorService}.
   */
  private int corePoolSize = 2;

  /**
   * The maximum thread pool size of the {@link #executorService}.
   */
  private int maximumPoolSize = 4;

  /**
   * The technical user to be used to create session when executing.
   */
  private URI technicalUserUri;

  /**
   * The technical user name of the user to be used to create session when executing.
   */
  private String technicalUserName;

  /**
   * The name of the channel that must be unique all over a tenant.
   */
  private String name;

  /**
   * The invocation api to execute the invocations.
   */
  private InvocationApi invocationApi;

  @Autowired(required = false)
  private SessionManagementApi sessionManagementApi;

  private InvocationRegisterApi invocationRegisterApi;

  /**
   * The URI of the {@link RuntimeAsyncChannel} object related with this channel in the current
   * runtime.
   */
  private URI uri;

  public AsyncInvocationChannelImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public void invoke(AsyncInvocationRequestEntry requestEntry) {
    executorService.submit(() -> {
      // decorate the thread of given call.
      if (technicalUserUri != null && sessionManagementApi != null) {
        sessionManagementApi.startTechnicalSession(technicalUserUri);
      } else if (requestEntry.request.getRequest().getSessionUri() != null) {
        sessionManagementApi.setSession(requestEntry.request.getRequest().getSessionUri());
      }
      InvocationResult result = new InvocationResult().startTime(OffsetDateTime.now());
      try {
        result.returnValue(invocationApi.invoke(requestEntry.request.getRequest()).getValue());
      } catch (Exception e) {
        log.warn("Exception occured while executing the " + requestEntry, e);
        result.error(
            new InvocationError().definition(e.getClass().getName()).message(e.getMessage()));
      } finally {
        result.endTime(OffsetDateTime.now());
        // Let's make a decision about the next step
        InvocationResultDecision decision = null;
        if (requestEntry.request.getEvaluate() != null) {
          // We set the parameter for the call.
          requestEntry.request.getEvaluate().getParameters().get(0).setValue(requestEntry.request);
          requestEntry.request.getEvaluate().getParameters().get(1).setValue(result);
          try {
            // TODO This is an object read it with ObjectDefinition!
            decision = (InvocationResultDecision) invocationApi
                .invoke(requestEntry.request.getEvaluate()).getValue();
          } catch (Exception e) {
            log.error("Exception occured while trying to evaluate the " + result + " for the "
                + requestEntry.request, e);
          }
        }
        if (decision == null) {
          // Make a hard wired decision if there was error then abort, if we have andThen then
          // continue.
          decision = new InvocationResultDecision()
              .decision(result.getError() == null ? DecisionEnum.CONTINUE : DecisionEnum.ABORT);
        }
        result.decision(decision);
        // Save the result into the asynchronous request. It will result a call to the listeners.
        invocationRegisterApi.saveAsyncInvocationResult(requestEntry, result);
      }
    });
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void start() {
    executorService =
        new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());
  }

  @Override
  public void stop() {
    if (executorService != null) {
      executorService.shutdown();
      try {
        executorService.awaitTermination(1, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.error("ExecutorService awaitTermination interrupted", e);
        executorService.shutdownNow();
      }
    }
  }

  public final AsyncInvocationChannelImpl technicalUserName(String technicalUserName) {
    this.technicalUserName = technicalUserName;
    return this;
  }

  @Override
  public final void setInvocationApi(InvocationApi invocationApi) {
    this.invocationApi = invocationApi;
  }

  @Override
  public void setInvocationRegisterApi(InvocationRegisterApi invocationRegisterApi) {
    this.invocationRegisterApi = invocationRegisterApi;
  }

  @Override
  public void setUri(URI uri) {
    this.uri = uri;
  }

  @Override
  public URI getUri() {
    return uri;
  }

}
