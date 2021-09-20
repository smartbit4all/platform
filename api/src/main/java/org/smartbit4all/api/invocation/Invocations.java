package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.domain.data.storage.ObjectReferenceRequest;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;

/**
 * The developer api for the invocation.
 * 
 * @author Peter Boros
 */
public class Invocations {

  public static final String PARAMETER1 = "parameter1";
  public static final String LOCAL = "local";
  public static final String REST_GEN = "rest-gen";
  public static final String EXECUTION = "execution-invocation";

  private static final Logger log = LoggerFactory.getLogger(Invocations.class);

  private Invocations() {
    super();
  }

  /**
   * Identify the method for the invocation.
   * 
   * @param api The api.
   * @param request The invocation request that contains all the parameters for the call.
   * @return The {@link Method} of the Api.
   */
  public static final Method getMethodToCall(Object api, InvocationRequest request) {
    Class<? extends Object> clazz = api.getClass();
    Class<?> parameterArray[];
    if (request.getParameters() == null) {
      parameterArray = new Class<?>[0];
    } else {
      parameterArray = new Class<?>[request.getParameters().size()];
      int i = 0;
      for (InvocationParameter p : request.getParameters()) {
        try {
          parameterArray[i++] = Class.forName(p.getTypeClass());
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(
              "The parameter type class is not found for the " + request, e);
        }
      }
    }
    try {
      if (request.getMethodName() != null) {
        return clazz.getMethod(request.getMethodName(), parameterArray);
      } else {
        // Try to identify the method by it's parameters.
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
          // Check the parameter types.
          Class<?>[] parameterTypes = method.getParameterTypes();
          int i = 0;
          boolean matching = true;
          for (Class<?> methodParamType : parameterTypes) {
            Class<?> parameterType = parameterArray[i];
            if (!methodParamType.equals(parameterType)) {
              matching = false;
              break;
            }
          }
          // In this way we can identify the method of the lambdas also.
          if (matching && !Object.class.equals(method.getDeclaringClass())) {
            return method;
          }
        }
        throw new UnsupportedOperationException("The method is not accessible for the " + request);
      }
    } catch (NoSuchMethodException | SecurityException e) {
      throw new UnsupportedOperationException("The method is not accessible for the " + request, e);
    }
  }

  /**
   * @param clazz
   * @param apiInstance
   * @param request
   * @return
   * @throws ClassNotFoundException
   */
  public static Object getApiToCall(Class<?> clazz, Object apiInstance, InvocationRequest request)
      throws ClassNotFoundException {
    if (apiInstance == null) {
      throw new IllegalArgumentException(
          "The " + request.getApiClass() + " was not found for the " + request + " request.");
    }
    Object api = apiInstance;
    if (request.getInnerApi() != null) {
      if (!(PrimaryApi.class.isAssignableFrom(clazz))) {
        throw new IllegalArgumentException(
            "The " + request.getApiClass() + " is not primary class, the " + request.getInnerApi()
                + " inner api can not be accessed for the " + request + " request.");
      }
      api = ((PrimaryApi<?>) apiInstance).findApiByName(request.getInnerApi());
      if (api == null) {
        throw new IllegalArgumentException(
            "The " + request.getApiClass() + "." + request.getInnerApi()
                + " inner api was not found for the " + request + " request.");
      }
    }
    return api;
  }

  public static InvocationRequest getModifiedRequestToCallInnerApi(InvocationRequest request,
      Object apiInstance,
      String executionApi) {

    if (apiInstance == null) {
      throw new IllegalArgumentException(
          "The " + request.getApiClass() + " was not found for the " + request + " request.");
    }
    if (request.getInnerApi() != null) {
      if (!(PrimaryApi.class.isAssignableFrom(apiInstance.getClass()))) {
        throw new IllegalArgumentException(
            "The " + request.getApiClass() + " is not primary class, the " + request.getInnerApi()
                + " inner api can not be accessed for the " + request + " request.");
      }
      Class<?> innerApiClass = ((PrimaryApi<?>) apiInstance).getInnerApiClass();
      InvocationRequest modifiedRequest = request.copy();
      modifiedRequest.setApiClass(innerApiClass);
      modifiedRequest.setExecutionApi(executionApi);
      modifiedRequest.setInnerApi(null);

      return modifiedRequest;
    }
    return request;
  }

  public static final InvocationRequest invoke(Class<?> apiClass) {
    return new InvocationRequest(apiClass);
  }

  public static final InvocationRequest invoke(UUID apiInstanceId) {
    return new InvocationRequest(apiInstanceId);
  }

  public static final void saveConsumer(URI uri, Consumer<URI> listener, String consumerName,
      Storage<?> storage,
      StorageApi storageApi, InvocationApi invocationApi)
      throws Exception {
    InvocationRequestTemplate invocationTemplate =
        new InvocationRequestTemplate().apiInstanceId(invocationApi.register(listener))
            .innerApi(consumerName).methodName("accept")
            .addParametersItem(new InvocationParameterTemplate().name(PARAMETER1));

    URI callbackUri = invocationApi.save(invocationTemplate);

    storage.saveReferences(new ObjectReferenceRequest(uri, InvocationRequestTemplate.class)
        .add(callbackUri.toString()));

  }

  public static final void callConsumers(String consumerName, URI uri, Class<?> storageClass,
      Storage<?> storage,
      StorageApi storageApi,
      InvocationApi invocationApi) {
    if (consumerName == null) {
      return;
    }
    ObjectReferenceRequest referenceRequest = new ObjectReferenceRequest(uri, storageClass);
    for (InvocationRequestTemplate requestTemplate : storageApi.loadReferences(uri, storageClass,
        InvocationRequestTemplate.class)) {
      if (consumerName.equals(requestTemplate.getInnerApi())) {
        if (requestTemplate.getParameters().size() == 1) {
          InvocationRequest request = InvocationRequest.of(requestTemplate).setParameters(uri);
          InvocationParameter result;
          try {
            result = invocationApi.invoke(request);
          } catch (Exception e) {
            log.debug("Unable to call the registered consumers for {} todo item {}", uri, request,
                e);
            referenceRequest.delete(requestTemplate.getUri().toString(),
                InvocationRequestTemplate.class.getName());
          }
        }
      }
    }
    storage.saveReferences(referenceRequest);

  }

}
