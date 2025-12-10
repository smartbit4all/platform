package org.smartbit4all.bff.api.mdm.apikey;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.multiSelectCombobox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.selectionDefinition;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.api.security.bean.ApiKeyScope;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.bean.ClipboardData;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.Style;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApiImpl;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.sec.apikey.ApiKeyApi;
import org.smartbit4all.sec.apikey.ApiKeyConstants;
import org.smartbit4all.sec.apikey.ApiKeyInnerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class UserApiKeyEditorPageApiImpl extends MDMEntryEditPageApiImpl
    implements UserApiKeyEditorPageApi {
  @Autowired
  private SessionApi sessionApi;

  private static final String APIKEY_CLASS_NAME = ApiKey.class.getSimpleName();
  public static final String APIKEY_USERS_VALUE_SET = "ApiKeyUserValueSet";

  private static final String LABEL1 = "apikey.show-token.label1";
  private static final String LABEL2 = "apikey.show-token.label2";
  private static final String TOKEN = "TOKEN";
  static final String ACTION_COPY_TO_CLIPBOARD = "COPY_TO_CLIPBOARD";

  @Autowired
  protected LocaleSettingApi localeSettingApi;
  @Autowired
  protected ApiKeyInnerApi apiKeyInnerApi;
  @Autowired
  protected ValueSetApi valueSetApi;
  @Autowired
  protected InvocationApi invocationApi;

  @Override
  public Object initModel(View view) {
    putValueSetsIntoView(view);
    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, getLayout());
    view.constraint(getViewConstraint(view));
    return super.initModel(view);
  }

  private void putValueSetsIntoView(View view) {
    ValueSet scopeValueSet = valueSetApi.getValueSetWithValues(
        ApiKeyApi.SCHEMA,
        ApiKeyConstants.APIKEY_SCOPE,
        view.getBranchUri(),
        ApiKeyScope.NAME);
    view.putValueSetsItem(ApiKeyConstants.APIKEY_SCOPE, scopeValueSet);

    List<String> enabledScopeNames = parameters(view).getAsList(ENABLED_SCOPE_NAMES, String.class);
    if (!ObjectUtils.isEmpty(enabledScopeNames)) {
      scopeValueSet.getValueSetData().getValues()
          .removeIf(value -> !enabledScopeNames
              .contains(objectApi.asType(Value.class, value).getDisplayValue()));
    }
    view.constraint(getConstraints());
  }

  private ViewConstraint getConstraints() {
    ViewConstraint viewConstraint = new ViewConstraint().componentConstraints(Arrays.asList(

        new ComponentConstraint()
            .dataName(ApiKey.SCOPE)
            .enabled(true).visible(true).mandatory(true)));
    return viewConstraint;

  }

  private SmartComponentLayoutDefinition getLayout() {
    List<SmartWidgetDefinition> widgets = new ArrayList<>();

    SmartWidgetDefinition scopeMsCb = multiSelectCombobox(
        ApiKey.SCOPE,
        localeSettingApi.get(APIKEY_CLASS_NAME, ApiKey.SCOPE),
        selectionDefinition(ApiKeyConstants.APIKEY_SCOPE, Value.DISPLAY_VALUE));
    widgets.add(scopeMsCb);

    SmartWidgetDefinition expirationDtp = ObjectLayoutBuilder.datePicker(
        ApiKey.EXPIRATION,
        localeSettingApi.get(APIKEY_CLASS_NAME, ApiKey.EXPIRATION));
    widgets.add(expirationDtp);
    return form(LayoutDirection.VERTICAL, widgets.toArray(new SmartWidgetDefinition[0]));
  }

  private ViewConstraint getViewConstraint(View view) {
    boolean isCreation = view.getObjectUri() == null;
    return new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint()
            .dataName(ApiKey.USER)
            .mandatory(true)
            .enabled(isCreation),
        new ComponentConstraint()
            .dataName(ApiKey.SCOPE)
            .mandatory(false)
            .enabled(isCreation),
        new ComponentConstraint()
            .dataName(ApiKey.EXPIRATION)
            .mandatory(false)
            .enabled(true)));
  }

  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    ObjectMapHelper actionRequestHelper = actionRequestHelper(request);
    ApiKey model = actionRequestHelper.require(UiActions.MODEL, ApiKey.class);

    if (ObjectUtils.isEmpty(model.getScope())) {
      viewApi.showMessage(
          new MessageData().viewUuid(viewUuid).text(localeSettingApi.get("api.key.save.error.text"))
              .header(localeSettingApi.get("api.key.save.error.header")));
      return;
    }
    if (model.getUri() == null) {

      String token = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
      ObjectNode apiKeyNode =
          apiKeyInnerApi.createApiKey(objectApi.getLatestUri(sessionApi.getUserUri()), token,
              model.getExpiration(), model.getScope());

      request.getParams().put(UiActions.MODEL, apiKeyNode.getObject(ApiKey.class));
      super.performSave(viewUuid, request);
      viewApi.showView(createApiKeyCreatedPage(token));
    } else {
      super.performSave(viewUuid, request);
    }
  }

  private String getLocalText(String code) {
    return localeSettingApi.get(this.getClass().getSimpleName(), code);
  }

  private View createApiKeyCreatedPage(String token) {
    return new View()
        .viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .putParametersItem(GenericPageApi.PARAM_MODEL, Collections.singletonMap(TOKEN, token))
        .putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT,
            form(LayoutDirection.VERTICAL,
                label(LABEL1, getLocalText(LABEL1)),
                textfield(TOKEN, StringConstant.EMPTY)
                    .toolbarId(TOKEN + UiActions.TOOLBAR_SUFFIX),
                label(LABEL2, getLocalText(LABEL2))))
        .style(new Style()
            .putStyleItem("text-align-last", "center")
            .putStyleItem("align-items", "center"))
        .constraint(new ViewConstraint().addComponentConstraintsItem(
            new ComponentConstraint()
                .dataName(TOKEN)
                .enabled(false)))
        .actions(UiActions.builder()
            .add(new UiAction()
                .code(GenericPageApi.ACTION_CLOSE_VIEW)
                .descriptor(new UiActionDescriptor()
                    .title("OK")
                    .type(UiActionButtonType.FLAT)
                    .feedbackType(UiActionFeedbackType.NONE)))
            .add(new UiAction()
                .code(ACTION_COPY_TO_CLIPBOARD)
                .model(false)
                .toolbar(TOKEN + UiActions.TOOLBAR_SUFFIX)
                .descriptor(new UiActionDescriptor()
                    .icon("content_cut")
                    .title(" ")
                    .type(UiActionButtonType.ICON)
                    .color(UiActions.Color.ACCENT)
                    .feedbackType(UiActionFeedbackType.SNACKBAR)
                    .feedbackText(getLocalText("copy-feedback"))))
            .build())
        .eventHandlers(Arrays.asList(
            new ViewEventHandler()
                .viewEventType(ViewEventTypeEnum.INSTEAD)
                .addPathItem(ViewEventApi.ACTION)
                .addPathItem(ACTION_COPY_TO_CLIPBOARD)
                .invocationRequest(invocationApi.builder(UserApiKeyEditorPageApi.class)
                    .build(api -> api.copyToClipboard(null, null, token)))));
  }

  @Override
  public void copyToClipboard(UUID viewUuid, UiActionRequest request, String token) {
    viewApi.copyToClipboard(new ClipboardData().text(token));
  }
}
