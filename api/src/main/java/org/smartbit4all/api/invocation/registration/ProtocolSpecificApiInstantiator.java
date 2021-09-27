package org.smartbit4all.api.invocation.registration;

import org.smartbit4all.api.apiregister.bean.ApiInfo;

public interface ProtocolSpecificApiInstantiator {

  Object instantiate(ApiInfo apiInfo) throws Exception;

  String getProtocol();
  
}
