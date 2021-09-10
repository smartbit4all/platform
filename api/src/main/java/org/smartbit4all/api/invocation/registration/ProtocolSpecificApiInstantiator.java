package org.smartbit4all.api.invocation.registration;

public interface ProtocolSpecificApiInstantiator {

  Object instantiate(ApiInfo apiInfo) throws Exception;

  String getProtocol();
  
}
