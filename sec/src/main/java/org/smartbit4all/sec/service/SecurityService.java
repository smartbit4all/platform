package org.smartbit4all.sec.service;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface SecurityService {

  Authentication getCurrentAuthentication();
  
  String getCurrentUserName();
  
  boolean isAuthenticated();
  
  Collection<? extends GrantedAuthority> getCurrentUserAuthorities();
  
}
