package org.smartbit4all.api.invocation.restclient.apiregister;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.apiregister.bean.ApiInfo;
import org.smartbit4all.api.invocation.ApiInvocationHandler;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.registration.ProtocolSpecificApiInstantiator;

public class RestGenApiInstantiator implements ProtocolSpecificApiInstantiator {

  private static final Logger log = LoggerFactory.getLogger(RestGenApiInstantiator.class);

  private RestGeneratedApiRegistry restGenApiRegistry;

  private InvocationApi invocationApi;

  public RestGenApiInstantiator(InvocationApi invocationApi) {
    this.invocationApi = invocationApi;
  }

  @Override
  public String getProtocol() {
    return Invocations.REST_GEN;
  }

  @Override
  public Object instantiate(ApiInfo apiInfo) throws Exception {
    String interfaceQualifiedName = apiInfo.getInterfaceQualifiedName();
    Class<?> interfaceType = Class.forName(interfaceQualifiedName);
    if (restGenApiRegistry == null) {
      log.error("There is no RestGeneratedApiRegistry set to provide the interface factory!");
      return null;
    }
    Function<ApiInfo, ?> restStubFactory = restGenApiRegistry.getRestStubFactory(interfaceType);
    if (restStubFactory == null) {
      log.error("There is no rest stub factory in the RestGeneratedApiRegistry for interface: {}",
          interfaceType);
      return null;
    }
    Object restStubInstance = restStubFactory.apply(apiInfo);

    Object proxy = ApiInvocationHandler.createProxy(interfaceType, restStubInstance, invocationApi,
        Invocations.REST_GEN);
    return proxy;
  }

  public void setRestGenApiRegistry(RestGeneratedApiRegistry restGenApiRegistry) {
    this.restGenApiRegistry = restGenApiRegistry;
  }

}
