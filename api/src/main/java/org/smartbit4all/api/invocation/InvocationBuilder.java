package org.smartbit4all.api.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.smartbit4all.api.invocation.Invocations.ListWrapper;
import org.smartbit4all.api.invocation.Invocations.MapWrapper;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestBatch;
import org.smartbit4all.api.invocation.bean.ObjectInvocationConfig;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.BeanMeta;
import org.smartbit4all.core.object.BeanMetaUtil;

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
  InvocationBuilder(Class<T> apiInterface) {
    super();
    clazz = apiInterface;
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

  /**
   * By calling the api method inside the consumer the builder can build the invocation request
   * ready to execute by the {@link InvocationApi}.
   * 
   * @param apiCall
   * @param objects The object configuration for the api calls.
   * @return The invocation request batch that can be executed by the InvocationApi.
   */
  public InvocationRequestBatch build(Consumer<T> apiCall, ObjectInvocationConfig objects) {
    InvocationRequestBatch result = new InvocationRequestBatch();
    if (objects == null || objects.getObjectUris() == null
        || objects.getObjectUris().isEmpty()) {
      // Return an empty batch with no included request.
      return result;
    }
    request = null;
    apiCall.accept(apiProxy);
    // The request must be copied for every object and the first parameter must be set from the
    // objects configuration
    BeanMeta meta = BeanMetaUtil.meta(InvocationRequest.class);
    objects.getObjectUris().stream().forEach(u -> {
      InvocationRequest requestsItem = (InvocationRequest) meta.deepCopy(request);
      requestsItem.getParameters().get(0).setValue(u);
      result.addRequestsItem(requestsItem);
    });
    return result;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    request = new InvocationRequest().name(clazz.getName()).interfaceClass(clazz.getName())
        .methodName(method.getName())
        .sessionUri(sessionApi == null ? null : sessionApi.getSessionUri());
    if (args != null && args.length != 0) {
      for (int i = 0; i < args.length; i++) {
        Parameter parameter = method.getParameters()[i];
        InvocationParameter invocationParameter =
            new InvocationParameter().name(parameter.getName())
                .typeClass(parameter.getType().getName()).value(args[i]);
        if (List.class.isAssignableFrom(parameter.getType())) {
          try {
            invocationParameter.setInnerTypeClass(
                ((ListWrapper) Proxy.getInvocationHandler(args[i])).getInnerType().getName());
          } catch (Exception e) {
            throw new IllegalArgumentException(
                "In invocation builder use Invocations.listOf warpper for passing list parameters.");
          }
          if (invocationParameter.getValue() instanceof Proxy) {
            invocationParameter.setValue(new ArrayList());
          }
        } else if (Map.class.isAssignableFrom(parameter.getType())) {
          try {
            invocationParameter.setInnerTypeClass(
                ((MapWrapper) Proxy.getInvocationHandler(args[i])).getInnerType().getName());
          } catch (Exception e) {
            throw new IllegalArgumentException(
                "In invocation builder use Invocations.mapOf warpper for passing map parameters.");
          }
          if (invocationParameter.getValue() instanceof Proxy) {
            invocationParameter.setValue(new HashMap());
          }
        }
        request.addParametersItem(invocationParameter);
      }
    }
    return null;
  }

  final InvocationBuilder<T> sessionApi(SessionApi sessionApi) {
    this.sessionApi = sessionApi;
    return this;
  }

}
