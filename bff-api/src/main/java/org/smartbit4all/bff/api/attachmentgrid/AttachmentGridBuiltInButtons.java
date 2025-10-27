package org.smartbit4all.bff.api.attachmentgrid;

import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_DELETE_LIST_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_DOWNLOAD_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_OPEN_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_REMOVE_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_SAVE_LIST_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_UPLOAD_HANDLER;
import java.util.function.Function;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonDescriptor;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionDialogDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionInputType;

public class AttachmentGridBuiltInButtons {

  public static final Function<LocaleSettingApi, UiAction> PREVIEWABLE_BUILT_IN_BUTTON =
      (localeSettingApi) -> new UiAction()
          .model(true)
          .code(ATTACHMENT_OPEN_HANDLER)
          .descriptor(new UiActionDescriptor()
              .type(UiActionButtonType.RAISED)
              .color(UiActions.Color.PRIMARY)
              .icon("eye").iconPosition(IconPosition.PRE)
              .title(localeSettingApi.get("open.attachment")));

  public static final Function<LocaleSettingApi, UiAction> DOWNLOAD_BUILT_IN_BUTTON =
      (localeSettingApi) -> new UiAction()
          .model(true)
          .code(ATTACHMENT_DOWNLOAD_HANDLER)
          .descriptor(new UiActionDescriptor()
              .type(UiActionButtonType.RAISED)
              .color(UiActions.Color.PRIMARY)
              .icon("download").iconPosition(IconPosition.PRE)
              .title(localeSettingApi.get("download.attachment")));

  public static final Function<LocaleSettingApi, UiAction> REMOVE_BUILT_IN_BUTTON =
      (localeSettingApi) -> new UiAction()
          .model(true)
          .confirm(true)
          .code(ATTACHMENT_REMOVE_HANDLER)
          .descriptor(new UiActionDescriptor()
              .title(localeSettingApi.get("remove.attachment"))
              .type(UiActionButtonType.RAISED)
              .color(UiActions.Color.WARN)
              .icon("times").iconPosition(IconPosition.PRE)
              .confirmDialog(new UiActionDialogDescriptor()
                  .title(localeSettingApi.get("remove.attachment.confirm.header"))
                  .text(localeSettingApi.get("remove.attachment.confirm.text"))
                  .actionButton(new UiActionButtonDescriptor()
                      .caption(localeSettingApi.get("remove.attachment.confirm.action"))
                      .color(UiActions.Color.WARN))
                  .cancelButton(new UiActionButtonDescriptor()
                      .caption(localeSettingApi.get("remove.attachment.confirm.cancel"))
                      .color(UiActions.Color.PRIMARY))));

  public static final Function<LocaleSettingApi, UiAction> UPLOAD_BUILT_IN_BUTTON =
      (localeSettingApi) -> new UiAction()
          .input2Type(UiActionInputType.MULTIPLE_FILES)
          .code(ATTACHMENT_UPLOAD_HANDLER)
          .model(true)
          .descriptor(new UiActionDescriptor()
              .type(UiActionButtonType.ICON)
              .icon("plus").iconPosition(IconPosition.PRE)
              .color(UiActions.Color.PRIMARY)
              .input2Dialog(
                  new UiActionDialogDescriptor()
                      .title(localeSettingApi.get("add.attachment.title"))
                      .cancelButton(new UiActionButtonDescriptor()
                          .caption(localeSettingApi.get("close"))
                          .color(UiActions.Color.SECONDARY))));

  public static final Function<LocaleSettingApi, UiAction> DELETE_BUILT_IN_BUTTON =
      (localeSettingApi) -> new UiAction()
          .model(true)
          .code(ATTACHMENT_DELETE_LIST_HANDLER)
          .descriptor(new UiActionDescriptor()
              .type(UiActionButtonType.ICON)
              .icon("trash").iconPosition(IconPosition.PRE)
              .color(UiActions.Color.WARN));

  public static final Function<LocaleSettingApi, UiAction> SAVE_BUILT_IN_BUTTON =
      (localeSettingApi) -> new UiAction()
          .code(ATTACHMENT_SAVE_LIST_HANDLER)
          .disabled(true)
          .submit(true)
          .model(true)
          .descriptor(new UiActionDescriptor()
              .type(UiActionButtonType.ICON)
              .icon("save").iconPosition(IconPosition.PRE)
              .color(UiActions.Color.PRIMARY)
              .feedbackText(localeSettingApi.get("attachment.succesful.save"))
              .feedbackType(UiActionFeedbackType.SNACKBAR));
}
