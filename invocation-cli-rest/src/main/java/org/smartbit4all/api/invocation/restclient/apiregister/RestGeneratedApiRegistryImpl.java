package org.smartbit4all.api.invocation.restclient.apiregister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.smartbit4all.api.invocation.registration.ApiInfo;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.ApiRegistrationListenerImpl;

/**
 * Holds the generated rest stub factories so they can be registered with {@link ApiRegister}. Also
 * holds the already instantiated rest stubs by subscribing to the {@link ApiRegister}.
 */
public class RestGeneratedApiRegistryImpl implements RestGeneratedApiRegistry {

  private Map<Class<?>, Function<ApiInfo, ?>> factoriesByInterfaceType = new HashMap<>();

  private Map<Class<?>, Object> instancesByInterfaceType = new HashMap<>();

  private ApiRegister apiRegister;

  public RestGeneratedApiRegistryImpl(ApiRegister apiRegister,
      RestGenApiInstantiator restGenApiInstantiator) {
    this.apiRegister = apiRegister;
    restGenApiInstantiator.setRestGenApiRegistry(this);
  }

  @Override
  public <I, R extends I> void addRestStubFactory(Class<I> interfaceType,
      Function<ApiInfo, R> stubFactory) {
    factoriesByInterfaceType.put(interfaceType, stubFactory);
    apiRegister.addRegistrationListener(
        new ApiRegistrationListenerImpl<>(interfaceType, (api, apiInfo) -> {
          instancesByInterfaceType.put(interfaceType, api);
        }));
  }

  @Override
  public List<Class<?>> getRestGenImplementedInterfaces() {
    return new ArrayList<>(factoriesByInterfaceType.keySet());
  }

  @Override
  public boolean hasInterfaceGeneratedRestStub(Class<?> interfaceType) {
    return factoriesByInterfaceType.keySet().contains(interfaceType);
  }

  @Override
  public Function<ApiInfo, ?> getRestStubFactory(Class<?> interfaceType) {
    return factoriesByInterfaceType.get(interfaceType);
  }

  @Override
  public Object getRestStubInstance(Class<?> interfaceType) {
    return instancesByInterfaceType.get(interfaceType);
  }

}
