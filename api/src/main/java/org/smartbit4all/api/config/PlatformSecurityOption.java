package org.smartbit4all.api.config;

import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.SecurityOption;

public class PlatformSecurityOption implements SecurityOption {

  public static final SecurityGroup admin =
      SecurityGroup.of().title("Platform admin").description("Plaform level administrator")
          .builtIn(true);

  public static final SecurityGroup userAdminSetPrimaryAccount =
      SecurityGroup.of().title("User account set primary account")
          .description("User administrator ability to set the primary account")
          .builtIn(true);
}
