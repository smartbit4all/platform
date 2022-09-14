package org.smartbit4all.sec.localauth;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.ObjectUtils;

/**
 * Creates a spring security authentication token based on a local authentication resulted
 * {@link AccountInfo} from the given session.
 */
public class LocalAuthTokenProvider implements SessionBasedAuthTokenProvider {

  @Override
  public boolean supports(Session session) {
    List<AccountInfo> authentications = session.getAuthentications();
    if (ObjectUtils.isEmpty(authentications)) {
      return false;
    }

    AccountInfo accountInfo = authentications.stream()
        .filter(a -> LocalAuthenticationService.KIND.equals(a.getKind()))
        .findFirst().orElse(null);

    return accountInfo != null;
  }

  @Override
  public AbstractAuthenticationToken getToken(Session session) {
    List<AccountInfo> authentications = session.getAuthentications();
    AccountInfo accountInfo = authentications.stream()
        .filter(a -> LocalAuthenticationService.KIND.equals(a.getKind()))
        .findFirst().orElse(null);

    SessionAuthPrincipal principal = SessionAuthPrincipal.of(session.getUri());

    return LocalAuthenticationToken.create(principal, accountInfo);
  }

  private static class LocalAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 7102074569421989869L;

    private SessionAuthPrincipal principal;

    private LocalAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
      super(authorities);
    }

    public static LocalAuthenticationToken create(SessionAuthPrincipal principal,
        AccountInfo accountInfo) {
      Objects.requireNonNull(principal);
      Objects.requireNonNull(accountInfo);

      List<GrantedAuthority> authorityList = ObjectUtils.isEmpty(accountInfo.getRoles())
          ? AuthorityUtils.createAuthorityList("ROLE_NONE")
          : AuthorityUtils.createAuthorityList(accountInfo.getRoles().toArray(new String[0]));
      LocalAuthenticationToken token = new LocalAuthenticationToken(authorityList);
      token.principal = principal;
      token.setDetails(accountInfo);
      token.setAuthenticated(true);
      return token;
    }

    @Override
    public Object getCredentials() {
      return null;
    }

    @Override
    public Object getPrincipal() {
      return principal;
    }

  }

}
