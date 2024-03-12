package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MDMEntrySetup;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.view.bean.ActionDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

public class ActionDefinitionApiImpl implements ActionDefinitionApi, MDMEntrySetup {

  /**
   * All the action providers available in the current runtime. As a result all these providers are
   * going to be saved into the registry of the action definitions.
   */
  @Autowired(required = false)
  private List<ActionProviderApi> providers;

  @Override
  public ActionDefinition getAction(String qualifiedName) {
    return null;
  }

  private final void refreshCache() {

  }

  @Override
  public String getDefinitionToSetup() {
    return MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION;
  }

  @Override
  public List<String> getEntriesToSetup() {
    List<String> result = new ArrayList<>();
    result.add(MDM_ACTION_DEFINITIONS);
    return result;
  }

  @Override
  public void setupEntries(Map<String, MDMEntryApi> entries) {
    if (providers != null) {
      // Iterate on every provider to get the available actions and save them into the MDM for
      // further management.
      MDMEntryApi entryApi = entries.get(MDM_ACTION_DEFINITIONS);
      if (entryApi != null) {
        Map<String, ObjectNode> nodesByName = entryApi.getList().nodes()
            .collect(toMap(n -> n.getValueAsString(ActionDefinition.QUALIFIED_NAME), n -> n));
        List<ObjectNode> nodesToSave = new ArrayList<>();
        for (ActionProviderApi providerApi : providers) {
          List<ActionDefinition> actionDefinitions = providerApi.getActionDefinitions();
          for (ActionDefinition actionDefinition : actionDefinitions) {
          }
        }
      }
    }
  }

}
