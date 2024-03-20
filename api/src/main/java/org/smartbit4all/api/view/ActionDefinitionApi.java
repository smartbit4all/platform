package org.smartbit4all.api.view;

import java.util.List;
import org.smartbit4all.api.mdm.MDMEntrySetup;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.bean.ActionDefinition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.ViewEvaluationContext;
import com.google.common.base.Strings;

/**
 * Primary api responsible for collection all the actions with the "performAction" invocations and
 * other settings for the actions. The action definitions are also saved into the
 * {@link MasterDataManagementApi#MDM_DEFINITION_SYSTEM_INTEGRATION} definition in the
 * {@link #MDM_ACTION_DEFINITIONS} entry. All the providers are registered automatically and we can
 * add more actions manually also.
 * 
 * @author Peter Boros
 */
public interface ActionDefinitionApi extends MDMEntrySetup {

  /**
   * The action definitions MDM entry name.
   */
  static final String MDM_ACTION_DEFINITIONS = "ActionDefinitions";

  String SCHEMA = "actionDefinition";

  /**
   * Retrieve the action definition by the qualified name.
   * 
   * @param qualifiedName The fully qualified name of the action.
   * @return The final version of the action based on the merged version of the action definitions.
   */
  ActionDefinition getAction(String qualifiedName);

  List<ActionDefinition> getActionsForView(final ViewEvaluationContext viewEvalContext);

  static UiAction definitionAsAction(ActionDefinition actionDefinition,
      LocaleSettingApi localeSettingApi) {
    final UiAction a = actionDefinition.getAction();
    if (actionDefinition.getDescriptor() != null) {
      a.setDescriptor(actionDefinition.getDescriptor());
    }

    final UiActionDescriptor descriptor = a.getDescriptor();
    if (descriptor != null) {
      final String title = descriptor.getTitle();
      if (!Strings.isNullOrEmpty(title)) {
        descriptor.setTitle(localeSettingApi.get(title));
      }
    }

    return a;
  }

}
