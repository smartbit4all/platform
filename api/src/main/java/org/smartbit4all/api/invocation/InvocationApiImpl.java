package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

/**
 * The implementation of the {@link InvocationApi}. It collects all the
 * {@link InvocationExecutionApi} we have. If we call the {@link #invoke(InvocationRequest)} then
 * there is routing to the appropriate execution api. We always have a local execution api
 * 
 * @author Peter Boros
 */
public final class InvocationApiImpl implements InvocationApi {

  @Autowired
  private InvocationRegisterApi invocationRegisterApi;

  @Autowired(required = false)
  private ApplicationRuntimeApi applicationRuntimeApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  /**
   * The {@link InvocationExecutionApi} is for handling remote calls.
   */
  @Autowired(required = false)
  private InvocationExecutionApi executionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private InvocationApi self;;

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws ApiNotFoundException {
    ApiDescriptor apiDescriptor =
        invocationRegisterApi.getApi(request.getInterfaceClass(), request.getName());

    if (apiDescriptor == null) {
      throw new ApiNotFoundException(request);
    }

    return invoke(apiDescriptor, request);
  }

  private InvocationParameter invoke(ApiDescriptor apiDescriptor, InvocationRequest request)
      throws ApiNotFoundException {

    ApiData apiData = apiDescriptor.getApiData();
    List<UUID> runtimes = invocationRegisterApi.getRuntimesForApi(apiData.getUri());

    if (CollectionUtils.isEmpty(runtimes)) {
      throw new ApiNotFoundException(apiData);
    }

    // If the applicationRuntimeApi is null, then we can only invoke the api call in our own runtime
    if (applicationRuntimeApi == null
        || runtimes.contains(applicationRuntimeApi.self().getUuid())) {
      Object apiInstance = invocationRegisterApi.getApiInstance(apiData.getUri());
      Method method = Invocations.getMethodToCall(apiInstance, request);
      return Invocations.invokeMethod(request, apiInstance, method);
    } else {
      UUID runtimeToRun = getRuntimeToRun(runtimes);

      return executionApi.invoke(runtimeToRun, request);
    }
  }

  @Override
  public void invoke(ObjectNode asyncInvocationNode) {
    invocationRegisterApi.saveAndEnqueueAsyncInvocationRequest(asyncInvocationNode);
  }

  private UUID getRuntimeToRun(List<UUID> runtimes) {
    // TODO decide which runtime to use
    return runtimes.get(0);
  }

  @Override
  public <T> InvocationBuilder<T> builder(Class<T> apiInterface) {
    return new InvocationBuilder<>(apiInterface).sessionApi(sessionApi);
  }

  @Override
  public AsyncInvocationBuilder asyncBuilder() {
    return new AsyncInvocationBuilder(objectApi, self);
  }

  @Override
  public void invokeAsync(InvocationRequest request, String channel) {
    invocationRegisterApi.saveAndEnqueueAsyncInvocationRequest(request, channel);
  }

  @Override
  public void invokeAt(InvocationRequest request, String channel, OffsetDateTime executeAt) {

    invocationRegisterApi.saveAndScheduleAsyncInvocationRequest(request, channel,
        Objects.requireNonNull(executeAt,
            "The execution time must be specified to schedule an invocation."));
  }

}
