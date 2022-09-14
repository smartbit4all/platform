package org.smartbit4all.sec.localauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class LocalAuthLogoutHandler implements LogoutHandler {

  @Autowired
  private LocalAuthenticationService localAuthService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    localAuthService.logout();
  }

}
