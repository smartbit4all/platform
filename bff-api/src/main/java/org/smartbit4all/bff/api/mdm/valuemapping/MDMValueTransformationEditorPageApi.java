package org.smartbit4all.bff.api.mdm.valuemapping;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(PlatformViewNames.VALUE_TRANSFORMATION_EDITOR)
public interface MDMValueTransformationEditorPageApi extends MDMEntryEditPageApi {
  String SCHEMA = "bffValueTransformation";
  static final String GRID_TRANSFORMATION_MAPPING = "GRID_TRANSFORMATION_MAPPING";

  static final String ACTION_ADD_VALUE_TRANSFORMATION_MAPPING =
      "ACTION_ADD_VALUE_TRANSFORMATION_MAPPING";

  static final String ACTION_EDIT_VALUE_TRANSFORMATION_MAPPING =
      "ACTION_EDIT_VALUE_TRANSFORMATION_MAPPING";

  static final String ACTION_REMOVE_VALUE_TRANSFORMATION_MAPPING =
      "ACTION_REMOVE_VALUE_TRANSFORMATION_MAPPING";

  @ActionHandler(ACTION_ADD_VALUE_TRANSFORMATION_MAPPING)
  void addMapping(UUID viewUuid, UiActionRequest request);

  void saveMapping(UUID dialogUuid, UiActionRequest request, UUID viewUuid, String gridId,
      String nodeId);

  @WidgetActionHandler(value = ACTION_EDIT_VALUE_TRANSFORMATION_MAPPING)
  void editMapping(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  @WidgetActionHandler(value = ACTION_REMOVE_VALUE_TRANSFORMATION_MAPPING)
  void removeMapping(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  GridPage onGridPageRender(GridPage gridPage, UUID viewUuid, String gridId);
}
