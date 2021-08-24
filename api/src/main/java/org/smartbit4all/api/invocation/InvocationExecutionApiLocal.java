package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implements the invocation of a local api service based on the Spring {@link ApplicationContext}.
 * 
 * @author Peter Boros
 */
public class InvocationExecutionApiLocal
    implements InvocationExecutionApi, ApplicationContextAware {

  private ApplicationContext appContext;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private TransferService transferService;

  @Override
  public String getName() {
    return Invocations.LOCAL;
  }

  @Override
  public InvocationParameter invoke(InvocationRequest request) throws ClassNotFoundException {
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

    Method method = Invocations.getMethodToCall(api, request);

    // Transfer the parameters for the call. Convert the primitives and the objects by the
    List<Object> parameterObjects = new ArrayList<>();
    int i = 0;
    for (InvocationParameter parameter : request.getParameters()) {
      switch (parameter.getKind()) {
        case PRIMITIVE:
          // It means that we use the TransferService to get the value from String
          if (method.getParameterTypes()[i].equals(String.class)) {
            parameterObjects.add(parameter.getValue());
          } else {
            parameterObjects
                .add(transferService.convert(parameter.getValue(), method.getParameterTypes()[i]));
          }
          break;
        case OBJECTURI:
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
    try {
      Object result = method.invoke(api, parameterObjects.toArray());
      InvocationParameter invocationResult = new InvocationParameter();
      invocationResult.setValue(result);
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
