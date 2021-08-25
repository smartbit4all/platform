package org.smartbit4all.api.org;

import org.smartbit4all.api.session.UserSessionApi;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * The module specific security option class having some {@link SecurityGroup} member to declare the
 * security group used by the module. This must be configured by Spring to enable as bean.
 * 
 * @author Peter Boros
 */
@Service
public class MyModuleSecurityOption implements SecurityOption {

  public SecurityGroup editor;

  public SecurityGroup viewer;

  public SecurityGroup admin;
  
  public MyModuleSecurityOption(UserSessionApi userSessionApi) {
    editor = new SecurityGroup("The my module editor group", userSessionApi);
    viewer = new SecurityGroup("The my module editor group", userSessionApi);
    admin = new SecurityGroup("The my module editor group", userSessionApi, viewer, editor);
  }
  
}
