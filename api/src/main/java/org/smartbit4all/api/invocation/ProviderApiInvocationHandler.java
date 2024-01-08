package org.smartbit4all.api.invocation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.EventSubscriptionData;
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;
import org.smartbit4all.api.invocation.bean.MethodData;
import org.smartbit4all.api.invocation.bean.ParameterData;
import org.smartbit4all.api.invocation.bean.PublishedEventData;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.StorageApi;
import static java.util.stream.Collectors.toList;

/**
 * If an api is provided by an application instance then a Proxy with this invocation handler should
 * be registered into the configuration. At the end of the configuration setup, the
 * {@link InvocationRegisterApi} will register the provided apis into the storage. The storage will
 * contains the updated information about apis and the other application instances will be refreshed
 * to make the {@link ApiInvocationHandler}s work. The invocation is served via an invocation call
 * notification. This is generic call that refers to the already saved invocation structure.
 * 
 * The naming of the apis is the following: <code>module:org.package.InterfaceClass(name)</code>
 * 
 * @author Peter Boros
 */
public class ProviderApiInvocationHandler<T> {

  /**
   * The name of the registered api.
   */
  private final String name;

  /**
   * The interface class of the proxy.
   */
  private final Class<T> interfaceClass;

  /**
   * The unique identifier of the given api for the {@link StorageApi}. It is assembled in the
   * following way. The scheme is the {@link Invocations#APIREGISTRATION_SCHEME}. The path starts
   * with the module that provides the given api. The rest of the path is the package and the class
   * name. It is a unique identifier that can be calculated easily from Java interface. There is no
   * need to use other identifier.
   */
  private URI uri;

  /**
   * The api instance
   */
  private final T apiInstance;


  /**
   * Constructs a new provider proxy invocation handler. This handler is responsible for sharing the
   * api data via the storage with other application instances.
   * 
   * @param name
   */
  ProviderApiInvocationHandler(Class<T> interfaceClass, String name, T apiInstance) {
    super();
    this.name = name;
    this.interfaceClass = interfaceClass;
    this.apiInstance = apiInstance;
    uri = uriOf(interfaceClass, name);
  }

  public static <T> URI uriOf(Class<T> interfaceClass, String name) {
    return URI
        .create(Invocations.APIREGISTRATION_SCHEME + StringConstant.COLON + StringConstant.SLASH
            + interfaceClass.getName().replace(StringConstant.DOT, StringConstant.SLASH)
            + StringConstant.SLASH + name);
  }

  static final <T> ProviderApiInvocationHandler<T> providerOf(Class<T> interfaceClass, String name,
      T apiInstance) {
    return new ProviderApiInvocationHandler<>(interfaceClass, name, apiInstance);
  }

  static final <T> ProviderApiInvocationHandler<T> providerOf(Class<T> interfaceClass,
      T apiInstance) {
    return new ProviderApiInvocationHandler<>(interfaceClass, interfaceClass.getName(),
        apiInstance);
  }

  public final String getName() {
    return name;
  }

  public final Class<?> getInterfaceClass() {
    return interfaceClass;
  }

  public final Object getApiInstance() {
    return apiInstance;
  }

  /**
   * @return Collect the information about the given api via Reflection. Identifies the methods, the
   *         published events and the subscriptions.
   */
  ApiData getData() {
    // Collects the subscriptions from the interface class.
    Set<Method> allMethods = ReflectionUtility.allMethods(interfaceClass);
    List<MethodData> methods = allMethods.stream().map(m -> {
      List<ParameterData> parameters = new ArrayList<>();
      StringBuilder sbParams = new StringBuilder();
      for (int i = 0; i < m.getParameterCount(); i++) {
        Parameter p = m.getParameters()[i];
        parameters.add(new ParameterData().name(p.getName()).typeName(p.getType().getName())
            .kind(InvocationParameterKind.BYVALUE));
        if (sbParams.length() != 0) {
          sbParams.append(StringConstant.COMMA);
        }
        sbParams.append(p.getType().getName());
      }
      return new MethodData().name(m.getName())
          .id(m.getName() + StringConstant.LEFT_PARENTHESIS + sbParams.toString()
              + StringConstant.RIGHT_PARENTHESIS)
          .returnType(m.getReturnType().getName())
          .parameters(parameters);
    }).collect(toList());
    List<PublishedEventData> publishers = allMethods.stream().map(m -> {
      PublishedEvent p =
          ReflectionUtility.getNearestAnnotation(m, PublishedEvent.class);
      if (p != null) {
        List<ParameterData> parameters = new ArrayList<>();
        for (int i = 0; i < m.getParameterCount(); i++) {
          Parameter param = m.getParameters()[i];
          parameters
              .add(new ParameterData().name(param.getName()).typeName(param.getType().getName())
                  .kind(InvocationParameterKind.BYVALUE));
        }
        return new PublishedEventData().api(interfaceClass.getName())
            .event(p.event()).parameters(parameters);
      }
      return null;
    }).filter(p -> p != null).collect(toList());
    List<EventSubscriptionData> subscriptions = allMethods.stream().map(m -> {
      EventSubscription s =
          ReflectionUtility.getNearestAnnotation(m, EventSubscription.class);
      return s == null ? null
          : new EventSubscriptionData().api(s.api()).event(s.event())
              .asynchronous(s.asynchronous()).channel(s.channel()).type(s.type())
              .subscribedApi(interfaceClass.getName()).subscribedMethod(m.getName());
    }).filter(s -> s != null).collect(toList());
    return new ApiData().interfaceName(interfaceClass.getName()).name(name).uri(uri)
        .methods(methods)
        .publishedEvents(publishers)
        .eventSubscriptions(subscriptions);
  }

}
