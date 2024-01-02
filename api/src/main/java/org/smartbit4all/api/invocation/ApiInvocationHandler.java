package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.validation.constraints.NotNull;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

/***
 * This{@link InvocationHandler} implementation is responsible for constructing the
 * {@link InvocationRequest} from the method call and pass it to the {@link InvocationApi}.
 *
 * @author Peter Boros
 *
 * @param <A>
 */
public class ApiInvocationHandler<A> implements InvocationHandler {

  private final InvocationApi invocationApi;

  private Class<? extends A> apiClass;

  private String name;

  public ApiInvocationHandler(Class<? extends A> primaryApiClass, String name,
      InvocationApi invocationApi) {
    super();
    this.apiClass = primaryApiClass;
    this.invocationApi = invocationApi;
    this.name = name;
  }

  @SuppressWarnings("unchecked")
  public static final <A> A createProxy(
      Class<? extends A> class1, @NotNull String name,
      InvocationApi invocationApi) {
    ApiInvocationHandler<A> invocationHandler =
        new ApiInvocationHandler<>(class1, name, invocationApi);
    return (A) Proxy.newProxyInstance(class1.getClassLoader(),
        new Class[] {class1}, invocationHandler);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    InvocationRequest invocation =
        Invocations.createInvocationRequest(method, args, apiClass, name);
    InvocationParameter result = invocationApi.invoke(invocation);
    return result != null ? result.getValue() : null;
  }

}
