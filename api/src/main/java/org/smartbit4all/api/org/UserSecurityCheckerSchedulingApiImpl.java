package org.smartbit4all.api.org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class UserSecurityCheckerSchedulingApiImpl implements UserSecurityCheckerSchedulingApi {

  @Autowired
  private UserSecurityCheckerApi userSecurityCheckerApi;



  @Override
  @Scheduled(fixedDelayString = "${user.security.policy.scheduling:43200000}")
  public void checkUsersBySecurityPolicy() {
    userSecurityCheckerApi.checkUsersBySecurityPolicy();
  }
}
