package org.smartbit4all.bff.api.mdm.oauth;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.api.view.UiActions.TOOLBAR_SUFFIX;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.KeyValuePair;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.utils.BffUtilsApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.sec.apikey.ApiKeyInnerApi;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOAuthPropertiesEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements DynamicOAuthPropertiesEditorPageApi {

  private static final String OAUTHPROPERTY_CLASS_NAME =
      OAuthClientProperties.class.getSimpleName();

  private static final List<String> ORDERED_COLUMNS =
      Arrays.asList(KeyValuePair.KEY, KeyValuePair.VALUE);

  @Autowired
  protected LocaleSettingApi localeSettingApi;
  @Autowired
  protected ApiKeyInnerApi apiKeyInnerApi;
  @Autowired
  protected ValueSetApi valueSetApi;
  @Autowired
  protected InvocationApi invocationApi;
  @Autowired
  protected GridModelApi gridModelApi;
  @Autowired
  protected BffUtilsApi bffUtilsApi;

  @Override
  public Object initModel(View view) {
    OAuthClientProperties model =
        objectApi.asType(OAuthClientProperties.class, super.initModel(view));
    if (model.getUserParameterMapping() == null) {
      model.userParameterMapping(new LinkedHashMap<>());
    }
    if (model.getRoleMapping() == null) {
      model.roleMapping(new LinkedHashMap<>());
    }
    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT,
        getLayout(GRID_USERPROP_MAPPING, GRID_ROLE_MAPPING));
    view.constraint(getViewConstraint());
    createGridModel(view.getUuid(), GRID_USERPROP_MAPPING);
    setGridData(view.getUuid(), GRID_USERPROP_MAPPING, model);
    createGridModel(view.getUuid(), GRID_ROLE_MAPPING);
    setGridData(view.getUuid(), GRID_ROLE_MAPPING, model);
    UiActions.add(view, uiActionModelTrue(ACTION_ADD_USERPROP_MAPPING)
        .toolbar(GRID_USERPROP_MAPPING + TOOLBAR_SUFFIX));
    UiActions.add(view,
        uiActionModelTrue(ACTION_ADD_ROLE_MAPPING)
            .toolbar(GRID_ROLE_MAPPING + TOOLBAR_SUFFIX));
    return model;
  }

  private SmartComponentLayoutDefinition getLayout(String userPropMapGridId, String roleMapGridId) {
    return container(LayoutDirection.VERTICAL)
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            textField(OAuthClientProperties.REGISTRATION_ID),
            textField(OAuthClientProperties.CLIENT_ID),
            textField(OAuthClientProperties.CLIENT_SECRET),
            textField(OAuthClientProperties.CLIENT_NAME),
            textField(OAuthClientProperties.AUTHORIZATION_URI),
            textField(OAuthClientProperties.TOKEN_URI),
            textField(OAuthClientProperties.USER_INFO_URI),
            textField(OAuthClientProperties.JWK_SET_URI),
            textField(OAuthClientProperties.ISSUER_URI),
            textField(OAuthClientProperties.REDIRECT_URI),
            textField(OAuthClientProperties.SCOPE),
            textField(OAuthClientProperties.AUTHORIZATION_GRANT_TYPE),
            textField(OAuthClientProperties.USER_NAME_ATTRIBUTE),
            textField(OAuthClientProperties.USER_INFO_AUTHENTICATION_METHOD),
            textField(OAuthClientProperties.COMMON_PROVIDER),
            textField(OAuthClientProperties.LOGOUT_OIDC_ENABLED),
            textField(OAuthClientProperties.LOGOUT_END_SESSION_ENDPOINT),
            textField(OAuthClientProperties.LOGOUT_REDIRECT_PATH),
            textField(OAuthClientProperties.LABEL),
            textField(OAuthClientProperties.LOGO),
            label(null, localeSettingApi.get(OAUTHPROPERTY_CLASS_NAME, userPropMapGridId))))
        .addComponentsItem(grid(userPropMapGridId))
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            label(null, localeSettingApi.get(OAUTHPROPERTY_CLASS_NAME, roleMapGridId))))
        .addComponentsItem(grid(roleMapGridId));
  }

  private SmartWidgetDefinition textField(String key) {
    return textfield(key, localeSettingApi.get(OAUTHPROPERTY_CLASS_NAME, key));
  }

  private ViewConstraint getViewConstraint() {
    return new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint()
            .dataName(OAuthClientProperties.REGISTRATION_ID)
            .mandatory(true)));
  }

  private void createGridModel(UUID viewUuid, String gridId) {
    GridModel gridModel = gridModelApi.createGridModel(
        KeyValuePair.class,
        ORDERED_COLUMNS, gridId);
    gridModel.getView().getDescriptor().showEditColumns(false);
    gridModel.paginator(true);
    gridModel.setPageSize(5);
    gridModel.setPageSizeOptions(Arrays.asList(5, 10));
    gridModelApi.initGridInView(viewUuid, gridId, gridModel);
    gridModelApi.addGridPageCallback(viewUuid, gridId,
        invocationApi
            .builder(DynamicOAuthPropertiesEditorPageApi.class)
            .build(api -> api.onGridPageRender(null, viewUuid, gridId)));
  }

  private void setGridData(UUID viewUuid, String gridId, OAuthClientProperties model) {
    Set<Entry<String, String>> entrySet;
    if (GRID_USERPROP_MAPPING.equals(gridId)) {
      entrySet = model.getUserParameterMapping().entrySet();
    } else if (GRID_ROLE_MAPPING.equals(gridId)) {
      entrySet = model.getRoleMapping().entrySet();
    } else {
      entrySet = Collections.emptySet();
    }
    List<KeyValuePair> gridData = entrySet.stream()
        .map(e -> new KeyValuePair().key(e.getKey()).value(e.getValue())).collect(toList());
    gridModelApi.setData(viewUuid, gridId, KeyValuePair.class, gridData);
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage, UUID viewUuid, String gridId) {
    List<UiAction> actions;
    if (GRID_USERPROP_MAPPING.equals(gridId)) {
      actions = Arrays.asList(uiActionModelTrue(ACTION_EDIT_USERPROP_MAPPING),
          uiActionModelTrue(ACTION_REMOVE_USERPROP_MAPPING));
    } else if (GRID_ROLE_MAPPING.equals(gridId)) {
      actions = Arrays.asList(uiActionModelTrue(ACTION_EDIT_ROLE_MAPPING),
          uiActionModelTrue(ACTION_REMOVE_ROLE_MAPPING));
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
    String gridId = ACTION_ADD_USERPROP_MAPPING.equals(request.getCode()) ? GRID_USERPROP_MAPPING
        : GRID_ROLE_MAPPING;
    bffUtilsApi.showMapEntryEditor(viewUuid, gridId, new KeyValuePair(), true,
        saveMappingRequest(viewUuid, gridId));
  }

  @Override
  public void saveMapping(UUID dialogUuid, UiActionRequest request, UUID viewUuid,
      String gridId) {
    KeyValuePair entry = actionRequestHelper(request).get(UiActions.MODEL, KeyValuePair.class);
    OAuthClientProperties model = objectApi.asType(OAuthClientProperties.class, getModel(viewUuid));
    if (GRID_USERPROP_MAPPING.equals(gridId)) {
      model.putUserParameterMappingItem(entry.getKey(), entry.getValue().toString());
    } else if (GRID_ROLE_MAPPING.equals(gridId)) {
      model.putRoleMappingItem(entry.getKey(), entry.getValue().toString());
    }
    setGridData(viewUuid, gridId, model);
    setModel(viewUuid, model);
    viewApi.closeView(dialogUuid);
  }

  private InvocationRequest saveMappingRequest(UUID viewUuid, String gridId) {
    return invocationApi.builder(DynamicOAuthPropertiesEditorPageApi.class)
        .build(api -> api.saveMapping(null, null, viewUuid, gridId));
  }

  @Override
  public void editMapping(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    Object model = extractClientModel(request);
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    String key = GridModels.getValueFromGridRow(gridModel, nodeId, KeyValuePair.KEY).toString();
    Object value = GridModels.getValueFromGridRow(gridModel, nodeId, KeyValuePair.VALUE);
    bffUtilsApi.showMapEntryEditor(viewUuid, widgetId, new KeyValuePair().key(key).value(value),
        false, saveMappingRequest(viewUuid, widgetId));
    setModel(viewUuid, model);
  }

  @Override
  public void removeMapping(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    OAuthClientProperties model =
        objectApi.asType(OAuthClientProperties.class, extractClientModel(request));
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    String key = GridModels.getValueFromGridRow(gridModel, nodeId, KeyValuePair.KEY).toString();
    if (GRID_USERPROP_MAPPING.equals(widgetId)) {
      model.getUserParameterMapping().remove(key);
    } else if (GRID_ROLE_MAPPING.equals(widgetId)) {
      model.getRoleMapping().remove(key);
    }
    setModel(viewUuid, model);
    setGridData(viewUuid, widgetId, model);
  }

}
