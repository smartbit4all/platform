package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationResult;
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
  public Future<InvocationResult> invoke(AsyncInvocationRequestEntry requestEntry) {
    return executorService.submit(() -> {
      AsyncInvocationRequest request = requestEntry.request;
      // decorate the thread of given call.
      if (Boolean.TRUE.equals(request.getRequest().getInheritSession())
          && request.getRequest().getSessionUri() != null) {
        sessionManagementApi.setSession(request.getRequest().getSessionUri());
      } else {
        URI userUri = getTechnicalUserUri();
        if (userUri != null) {
          ensureUriTechnicalSession();
        } else if (request.getRequest().getSessionUri() != null) {
          sessionManagementApi.setSession(request.getRequest().getSessionUri());
        }
      }
      return invocationApi.executeAsyncInvocationRequest(requestEntry);
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
