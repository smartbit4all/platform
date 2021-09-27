package org.smartbit4all.api.invocation.registration;

import org.smartbit4all.api.apiregister.bean.ApiInfo;

public interface ApiRegister {

  void register(ApiInfo apiInfo);

  void addRegistrationListener(ApiRegistrationListener registrationListener);

}
