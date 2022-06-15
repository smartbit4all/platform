package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

  @Autowired
  private ApplicationRuntimeApi applicationRuntimeApi;

  /**
   * The {@link InvocationExecutionApi} is for handling remote calls.
   */
  @Autowired(required = false)
  private InvocationExecutionApi executionApi;


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

    ApplicationRuntime self = applicationRuntimeApi.self();
    if (runtimes.contains(self.getUuid())) {
      Object apiInstance = invocationRegisterApi.getApiInstance(apiData.getUri());
      Method method = Invocations.getMethodToCall(apiInstance, request);
      return Invocations.invokeMethod(request, apiInstance, method);
    } else {
      UUID runtimeToRun = getRuntimeToRun(runtimes);

      return executionApi.invoke(runtimeToRun, request);
    }
  }

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    invocationRegisterApi.setInvocationApi(this);
  }

  private UUID getRuntimeToRun(List<UUID> runtimes) {
    // TODO decide which runtime to use
    return runtimes.get(0);
  }
}
