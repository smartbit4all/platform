package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.ApiRegistrationListenerImpl;

public class ApiPlaceholder {

  private ApiPlaceholder() {}

  @SuppressWarnings("unchecked")
  public static <A> A create(Class<A> apiInterface, ApiRegister apiRegister) {

    ApiPlaceholderInvocationHandler<A> invocationHandler =
        new ApiPlaceholderInvocationHandler<>(apiInterface, apiRegister);
    A apiProxy = (A) Proxy.newProxyInstance(apiInterface.getClassLoader(),
        new Class[] {apiInterface}, invocationHandler);
    return apiProxy;
  }

  public static class ApiPlaceholderInvocationHandler<A> implements InvocationHandler {

    private A instance;
    private Class<A> apiInterface;

    public ApiPlaceholderInvocationHandler(Class<A> apiInterface, ApiRegister apiRegister) {
      this.apiInterface = apiInterface;
      apiRegister.addRegistrationListener(
          new ApiRegistrationListenerImpl<A>(apiInterface, (api, apiImpl) -> {
            instance = api;
          }));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (instance == null) {
        throw new Exception("There is no instance registered for api: " + apiInterface.getName());
      }
      return method.invoke(instance, args);
    }

  }

}
