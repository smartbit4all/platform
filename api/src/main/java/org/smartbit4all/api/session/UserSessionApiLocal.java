package org.smartbit4all.api.session;

import org.smartbit4all.api.org.bean.User;

public class UserSessionApiLocal implements UserSessionApi{
  
  protected User currentUser;
  
  public void setCurrentUser(User user) {
    this.currentUser = user;
  }
  
  @Override
  public User currentUser() {
    return currentUser;
  }
}
