package org.smartbit4all.bff.api.attachmentgrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridViewDescriptor.KindEnum;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class AttachmentGridApiImpl implements AttachmentGridApi {

  private static final Logger log = LoggerFactory.getLogger(AttachmentGridApiImpl.class);

  @Autowired
  private GridModelApi gridModelApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired(required = false)
  private ViewApi viewApi;
  @Autowired
  private AttachmentGridInvocationApi attachmentGridInvocationApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  private static List<String> attachmentOrderedColumns =
      Arrays.asList(BinaryContentData.FILE_NAME, BinaryContentData.DATA_URI,
          BinaryContentData.EXTENSION);

  @Override
  public void createAttachmentGrid(AttachmentGridDescriptor descriptor) {
    Objects.requireNonNull(descriptor,
        "AttachmentGridDescriptor cannot be null");
    Objects.requireNonNull(descriptor.getViewUuid(),
        "AttachmentGridDescriptor ViewUUID cannot be null");
    Objects.requireNonNull(descriptor.getGridWidgetId(),
        "AttachmentGridDescriptor GridWidgetId cannot be null");
    Objects.requireNonNull(descriptor.getLogicalSchema(),
        "AttachmentGridDescriptor LogicalSchema cannot be null");
    if (descriptor.getIsEditable()) {
      Objects.requireNonNull(descriptor.getSaveRequest(),
          "AttachmentGridDescriptor SaveRequest cannot be null, if it is editable");
      InvocationParameter invocationParameter = descriptor.getSaveRequest().getParameters().get(0);
      if (!invocationParameter.getTypeClass().equals("java.util.List")) {
        log.error(
            "AttachmentGridDescriptor SaveRequest first parameter should be List<BinaryContentData>");
      }
    }
    View view = viewApi.getView(descriptor.getViewUuid());

    AttachmentGridHelper.saveDescriptorToView(descriptor, viewApi);
    setGridModel(descriptor);

    attachmentGridInvocationApi.getUiActions(descriptor).forEach(view::addActionsItem);
    attachmentGridInvocationApi.getEventHandlers(descriptor).forEach(view::addEventHandlersItem);

  }

  @Override
  public void createDialogAttachmentGrid(AttachmentGridDescriptor descriptor) {
    Objects.requireNonNull(descriptor,
        "AttachmentGridDescriptor  can not be null");
    Objects.requireNonNull(descriptor.getGridWidgetId(),
        "AttachmentGridDescriptor GridWidgetId can not be null");
    Objects.requireNonNull(descriptor.getLogicalSchema(),
        "AttachmentGridDescriptor LogicalSchema can not be null");
    if (descriptor.getIsEditable()) {
      Objects.requireNonNull(descriptor.getSaveRequest(),
          "AttachmentGridDescriptor SaveRequest can not be null, if it is editable");
      InvocationParameter invocationParameter = descriptor.getSaveRequest().getParameters().get(0);
      if (!invocationParameter.getTypeClass().equals("java.util.List")) {
        log.error(
            "AttachmentGridDescriptor SaveRequest first parameter should be List<BinaryContentData>");
      }
    }
    if (descriptor.getIsPaginatorEnabled().equals(false) && descriptor.getPageSize() != null) {
      log.warn(
          "AttachmentGridDescriptor paginator is turned off, but page size is set. This could lead to not showing all attachments");
    }

    List<UiAction> actions = attachmentGridInvocationApi.getUiActions(descriptor);
    actions.add(getCloseViewAction(descriptor));

    List<ViewEventHandler> eventHandlers = attachmentGridInvocationApi.getEventHandlers(descriptor);
    eventHandlers.add(new ViewEventHandler()
        .viewEventType(ViewEventTypeEnum.INSTEAD)
        .addPathItem(ViewEventApi.ACTION)
        .addPathItem(AttachmentGridInvocationApi.ATTACHMENT_DIALOG_CLOSE_HANDLER)
        .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
            .build(api -> api.closeDialogWindow(null, null))));

    View view = new View().viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, createDialogGridLayout(descriptor))
        .actions(actions)
        .eventHandlers(eventHandlers);

    viewApi.showView(view);

    descriptor.setViewUuid(view.getUuid());
    AttachmentGridHelper.saveDescriptorToView(descriptor, viewApi);
    setGridModel(descriptor);
  }

  private void setGridModel(AttachmentGridDescriptor descriptor) {
    View view = viewApi.getView(descriptor.getViewUuid());
    String gridId = descriptor.getGridWidgetId();
    List<BinaryContentData> attachmentList = descriptor.getAttachmentList();

    GridModel gridModel =
        gridModelApi.createGridModel(BinaryContentData.class,
            attachmentOrderedColumns, gridId);
    GridModels.hideColumns(gridModel,
        Arrays.asList(BinaryContentData.DATA_URI, BinaryContentData.EXTENSION));
    gridModel.getView().getDescriptor().kind(KindEnum.TABLE);

    UUID uuid = view.getUuid();
    gridModelApi.initGridInView(uuid, gridId, gridModel);
    gridModelApi.addGridPageCallback(view.getUuid(), gridId,
        invocationApi.builder(AttachmentGridInvocationApi.class)
            .build(a -> a.extendPageDataForAttachment(null, uuid, gridId)));
    if (ObjectUtils.isEmpty(attachmentList)) {
      attachmentList = new ArrayList<>();
    }
    gridModelApi.setData(uuid, gridId, BinaryContentData.class, attachmentList);

    if (descriptor.getPageSize() != null) {
      gridModelApi.setPageSize(uuid, gridId,
          (m) -> descriptor.getPageSize());
    }

    if (!descriptor.getIsPaginatorEnabled()) {
      gridModel.setPaginator(false);
      if (!ObjectUtils.isEmpty(descriptor.getAttachmentList())
          && descriptor.getPageSize() != null) {
        gridModelApi.setPageSize(uuid, gridId,
            (m) -> descriptor.getAttachmentList().size());
      }
    }
    AttachmentGridHelper.saveOriginalAttachmentList(descriptor, viewApi);
  }

  private SmartComponentLayoutDefinition createDialogGridLayout(
      AttachmentGridDescriptor descriptor) {
    return ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(ObjectLayoutBuilder.grid(descriptor.getGridWidgetId()))
        .addComponentsItem(ObjectLayoutBuilder.toolbar(
            descriptor.getGridWidgetId() + AttachmentGridHelper.ATTACHMENT_TOOLBAR_POSTFIX));
  }

  public UiAction getCloseViewAction(AttachmentGridDescriptor descriptor) {
    return new UiAction()
        .code(AttachmentGridInvocationApi.ATTACHMENT_DIALOG_CLOSE_HANDLER)
        .toolbar(descriptor.getGridWidgetId() + AttachmentGridHelper.ATTACHMENT_TOOLBAR_POSTFIX)
        .descriptor(new UiActionDescriptor()
            .type(UiActionButtonType.FLAT)
            .title(localeSettingApi.get("close"))
            .icon("times").iconPosition(IconPosition.PRE)
            .color(UiActions.Color.SECONDARY));
  }

}
