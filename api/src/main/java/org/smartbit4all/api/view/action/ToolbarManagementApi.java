package org.smartbit4all.api.view.action;

import java.util.List;
import org.smartbit4all.api.toolbar.bean.ToolbarDefinition;
import org.smartbit4all.api.view.bean.UiAction;

public interface ToolbarManagementApi {

  /**
   * Creates a list of actions based on the toolbar definition, and an original list of actions.
   *
   * @param toolbar
   * @param actions
   * @return
   */
  List<UiAction> getActionsForToolbar(ToolbarDefinition toolbar, List<UiAction> actions);

}
