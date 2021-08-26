package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import org.smartbit4all.api.contribution.ContributionApi;

/**
 * This {@link InvocationHandler} implementation is responsible for constructing the
 * {@link InvocationRequest} from the method call and pass it to the {@link InvocationApi}.
 * 
 * @author Peter Boros
 *
 * @param <A>
 */
public class ApiInvocationHandler<A, I extends ContributionApi> implements InvocationHandler {

  private final Class<? extends A> primaryApiClass;

  private final A primaryApi;

  private final Class<? extends I> innerApiClass;

  private final I innerApi;

  private final InvocationApi invocationApi;

  private String executionApi;

  private ApiInvocationProxy invocationProxy = new ApiInvocationProxy() {

    @Override
    public Object getOriginalApi() {
      return getApi();
    }

    @Override
    public ApiInvocationHandler<?, ?> getInvocationHandler() {
      return ApiInvocationHandler.this;
    }
  };

  public ApiInvocationHandler(Class<? extends A> primaryApiClass, A primaryApi,
      Class<? extends I> innerApiClass, I innerApi, InvocationApi invocationApi,
      String executionApi) {
    super();
    this.primaryApiClass = primaryApiClass;
    this.primaryApi = primaryApi;
    this.innerApiClass = innerApiClass;
    this.innerApi = innerApi;
    this.invocationApi = invocationApi;
  }

  @SuppressWarnings("unchecked")
  public static final <A, I extends ContributionApi> I createProxyInner(
      Class<? extends A> primaryApiClass, A primaryApi,
      Class<? extends I> innerApiClass, I innerApi, InvocationApi invocationApi,
      String executionApi) {
    ApiInvocationHandler<A, I> invocationHandler =
        new ApiInvocationHandler<>(primaryApiClass, primaryApi, innerApiClass, innerApi,
            invocationApi, executionApi);
    I apiProxy = (I) Proxy.newProxyInstance(innerApiClass.getClassLoader(),
        new Class[] {innerApiClass, ApiInvocationProxy.class}, invocationHandler);
    return apiProxy;
  }

  @SuppressWarnings("unchecked")
  public static final <A> A createProxy(
      Class<? extends A> primaryApiClass, A primaryApi,
      InvocationApi invocationApi,
      String executionApi) {
    ApiInvocationHandler<A, ?> invocationHandler =
        new ApiInvocationHandler<>(primaryApiClass, primaryApi, null, null,
            invocationApi, executionApi);
    A apiProxy = (A) Proxy.newProxyInstance(primaryApiClass.getClassLoader(),
        new Class[] {primaryApiClass, ApiInvocationProxy.class}, invocationHandler);
    return apiProxy;
  }

  private final boolean isInner() {
    return innerApi != null;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getDeclaringClass().equals(ApiInvocationProxy.class)) {
      return method.invoke(invocationProxy, args);
    }
    InvocationRequest invocation = Invocations.invoke(primaryApiClass).method(method.getName());
    if (isInner()) {
      invocation.innerApi(innerApi.getApiName());
    }
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < args.length; i++) {
      Parameter parameter = parameters[i];
      Object parameterValue = args[i];
      // TODO For the conversion to string must be implemented with TransferService!
      invocation.addParameter(parameter.getName(), InvocationParameter.Kind.BYVALUE,
          parameterValue,
          parameter.getType().getName());
    }
    InvocationParameter result = invocationApi.invoke(invocation);
    return result != null ? result.getValue() : null;
  }

  final Object getApi() {
    return innerApi != null ? innerApi : primaryApi;
  }

}
