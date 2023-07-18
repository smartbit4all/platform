package org.smartbit4all.bff.api.mdm;

import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.view.PageApi;


/**
 * This page is the generic page api for the master data management administration view.
 */
public interface MDMAdminPageApi extends PageApi<MDMDefinition> {

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

}
