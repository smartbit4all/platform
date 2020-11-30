package org.smartbit4all.sec.service;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityServiceImpl implements SecurityService {

  @Override
  public Authentication getCurrentAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  @Override
  public String getCurrentUserName() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  @Override
  public boolean isAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
  }

  @Override
  public Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
  }

  
  
}
