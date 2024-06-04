package org.smartbit4all.bff.api.serviceconnection;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = "MDMServiceConnectionEditor")
public interface ServiceConnectionEditorPageApi extends MDMEntryEditPageApi {

  String ADD_PARAMETER = "ADD_PARAMETER";
  String EDIT_PARAMETER = "EDIT_PARAMETER";
  String DELETE_PARAMETER = "DELETE_PARAMETER";

  String GRID_ID = "parameterGrid";

  GridPage onGridPageRender(GridPage gridPage);

  @ActionHandler(ADD_PARAMETER)
  void addParameter(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(widget = GRID_ID, value = EDIT_PARAMETER)
  void editParameter(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  @WidgetActionHandler(widget = GRID_ID, value = DELETE_PARAMETER)
  void removeParameter(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  void saveParameterCallback(UUID dialogUuid, UiActionRequest request, UUID viewUuid);

}
