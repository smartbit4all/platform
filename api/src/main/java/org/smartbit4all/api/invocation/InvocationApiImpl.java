package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor.CollectionTypeEnum;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.FutureAwait;
import org.smartbit4all.api.invocation.bean.InvocationBatchResult;
import org.smartbit4all.api.invocation.bean.InvocationError;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestBatch;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.invocation.bean.InvocationResult;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision;
import org.smartbit4all.api.invocation.bean.InvocationResultDecision.DecisionEnum;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.SessionInfoData;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectPropertyResolver;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.application.ApplicationRuntime;
import org.smartbit4all.domain.application.ApplicationRuntimeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Strings;

/**
 * The implementation of the {@link InvocationApi}. It collects all the
 * {@link InvocationExecutionApi} we have. If we call the
 * {@link #invoke(InvocationRequest, Object...)} then there is routing to the appropriate execution
 * api. We always have a local execution api
 *
 * @author Peter Boros
 */
public class InvocationApiImpl implements InvocationApi {

  public static final String INVOKE_API = "/invokeApi";

  public static final String INVOKE_DOWNLOAD = "/invokeDownload";

  public static final String INVOKE_UPLOAD = "/invokeUploadMultiple";

  public static final String INVOKE_UPLOAD_DOWNLOAD = "/invokeUploadDownloadMultiple";

  private static final Logger log = LoggerFactory.getLogger(InvocationApiImpl.class);

  @Autowired
  private InvocationRegisterApi invocationRegisterApi;

  @Autowired(required = false)
  private ApplicationRuntimeApi applicationRuntimeApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired(required = false)
  private SessionManagementApi sessionManagementApi;

  /**
   * The registered {@link InvocationExecutionApi}s that can be referred as executorApi for APIs.
   */
  @Autowired(required = false)
  private List<InvocationExecutionApi> executionApis;

  private StoredCollectionDescriptor serviceConnectionList =
      new StoredCollectionDescriptor().collectionType(CollectionTypeEnum.LIST)
          .schema(Invocations.INVOCATION_SCHEME)
          .name(InvocationApiMdmConfig.MDM_ENTRY_SERVICECONNECTION);

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private InvocationApi self;

  /**
   * By default the platform uses the rest client to access and call the api of a module over the
   * same storage.
   */
  private String defaultExecutionApiName =
      "org.smartbit4all.api.invocation.restclient.InvocationExecutionApiRestclient";

  @Override
  public InvocationParameter invoke(InvocationRequest request, Object... args)
      throws ApiNotFoundException {
    Objects.requireNonNull(request);
    if (Invocations.isScript(request)) {
      return invokeScript(request);
    }

    ApiDescriptor apiDescriptor =
        invocationRegisterApi.getApi(request.getInterfaceClass(), request.getName());

    if (apiDescriptor == null) {
      throw new ApiNotFoundException(request);
    }
    if (args != null) {
      for (int i = 0; i < args.length; i++) {
        request.getParameters().get(i).setValue(args[i]);
      }
    }

    return invoke(apiDescriptor, request);
  }

  @Override
  public Optional<InvocationParameter> tryInvoke(InvocationRequest request, Object... args) {
    try {
      return Optional.ofNullable(invoke(request, args));
    } catch (ApiNotFoundException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  private InvocationParameter invoke(ApiDescriptor apiDescriptor, InvocationRequest request)
      throws ApiNotFoundException {

    ApiData apiData = apiDescriptor.getApiData();

    // If we have a denoted execution api and it exists then we use it to invoke the given service.
    if (!Strings.isNullOrEmpty(apiData.getExecutionApi())) {
      InvocationExecutionApi executionApi = getExecutionApi(apiData.getExecutionApi());
      if (executionApi != null) {
        StoredList list = collectionApi.list(serviceConnectionList);
        Optional<ObjectNode> serviceConnection =
            list.nodesFromCache().filter(n -> Objects.equals(apiData.getServiceConnection(),
                n.getValueAsString(ServiceConnection.NAME))).findFirst();
        return executionApi
            .invoke(serviceConnection.orElseThrow(() -> new UnsupportedOperationException(
                "The service connection is not configured to access the following api (" + apiData
                    + ")"))
                .getObject(ServiceConnection.class), request);
      }
    }

    List<UUID> runtimes = invocationRegisterApi.getRuntimesForApi(apiData.getUri());

    if (runtimes.isEmpty()) {
      throw new ApiNotFoundException(apiData);
    }

    // If the applicationRuntimeApi is null, then we can only invoke the api call in our own runtime
    if (applicationRuntimeApi == null
        || runtimes.contains(applicationRuntimeApi.self().getUuid())) {
      return invokeLocalApi(request, apiData);
    } else {
      UUID runtimeToRun = getRuntimeToRun(runtimes);
      InvocationExecutionApi executionApi = getExecutionApi(defaultExecutionApiName);
      if (executionApi == null) {
        throw new UnsupportedOperationException("The " + defaultExecutionApiName
            + " execution api is not registered to access the following api (" + apiData + ")");
      }
      return executionApi.invoke(getServiceConnectionOfRuntime(runtimeToRun), request);
    }
  }

  private final InvocationParameter invokeLocalApi(InvocationRequest request, ApiData apiData) {
    Object apiInstance = invocationRegisterApi.getApiInstance(apiData.getUri());
    Method method = Invocations.getMethodToCall(apiInstance, request);
    return Invocations.invokeMethod(objectApi, request, apiInstance, method);
  }

  private final ServiceConnection getServiceConnectionOfRuntime(UUID runtimeUuid) {
    ApplicationRuntime applicationRuntime = applicationRuntimeApi.get(runtimeUuid);
    if (applicationRuntime == null) {
      return null;
    }
    String ipAddress = applicationRuntime.getIpAddress();
    String baseUrl = applicationRuntime.getBaseUrl();
    int serverPort = applicationRuntime.getServerPort();

    return new ServiceConnection().endpoint(
        (baseUrl != null ? baseUrl : "http://" + ipAddress + ":" + serverPort) + INVOKE_API)
        .authToken(getSessionToken());
  }

  private String getSessionToken() {
    return sessionApi != null
        ? sessionApi.getParameter(SessionInfoData.SID)
        : null;
  }

  private final InvocationParameter invokeScript(InvocationRequest request)
      throws ApiNotFoundException {
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    // Set all the parameters to the script as global variable.
    List<Object> parameterObjects = Invocations.getParameterObjects(objectApi, request);
    int i = 0;
    for (InvocationParameter p : request.getParameters()) {
      // We must ensure that the parameters are converted to the referred types.
      scriptEngineManager.put(p.getName(), parameterObjects.get(i++));
    }
    ScriptEngine engine = scriptEngineManager.getEngineByName(request.getScriptKind());
    if (engine == null) {
      throw new ApiNotFoundException(request);
    }
    try {
      Object result = engine.eval(request.getScriptBody());
      return new InvocationParameter().value(result)
          .typeClass(result != null ? result.getClass().getName() : null);
    } catch (ScriptException e) {
      log.error("Failed to execute the {} script with the {} engine.", request.getScriptBody(),
          request.getScriptKind(), e);
      throw new UnsupportedOperationException(
          "Failed to execute the script with the " + request.getScriptKind() + " engine.", e);
    }
  }

  @Override
  public void invokeAsyncRequest(ObjectNode asyncInvocationNode) {
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
  public URI invokeAt(InvocationRequest request, String channel, OffsetDateTime executeAt) {

    AsyncInvocationRequest asyncInvocationRequest =
        invocationRegisterApi.saveAndScheduleAsyncInvocationRequest(request, channel,
            Objects.requireNonNull(executeAt,
                "The execution time must be specified to schedule an invocation."));

    return asyncInvocationRequest.getUri();
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
          parameter.setValue(resolver.resolve(paramResolver.getPropertyUri()));
        }
      }
    }
    return request;
  }

  @Override
  public InvocationBatchResult invokeBatch(InvocationRequestBatch batch)
      throws ApiNotFoundException {
    InvocationBatchResult result = new InvocationBatchResult();
    batch.getRequests().stream().forEach(r -> {
      InvocationResult invocationResult = new InvocationResult().startTime(OffsetDateTime.now());
      try {
        invocationResult.returnValue(invoke(r).getValue());
      } catch (Exception e) {
        log.warn("Exception occured while executing the " + r, e);
        invocationResult.error(
            new InvocationError().definition(e.getClass().getName()).message(e.getMessage()));
      } finally {
        invocationResult.endTime(OffsetDateTime.now());
      }
      result.addResultsItem(invocationResult);
    });
    return result;
  }

  @Override
  public void invokeAsyncBatch(InvocationRequestBatch batch, String channel) {
    batch.getRequests().stream().forEach(r -> {
      invocationRegisterApi.saveAndEnqueueAsyncInvocationRequest(r, channel);
    });
  }

  @Override
  public InvocationRequest prepareByPosition(InvocationRequest request, Object... parameters) {
    InvocationRequest result = copyInvovationRequestHead(request);
    int i = 0;
    List<InvocationParameter> resultParams = new ArrayList<>();
    for (InvocationParameter param : request.getParameters()) {
      Object paramValue = param.getValue();
      if (parameters != null && i < parameters.length && parameters[i] != LEAVE) {
        paramValue = parameters[i];
      }
      resultParams.add(new InvocationParameter().innerTypeClass(param.getInnerTypeClass())
          .name(param.getName()).typeClass(param.getTypeClass()).value(paramValue));
      i++;
    }
    result.setParameters(resultParams);
    if (sessionApi != null) {
      result.setSessionUri(sessionApi.getSessionUri());
    }
    return result;
  }

  private final InvocationRequest copyInvovationRequestHead(InvocationRequest request) {
    return new InvocationRequest().interfaceClass(request.getInterfaceClass())
        .methodName(request.getMethodName()).name(request.getName())
        .scriptBody(request.getScriptBody()).scriptKind(request.getScriptKind());
  }

  @Override
  public InvocationRequest prepareByName(InvocationRequest request,
      Map<String, Object> parameters) {
    InvocationRequest result = copyInvovationRequestHead(request);
    int i = 0;
    List<InvocationParameter> resultParams = new ArrayList<>();
    for (InvocationParameter param : request.getParameters()) {
      Object paramValue = param.getValue();
      if (parameters != null) {
        Object paramValueByMap = parameters.get(param.getName());
        if (paramValueByMap != null) {
          paramValue = paramValueByMap;
        }
      }
      resultParams.add(new InvocationParameter().innerTypeClass(param.getInnerTypeClass())
          .name(param.getName()).typeClass(param.getTypeClass()).value(paramValue));
    }
    result.setParameters(resultParams);
    if (sessionApi != null) {
      result.setSessionUri(sessionApi.getSessionUri());
    }
    return result;
  }

  @Override
  public Object executeScript(String scriptEngine, String script,
      Map<String, ObjectNode> contextObjects, Map<String, Object> inputParams)
      throws ScriptException {
    Objects.requireNonNull(scriptEngine, "scriptEngine cannot be null!");
    Objects.requireNonNull(script, "script cannot be null!");

    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    if (contextObjects != null && !contextObjects.isEmpty()) {
      contextObjects.forEach(scriptEngineManager::put);
    }
    if (inputParams != null && !inputParams.isEmpty()) {
      inputParams.forEach(scriptEngineManager::put);
    }

    ScriptEngine engine = scriptEngineManager.getEngineByName(scriptEngine);
    return engine.eval(script);
  }

  @Override
  public boolean checkCallable(Object api) {
    if (api == null) {
      return false;
    }
    if (Proxy.isProxyClass(api.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(api);
      if (invocationHandler instanceof ApiInvocationHandler) {
        @SuppressWarnings("rawtypes")
        ApiInvocationHandler apiInvocationHandler = (ApiInvocationHandler) invocationHandler;
        ApiDescriptor apiDescriptor =
            invocationRegisterApi.getApi(apiInvocationHandler.getApiClass().getName(), null);
        if (apiDescriptor == null) {
          return false;
        }
        List<UUID> runtimesForApi =
            invocationRegisterApi.getRuntimesForApi(apiDescriptor.getApiData().getUri());
        if (runtimesForApi.isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  private final InvocationExecutionApi getExecutionApi(String api) {
    if (executionApis == null || executionApis.isEmpty() || api == null) {
      return null;
    }
    return executionApis.stream().filter(a -> api.equals(a.getClass().getName())).findFirst()
        .orElse(null);
  }

  @Override
  public URI awaitFor(String scheme, String id, InvocationRequest request) {
    return awaitFor(scheme, id, request, 1);
  }

  @Override
  public URI awaitFor(String scheme, String id, InvocationRequest request, int firstParamIndex) {
    return objectApi.saveAsNew(scheme,
        new FutureAwait().id(id).firstParamIndex(firstParamIndex).request(request));
  }

  @Override
  public void signalFuture(String scheme, String id, Object... parameters) {
    ObjectNode futureAwaitNode =
        objectApi.loadLatest(scheme, objectApi.definition(FutureAwait.class), id);
    String futureId = scheme + StringConstant.DOT + id;
    if (futureAwaitNode == null) {
      log.error("Unable to signal the Future {} - the future is missing. parameters: {}",
          futureId, parameters);
      return;
    }
    InvocationRequest invocationRequest =
        futureAwaitNode.getValue(InvocationRequest.class, FutureAwait.REQUEST);
    if (invocationRequest == null) {
      log.error(
          "Unable to signal the Future {} - the invocation is missing from the future. parameters: {}",
          futureId, parameters);
      return;
    }

    Integer firstParamIndexInt =
        futureAwaitNode.getValue(Integer.class, FutureAwait.FIRST_PARAM_INDEX);
    int firstParamIndex = firstParamIndexInt == null ? 1 : firstParamIndexInt.intValue();

    // Set the parameters of the signal from the 1. parameter. The 0. is reserved as the context
    // identifier.
    for (int i = 0; i < parameters.length; i++) {
      int j = i + firstParamIndex;
      if (j < invocationRequest.getParameters().size()) {
        InvocationParameter parameter = invocationRequest.getParameters().get(j);
        if (parameter != null) {
          parameter.value(parameters[i]);
        } else {
          invocationRequest.getParameters().set(j, new InvocationParameter().value(parameters[i]));
        }
      } else {
        break;
      }
    }
    try {
      invoke(invocationRequest);
    } catch (Exception e) {
      log.error(
          "Unable to signal the Future {} - the invocation is failed. parameters: {}",
          futureId, parameters, e);
    }
  }

  //@Transactional // TODO
  @Override
  public void executeAsyncInvocationRequest(AsyncInvocationRequestEntry requestEntry) {
    AsyncInvocationRequest request = requestEntry.request;
    InvocationResult result = new InvocationResult().startTime(OffsetDateTime.now());
    try {
      if (log.isDebugEnabled()) {
        log.debug("Executing: {}", requestEntry.toLog());
      }
      result.returnValue(self.invoke(request.getRequest()).getValue());
    } catch (Exception e) {
      log.warn("Exception occured while executing the " + requestEntry, e);
      result.error(
          new InvocationError().definition(e.getClass().getName()).message(e.getMessage()));
    } finally {
      result.endTime(OffsetDateTime.now());
      // Let's make a decision about the next step
      InvocationResultDecision decision = null;
      InvocationRequest evaluate = request.getEvaluate();
      if (evaluate != null) {
        // We set the request and the result parameters for the call when they are present in the
        // signature.
        for (InvocationParameter parameter : evaluate.getParameters()) {
          if (AsyncInvocationRequest.class.getName().equals(parameter.getTypeClass())) {
            parameter.setValue(request);
          } else if (InvocationResult.class.getName().equals(parameter.getTypeClass())) {
            parameter.setValue(result);
          }
        }
        try {
          // TODO This is an object read it with ObjectDefinition!
          decision = (InvocationResultDecision) self.invoke(evaluate).getValue();
        } catch (Exception e) {
          log.error("Exception occured while trying to evaluate the " + result + " for the "
              + request, e);
        }
      }
      if (decision == null) {
        // Make a hard wired decision if there was error then abort, if we have andThen then
        // continue.
        decision = new InvocationResultDecision()
            .decision(result.getError() == null ? DecisionEnum.CONTINUE : DecisionEnum.ABORT);
      } else {
        int size = request.getResults() == null ? 0
            : request.getResults().size();
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
  }
}
