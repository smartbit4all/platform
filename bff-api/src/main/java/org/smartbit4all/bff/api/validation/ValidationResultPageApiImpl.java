package org.smartbit4all.bff.api.validation;

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
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class ValidationResultPageApiImpl extends PageApiImpl<ValidationResultPageModel>
    implements ValidationResultPageApi {
  final String LOCALE_PREFIX = "ValidationResultPage.";

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
        parameters.get(VALIDATION_PAGE_MODEL, ObjectValidationResult.class);
    return new ValidationResultPageModel().validationItems(
        result.getItems().stream().map(item -> new ValidationItem().severity(item.getSeverity())
            .message(getMessage(item))).collect(toList()));
  }

  private String getMessage(ObjectValidationItem item) {
    LangString message = item.getMessage();

    if (Objects.isNull(message)) {
      return "";
    }

    if (Objects.nonNull(message.getDefaultValue())) {
      return item.getMessage().getDefaultValue();
    } else if (Objects.nonNull(item.getMessage().getValueByLocale())) {
      return localeSettingApi.get(item.getMessage());
    } else {
      return "";
    }
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
    view.actions(
        UiActions.builder()
            .addIf(
                new UiAction().code(OK)
                    .descriptor(getUiActionDescriptor(LOCALE_PREFIX + OK, UiActions.Color.PRIMARY)),
                hasAny(items, ObjectValidationSeverity.ERROR))
            .addIf(
                new UiAction()
                    .code(CONTINUE)
                    .descriptor(
                        getUiActionDescriptor(LOCALE_PREFIX + CONTINUE + StringConstant.DOT
                            + ObjectValidationSeverity.WARNING, UiActions.Color.PRIMARY)),
                !hasAny(items, ObjectValidationSeverity.ERROR)
                    && hasAny(items, ObjectValidationSeverity.WARNING))
            .addIf(new UiAction()
                .code(CONTINUE)
                .descriptor(
                    getUiActionDescriptor(
                        LOCALE_PREFIX + CONTINUE + StringConstant.DOT + ObjectValidationSeverity.OK,
                        UiActions.Color.PRIMARY)),
                !hasAny(items, ObjectValidationSeverity.ERROR)
                    && !hasAny(items, ObjectValidationSeverity.WARNING))
            .addIf(new UiAction().code(CANCEL).descriptor(
                getUiActionDescriptor(LOCALE_PREFIX + CANCEL, UiActions.Color.ACCENT)),
                !hasAny(items, ObjectValidationSeverity.ERROR))
            .build());
  }

  private boolean hasAny(List<ValidationItem> items, ObjectValidationSeverity severity) {
    return items.stream().anyMatch(item -> item.getSeverity().equals(severity));
  }

  private UiActionDescriptor getUiActionDescriptor(String title, String color) {
    return new UiActionDescriptor().title(localeSettingApi.get(title)).color(color)
        .type(UiActionButtonType.RAISED);
  }
}
