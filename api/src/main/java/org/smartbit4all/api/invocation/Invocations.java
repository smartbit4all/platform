package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import org.smartbit4all.api.contribution.PrimaryApi;

/**
 * The developer api for the invocation.
 * 
 * @author Peter Boros
 */
public class Invocations {

  public static final String LOCAL = "local";

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
      return clazz.getMethod(request.getMethodName(), parameterArray);
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

  public static final InvocationRequest invoke(Class<?> apiClass) {
    return new InvocationRequest(apiClass);
  }

}
