package org.smartbit4all.secms365.service;

import org.smartbit4all.sec.service.SecurityServiceImpl;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class MsSecurityServiceImpl extends SecurityServiceImpl implements MsSecurityService {

  @Override
  public String getCurrentUserId() {
    OidcUser oidcUser = (OidcUser) this.getCurrentAuthentication().getPrincipal();
    String id = oidcUser.getAttribute("oid");
    return id;
  }

}
