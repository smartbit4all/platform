package org.smartbit4all.testing.mdm;

import static org.smartbit4all.core.utility.StringConstant.joinDot;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.SecurityOption;

public class MDMSecurityOptions implements SecurityOption {

  private static String name(String name) {
    return joinDot(MDMSecurityOptions.class.getName(), name);
  }

  public static final SecurityGroup admin =
      SecurityGroup.of(name("admin"))
          .title("MDM administrator")
          .description("MDM administrator")
          .builtIn(true);

}
