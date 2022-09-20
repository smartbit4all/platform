package org.smartbit4all.sec.authprincipal;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class SessionAuthToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 7102074569421989869L;

  private SessionAuthPrincipal principal;

  private SessionAuthToken(Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
  }

  public static SessionAuthToken create(Session session) {
    Assert.notNull(session, "session cannot be null");
    return create(session.getUri(), session.getAuthentications());
  }

  public static SessionAuthToken create(URI sessionUri,
      Collection<AccountInfo> accounts) {
    Assert.notNull(sessionUri, "sessionUri cannot be null");
    Assert.notNull(accounts, "accounts cannot be null");

    SessionAuthPrincipal principal = SessionAuthPrincipal.of(sessionUri);

    List<GrantedAuthority> authorityList = ObjectUtils.isEmpty(accounts)
        ? AuthorityUtils.createAuthorityList("ROLE_user")
        : accounts.stream()
            .flatMap(
                ai -> ObjectUtils.isEmpty(ai.getRoles())
                    ? Collections.<String>emptyList().stream()
                    : ai.getRoles().stream())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    SessionAuthToken token = new SessionAuthToken(authorityList);
    token.principal = principal;
    token.setAuthenticated(true);
    return token;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public SessionAuthPrincipal getPrincipal() {
    return principal;
  }

}
