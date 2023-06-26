package org.smartbit4all.testing.mdm;

import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.SecurityOption;

public class MDMSecurityOptions implements SecurityOption {

  public static final SecurityGroup admin =
      SecurityGroup.of().title("MDM administrator").description("MDM administrator")
          .builtIn(true);

}
