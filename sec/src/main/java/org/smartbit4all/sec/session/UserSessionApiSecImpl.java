package org.smartbit4all.sec.session;

import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.UserSessionApi;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserSessionApiSecImpl implements UserSessionApi{
  
  @Override
  public User currentUser() {
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      return null;
    }
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
