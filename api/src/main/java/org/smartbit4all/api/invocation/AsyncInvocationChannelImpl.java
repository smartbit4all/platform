package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.session.SessionManagementApi;

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

  private String technicalUserName;

  /**
   * The technical user to be used to create session when
   */
  private URI technicalUserUri;

  private String name;

  private InvocationApi invocationApi;

  private CollectionApi collectionApi;

  private SessionManagementApi sessionManagementApi;

  public AsyncInvocationChannelImpl(String name) {
    super();
    this.name = name;
  }

  @Override
  public void invoke(AsyncInvocationRequest request) {
    executorService.submit(() -> {
      // decorate the thread of given call.
      if (technicalUserUri != null && sessionManagementApi != null) {
        sessionManagementApi.startTechnicalSession(technicalUserUri);
      } else if (request.getRequest().getSessionUri() != null) {
        sessionManagementApi.setSession(request.getRequest().getSessionUri());
      }
      try {
        InvocationParameter returnValue = invocationApi.invoke(request.getRequest());
        collectionApi
            .list(request.getRuntimeUri(), Invocations.INVOCATION_SCHEME, name)
            .remove(request.getUri());
        // Save the result into the asynchronous request. It will result a call to the listeners.
      } catch (Exception e) {
        log.warn("Exception occured while executing the " + request, e);
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
  public final AsyncInvocationChannelImpl invocationApi(InvocationApi invocationApi) {
    this.invocationApi = invocationApi;
    return this;
  }

  @Override
  public final void setSessionManagementApi(SessionManagementApi sessionManagementApi) {
    this.sessionManagementApi = sessionManagementApi;
  }

  @Override
  public AsyncInvocationChannel collectionApi(CollectionApi collectionApi) {
    this.collectionApi = collectionApi;
    return this;
  }

}
