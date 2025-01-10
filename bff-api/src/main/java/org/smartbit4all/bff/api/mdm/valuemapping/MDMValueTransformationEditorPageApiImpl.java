package org.smartbit4all.bff.api.mdm.valuemapping;

import static org.smartbit4all.api.view.UiActions.TOOLBAR_SUFFIX;
import static org.smartbit4all.core.object.ObjectLayoutApi.DEFAULT_LAYOUT;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.value.bean.ValueTransformationConfig;
import org.smartbit4all.api.value.bean.ValueTransformationConfigData;
import org.smartbit4all.api.value.bean.ValueTransformationKind;
import org.smartbit4all.api.value.bean.ValueTransformationMappingItem;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.utils.BffUtilsApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMValueTransformationEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements MDMValueTransformationEditorPageApi {

  @Autowired
  protected LocaleSettingApi localeSettingApi;
  @Autowired
  protected GridModelApi gridModelApi;
  @Autowired
  protected ValueSetApi valueSetApi;
  @Autowired
  protected InvocationApi invocationApi;
  @Autowired
  protected BffUtilsApi bffUtilsApi;

  private static final List<String> ORDERED_COLUMNS =
      Arrays.asList(ValueTransformationMappingItem.SOURCE_VALUE,
          ValueTransformationMappingItem.TARGET_VALUE);

  @Override
  public Object initModel(View view) {
    super.initModel(view);
    ValueTransformationConfig valueTransformationConfig =
        objectApi.asType(ValueTransformationConfig.class, super.initModel(view));

    if (valueTransformationConfig.getData() == null) {
      valueTransformationConfig
          .data(new ValueTransformationConfigData().mappings(new ArrayList<>()));
    }
    putValueSetsIntoView(view);
    setConstraints(view);

    view.putComponentLayoutsItem("default", getLayout(GRID_TRANSFORMATION_MAPPING));
    createGridModel(view.getUuid(), GRID_TRANSFORMATION_MAPPING);

    setGridData(view.getUuid(), GRID_TRANSFORMATION_MAPPING, valueTransformationConfig);
    UiActions.add(view, uiActionModelTrue(ACTION_ADD_VALUE_TRANSFORMATION_MAPPING)
        .toolbar(GRID_TRANSFORMATION_MAPPING + TOOLBAR_SUFFIX)
        .descriptor(new UiActionDescriptor().title(localeSettingApi.get("addNewEntryItem"))));

    return valueTransformationConfig;
  }

  private SmartComponentLayoutDefinition getLayout(String transformationMapId) {
    return ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            ObjectLayoutBuilder.textfield(
                widgetKey(ValueTransformationConfig.DATA, ValueTransformationConfigData.NAME),
                localeSettingApi.get(ValueTransformationConfigData.class.getName(),
                    ValueTransformationConfigData.NAME)),
            ObjectLayoutBuilder.combobox(
                widgetKey(ValueTransformationConfig.DATA, ValueTransformationConfigData.KIND),
                localeSettingApi.get(ValueTransformationConfigData.class.getName(),
                    ValueTransformationConfigData.KIND),
                ObjectLayoutBuilder.selectionDefinition(ValueTransformationKind.class.getName(),
                    Value.DISPLAY_VALUE))))
        .addComponentsItem(grid(transformationMapId));
  }


  private void putValueSetsIntoView(View view) {

    ValueTransformationKind[] transformationKinds = ValueTransformationKind.values();

    List<Object> kindList = new ArrayList<>();
    Arrays.asList(transformationKinds)
        .forEach(kind -> kindList
            .add(new GenericValue()
                .name(localeSettingApi.get(kind))
                .code(kind.getValue())));

    ValueSetData kindValueSet = new ValueSetData()
        .keyProperty(GenericValue.CODE)
        .values(kindList);

    view.putValueSetsItem(ValueTransformationKind.class.getName(), new ValueSet()
        .valueSetData(kindValueSet)
        .valueSetName(ValueTransformationKind.class.getName()));

  }

  private void setConstraints(View view) {
    ViewConstraint viewConstraint = new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint()
            .dataName(widgetKey(ValueTransformationConfig.DATA, ValueTransformationConfigData.NAME))
            .enabled(true).mandatory(true).visible(true),
        new ComponentConstraint()
            .dataName(widgetKey(ValueTransformationConfig.DATA, ValueTransformationConfigData.KIND))
            .enabled(true).mandatory(true).visible(true)));
    view.constraint(viewConstraint);
  }

  private void createGridModel(UUID viewUuid, String gridId) {
    GridModel gridModel = gridModelApi.createGridModel(
        ValueTransformationMappingItem.class,
        ORDERED_COLUMNS, gridId);
    gridModel.getView().getDescriptor().showEditColumns(false);
    gridModel.paginator(true);
    gridModelApi.initGridInView(viewUuid, gridId, gridModel);
    gridModelApi.addGridPageCallback(viewUuid, gridId,
        invocationApi
            .builder(MDMValueTransformationEditorPageApi.class)
            .build(api -> api.onGridPageRender(null, viewUuid, gridId)));
  }

  private void setGridData(UUID viewUuid, String gridId, ValueTransformationConfig model) {
    List<ValueTransformationMappingItem> entrySet;
    if (GRID_TRANSFORMATION_MAPPING.equals(gridId)) {
      entrySet = model.getData().getMappings();
    } else {
      entrySet = Collections.emptyList();
    }
    List<ValueTransformationMappingItem> gridData = entrySet;
    gridModelApi.setData(viewUuid, gridId, ValueTransformationMappingItem.class, gridData);
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage, UUID viewUuid, String gridId) {
    List<UiAction> actions;
    if (GRID_TRANSFORMATION_MAPPING.equals(gridId)) {
      actions = Arrays.asList(uiActionModelTrue(ACTION_EDIT_VALUE_TRANSFORMATION_MAPPING)
          .descriptor(new UiActionDescriptor().title(localeSettingApi.get("editEntryItem"))),
          uiActionModelTrue(ACTION_REMOVE_VALUE_TRANSFORMATION_MAPPING)
              .descriptor(new UiActionDescriptor().title(localeSettingApi.get("deleteEntryItem"))));
    } else {
      actions = Collections.emptyList();
    }
    gridPage.getRows().forEach(row -> row.actions(actions));
    return gridPage;
  }

  private UiAction uiActionModelTrue(String code) {
    return new UiAction().code(code).model(true);
  }

  @Override
  public void addMapping(UUID viewUuid, UiActionRequest request) {
    setModel(viewUuid, extractClientModel(request));
    showMapEntryEditor(viewUuid, GRID_TRANSFORMATION_MAPPING,
        new ValueTransformationMappingItem(), true,
        saveMappingRequest(viewUuid, GRID_TRANSFORMATION_MAPPING, null));
  }

  private InvocationRequest saveMappingRequest(UUID viewUuid, String gridId, String nodeId) {
    return invocationApi.builder(MDMValueTransformationEditorPageApi.class)
        .build(api -> api.saveMapping(null, null, viewUuid, gridId, nodeId));
  }

  @Override
  public void saveMapping(UUID dialogUuid, UiActionRequest request, UUID viewUuid, String gridId,
      String nodeId) {
    ValueTransformationMappingItem entry =
        actionRequestHelper(request).get(UiActions.MODEL, ValueTransformationMappingItem.class);
    ValueTransformationConfig model =
        objectApi.asType(ValueTransformationConfig.class, getModel(viewUuid));
    if (nodeId == null) {
      model.getData().addMappingsItem(entry);
    } else {
      model.getData().getMappings().get(Integer.parseInt(nodeId))
          .sourceValue(entry.getSourceValue()).targetValue(entry.getTargetValue());
    }

    setGridData(viewUuid, gridId, model);
    setModel(viewUuid, model);
    viewApi.closeView(dialogUuid);
  }

  @Override
  public void editMapping(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    Object model = extractClientModel(request);
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);

    String key = GridModels
        .getValueFromGridRow(gridModel, nodeId, ValueTransformationMappingItem.SOURCE_VALUE)
        .toString();
    Object value = GridModels.getValueFromGridRow(gridModel, nodeId,
        ValueTransformationMappingItem.TARGET_VALUE);

    showMapEntryEditor(viewUuid, widgetId,
        new ValueTransformationMappingItem().sourceValue(key).targetValue(value),
        false, saveMappingRequest(viewUuid, widgetId, nodeId));
    setModel(viewUuid, model);
  }

  @Override
  public void removeMapping(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    ValueTransformationConfig model =
        objectApi.asType(ValueTransformationConfig.class, extractClientModel(request));

    model.getData().getMappings().remove(Integer.parseInt(nodeId));

    setModel(viewUuid, model);
    setGridData(viewUuid, widgetId, model);
  }

  public void showMapEntryEditor(UUID viewUuid, String gridId,
      ValueTransformationMappingItem pageModel,
      boolean keyEditable, InvocationRequest saveRequest) {
    SmartComponentLayoutDefinition layout = ObjectLayoutBuilder.form(LayoutDirection.VERTICAL,
        ObjectLayoutBuilder.textfield(ValueTransformationMappingItem.SOURCE_VALUE,
            localeSettingApi.get(gridId, ValueTransformationMappingItem.SOURCE_VALUE)),
        ObjectLayoutBuilder.textfield(ValueTransformationMappingItem.TARGET_VALUE,
            localeSettingApi.get(gridId, ValueTransformationMappingItem.TARGET_VALUE)));

    List<UiAction> dialogActions = Arrays.asList(new UiAction().code("SAVE").submit(true),
        new UiAction().code(GenericPageApi.ACTION_CLOSE_VIEW)
            .descriptor(new UiActionDescriptor().title("Mégse")));
    viewApi.showView(new View()
        .viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .putParametersItem(GenericPageApi.PARAM_MODEL, pageModel)
        .putComponentLayoutsItem(DEFAULT_LAYOUT,
            layout)
        .constraint(new ViewConstraint()
            .componentConstraints(
                Arrays.asList(
                    new ComponentConstraint().dataName(ValueTransformationMappingItem.SOURCE_VALUE)
                        .mandatory(true)
                        .enabled(keyEditable),
                    new ComponentConstraint().dataName(ValueTransformationMappingItem.TARGET_VALUE)
                        .mandatory(true))))
        .actions(dialogActions)
        .eventHandlers(Arrays.asList(new ViewEventHandler()
            .viewEventType(ViewEventTypeEnum.INSTEAD)
            .addPathItem(ViewEventApi.ACTION)
            .addPathItem("SAVE")
            .invocationRequest(saveRequest))));
  }

}
