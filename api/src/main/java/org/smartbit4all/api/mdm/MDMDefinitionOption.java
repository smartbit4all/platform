package org.smartbit4all.api.mdm;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModificationItem;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectDefinitionProvidedApi;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The object that can be registered into the spring context to define a definition. It will be able
 * to define if the given definition can be edited in the storage. If yes then the settings from
 * this bean will overwrite the storage. Else we just update the records or leave them untouched.
 */
public class MDMDefinitionOption {

  public static final String UPDATED_TIMESTAMP =
      MDMEntryApi.Props.UPDATED + StringConstant.UNDERLINE
          + UserActivityLog.TIMESTAMP;
  public static final String UPDATED_NAME =
      MDMEntryApi.Props.UPDATED + StringConstant.UNDERLINE + UserActivityLog.NAME;
  public static final String CREATED_TIMESTAMP =
      MDMEntryApi.Props.CREATED + StringConstant.UNDERLINE
          + UserActivityLog.TIMESTAMP;
  public static final String CREATED_NAME =
      MDMEntryApi.Props.CREATED + StringConstant.UNDERLINE + UserActivityLog.NAME;
  public static final String MERGED_TIMESTAMP =
      MDMEntryApi.Props.MERGED + StringConstant.UNDERLINE + UserActivityLog.TIMESTAMP;
  public static final String MERGED_NAME =
      MDMEntryApi.Props.MERGED + StringConstant.UNDERLINE
          + UserActivityLog.NAME;
  public static final String STATE_NAME = "stateName";

  /**
   * The definition object for the given option. It will be merged with the storage when the given
   * node starts.
   */
  private MDMDefinition definition;

  public MDMDefinitionOption(MDMDefinition definition) {
    super();
    this.definition = definition;
    if (definition.getBranchingStrategy() == null) {
      definition.setBranchingStrategy(MDMBranchingStrategy.GLOBAL);
    }
  }

  public MDMEntryDescriptor addDefaultDescriptor(Class<?> clazz) {
    return addDefaultDescriptor(clazz, clazz.getSimpleName());
  }

  public MDMEntryDescriptor addDefaultDescriptor(Class<?> clazz, String name) {
    MDMEntryDescriptor result =
        new MDMEntryDescriptor().schema(definition.getName()).name(name)
            .typeQualifiedName(clazz.getName());
    addDescriptor(result);
    return result;
  }

  public MDMEntryDescriptor addObjectDefinitionData() {
    MDMEntryDescriptor result =
        new MDMEntryDescriptor().schema(definition.getName())
            .name(ObjectDefinitionData.class.getSimpleName())
            .addUniquePropertyPathsItem(Arrays.asList(ObjectDefinitionData.QUALIFIED_NAME))
            .typeQualifiedName(ObjectDefinitionData.class.getName())
            .uriConstructor(new InvocationRequest()
                .interfaceClass(ObjectDefinitionProvidedApi.class.getName())
                .methodName(ObjectDefinitionProvidedApi.constructUriFromQualifiedName)
                .name(ObjectDefinitionProvidedApi.class.getName())
                .addParametersItem(new InvocationParameter().name("objectDefinitionNode")
                    .typeClass(ObjectDefinitionData.class.getName())))
            .addEventHandlersBeforeSaveItem(new InvocationRequest()
                .interfaceClass(ObjectDefinitionProvidedApi.class.getName())
                .methodName(ObjectDefinitionProvidedApi.synchronizeOutgoingReferences)
                .name(ObjectDefinitionProvidedApi.class.getName())
                .addParametersItem(new InvocationParameter().name("objectDefinitionNode")
                    .typeClass(ObjectDefinitionData.class.getName())));
    addDescriptor(result);
    return result;
  }

  public void addDescriptor(MDMEntryDescriptor descriptor) {
    if (definition.getDescriptors().containsKey(descriptor.getName())) {
      throw new IllegalArgumentException(
          "descriptor already registered (" + definition.getName()
              + "." + descriptor.getName() + ")");
    }
    if (descriptor.getBranchingStrategy() == null) {
      descriptor.setBranchingStrategy(definition.getBranchingStrategy());
    }
    definition.putDescriptorsItem(descriptor.getName(), descriptor);
  }

  public void addTemplate(MDMEntryDescriptor descriptor) {
    if (definition.getDescriptors().containsKey(descriptor.getName())) {
      throw new IllegalArgumentException(
          "temőlate already registered (" + definition.getName()
              + "." + descriptor.getName() + ")");
    }
    if (descriptor.getBranchingStrategy() == null) {
      descriptor.setBranchingStrategy(definition.getBranchingStrategy());
    }
    definition.putTemplatesItem(descriptor.getName(), descriptor);
  }

  public final MDMDefinition getDefinition() {
    return definition;
  }

  public final void setDefinition(MDMDefinition definition) {
    this.definition = definition;
  }

  public static void addCreatedUpdatedExtraProperties(MDMEntryDescriptor descriptor) {
    addExtraProperties(descriptor, Arrays.asList(
        new MDMTableColumnDescriptor()
            .name(CREATED_NAME)
            .typeClass(String.class.getName())
            .path(Arrays.asList(MDMEntryApi.Props.CREATED, UserActivityLog.NAME)),
        new MDMTableColumnDescriptor()
            .name(CREATED_TIMESTAMP)
            .typeClass(OffsetDateTime.class.getName())
            .path(Arrays.asList(MDMEntryApi.Props.CREATED, UserActivityLog.TIMESTAMP)),
        new MDMTableColumnDescriptor()
            .name(UPDATED_NAME)
            .typeClass(String.class.getName())
            .path(Arrays.asList(MDMEntryApi.Props.UPDATED, UserActivityLog.NAME)),
        new MDMTableColumnDescriptor()
            .name(UPDATED_TIMESTAMP)
            .typeClass(OffsetDateTime.class.getName())
            .path(Arrays.asList(MDMEntryApi.Props.UPDATED, UserActivityLog.TIMESTAMP))));
  }

  protected static void addExtraProperties(MDMEntryDescriptor descriptor,
      List<MDMTableColumnDescriptor> extraProperties) {
    List<MDMTableColumnDescriptor> columns = new ArrayList<>();
    if (descriptor.getTableColumns() != null) {
      columns.addAll(descriptor.getTableColumns());
    }
    columns.addAll(extraProperties);
    descriptor.tableColumns(columns);
  }

  public static void addMergedExtraProperties(MDMEntryDescriptor descriptor) {
    addExtraProperties(descriptor, Arrays.asList(
        new MDMTableColumnDescriptor()
            .name(MERGED_NAME)
            .typeClass(String.class.getName())
            .path(Arrays.asList(MDMEntryApi.Props.MERGED, UserActivityLog.NAME)),
        new MDMTableColumnDescriptor()
            .name(MERGED_TIMESTAMP)
            .typeClass(OffsetDateTime.class.getName())
            .path(Arrays.asList(MDMEntryApi.Props.MERGED, UserActivityLog.TIMESTAMP))));
  }

  public static void addStateExtraProperties(MDMEntryDescriptor descriptor) {
    addExtraProperties(descriptor, Arrays.asList(
        new MDMTableColumnDescriptor()
            .name(MDMModificationItem.STATE)
            .typeClass(String.class.getName())
            .path(Arrays.asList(MDMModificationItem.STATE)),
        new MDMTableColumnDescriptor()
            .name(STATE_NAME)
            .typeClass(String.class.getName())
            .path(Arrays.asList(STATE_NAME))));
  }
}
