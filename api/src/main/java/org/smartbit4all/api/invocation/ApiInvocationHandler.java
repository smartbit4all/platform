package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

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
      Class<? extends I> innerApiClass, I innerApi, InvocationApi invocationApi) {
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
      Class<? extends I> innerApiClass, I innerApi, InvocationApi invocationApi) {
    ApiInvocationHandler<A, I> invocationHandler =
        new ApiInvocationHandler<>(primaryApiClass, primaryApi, innerApiClass, innerApi,
            invocationApi);
    I apiProxy = (I) Proxy.newProxyInstance(innerApiClass.getClassLoader(),
        new Class[] {innerApiClass, ApiInvocationProxy.class}, invocationHandler);
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
    Invocation invocation = Invocations.invoke(primaryApiClass).method(method.getName());
    if (isInner()) {
      invocation.innerApi(innerApi.getApiName());
    }
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < args.length; i++) {
      Parameter parameter = parameters[i];
      Object parameterValue = args[i];
      // TODO For the conversion to string must be implemented with TransferService!
      invocation.parameter(InvocationParameterKind.PRIMITIVE, parameterValue.toString(),
          parameter.getType().getName());
    }
    invocationApi.invoke(invocation.build());
    return null;
  }

  final Object getApi() {
    return innerApi != null ? innerApi : primaryApi;
  }

}
