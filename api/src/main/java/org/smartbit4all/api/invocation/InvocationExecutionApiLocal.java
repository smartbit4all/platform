package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.invocation.InvocationParameter.Kind;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implements the invocation of a local api service based on the Spring {@link ApplicationContext}.
 * 
 * @author Peter Boros
 */
public class InvocationExecutionApiLocal implements InvocationExecutionApi, ApplicationContextAware {

  protected ApplicationContext appContext;

  @Autowired
  protected StorageApi storageApi;
  
  @Override
  public String getName() {
    return Invocations.LOCAL;
  }

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws Exception {

    Object api = getApi(request);
    if (api == null) {
      // FIXME throw exception or log.warning?
      throw new Exception(
          "There is no api found that could be called with the request: " + request);
    }
    
    if(api instanceof InvocationExecutionApi) {
      // when the api itself an InvocationExecutionApi, delegate the call.
      return ((InvocationExecutionApi) api).invoke(request);
    }
    
    Method method = getMethod(request, api);
    List<Object> parameterObjects = getParameterObjects(request, method);
    
    return invokeMethod(request, api, method, parameterObjects);

  }

  protected Object getApi(InvocationRequest request) throws Exception {
    // Get the primary api in a Spring specific way. The rest of the call is not Spring aware.
    Object apiInstance = appContext.getBean(request.getApiClass());
    
    Object api = Invocations.getApiToCall(request.getApiClass(), apiInstance, request);
    if (api == null) {
      return null;
    }
    
    // If we have a Proxy with ApiInvocationHandler then we need the original api reference to call
    // it directly.
    if (api instanceof ApiInvocationProxy) {
      api = ((ApiInvocationProxy) api).getOriginalApi();
    }
    return api;
  }

  protected Method getMethod(InvocationRequest request, Object api) {
    Method method = Invocations.getMethodToCall(api, request);
    return method;
  }

  protected List<Object> getParameterObjects(InvocationRequest request, Method method) {
    // Transfer the parameters for the call. Convert the primitives and the objects by the
    List<Object> parameterObjects = new ArrayList<>();
    int i = 0;
    for (InvocationParameter parameter : request.getParameters()) {
      switch (parameter.getKind()) {
        case BYVALUE:
          // In case of primitive
          parameterObjects.add(parameter.getValue());
          break;
        case BYREFERENCE:
          // In this case we have a direct URI to an object.
          Storage<?> storage = storageApi.get(method.getParameterTypes()[i]);
          try {
            parameterObjects.add(storage.load(URI.create(parameter.getStringValue())));
          } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid URI parameter " + parameter.getValue() + " in the request: " + request, e);
          }

        default:
          break;
      }
      i++;
    }
    return parameterObjects;
  }

  protected InvocationParameter invokeMethod(InvocationRequest request, Object api, Method method,
      List<Object> parameterObjects) {
    try {
      Object result = method.invoke(api, parameterObjects.toArray());
      InvocationParameter invocationResult = new InvocationParameter();
      invocationResult.setValue(result);
      invocationResult.setKind(Kind.BYVALUE);
      if(result != null) {
        invocationResult.setTypeClass(result.getClass().getName());
        invocationResult.setStringValue(result.toString());
      }
      return invocationResult;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException("Unable to call the method for the " + request, e);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.appContext = applicationContext;
  }

}
