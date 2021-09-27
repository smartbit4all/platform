package org.smartbit4all.api.apiregister.restserver;

import org.smartbit4all.api.apiregister.bean.ApiInfo;
import org.smartbit4all.api.apiregister.bean.Registration;
import org.smartbit4all.api.invocation.registration.ApiRegister;
import org.springframework.http.ResponseEntity;

public class ApiRegisterReststub implements ApiRegisterApiDelegate {

  private ApiRegister apiRegister;

  public ApiRegisterReststub(ApiRegister apiRegister) {
    this.apiRegister = apiRegister;
  }

  @Override
  public ResponseEntity<Registration> register(ApiInfo apiInfo) throws Exception {
    apiRegister.register(apiInfo);
    // TODO add registration as return value to ApiRegister interface, then return it!
    return ResponseEntity.ok(null);
  }

}
