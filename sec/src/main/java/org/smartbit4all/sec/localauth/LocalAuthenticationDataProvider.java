package org.smartbit4all.sec.localauth;

import java.util.Collections;
import java.util.List;
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
  public List<AuthenticationProviderData> getProviderDataList(Session session) {
    AuthenticationProviderData providerData = super.getProviderDataList(session).get(0);
    providerData.putParametersItem("authenticationPath", authenticationPath + "/login");
    return Collections.singletonList(providerData);
  }

}
