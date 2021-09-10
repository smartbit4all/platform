package org.smartbit4all.api.invocation.registration;

public interface ApiRegister {

  void register(ApiInfo apiInfo);
  
  void addRegistrationListener(ApiRegistrationListener registrationListener);
  
}
