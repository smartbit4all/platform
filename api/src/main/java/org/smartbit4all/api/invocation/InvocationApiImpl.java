package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyResolver;
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

  @Autowired(required = false)
  private SessionManagementApi sessionManagementApi;

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
      return Invocations.invokeMethod(objectApi, request, apiInstance, method);
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

  @Override
  public <P, S> EventPublisher<P, S> publisher(Class<P> publisherApiInterface,
      Class<S> subscriberApiInterface, String event) {
    return new EventPublisher<>(self, invocationRegisterApi, sessionManagementApi,
        publisherApiInterface, subscriberApiInterface,
        event);
  }

  @Override
  public InvocationRequest resolve(InvocationRequestDefinition definition,
      ObjectPropertyResolverContext context) {
    Objects.requireNonNull(definition, "The invocation definition is null, unable to resolve");
    InvocationRequest request = definition.getRequest();
    Objects.requireNonNull(request,
        () -> "The request in the " + definition + " must not be null.");
    // TODO Copy new instance from request!!!!
    if (context != null && definition.getResolvers() != null) {
      ObjectPropertyResolver resolver = objectApi.resolver().addContextObjects(context);
      for (InvocationParameterResolver paramResolver : definition.getResolvers()) {
        InvocationParameter parameter = null;
        if (request.getParameters() != null) {
          if (paramResolver.getName() != null) {
            parameter = request.getParameters().stream()
                .filter(p -> paramResolver.getName().equals(p.getName())).findFirst().orElse(null);
          } else if (paramResolver.getPosition() != null) {
            parameter = request.getParameters().get(paramResolver.getPosition());
          }
        }
        if (parameter != null) {
          parameter.setValue(resolver.resolve(paramResolver.getDataUri()));
        }
      }
    }
    return request;
  }

}
