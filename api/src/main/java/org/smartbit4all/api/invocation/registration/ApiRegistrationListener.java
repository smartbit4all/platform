package org.smartbit4all.api.invocation.registration;

import org.smartbit4all.api.apiregister.bean.ApiInfo;

public interface ApiRegistrationListener {

  String getInterfaceQName();
  
  void onRegistration(Object apiInstance, ApiInfo apiInfo) throws Exception;
  
}
