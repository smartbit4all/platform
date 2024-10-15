package org.smartbit4all.api.invocation.config;

import org.smartbit4all.api.config.PlatformSecurityOption;
import org.smartbit4all.api.invocation.bean.MethodTemplate;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvocationApiMdmConfig {

  public static final String METHOD_TEMPLATES = "METHOD_TEMPLATES";

  @Bean
  MDMDefinitionOption mdmOption() {
    MDMDefinition mdmDefinition =
        new MDMDefinition().name(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION)
            .adminGroupName(PlatformSecurityOption.admin.getName());
    MDMDefinitionOption result =
        new MDMDefinitionOption(mdmDefinition);

    MDMEntryDescriptor entry = new MDMEntryDescriptor()
        .schema(MasterDataManagementApi.SCHEMA)
        .publishedListName(METHOD_TEMPLATES)
        .name(METHOD_TEMPLATES)
        .adminGroupName(PlatformSecurityOption.methodTemplateEditor.getName())
        .addConstraintsItem(new MDMEntryConstraint()
            .kind(KindEnum.UNIQUECASEINSENSITIVE)
            .addPathItem(MethodTemplate.INTERFACE_NAME))
        .editorViewName(MDMConstants.MDM_EDIT)
        .displayNameList(new LangString().defaultValue("Method templates")
            .putValueByLocaleItem("hu", "Metódus sablonok")
            .putValueByLocaleItem("en", "Method templates"))
        .displayNameForm(new LangString().defaultValue("Method template")
            .putValueByLocaleItem("hu", "Metódus sablon")
            .putValueByLocaleItem("en", "Method template"))
        .order(200l)
        .typeQualifiedName(MethodTemplate.class.getName())
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name("Interface name")
                .addPathItem(MethodTemplate.INTERFACE_NAME))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name("Api name")
                .addPathItem(MethodTemplate.API_NAME));
    result.addDescriptor(entry);
    return result;
  }

}
