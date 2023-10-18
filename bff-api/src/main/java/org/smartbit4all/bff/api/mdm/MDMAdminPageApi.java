package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.MDMConstants;
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
public interface MDMAdminPageApi extends PageApi<Object> {

  static final String MDM_ADMIN = MDMConstants.MDM_ADMIN;
  static final String MDM_ADMIN_VALUES = MDMConstants.MDM_ADMIN_VALUES;
  static final String MDM_LIST = MDMConstants.MDM_LIST;
  static final String MDM_LIST_VALUES = MDMConstants.MDM_LIST_VALUES;
  static final String MDM_EDIT = MDMConstants.MDM_EDIT;

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

  /**
   * The identifier of the add new action.
   */
  static final String ACTION_ADD_NEW_ENTRY = "ADD_NEW_ENTRY";

  @ActionHandler
  void openList(UUID viewUuid, UiActionRequest request);

  @ActionHandler(ACTION_ADD_NEW_ENTRY)
  void performNewEntry(UUID viewUuid, UiActionRequest request);

}
