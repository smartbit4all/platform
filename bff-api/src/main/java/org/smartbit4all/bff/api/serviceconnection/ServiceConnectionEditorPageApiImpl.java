package org.smartbit4all.bff.api.serviceconnection;

import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.KeyValuePair;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.utils.BffUtilsApi;
import org.smartbit4all.domain.service.dataset.TableDataApi;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceConnectionEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements ServiceConnectionEditorPageApi {

  private static final List<String> ORDERED_COLUMNS =
      Arrays.asList(KeyValuePair.KEY, KeyValuePair.VALUE);

  @Autowired
  GridModelApi gridModelApi;
  @Autowired
  InvocationApi invocationApi;
  @Autowired
  TableDataApi tableDataApi;
  @Autowired
  LocaleSettingApi localeSettingApi;
  @Autowired
  BffUtilsApi bffUtilsApi;

  @Override
  public Object initModel(View view) {
    ServiceConnection convertedModel =
        objectApi.asType(ServiceConnection.class, super.initModel(view));
    if (convertedModel.getParameters() == null) {
      convertedModel.parameters(new LinkedHashMap<>());
    }
    initGrid(view.getUuid());
    setGridData(view.getUuid(), convertedModel);
    UiActions.add(view,
        new UiAction().code(ADD_PARAMETER).model(true).toolbar(GRID_ID + UiActions.TOOLBAR_SUFFIX));
    return convertedModel;
  }

  private void initGrid(UUID viewUuid) {
    GridModel gridModel =
        gridModelApi.createGridModel(KeyValuePair.class, ORDERED_COLUMNS,
            GRID_ID);
    gridModel.setPageSize(5);
    gridModel.setPageSizeOptions(Arrays.asList(5, 10));
    gridModelApi.initGridInView(viewUuid, GRID_ID, gridModel);
    gridModelApi.addGridPageCallback(viewUuid, GRID_ID,
        invocationApi.builder(ServiceConnectionEditorPageApi.class)
            .build(a -> a.onGridPageRender(null)));
  }

  private void setGridData(UUID viewUuid, ServiceConnection serviceConnection) {
    List<KeyValuePair> gridData = serviceConnection.getParameters().entrySet().stream()
        .map(e -> new KeyValuePair().key(e.getKey()).value(e.getValue())).collect(toList());
    gridModelApi.setData(viewUuid, GRID_ID, KeyValuePair.class, gridData);
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage) {
    gridPage.getRows()
        .forEach(row -> row.actions(Arrays.asList(new UiAction().code(EDIT_PARAMETER).submit(true),
            new UiAction().code(DELETE_PARAMETER).submit(true))));
    return gridPage;
  }

  @Override
  public void addParameter(UUID viewUuid, UiActionRequest request) {
    setModel(viewUuid, extractClientModel(request));
    bffUtilsApi.showMapEntryEditor(viewUuid, GRID_ID, new KeyValuePair(), true,
        saveInvocationRequest(viewUuid));
  }

  @Override
  public void editParameter(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    ServiceConnection serviceConnection =
        objectApi.asType(ServiceConnection.class, extractClientModel(request));
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    String key = GridModels.getValueFromGridRow(gridModel, nodeId, KeyValuePair.KEY).toString();
    Object value = serviceConnection.getParameters().get(key);
    bffUtilsApi.showMapEntryEditor(viewUuid, GRID_ID, new KeyValuePair().key(key).value(value),
        false, saveInvocationRequest(viewUuid));
    setModel(viewUuid, serviceConnection);
  }

  private InvocationRequest saveInvocationRequest(UUID viewUuid) {
    return invocationApi.builder(ServiceConnectionEditorPageApi.class)
        .build(api -> api.saveParameterCallback(null, null, viewUuid));
  }

  @Override
  public void removeParameter(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    ServiceConnection serviceConnection =
        objectApi.asType(ServiceConnection.class, extractClientModel(request));
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    String key = GridModels.getValueFromGridRow(gridModel, nodeId, KeyValuePair.KEY).toString();
    serviceConnection.getParameters().remove(key);
    setModel(viewUuid, serviceConnection);
    setGridData(viewUuid, serviceConnection);
  }

  @Override
  public void saveParameterCallback(UUID dialogUuid, UiActionRequest request, UUID viewUuid) {
    KeyValuePair entry = actionRequestHelper(request).get(UiActions.MODEL, KeyValuePair.class);
    ServiceConnection model = objectApi.asType(ServiceConnection.class, getModel(viewUuid));
    model.putParametersItem(entry.getKey(), entry.getValue());
    setModel(viewUuid, model);
    setGridData(viewUuid, model);
    viewApi.closeView(dialogUuid);
  }

}
