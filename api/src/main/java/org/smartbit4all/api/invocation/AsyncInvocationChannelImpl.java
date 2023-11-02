package org.smartbit4all.api.invocation;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationError;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision.DecisionEnum;
import org.smartbit4all.api.invocation.bean.RuntimeAsyncChannel;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
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
   * The technical session uri for every used thread.
   */
  private URI technicalSessionUri;

  private Lock sessionLock = new ReentrantLock();

  /**
   * Core thread pool size of the {@link #executorService}.
   */
  private int corePoolSize = 2;

  /**
   * The maximum thread pool size of the {@link #executorService}.
   */
  private int maximumPoolSize = 4;

  private long keepAliveTime = 1;

  private TimeUnit timeUnit = TimeUnit.MINUTES;

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

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired(required = false)
  private OrgApi orgApi;

  private InvocationRegisterApi invocationRegisterApi;

  /**
   * The URI of the {@link RuntimeAsyncChannel} object related with this channel in the current
   * runtime.
   */
  private URI uri;


  private User userByUsername;

  public AsyncInvocationChannelImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public void invoke(AsyncInvocationRequestEntry requestEntry) {
    executorService.submit(() -> {
      // decorate the thread of given call.
      URI userUri = getTechnicalUserUri();
      if (userUri != null && sessionManagementApi != null) {
        ensureUriTechnicalSession();
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
        InvocationRequest evaluate = requestEntry.request.getEvaluate();
        if (evaluate != null) {
          // We set the request and the result parameters for the call when they are present in the
          // signature.
          for (InvocationParameter parameter : evaluate.getParameters()) {
            if (AsyncInvocationRequest.class.getName().equals(parameter.getTypeClass())) {
              parameter.setValue(requestEntry.request);
            } else if (InvocationResult.class.getName().equals(parameter.getTypeClass())) {
              parameter.setValue(result);
            }
          }
          try {
            // TODO This is an object read it with ObjectDefinition!
            decision = (InvocationResultDecision) invocationApi.invoke(evaluate).getValue();
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
        } else {
          int size = requestEntry.request.getResults() == null ? 0
              : requestEntry.request.getResults().size();
          int gradient = size / 50;
          gradient = gradient * gradient;
          OffsetDateTime now = OffsetDateTime.now();
          OffsetDateTime requiredScheduledAt = now.plusSeconds(gradient * 5);
          if ((decision.getScheduledAt() != null
              && requiredScheduledAt.isAfter(decision.getScheduledAt()))
              || decision.getScheduledAt() == null) {
            decision.scheduledAt(requiredScheduledAt);
          }
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
        new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
            new LinkedBlockingQueue<>());
  }

  public AsyncInvocationChannelImpl threadPool(int corePoolSize, int maximumPoolSize,
      long keepAliveTime, TimeUnit timeUnit) {
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.keepAliveTime = keepAliveTime;
    this.timeUnit = timeUnit;
    return this;
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

  public URI getTechnicalUserUri() {
    if (technicalUserUri != null) {
      return technicalUserUri;
    }
    if (technicalUserName != null && orgApi != null) {
      User user = orgApi.getUserByUsername(technicalUserName);
      if (user != null) {
        technicalUserUri = user.getUri();
      } else {
        technicalUserName = null;
      }
    }
    return technicalUserUri;
  }

  public String getTechnicalUserName() {
    return technicalUserName;
  }

  private void ensureUriTechnicalSession() {
    if (technicalSessionUri != null) {
      try {
        sessionManagementApi.setSession(technicalSessionUri);
        // we managed to set the session, thus it is valid => we can return:
        return;
      } catch (Exception e) {
        technicalSessionUri = null;
      }
    }
    if (sessionManagementApi != null && sessionApi != null) {
      sessionLock.lock();
      try {
        if (technicalSessionUri != null) {
          // some other thread managed to initialise the technical session URI. We can simply set it
          // for ourselves, and our job is done:
          sessionManagementApi.setSession(technicalSessionUri);
          return;
        }
        URI userUri = getTechnicalUserUri();
        if (userUri != null) {
          sessionManagementApi.startTechnicalSession(userUri);
          technicalSessionUri = sessionApi.getSessionUri();
        }
      } finally {
        sessionLock.unlock();
      }
    }
  }

}
