package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;


/**
 * This page is the generic page api for the master data management administration view. You can use
 * any of its implementations:
 * <ul>
 * <li>MDMAdminPageApi: will display all an action for all MDMEntryDescriptors
 * <li>MDMAdminPageWithValues: will display an action for all non ValueSet MDMEntryDescriptors, and
 * an additional action to open MDM_ADMIN_VALUES view
 * </ul>
 *
 */
public interface MDMAdminPageApi extends PageApi<MDMDefinition> {

  static final String MDM_ADMIN = "MDMAdmin";
  static final String MDM_ADMIN_VALUES = MDM_ADMIN + "Values";
  static final String MDM_LIST = "MDMList";
  static final String MDM_LIST_VALUES = MDM_LIST + "Values";
  static final String MDM_EDIT = "MDMEdit";

  /**
   * Open the given MDMListPage for the descriptor.
   */
  static final String ACTION_OPEN_LIST = "OPEN_LIST";

  /**
   * The identifier of of the {@link MDMDefinition} object parameter.
   */
  static final String PARAM_MDM_DEFINITION = "MDM_DEFINITION";

  /**
   * The identifier of of the {@link MDMEntryDescriptor} object parameter.
   */
  static final String ACTION_PARAM_MDM_ENTRY = "MDM_ENTRY";

  @ActionHandler
  void openList(UUID viewUuid, UiActionRequest request);

}
