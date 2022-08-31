package org.smartbit4all.sec.utils;

import java.util.Objects;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.Session;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.sec.scheduling.SecurityContextScheduling.TechnicalUserProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtility {

  private SecurityContextUtility() {}

  public static SecurityContext createSecurityContext(UserSessionApi userSessionApi,
      TechnicalUserProvider technicalUserProvider) {
    SecurityContext createEmptyContext = SecurityContextHolder.createEmptyContext();
    User technicalUser = technicalUserProvider.getTechnicalUser();
    Objects.requireNonNull(technicalUser, "technicalUser can not be null!");
    Session session = userSessionApi.startSession(technicalUser);
    Authentication auth = new UsernamePasswordAuthenticationToken(session, null);
    createEmptyContext.setAuthentication(auth);
    return createEmptyContext;
  }
}
