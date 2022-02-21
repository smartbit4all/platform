package org.smartbit4all.api.invocation;

import java.net.URI;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.StorageApi;

/**
 * If an api is provided by an application instance then a Proxy with this invocation handler should
 * be registered into the configuration. At the end of the configuration setup, the
 * {@link InvocationRegisterApi} will register the provided apis into the storage. The storage will
 * contains the updated information about apis and the other application instances will ber
 * refreshed to make the {@link RemoteApiInvocationHandler}s work. The invocation is served via an
 * invocation call notification. This is generic call that refers to the already saved invocation
 * structure.
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
   * The name of the module.
   */
  private final String module;

  /**
   * The interface class of the proxy.
   */
  private final Class<?> interfaceClass;

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
   * @param module The name of the module that provides the given api.
   * @param name
   */
  ProviderApiInvocationHandler(Class<T> interfaceClass, String module, String name,
      T apiInstance) {
    super();
    this.module = module;
    this.name = name;
    this.interfaceClass = interfaceClass;
    this.apiInstance = apiInstance;
    uri =
        URI.create(Invocations.APIREGISTRATION_SCHEME + StringConstant.COLON + StringConstant.SLASH
            + interfaceClass.getName().replace(StringConstant.DOT, StringConstant.SLASH));
  }

  static final <T> ProviderApiInvocationHandler<T> providerOf(Class<T> interfaceClass,
      String module, String name,
      T apiInstance) {
    return new ProviderApiInvocationHandler<>(interfaceClass, module, name, apiInstance);
  }

  public final String getName() {
    return name;
  }

  public final String getModule() {
    return module;
  }

  public final Class<?> getInterfaceClass() {
    return interfaceClass;
  }

  public final Object getApiInstance() {
    return apiInstance;
  }

  ApiData getData() {
    return new ApiData().module(module).interfaceName(interfaceClass.getName()).name(name)
        .uri(uri);
  }

}
