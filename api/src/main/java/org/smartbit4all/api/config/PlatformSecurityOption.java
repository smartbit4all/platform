package org.smartbit4all.api.config;

import static org.smartbit4all.core.utility.StringConstant.joinDot;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.SecurityOption;

public class PlatformSecurityOption implements SecurityOption {

  private static String name(String name) {
    return joinDot(PlatformSecurityOption.class.getName(), name);
  }

  public static final SecurityGroup admin =
      SecurityGroup.of(name("admin"))
          .title("Platform admin")
          .description("Plaform level administrator")
          .builtIn(true);

  public static final SecurityGroup userAdminSetPrimaryAccount =
      SecurityGroup.of(name("userAdminSetPrimaryAccount"))
          .title("User account set primary account")
          .description("User administrator ability to set the primary account")
          .builtIn(true);

  public static final SecurityGroup embeddingConectionEditor =
      SecurityGroup.of(name("embeddingConectionEditor"))
          .title("Platform embedding conection editor")
          .description("Plaform level embedding conection editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup vectorDbEditor =
      SecurityGroup.of(name("vectorDbEditor"))
          .title("Platform vector db editor")
          .description("Plaform level vector db editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup ticketingEditor =
      SecurityGroup.of(name("ticketingEditor"))
          .title("Platform ticketing editor")
          .description("Plaform level ticketing editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup objectValidationEditor =
      SecurityGroup.of(name("objectValidationEditor"))
          .title("Platform object validation editor")
          .description("Plaform level object validation editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup serviceConnectionEditor =
      SecurityGroup.of(name("serviceConnectionEditor"))
          .title("Platform servcie connection editor")
          .description("Plaform level servcie connection editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup actionDefinitionEditor =
      SecurityGroup.of(name("actionDefinitionEditor"))
          .title("Platform action definition editor")
          .description("Plaform action definition editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup conversionServiceEditor =
      SecurityGroup.of(name("conversionServiceEditor"))
          .title("Platform conversion service editor")
          .description("Plaform level conversion service editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup valueTransformationEditor =
      SecurityGroup.of(name("valueTransformationEditor"))
          .title("Platform value tarnsformation editor")
          .description("Plaform level value tarnsformation editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup apiKeyEditor =
      SecurityGroup.of(name("apiKeyEditor"))
          .title("Platform api key editor")
          .description("Plaform level api key editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup oAuthPropEditor =
      SecurityGroup.of(name("oAuthPropEditor"))
          .title("Platform oAuth property editor")
          .description("Plaform level oAuth property editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup methodTemplateEditor =
      SecurityGroup.of(name("methodTemplateEditor"))
          .title("Platform method template editor")
          .description("Platform method template editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup invocationApiAdmin =
      SecurityGroup.of(name("invocationApiAdmin"))
          .title("Platform invocation api administrator")
          .description("Platform invocation api administrator")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup layoutDescriptorEditor =
      SecurityGroup.of(name("layoutDescriptorEditor"))
          .title("Platform layout descriptor editor")
          .description("Platform layout descriptor editor")
          .subgroup(admin)
          .builtIn(true);
  public static final SecurityGroup filterHierarchyEditor =
      SecurityGroup.of(name("filterHierarchyEditor"))
          .title("Platform filter hierarchy descriptor editor")
          .description("Platform filter hierarchy descriptor editor")
          .subgroup(admin)
          .builtIn(true);


}
