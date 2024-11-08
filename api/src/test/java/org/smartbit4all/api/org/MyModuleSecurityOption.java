package org.smartbit4all.api.org;

import static org.smartbit4all.core.utility.StringConstant.joinDot;
import org.springframework.stereotype.Service;

/**
 * The module specific security option class having some {@link SecurityGroup} member to declare the
 * security group used by the module. This must be configured by Spring to enable as bean.
 *
 * @author Peter Boros
 */
@Service
public class MyModuleSecurityOption implements SecurityOption {

  private static String name(String name) {
    return joinDot(MyModuleSecurityOption.class.getName(), name);
  }

  public SecurityGroup editor = SecurityGroup.of(name("editor"))
      .description("The my module editor group");

  public SecurityGroup viewer = SecurityGroup.of(name("viewer"))
      .description("The my module viewer group");

  public SecurityGroup admin = SecurityGroup.of(name("admin"))
      .description("The my module admin group");

}
