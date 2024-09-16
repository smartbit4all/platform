package org.smartbit4all.api.config;

import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.SecurityOption;

public class PlatformSecurityOption implements SecurityOption {

  public static final SecurityGroup userAdminSetPrimaryAccount =
      SecurityGroup.of().title("User account set primary account")
          .description("User administrator ability to set the primary account")
          .builtIn(true);

  public static final SecurityGroup embeddingConectionEditor =
      SecurityGroup.of().title("Platform embedding conection editor")
          .description("Plaform level embedding conection editor")
          .builtIn(true);
  public static final SecurityGroup vectorDbEditor =
      SecurityGroup.of().title("Platform vector db editor")
          .description("Plaform level vector db editor")
          .builtIn(true);
  public static final SecurityGroup ticketingEditor =
      SecurityGroup.of().title("Platform ticketing editor")
          .description("Plaform level ticketing editor")
          .builtIn(true);
  public static final SecurityGroup objectValidationEditor =
      SecurityGroup.of().title("Platform object validation editor")
          .description("Plaform level object validation editor")
          .builtIn(true);
  public static final SecurityGroup serviceConnectionEditor =
      SecurityGroup.of().title("Platform servcie connection editor")
          .description("Plaform level servcie connection editor")
          .builtIn(true);
  public static final SecurityGroup actionDefinitionEditor =
      SecurityGroup.of().title("Platform action definition editor")
          .description("Plaform action definition editor")
          .builtIn(true);
  public static final SecurityGroup conversionServiceEditor =
      SecurityGroup.of().title("Platform conversion service editor")
          .description("Plaform level conversion service editor")
          .builtIn(true);
  public static final SecurityGroup valueTransformationEditor =
      SecurityGroup.of().title("Platform value tarnsformation editor")
          .description("Plaform level value tarnsformation editor")
          .builtIn(true);
  public static final SecurityGroup apiKeyEditor =
      SecurityGroup.of().title("Platform api key editor")
          .description("Plaform level api key editor")
          .builtIn(true);
  public static final SecurityGroup oAuthPropEditor =
      SecurityGroup.of().title("Platform oAuth property editor")
          .description("Plaform level oAuth property editor")
          .builtIn(true);

  public static final SecurityGroup admin =
      SecurityGroup.of().title("Platform admin").description("Plaform level administrator")
          .subgroup(embeddingConectionEditor)
          .subgroup(vectorDbEditor)
          .subgroup(ticketingEditor)
          .subgroup(objectValidationEditor)
          .subgroup(serviceConnectionEditor)
          .subgroup(actionDefinitionEditor)
          .subgroup(conversionServiceEditor)
          .subgroup(valueTransformationEditor)
          .subgroup(apiKeyEditor)
          .subgroup(oAuthPropEditor)
          .builtIn(true);

}
