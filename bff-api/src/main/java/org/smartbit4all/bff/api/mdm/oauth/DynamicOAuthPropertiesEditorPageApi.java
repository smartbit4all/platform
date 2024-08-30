package org.smartbit4all.bff.api.mdm.oauth;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.DYNAMIC_OAUTH_PROPERTIES_EDITOR)
public interface DynamicOAuthPropertiesEditorPageApi extends MDMEntryEditPageApi {

  static final String GRID_USERPROP_MAPPING = "GRID_USERPROP_MAPPING";
  static final String GRID_ROLE_MAPPING = "GRID_ROLE_MAPPING";

  static final String ACTION_ADD_USERPROP_MAPPING = "ADD_USERPROP_MAPPING";
  static final String ACTION_ADD_ROLE_MAPPING = "ADD_ROLE_MAPPING";

  static final String ACTION_EDIT_USERPROP_MAPPING = "EDIT_USERPROP_MAPPING";
  static final String ACTION_EDIT_ROLE_MAPPING = "EDIT_ROLE_MAPPING";

  static final String ACTION_REMOVE_USERPROP_MAPPING = "REMOVE_USERPROP_MAPPING";
  static final String ACTION_REMOVE_ROLE_MAPPING = "REMOVE_ROLE_MAPPING";

  @ActionHandler({ACTION_ADD_USERPROP_MAPPING, ACTION_ADD_ROLE_MAPPING})
  void addMapping(UUID viewUuid, UiActionRequest request);

  void saveMapping(UUID dialogUuid, UiActionRequest request, UUID viewUuid, String gridId);

  @WidgetActionHandler(value = {ACTION_EDIT_USERPROP_MAPPING, ACTION_EDIT_ROLE_MAPPING})
  void editMapping(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  @WidgetActionHandler(value = {ACTION_REMOVE_ROLE_MAPPING, ACTION_REMOVE_USERPROP_MAPPING})
  void removeMapping(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  GridPage onGridPageRender(GridPage gridPage, UUID viewUuid, String gridId);

}
