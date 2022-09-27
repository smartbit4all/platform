package org.smartbit4all.sec.authentication;

import java.util.List;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.AuthenticationProviderData;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class DefaultAuthenticationDataProvider implements AuthenticationDataProvider {

  private final String kind;

  public DefaultAuthenticationDataProvider(String kind) {
    Assert.notNull(kind, "kind cannot be null");
    this.kind = kind;
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
        .filter(a -> kind.equals(a.getKind()))
        .findFirst().orElse(null);

    return foundInfo == null;
  }

  @Override
  public AuthenticationProviderData getProviderData(Session session) {
    return new AuthenticationProviderData()
        .kind(kind);
  }

}
