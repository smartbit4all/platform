package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.bean.MDMEntryDescriptorPageModel;

@ViewApi(value = MDMConstants.MDM_ENTRY_DESCRIPTOR)
public interface MDMEntryDescriptorPageApi
    extends PageApi<MDMEntryDescriptorPageModel> {

  String SAVE = "SAVE";

  UiAction ACTION_SAVE = new UiAction().code(SAVE).submit(true);

  String PARAM_MDM_DEFINITION = "MDM_DEFINITION";

  String PARAM_MDM_ENTRY_DESCRIPTOR = "mdmEntryDescriptor";

  String CALLBACK_REFRESH_ACTIONS = "refreshActions";

  @ActionHandler(SAVE)
  void saveEntry(UUID viewUuid, UiActionRequest request);

}
