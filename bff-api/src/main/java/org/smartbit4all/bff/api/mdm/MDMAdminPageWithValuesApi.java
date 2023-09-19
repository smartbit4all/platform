package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * This page is the generic page api for the master data management, with an extra uiAction for
 * opening {@link MDMAdminValuesPageApi} for handling ValueSets in a separate page.
 */
public interface MDMAdminPageWithValuesApi extends MDMAdminPageApi {

  static final String ACTION_OPEN_VALUES = "OPEN_VALUES";

  @ActionHandler(ACTION_OPEN_VALUES)
  void openValues(UUID viewUuid, UiActionRequest request);

}
