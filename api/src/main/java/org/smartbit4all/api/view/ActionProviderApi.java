package org.smartbit4all.api.view;

import java.util.List;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.toolbar.bean.ActionDefinition;
import org.smartbit4all.api.view.bean.UiAction;

/**
 * This is a marker interface to show which apis are providing perform actions for {@link UiAction}
 * execution. We collect and save all the api call of this apis into the MDM entry initiated for the
 * {@link ActionDefinitionApi#MDM_ACTION_DEFINITIONS} in the
 * {@link MasterDataManagementApi#MDM_DEFINITION_SYSTEM_INTEGRATION}.
 * 
 * @author Peter Boros
 */
public interface ActionProviderApi {

  List<ActionDefinition> getActionDefinitions();

}
