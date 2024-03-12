package org.smartbit4all.api.view;

import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.view.bean.ActionDefinition;

/**
 * Primary api responsible for collection all the actions with the "performAction" invocations and
 * other settings for the actions. The action definitions are also saved into the
 * {@link MasterDataManagementApi#MDM_DEFINITION_SYSTEM_INTEGRATION} definition in the
 * {@link #MDM_ACTION_DEFINITIONS} entry. All the providers are registered automatically and we can
 * add more actions manually also.
 * 
 * @author Peter Boros
 */
public interface ActionDefinitionApi {

  /**
   * The action definitions MDM entry name.
   */
  static final String MDM_ACTION_DEFINITIONS = "ActionDefinitions";

  /**
   * Retrieve the action definition by the qualified name.
   * 
   * @param qualifiedName The fully qualified name of the action.
   * @return The final version of the action based on the merged version of the action definitions.
   */
  ActionDefinition getAction(String qualifiedName);

}
