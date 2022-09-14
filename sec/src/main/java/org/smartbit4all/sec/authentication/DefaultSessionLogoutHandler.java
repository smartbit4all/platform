package org.smartbit4all.sec.authentication;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.ObjectUtils;
import io.jsonwebtoken.lang.Assert;

public class DefaultSessionLogoutHandler implements LogoutHandler {

  private static final Logger log = LoggerFactory.getLogger(DefaultSessionLogoutHandler.class);

  private SessionApi sessionApi;

  private final List<String> kinds;

  public DefaultSessionLogoutHandler(SessionApi sessionApi) {
    Assert.notNull(sessionApi, "sessionApi can not be null!");
    this.sessionApi = sessionApi;
    this.kinds = Collections.emptyList();
  }

  public DefaultSessionLogoutHandler(SessionApi sessionApi, List<String> kindsToLogout) {
    Assert.notNull(sessionApi, "sessionApi can not be null!");
    Assert.notNull(kindsToLogout, "kindsToLogout can not be null!");
    this.sessionApi = sessionApi;
    this.kinds = kindsToLogout;
  }


  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof SessionAuthPrincipal) {
      SessionAuthPrincipal authPrincipal = (SessionAuthPrincipal) principal;
      URI sessionUri = authPrincipal.getSessionUri();
      Session session = sessionApi.readSession(sessionUri);
      session.getAuthentications().stream()
          .filter(ai -> ObjectUtils.isEmpty(kinds) || kinds.contains(ai.getKind()))
          .forEach(ai -> sessionApi.removeSessionAuthentication(sessionUri, ai.getKind()));

    } else {
      log.warn(
          "The security context authentication does not contain a SessionAuthPrincipal so this LogoutHandler can not operate!");
    }

  }

}
