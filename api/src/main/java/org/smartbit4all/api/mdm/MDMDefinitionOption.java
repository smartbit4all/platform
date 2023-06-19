package org.smartbit4all.api.mdm;

import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor.BranchStrategyEnum;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.core.object.ObjectDefinitionProvidedApi;

/**
 * The object that can be registered into the spring context to define a definition. It will be able
 * to define if the given definition can be edited in the storage. If yes then the settings from
 * this bean will overwrite the storage. Else we just update the records or leave them untouched.
 */
public class MDMDefinitionOption {

  /**
   * The definition object for the given option. It will be merged with the storage when the given
   * node starts.
   */
  private MDMDefinition definition;

  public MDMDefinitionOption(MDMDefinition definition) {
    super();
    this.definition = definition;
  }

  public MDMEntryDescriptor addDefaultDescriptor(Class<?> clazz) {
    MDMEntryDescriptor result =
        new MDMEntryDescriptor().schema(definition.getName()).name(clazz.getSimpleName())
            .branchStrategy(BranchStrategyEnum.ENTRYLEVEL).publishInList(Boolean.TRUE);
    definition.putDescriptorsItem(result.getName(), result);
    return result;
  }

  public MDMEntryDescriptor addObjectDefinitionData() {
    MDMEntryDescriptor result =
        new MDMEntryDescriptor().schema(definition.getName())
            .name(ObjectDefinitionData.class.getSimpleName())
            .branchStrategy(BranchStrategyEnum.ENTRYLEVEL).publishInList(Boolean.TRUE)
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
    definition.putDescriptorsItem(result.getName(), result);
    return result;
  }

  public final MDMDefinition getDefinition() {
    return definition;
  }

  public final void setDefinition(MDMDefinition definition) {
    this.definition = definition;
  }

}
