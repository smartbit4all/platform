package org.smartbit4all.api.apiregister.restclient;

import org.smartbit4all.api.apiregister.bean.ApiInfo;
import org.smartbit4all.api.apiregister.restclientgen.ApiRegisterApi;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.smartbit4all.api.invocation.registration.ApiRegistrationListener;

public class ApiRegisterRestclient implements ApiRegister {

  ApiRegisterApi apiRegisterRest;

  public ApiRegisterRestclient(ApiRegisterApi apiRegisterRest) {
    this.apiRegisterRest = apiRegisterRest;
  }

  @Override
  public void register(ApiInfo apiInfo) {
    apiRegisterRest.register(apiInfo);
  }

  @Override
  public void addRegistrationListener(ApiRegistrationListener registrationListener) {
    // TODO nope
  }

}
