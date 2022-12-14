package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.session.SessionApi;

/**
 * The invocation api can initiate this builder. This is an {@link InvocationHandler} at the same
 * time and captures the method invocations of the api {@link #clazz} interface.
 * 
 * @author Peter Boros
 */
public class InvocationBuilder<T> implements InvocationHandler {

  private InvocationRequest request;

  private T apiProxy;

  /**
   * The Api interface class.
   */
  private Class<T> clazz;

  private SessionApi sessionApi;

  @SuppressWarnings("unchecked")
  InvocationBuilder(T api) {
    super();
    for (int i = 0; i < api.getClass().getInterfaces().length; i++) {
      Class<?> interf = api.getClass().getInterfaces()[i];
      clazz = (Class<T>) interf;
      break;
    }
    apiProxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
        new Class[] {clazz}, this);
  }

  /**
   * By calling the api method inside the consumer the builder can build the invocation request
   * ready to execute by the {@link InvocationApi}.
   * 
   * @param apiCall
   * @return
   */
  public InvocationRequest build(Consumer<T> apiCall) {
    request = null;
    apiCall.accept(apiProxy);
    return request;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    request = new InvocationRequest().name(clazz.getName()).interfaceClass(clazz.getName())
        .methodName(method.getName())
        .sessionUri(sessionApi == null ? null : sessionApi.getSessionUri());
    if (args != null && args.length != 0) {
      for (int i = 0; i < args.length; i++) {
        Parameter parameter = method.getParameters()[i];
        request.addParametersItem(new InvocationParameter().name(parameter.getName())
            .typeClass(parameter.getType().getName()).value(args[i]));
      }
    }
    return null;
  }

  final InvocationBuilder<T> sessionApi(SessionApi sessionApi) {
    this.sessionApi = sessionApi;
    return this;
  }

}
