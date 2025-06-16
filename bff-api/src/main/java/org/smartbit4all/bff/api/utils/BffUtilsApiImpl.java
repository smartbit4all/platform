package org.smartbit4all.bff.api.utils;

import static org.smartbit4all.core.object.ObjectLayoutApi.DEFAULT_LAYOUT;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.KeyValuePair;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class BffUtilsApiImpl implements BffUtilsApi {

  @Autowired
  private ViewApi viewApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;
  @Autowired
  InvocationApi invocationApi;

  @Override
  public void showMapEntryEditor(UUID viewUuid, String gridId, KeyValuePair pageModel,
      boolean keyEditable, InvocationRequest saveRequest) {
    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        ObjectLayoutBuilder.textfield(KeyValuePair.KEY,
            localeSettingApi.get(gridId, KeyValuePair.KEY)),
        ObjectLayoutBuilder.textfield(KeyValuePair.VALUE,
            localeSettingApi.get(gridId, KeyValuePair.VALUE)));
    List<UiAction> dialogActions = Arrays.asList(new UiAction().code("SAVE").submit(true),
        new UiAction().code(GenericPageApi.ACTION_CLOSE_VIEW));
    viewApi.showView(new View()
        .viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .putParametersItem(GenericPageApi.PARAM_MODEL, pageModel)
        .putComponentLayoutsItem(DEFAULT_LAYOUT,
            layout)
        .constraint(new ViewConstraint()
            .componentConstraints(
                Arrays.asList(
                    new ComponentConstraint().dataName(KeyValuePair.KEY).mandatory(true)
                        .enabled(keyEditable),
                    new ComponentConstraint().dataName(KeyValuePair.VALUE).mandatory(true))))
        .actions(dialogActions)
        .eventHandlers(Arrays.asList(new ViewEventHandler()
            .viewEventType(ViewEventTypeEnum.INSTEAD)
            .addPathItem(ViewEventApi.ACTION)
            .addPathItem("SAVE")
            .invocationRequest(saveRequest))));
  }


  @Override
  public void showMapEntryEditor(UUID viewUuid, String gridId, GenericValue pageModel,
      boolean keyEditable, InvocationRequest saveRequest) {
    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        ObjectLayoutBuilder.textfield(GenericValue.CODE,
            localeSettingApi.get(gridId, GenericValue.CODE)),
        ObjectLayoutBuilder.textfield(GenericValue.NAME,
            localeSettingApi.get(gridId, GenericValue.NAME)));
    List<UiAction> dialogActions = Arrays.asList(new UiAction().code("SAVE").submit(true),
        new UiAction().code(GenericPageApi.ACTION_CLOSE_VIEW));
    viewApi.showView(new View()
        .viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .putParametersItem(GenericPageApi.PARAM_MODEL, pageModel)
        .putComponentLayoutsItem(DEFAULT_LAYOUT,
            layout)
        .constraint(new ViewConstraint()
            .componentConstraints(
                Arrays.asList(
                    new ComponentConstraint().dataName(GenericValue.CODE).mandatory(true)
                        .enabled(keyEditable),
                    new ComponentConstraint().dataName(GenericValue.NAME).mandatory(true))))
        .actions(dialogActions)
        .eventHandlers(Arrays.asList(new ViewEventHandler()
            .viewEventType(ViewEventTypeEnum.INSTEAD)
            .addPathItem(ViewEventApi.ACTION)
            .addPathItem("SAVE")
            .invocationRequest(saveRequest))));

  }

  @Override
  public <T> T getValueFromGridRow(UUID viewUuid, String widgetId, String nodeId, String key,
      Class<T> clazz) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    return objectApi
        .asType(clazz,
            GridModels.getValueFromGridRow(gridModel, nodeId, key));
  }

}
