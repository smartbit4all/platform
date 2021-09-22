package org.smartbit4all.secms365.session;

import java.net.URI;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.sec.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class UserSessionApiMs365Impl implements UserSessionApi{
  
  @Autowired
  private SecurityService securityService;
  
  @Override
  public User currentUser() {
    DefaultOidcUser principal = (DefaultOidcUser) securityService.getCurrentAuthentication().getPrincipal();
    return createUser(principal);
  }
  
  private User createUser(DefaultOidcUser user) {
    return new User()
        .uri(URI.create("user:/id#" + user.getAttribute("oid")))
        .username(user.getName().replaceAll("\\s",""))
        .name(user.getName())
        .email(user.getEmail());
  }
}

