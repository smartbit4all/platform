package org.smartbit4all.sec.localauth;

import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authentication.DefaultAuthenticationDataProvider;
import org.springframework.beans.factory.annotation.Value;

public class LocalAuthenticationDataProvider extends DefaultAuthenticationDataProvider {

  @Value("${openapi.localAuthentication.base-path:/}")
  private String authenticationPath;

  public LocalAuthenticationDataProvider() {
    super(LocalAuthenticationService.KIND);
  }

  @Override
  public AuthenticationProviderData getProviderData(Session session) {
    AuthenticationProviderData providerData = super.getProviderData(session);
    providerData.putParametersItem("authenticationPath", authenticationPath + "/login");
    return providerData;
  }

}
