package org.smartbit4all.sec.localauth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authentication.AuthenticationDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

public class LocalAuthenticationDataProvider implements AuthenticationDataProvider {

  @Value("${openapi.localAuthentication.base-path:/}")
  private String authenticationPath;

  @Override
  public boolean supports(Session session) {
    List<AccountInfo> authentications = session.getAuthentications();
    if (ObjectUtils.isEmpty(authentications)) {
      return true;
    }

    // if the session was already authenticated with this kind, it no longer supports the
    // authentication
    AccountInfo foundInfo = authentications.stream()
        .filter(a -> LocalAuthenticationService.KIND.equals(a.getKind()))
        .findFirst().orElse(null);

    return foundInfo == null;
  }

  @Override
  public AuthenticationProviderData getProviderData(Session session) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("authenticationPath", authenticationPath + "/login");

    return new AuthenticationProviderData()
        .kind(LocalAuthenticationService.KIND)
        .parameters(parameters);
  }

}
