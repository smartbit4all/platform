package org.smartbit4all.bff.api.generic;

import java.util.UUID;
import java.util.function.Function;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewEventHandler;

/**
 * The GenericPageApi doesn't have any specific action, it's actions are handled in via
 * {@link ViewEventHandler}s in {@link View#EVENT_HANDLERS}.
 *
 * This pageApi is always present and registered.
 *
 */
@ViewApi(PlatformViewNames.GENERIC_PAGE)
public interface GenericPageApi extends PageApi<Object> {

  static final String PARAM_MODEL = "PARAM_MODEL";

  static final String ACTION_CLOSE_VIEW = "CLOSE_VIEW";

  static final String ACTION_SAVE = "SAVE";

  static final Function<LocaleSettingApi, UiAction> ACTION_CLOSE_VIEW_FUNCTION =
      (localeSettingApi) -> new UiAction()
          .code(DEFAULT_CLOSE)
          .descriptor(new UiActionDescriptor()
              .color(UiActions.Color.SECONDARY)
              .type(UiActionButtonType.NORMAL)
              .title(localeSettingApi.get(ACTION_CLOSE_VIEW))
              .icon("times").iconPosition(IconPosition.PRE));

  Function<LocaleSettingApi, UiAction> ACTION_SAVE_FUNCTION =
      (localeSettingApi) -> new UiAction()
          .code(ACTION_SAVE)
          .submit(true)
          .descriptor(new UiActionDescriptor()
              .color(UiActions.Color.PRIMARY)
              .type(UiActionButtonType.RAISED)
              .title(localeSettingApi.get(ACTION_SAVE))
              .icon("save").iconPosition(IconPosition.PRE));

  @ActionHandler(ACTION_CLOSE_VIEW)
  void closeView(UUID viewUuid, UiActionRequest request);

}
