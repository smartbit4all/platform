package org.smartbit4all.bff.api.object;

import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.OBJECT_PROPERTY_DESCRIPTOR_PAGE)
public interface ObjectDescriptorEditorPageApi extends MDMEntryEditPageApi {

  String ADD_PROPERTY = "ADD_PROPERTY";
  String EDIT_PROPERTY = "EDIT_PROPERTY";
  String DELETE_PROPERTY = "DELETE_PROPERTY";

  String DEFINITION_GRID = "definitionGrid";
  String EXTENSION_GRID = "extensionGrid";

  GridPage onGridPageRender(GridPage gridPage);

  @ActionHandler(ADD_PROPERTY)
  void addProperty(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(widget = EXTENSION_GRID, value = EDIT_PROPERTY)
  void editProperty(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  @WidgetActionHandler(widget = EXTENSION_GRID, value = DELETE_PROPERTY)
  void removeProperty(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  void savePropertyCallback(UUID viewUuid, ObjectPropertyDescriptor propertyDescriptor);

}
