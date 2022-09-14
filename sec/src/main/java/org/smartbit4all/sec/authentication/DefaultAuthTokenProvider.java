package org.smartbit4all.sec.authentication;

import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.smartbit4all.sec.authprincipal.SessionAuthToken;
import org.smartbit4all.sec.token.SessionBasedAuthTokenProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.ObjectUtils;

public class DefaultAuthTokenProvider implements SessionBasedAuthTokenProvider {

  @Override
  public boolean supports(Session session) {
    return !ObjectUtils.isEmpty(session.getAuthentications());

  }

  @Override
  public AbstractAuthenticationToken getToken(Session session) {

    SessionAuthPrincipal principal = SessionAuthPrincipal.of(session.getUri());

    return SessionAuthToken.create(principal, session);
  }

}
