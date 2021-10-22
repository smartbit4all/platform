package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.InvocationParameterTemplate;
import org.smartbit4all.api.invocation.bean.InvocationRequestTemplate;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectReferenceEntry;

/**
 * The developer api for the invocation.
 * 
 * @author Peter Boros
 */
public class Invocations {

  public static final String INVOCATION_SCHEME = "invocation";

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

  public static final String SUBSCRIBED_CONSUMERS_PREFIX = "subscription.";

  /**
   * This operation will prepare the {@link StorageObject}, save the related
   * {@link InvocationRequestTemplate} and add it to the {@link #SUBSCRIBED_CONSUMERS_PREFIX} + name
   * collection.
   * 
   * @param object The storage object that is preparing for save.
   * @param listener The Consumer to save. It will be registered into the
   *        {@link InvocationApi#register(Object)} to be able to access as an object in case of
   *        local call.
   * @param consumerName The name of the consumer because when we try to retrieve them we need to
   *        separate.
   * @param invocationApi The invocation Api to be able to register the Consumer.
   * @throws Exception
   */
  public static final void saveConsumer(StorageObject<?> object, Consumer<URI> listener,
      String consumerName, InvocationApi invocationApi)
      throws Exception {
    InvocationRequestTemplate invocationTemplate =
        new InvocationRequestTemplate().apiInstanceId(invocationApi.register(listener))
            .innerApi(consumerName).methodName("accept")
            .addParametersItem(new InvocationParameterTemplate().name(PARAMETER1));

    Storage storage = object.getStorage();
    StorageObject<InvocationRequestTemplate> soInvocationRequest =
        storage.instanceOf(InvocationRequestTemplate.class);
    soInvocationRequest.setObject(invocationTemplate);
    URI callbackUri = storage.save(soInvocationRequest);

    object.addCollectionEntry(
        constructConsumerName(consumerName),
        new ObjectReference().uri(callbackUri));
  }

  /**
   * This operation tries to call the consumers and if it's failed that remove the given consumer
   * from the collection.
   * 
   * @param consumerName
   * @param uri
   * @param storageClass
   * @param storage
   * @param storageApi
   * @param invocationApi
   */
  public static final void callConsumers(
      StorageObject<?> object,
      String consumerName,
      InvocationApi invocationApi) {

    if (consumerName == null) {
      return;
    }

    Storage storage = object.getStorage();
    for (StorageObjectReferenceEntry refEntry : object
        .getCollection(constructConsumerName(consumerName))) {

      if (refEntry.getReferenceData() != null) {
        Optional<InvocationRequestTemplate> invocationReqOpt = storage
            .read(refEntry.getReferenceData().getUri(), InvocationRequestTemplate.class);
        if (invocationReqOpt.isPresent()) {
          // If we have the request template then parameterize and call.
          InvocationRequestTemplate requestTemplate = invocationReqOpt.get();
          if (requestTemplate.getParameters().size() == 1) {
            InvocationRequest request =
                InvocationRequest.of(requestTemplate).setParameters(object.getUri());
            try {
              invocationApi.invoke(request);
            } catch (Exception e) {
              log.debug("Unable to call the registered consumers for {} storage object {}",
                  object.getUri(), request,
                  e);
              refEntry.setDelete(true);
            }
          }
        }
      }
    }

    // TODO Should be saved here, or create two versions, where the object can be saved eg. in the
    // APIs? This may cause multiple versions of the object just because of the invocation save.
    storage.save(object);
  }

  private static String constructConsumerName(String consumerName) {
    return SUBSCRIBED_CONSUMERS_PREFIX + consumerName;
  }

}
