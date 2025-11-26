package org.smartbit4all.bff.api.mdm.jsoneditor;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.JSON_EDITOR)
public interface JsonEditorPageApi extends MDMEntryEditPageApi {


  public static final String DESERIALIZATION_ERROR_TEXT = "DESERIALIZATION_ERROR_TEXT";
  public static final String DESERIALIZATION_ERROR_TITLE = "DESERIALIZATION_ERROR_TITLE";

  public static final String JSON_EDITOR_TITLE = "JSON_EDITOR_TITLE";
  public static final String JSON_UPLOAD_TEXT = "JSON_UPLOAD_TEXT";

  public static final String UPLOAD = "UPLOAD";

  Function<LocaleSettingApi, UiAction> ACTION_SAVE_BUTTON =
      (localeSettingApi) -> new UiAction()
          .code(MDMEntryEditPageApi.ACTION_SAVE)
          .model(true)
          .descriptor(new UiActionDescriptor()
              .title(localeSettingApi.get(MDMEntryEditPageApi.ACTION_SAVE))
              .icon("save").iconPosition(IconPosition.PRE)
              .type(UiActionButtonType.RAISED)
              .color(UiActions.Color.PRIMARY));

  public static final String EXPORT_AS_JSON = "EXPORT_AS_JSON";
  Function<LocaleSettingApi, UiAction> ACTION_EXPORT =
      (localeSettingApi) -> new UiAction()
          .code(EXPORT_AS_JSON)
          .model(true)
          .descriptor(new UiActionDescriptor()
              .title(localeSettingApi.get(EXPORT_AS_JSON))
              .icon("file-export").iconPosition(IconPosition.PRE)
              .type(UiActionButtonType.RAISED)
              .color(UiActions.Color.PRIMARY));

  @ActionHandler(EXPORT_AS_JSON)
  void export(UUID viewUuid, UiActionRequest request);

  Function<LocaleSettingApi, UiAction> ACTION_CLOSE_BUTTON =
      (localeSettingApi) -> new UiAction()
          .code(MDMEntryEditPageApi.ACTION_CANCEL)
          .model(true)
          .descriptor(new UiActionDescriptor()
              .title(localeSettingApi.get(MDMEntryEditPageApi.DEFAULT_CLOSE))
              .icon("times").iconPosition(IconPosition.PRE)
              .type(UiActionButtonType.NORMAL)
              .color(UiActions.Color.SECONDARY));

  Supplier<UiAction> ACTION_UPLOAD = () -> new UiAction()
      .model(true)
      .code(UPLOAD);

  @ActionHandler(UPLOAD)
  void upload(UUID viewUuid, UiActionRequest request);
}
