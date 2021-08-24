package org.smartbit4all.api.session;

import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;

public class UserSessionApiTestImpl implements UserSessionApi{

  private OrgApi orgApi;
  
  public UserSessionApiTestImpl(OrgApi orgApi) {
    this.orgApi= orgApi;
  }
  
  @Override
  public User currentUser() {
    return orgApi.getAllUsers().get(0);
  }
}
