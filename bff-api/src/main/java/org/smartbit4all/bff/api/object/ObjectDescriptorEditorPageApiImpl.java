package org.smartbit4all.bff.api.object;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.serviceconnection.ServiceConnectionEditorPageApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectDescriptorEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements ObjectDescriptorEditorPageApi {

  private static final String OBJECT_DESCRIPTOR = ObjectDescriptor.class.getSimpleName();

  private static final List<String> ORDERED_COLUMNS =
      List.of(ObjectPropertyDescriptor.PROPERTY_NAME);

  @Autowired
  private GridModelApi gridModelApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public ObjectDescriptor initModel(View view) {
    ObjectDescriptor model =
        objectApi.asType(ObjectDescriptor.class, super.initModel(view));
    if (model.getDefinitionProperties() == null) {
      model.setDefinitionProperties(new LinkedHashMap<>());
    }
    if (model.getExtensionProperties() == null) {
      model.setExtensionProperties(new LinkedHashMap<>());
    }
    initGrid(view.getUuid(), DEFINITION_GRID);
    setGridData(view.getUuid(), DEFINITION_GRID, model.getDefinitionProperties());
    initGrid(view.getUuid(), EXTENSION_GRID);
    setGridData(view.getUuid(), EXTENSION_GRID, model.getExtensionProperties());
    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT,
        ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
            .addComponentsItem(form(LayoutDirection.VERTICAL, ObjectLayoutBuilder.textfield(
                ObjectDescriptor.NAME,
                localeSettingApi.get(OBJECT_DESCRIPTOR, ObjectDescriptor.NAME))))
            .addComponentsItem(grid(DEFINITION_GRID))
            .addComponentsItem(grid(EXTENSION_GRID)));

    UiActions.add(view,
        new UiAction().code(ADD_PROPERTY).model(true)
            .toolbar(EXTENSION_GRID + UiActions.TOOLBAR_SUFFIX));
    // TODO create an action that lets the user select a type
    return model;
  }

  private void initGrid(UUID viewUuid, String gridId) {
    GridModel gridModel =
        gridModelApi.createGridModel(ObjectPropertyDescriptor.class, ORDERED_COLUMNS,
            gridId);
    gridModel.setPageSize(5);
    gridModel.setPageSizeOptions(Arrays.asList(5, 10));
    gridModelApi.initGridInView(viewUuid, gridId, gridModel);
    if (EXTENSION_GRID.equals(gridId)) {
      gridModelApi.addGridPageCallback(viewUuid, gridId,
          invocationApi.builder(ServiceConnectionEditorPageApi.class)
              .build(a -> a.onGridPageRender(null)));
    }
  }

  private void setGridData(UUID viewUuid, String gridId, Map<String, URI> data) {
    List<ObjectPropertyDescriptor> list = data.values().stream()
        .map(u -> objectApi.loadLatest(u).getObject(ObjectPropertyDescriptor.class))
        .collect(Collectors.toList());
    gridModelApi.setData(viewUuid, gridId, ObjectPropertyDescriptor.class, list);
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage) {
    gridPage.getRows()
        .forEach(row -> row.actions(List.of(new UiAction().code(EDIT_PROPERTY).model(true),
            new UiAction().code(DELETE_PROPERTY).model(true))));
    return gridPage;
  }

  @Override
  public void addProperty(UUID viewUuid, UiActionRequest request) {
    setModel(viewUuid, extractClientModel(request));
    showPropertyDescriptorPage(viewUuid, new ObjectPropertyDescriptor());
  }

  @Override
  public void editProperty(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    setModel(viewUuid, extractClientModel(request));
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    URI uri = objectApi.asType(URI.class,
        GridModels.getValueFromGridRow(gridModel, nodeId, ObjectPropertyDescriptor.URI));
    ObjectPropertyDescriptor descriptor =
        objectApi.loadLatest(uri).getObject(ObjectPropertyDescriptor.class);
    showPropertyDescriptorPage(viewUuid, descriptor);

  }

  private void showPropertyDescriptorPage(UUID viewUuid,
      ObjectPropertyDescriptor model) {
    InvocationRequest callback = invocationApi.builder(ObjectDescriptorEditorPageApi.class)
        .build(api -> api.savePropertyCallback(viewUuid, null));
    View view = new View()
        .viewName(PlatformViewNames.OBJECT_PROPERTY_DESCRIPTOR_PAGE)
        .type(ViewType.DIALOG)
        .putParametersItem(ObjectPropertyDescriptorPageApi.PARAM_MODEL, model)
        .putCallbacksItem(ObjectPropertyDescriptorPageApi.CALLBACK_SAVE, callback);
    viewApi.showView(view);
  }

  @Override
  public void removeProperty(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    ObjectDescriptor model = objectApi.asType(ObjectDescriptor.class, extractClientModel(request));
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    String propertyName = objectApi.asType(String.class,
        GridModels.getValueFromGridRow(gridModel, nodeId, ObjectPropertyDescriptor.PROPERTY_NAME));
    Map<String, URI> propertyMap =
        DEFINITION_GRID.equals(widgetId) ? model.getDefinitionProperties()
            : model.getExtensionProperties();
    propertyMap.remove(propertyName);
    setModel(viewUuid, model);
  }

  @Override
  public void savePropertyCallback(UUID viewUuid, ObjectPropertyDescriptor propertyDescriptor) {
    ObjectDescriptor model = objectApi.asType(ObjectDescriptor.class, getModel(viewUuid));
    Map<String, URI> propertyMap = model.getExtensionProperties();

    URI propertyDescriptorUri;
    if (propertyDescriptor.getUri() == null) {
      propertyDescriptorUri =
          objectApi.saveAsNew(MasterDataManagementApi.SCHEMA, propertyDescriptor);
    } else {
      ObjectNode descriptorNode = objectApi.loadLatest(propertyDescriptor.getUri());
      descriptorNode
          .setValuesWithReference(descriptorNode.getDefinition().toMap(propertyDescriptor));
      propertyDescriptorUri = objectApi.save(descriptorNode);
    }
    propertyMap.put(propertyDescriptor.getPropertyName(), propertyDescriptorUri);
    setModel(viewUuid, model);
    setGridData(viewUuid, EXTENSION_GRID, propertyMap);
  }

}
