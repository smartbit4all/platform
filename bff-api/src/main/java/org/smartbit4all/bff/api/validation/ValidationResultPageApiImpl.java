package org.smartbit4all.bff.api.validation;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.utility.StringConstant.DOT;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectValidationItem;
import org.smartbit4all.api.object.bean.ObjectValidationResult;
import org.smartbit4all.api.object.bean.ObjectValidationSeverity;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.validationresult.bean.ValidationItem;
import org.smartbit4all.bff.api.validationresult.bean.ValidationResultPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class ValidationResultPageApiImpl extends PageApiImpl<ValidationResultPageModel>
    implements ValidationResultPageApi {

  private static final String LOCALE_PREFIX = "ValidationResultPage";
  private static final String CONTINUE_OK = CONTINUE + DOT + ObjectValidationSeverity.OK;
  private static final String CONTINUE_WARNING = CONTINUE + DOT + ObjectValidationSeverity.WARNING;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Autowired
  private InvocationApi invocationApi;

  public ValidationResultPageApiImpl() {
    super(ValidationResultPageModel.class);
  }

  @Override
  public ValidationResultPageModel initModel(View view) {
    ObjectMapHelper parameters = parameters(view);
    ValidationResultPageModel validationResultPageModel =
        mapObjectValidationResult(parameters);
    Objects.requireNonNull(validationResultPageModel, "ValidationResultPageModel is not provided!");
    addUiActions(view, validationResultPageModel);

    return validationResultPageModel;
  }

  private ValidationResultPageModel mapObjectValidationResult(ObjectMapHelper parameters) {
    ObjectValidationResult result =
        parameters.get(VALIDATION_RESULT, ObjectValidationResult.class);
    return new ValidationResultPageModel().validationItems(
        result.getItems().stream().map(item -> new ValidationItem().severity(item.getSeverity())
            .message(getMessage(item))).collect(toList()));
  }

  private String getMessage(ObjectValidationItem item) {
    LangString message = item.getMessage();
    if (message == null) {
      return "";
    }
    return localeSettingApi.get(message);
  }

  @Override
  public void performContinue(UUID viewUuid, UiActionRequest request) {
    try {
      InvocationRequest callback = objectApi.asType(InvocationRequest.class,
          viewApi.getView(viewUuid).getCallbacks().get(VALIDATION_PAGE_INVOCATION_REQUEST));
      invocationApi.invoke(callback);
      viewApi.closeView(viewUuid);
    } catch (ApiNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void performCancel(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  protected void addUiActions(View view, ValidationResultPageModel model) {
    List<ValidationItem> items = model.getValidationItems();
    boolean hasAnyWarning = hasAny(items, ObjectValidationSeverity.WARNING);
    boolean hasAnyError = hasAny(items, ObjectValidationSeverity.ERROR);
    view.actions(
        UiActions.builder()
            .addIf(
                uiAction(OK, OK, UiActions.Color.PRIMARY),
                hasAnyError)
            .addIf(
                uiAction(CONTINUE, CONTINUE_WARNING, UiActions.Color.PRIMARY),
                !hasAnyError && hasAnyWarning)
            .addIf(
                uiAction(CONTINUE, CONTINUE_OK, UiActions.Color.PRIMARY),
                !hasAnyError && !hasAnyWarning)
            .addIf(
                uiAction(CANCEL, CANCEL, UiActions.Color.ACCENT),
                !hasAnyError)
            .build());
  }

  private boolean hasAny(List<ValidationItem> items, ObjectValidationSeverity severity) {
    return items.stream().anyMatch(item -> item.getSeverity().equals(severity));
  }

  private UiAction uiAction(String code, String title, String color) {
    return new UiAction()
        .code(code)
        .descriptor(getUiActionDescriptor(title, color));
  }

  private UiActionDescriptor getUiActionDescriptor(String title, String color) {
    return new UiActionDescriptor()
        .title(localeSettingApi.get(LOCALE_PREFIX, title))
        .color(color)
        .type(UiActionButtonType.RAISED);
  }
}
