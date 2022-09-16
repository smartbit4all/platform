package org.smartbit4all.sec.authentication;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.Session;
import org.smartbit4all.sec.authprincipal.SessionAuthPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.ObjectUtils;
import io.jsonwebtoken.lang.Assert;

/**
 * On logout this logoutHandler clears the {@link AccountInfo} beans matching the {@link #kinds}
 * from the {@link Session}. When {@link #kinds} is not set, all the {@link AccountInfo} beans get
 * removed!
 */
public class DefaultSessionLogoutHandler implements LogoutHandler {

  private static final Logger log = LoggerFactory.getLogger(DefaultSessionLogoutHandler.class);

  private SessionManagementApi sessionManagementApi;

  private final List<String> kinds;

  public DefaultSessionLogoutHandler(SessionManagementApi sessionManagementApi) {
    Assert.notNull(sessionManagementApi, "sessionManagementApi can not be null!");
    this.sessionManagementApi = sessionManagementApi;
    this.kinds = Collections.emptyList();
  }

  public DefaultSessionLogoutHandler(SessionManagementApi sessionManagementApi,
      List<String> kindsToLogout) {
    Assert.notNull(sessionManagementApi, "sessionManagementApi can not be null!");
    Assert.notNull(kindsToLogout, "kindsToLogout can not be null!");
    this.sessionManagementApi = sessionManagementApi;
    this.kinds = kindsToLogout;
  }


  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof SessionAuthPrincipal) {
      SessionAuthPrincipal authPrincipal = (SessionAuthPrincipal) principal;
      URI sessionUri = authPrincipal.getSessionUri();
      Session session = sessionManagementApi.readSession(sessionUri);
      session.getAuthentications().stream()
          .filter(ai -> ObjectUtils.isEmpty(kinds) || kinds.contains(ai.getKind()))
          .forEach(
              ai -> sessionManagementApi.removeSessionAuthentication(sessionUri, ai.getKind()));

    } else {
      log.warn(
          "The security context authentication does not contain a SessionAuthPrincipal so this LogoutHandler can not operate!");
    }

  }

}
