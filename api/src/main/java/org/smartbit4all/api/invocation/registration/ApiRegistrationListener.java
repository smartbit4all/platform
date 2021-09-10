package org.smartbit4all.api.invocation.registration;

public interface ApiRegistrationListener {

  String getInterfaceQName();
  
  void onRegistration(Object apiInstance, ApiInfo apiInfo) throws Exception;
  
}
