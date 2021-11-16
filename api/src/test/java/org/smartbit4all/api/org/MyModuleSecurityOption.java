package org.smartbit4all.api.org;

import org.springframework.stereotype.Service;

/**
 * The module specific security option class having some {@link SecurityGroup} member to declare the
 * security group used by the module. This must be configured by Spring to enable as bean.
 * 
 * @author Peter Boros
 */
@Service
public class MyModuleSecurityOption implements SecurityOption {

  public SecurityGroup editor = SecurityGroup.of().description("The my module editor group");

  public SecurityGroup viewer = SecurityGroup.of().description("The my module viewer group");

  public SecurityGroup admin = SecurityGroup.of().description("The my module admin group");

}
