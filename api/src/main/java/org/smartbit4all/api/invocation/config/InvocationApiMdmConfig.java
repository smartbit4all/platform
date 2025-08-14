package org.smartbit4all.api.invocation.config;

import java.util.Arrays;
import org.smartbit4all.api.config.PlatformSecurityOption;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationExecutionApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.JobDefinition;
import org.smartbit4all.api.invocation.bean.MethodTemplate;
import org.smartbit4all.api.invocation.bean.ScheduledJobDefinition;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.setting.Locales;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvocationApiMdmConfig {

  private static final String INVOCATION_API_REGISTRY_HU = "Invocation Api lista";

  private static final String INVOCATION_API_REGISTRY = "Invocation Api Registry";

  private static final String INVOCATION_SERVICE_CONNECTIONS_HU =
      "Invocation kiszolgáló kapcsolatok";

  private static final String INVOCATION_SERVICE_CONNECTIONS = "Invocation Service Connections";

  private static final String INVOCATION_JOB_DEFINTIONS_HU =
      "Invocation feladatok";

  private static final String INVOCATION_JOB_DEFINTIONS = "Invocaton Job definitions";

  private static final String INVOCATION_SCHEDULED_JOB_DEFINTIONS_HU =
      "Invocation ütemezett feladatok";

  private static final String INVOCATION_SCHEDULED_JOB_DEFINTIONS =
      "Invocaton Scheduled job definitions";

  public static final String METHOD_TEMPLATES = "METHOD_TEMPLATES";

  /**
   * The name of the MDM entry containing the service connections for the
   * {@link InvocationExecutionApi}s.
   */
  public static final String MDM_ENTRY_SERVICECONNECTION = "InvocationServiceConnection";

  /**
   * The name of the MDM entry containing the {@link ApiData} as the dynamic registry for the
   * {@link InvocationApi} managed invocations.
   */
  public static final String MDM_ENTRY_APIREGISTRY = "InvocationApiRegistry";

  /**
   * The name of the MDM entry containing the {@link ScheduledJobDefinition} as the dynamic registry
   * for the {@link InvocationApi} managed invocations.
   */
  public static final String MDM_ENTRY_SCHEDULEDJOBDEFINITION = "InvocationScheduledJobDefinition";

  /**
   * The name of the MDM entry containing the {@link JobDefinition} as the dynamic registry for the
   * {@link InvocationApi} managed invocations.
   */
  public static final String MDM_ENTRY_JOBDEFINITION = "InvocationJobDefinition";

  @Bean
  MDMDefinitionOption mdmOption() {
    MDMDefinition mdmDefinition =
        new MDMDefinition().name(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION)
            .adminGroupName(PlatformSecurityOption.admin.getName());
    MDMDefinitionOption result =
        new MDMDefinitionOption(mdmDefinition);

    result.addDescriptor(new MDMEntryDescriptor()
        .schema(Invocations.INVOCATION_SCHEME)
        .publishedListName(METHOD_TEMPLATES)
        .name(METHOD_TEMPLATES)
        .adminGroupName(PlatformSecurityOption.methodTemplateEditor.getName())
        .addConstraintsItem(new MDMEntryConstraint()
            .kind(KindEnum.UNIQUECASEINSENSITIVE)
            .addPathItem(MethodTemplate.INTERFACE_NAME))
        .editorViewName(MDMConstants.MDM_EDIT)
        .displayNameList(new LangString().defaultValue("Method templates")
            .putValueByLocaleItem(Locales.L_HU, "Metódus sablonok")
            .putValueByLocaleItem(Locales.L_EN, "Method templates"))
        .displayNameForm(new LangString().defaultValue("Method template")
            .putValueByLocaleItem(Locales.L_HU, "Metódus sablon")
            .putValueByLocaleItem(Locales.L_EN, "Method template"))
        .order(200l)
        .typeQualifiedName(MethodTemplate.class.getName())
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name("Interface name")
                .addPathItem(MethodTemplate.INTERFACE_NAME))
        .addTableColumnsItem(
            new MDMTableColumnDescriptor()
                .name("Api name")
                .addPathItem(MethodTemplate.API_NAME)));

    result.addDescriptor(new MDMEntryDescriptor()
        .schema(Invocations.INVOCATION_SCHEME)
        .publishedListName(MDM_ENTRY_SERVICECONNECTION)
        .name(MDM_ENTRY_SERVICECONNECTION)
        .editorViewName(PlatformViewNames.SERVICE_CONNECTION_EDITOR)
        .displayNameList(new LangString().defaultValue(INVOCATION_SERVICE_CONNECTIONS)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_SERVICE_CONNECTIONS_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_SERVICE_CONNECTIONS))
        .displayNameForm(new LangString().defaultValue(INVOCATION_SERVICE_CONNECTIONS)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_SERVICE_CONNECTIONS_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_SERVICE_CONNECTIONS))
        .order(100l)
        .typeQualifiedName(ServiceConnection.class.getName())
        .tableColumns(Arrays.asList(
            new MDMTableColumnDescriptor()
                .name("Name")
                .path(Arrays.asList(ServiceConnection.NAME)),
            new MDMTableColumnDescriptor()
                .name("API name")
                .path(Arrays.asList(ServiceConnection.API_NAME)),
            new MDMTableColumnDescriptor()
                .name("Endpoint")
                .path(Arrays.asList(ServiceConnection.ENDPOINT)),
            new MDMTableColumnDescriptor()
                .name("API version")
                .path(Arrays.asList(ServiceConnection.API_VERSION)))));

    result.addDescriptor(new MDMEntryDescriptor()
        .schema(Invocations.INVOCATION_SCHEME)
        .publishedListName(MDM_ENTRY_APIREGISTRY)
        .name(MDM_ENTRY_APIREGISTRY)
        .displayNameList(new LangString().defaultValue(INVOCATION_API_REGISTRY)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_API_REGISTRY_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_API_REGISTRY))
        .displayNameForm(new LangString().defaultValue(INVOCATION_API_REGISTRY)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_API_REGISTRY_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_API_REGISTRY))
        .order(100l)
        .typeQualifiedName(ApiData.class.getName())
        .tableColumns(Arrays.asList(
            new MDMTableColumnDescriptor()
                .name("Name")
                .path(Arrays.asList(ApiData.NAME)),
            new MDMTableColumnDescriptor()
                .name("API interface")
                .path(Arrays.asList(ApiData.INTERFACE_NAME)),
            new MDMTableColumnDescriptor()
                .name("Service connection name")
                .path(Arrays.asList(ApiData.SERVICE_CONNECTION)))));

    result.addDescriptor(new MDMEntryDescriptor()
        .schema(Invocations.INVOCATION_SCHEME)
        .publishedListName(MDM_ENTRY_JOBDEFINITION)
        .name(MDM_ENTRY_JOBDEFINITION)
        .addConstraintsItem(new MDMEntryConstraint()
            .kind(KindEnum.UNIQUECASEINSENSITIVE)
            .addPathItem(JobDefinition.CODE))
        .editorViewName(PlatformViewNames.JOB_DEFINITION_EDITOR)
        .displayNameList(new LangString()
            .defaultValue(MDM_ENTRY_JOBDEFINITION)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_JOB_DEFINTIONS_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_JOB_DEFINTIONS))
        .displayNameForm(new LangString()
            .defaultValue(MDM_ENTRY_JOBDEFINITION)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_JOB_DEFINTIONS_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_JOB_DEFINTIONS))
        .order(100l)
        .typeQualifiedName(JobDefinition.class.getName())
        .tableColumns(Arrays.asList(
            new MDMTableColumnDescriptor()
                .name("Code")
                .path(Arrays.asList(JobDefinition.CODE)),
            new MDMTableColumnDescriptor()
                .name("Name")
                .path(Arrays.asList(JobDefinition.NAME)),
            new MDMTableColumnDescriptor()
                .name("Description")
                .path(Arrays.asList(JobDefinition.DESCRIPTION)))));

    result.addDescriptor(new MDMEntryDescriptor()
        .schema(Invocations.INVOCATION_SCHEME)
        .publishedListName(MDM_ENTRY_SCHEDULEDJOBDEFINITION)
        .name(MDM_ENTRY_SCHEDULEDJOBDEFINITION)
        .addConstraintsItem(new MDMEntryConstraint()
            .kind(KindEnum.UNIQUECASEINSENSITIVE)
            .addPathItem(JobDefinition.CODE))
        .editorViewName(PlatformViewNames.SCHEDULED_JOB_DEFINITION_EDITOR)
        .displayNameList(new LangString()
            .defaultValue(MDM_ENTRY_SCHEDULEDJOBDEFINITION)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_SCHEDULED_JOB_DEFINTIONS_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_SCHEDULED_JOB_DEFINTIONS))
        .displayNameForm(new LangString()
            .defaultValue(MDM_ENTRY_SCHEDULEDJOBDEFINITION)
            .putValueByLocaleItem(Locales.L_HU, INVOCATION_SCHEDULED_JOB_DEFINTIONS_HU)
            .putValueByLocaleItem(Locales.L_EN, INVOCATION_SCHEDULED_JOB_DEFINTIONS))
        .order(100l)
        .typeQualifiedName(ScheduledJobDefinition.class.getName())
        .tableColumns(Arrays.asList(
            new MDMTableColumnDescriptor()
                .name("Code")
                .path(Arrays.asList(ScheduledJobDefinition.CODE)),
            new MDMTableColumnDescriptor()
                .name("Name")
                .path(Arrays.asList(ScheduledJobDefinition.NAME)),
            new MDMTableColumnDescriptor()
                .name("Description")
                .path(Arrays.asList(ScheduledJobDefinition.DESCRIPTION)),
            new MDMTableColumnDescriptor()
                .name("Job definition")
                .path(Arrays.asList(ScheduledJobDefinition.JOB_DEFINITION_CODE)),
            new MDMTableColumnDescriptor()
                .name("Cron expression")
                .path(Arrays.asList(ScheduledJobDefinition.CRON_EXPRESSION)),
            new MDMTableColumnDescriptor()
                .name("Execution scope")
                .path(Arrays.asList(ScheduledJobDefinition.EXECUTION_SCOPE)))));

    return result;
  }

}
