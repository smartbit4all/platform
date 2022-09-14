package org.smartbit4all.sec.oauth2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authentication.AuthenticationDataProvider;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.util.ObjectUtils;
import io.jsonwebtoken.lang.Assert;

public class OAuth2AuthenticationDataProvider implements AuthenticationDataProvider {

  private static final String OAUTH2_KIND_BASE = "oauth2-";

  private String registrationId;

  private String authorizationRequestBaseUri =
      OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

  public OAuth2AuthenticationDataProvider(String registrationId) {
    Assert.notNull(registrationId, "registrationId can not be null!");
    this.registrationId = registrationId;
  }

  @Override
  public boolean supports(Session session) {
    List<AccountInfo> authentications = session.getAuthentications();
    if (ObjectUtils.isEmpty(authentications)) {
      return true;
    }

    // if the session was already authenticated with this kind, it no longer supports the
    // authentication
    AccountInfo foundInfo = authentications.stream()
        .filter(a -> getKind().equals(a.getKind()))
        .findFirst().orElse(null);

    return foundInfo == null;
  }

  @Override
  public AuthenticationProviderData getProviderData(Session session) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("authUrl", authorizationRequestBaseUri + "/" + registrationId);

    return new AuthenticationProviderData()
        .kind(getKind())
        .parameters(parameters);
  }

  public String getKind() {
    return getKind(registrationId);
  }

  public String getAuthorizationRequestBaseUri() {
    return authorizationRequestBaseUri;
  }

  public void setAuthorizationRequestBaseUri(String authorizationRequestBaseUri) {
    this.authorizationRequestBaseUri = authorizationRequestBaseUri;
  }

  public static String getKind(String registrationId) {
    return OAUTH2_KIND_BASE + registrationId;
  }

}
