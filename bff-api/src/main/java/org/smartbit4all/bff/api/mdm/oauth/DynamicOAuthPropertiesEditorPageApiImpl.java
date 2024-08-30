package org.smartbit4all.bff.api.mdm.oauth;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.security.bean.OAuthClientProperties;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridItem;
import org.smartbit4all.sec.apikey.ApiKeyInnerApi;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOAuthPropertiesEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements DynamicOAuthPropertiesEditorPageApi {

  private static final String OAUTHPROPERTY_CLASS_NAME =
      OAuthClientProperties.class.getSimpleName();

  private static final String LABEL1 = "apikey.show-token.label1";

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

  @Override
  public Object initModel(View view) {


    // view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, getLayout());
    view.constraint(getViewConstraint(view));
    return super.initModel(view);
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

  private ViewConstraint getViewConstraint(View view) {
    return new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint()
            .dataName(OAuthClientProperties.REGISTRATION_ID)
            .mandatory(true)));
  }

  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    super.performSave(viewUuid, request);
  }

  private String getLocalText(String code) {
    return localeSettingApi.get(this.getClass().getSimpleName(), code);
  }

  protected void initGridInView(View view, String gridId) {
    UUID viewUuid = view.getUuid();
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    if (gridModel == null) {
      createGridModel(viewUuid, gridId);
      view.addActionsItem(new UiAction()
          // .code(ADD_SUBJECT)
          .model(true)
          .toolbar(gridId + UiActions.TOOLBAR_SUFFIX)
          .identifier(gridId)
          .descriptor(new UiActionDescriptor()
              .icon("Plus")
              .title(" ")
              .type(UiActionButtonType.ICON)
              .color(UiActions.Color.ACCENT)));
    }
  }


  private GridModel createGridModel(UUID viewUuid, String gridId) {
    List<String> columns = Arrays.asList(Value.CODE, Value.DISPLAY_VALUE);
    GridModel gridModel = gridModelApi.createGridModel(
        AclGridItem.class,
        columns);

    gridModel.getView().getDescriptor().showEditColumns(false);
    gridModel.paginator(true);
    gridModelApi.initGridInView(viewUuid, gridId, gridModel);

    // gridModelApi.addGridPageCallback(viewUuid, gridId,
    // invocationApi
    // .builder(DynamicOAuthPropertiesEditorPageApi.class)
    // .build(api -> api.addGridActions(null, viewUuid, gridId)));

    return gridModel;
  }

}
