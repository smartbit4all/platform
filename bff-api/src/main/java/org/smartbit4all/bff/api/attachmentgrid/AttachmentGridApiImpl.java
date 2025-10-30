package org.smartbit4all.bff.api.attachmentgrid;

import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_DIALOG_CLOSE_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_TOOLBAR_POSTFIX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridViewDescriptor.KindEnum;
import org.smartbit4all.api.invocation.InvocationApi;
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
import org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridHelper;
import org.smartbit4all.core.object.ObjectApi;
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
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private ObjectApi objectApi;

  private static List<String> attachmentOrderedColumns =
      Arrays.asList(
          BinaryContentData.FILE_NAME,
          BinaryContentData.DATA_URI,
          BinaryContentData.EXTENSION);

  @Override
  public void createAttachmentGrid(AttachmentGridDescriptor descriptor, GridModel gridModel) {

    View view = viewApi.getView(descriptor.getViewUuid());

    AttachmentGridHelper.saveDescriptorToView(descriptor, viewApi);
    setGridModel(gridModel, descriptor);

    UiActions.add(view, attachmentGridInvocationApi.getUiActions(descriptor));
    attachmentGridInvocationApi.getEventHandlers(descriptor).forEach(view::addEventHandlersItem);
  }

  @Override
  public void createDialogAttachmentGrid(AttachmentGridDescriptor descriptor, GridModel gridModel) {

    List<UiAction> actions = attachmentGridInvocationApi.getUiActions(descriptor);
    actions.add(getCloseViewAction(descriptor));

    List<ViewEventHandler> eventHandlers = attachmentGridInvocationApi.getEventHandlers(descriptor);
    eventHandlers.add(new ViewEventHandler()
        .viewEventType(ViewEventTypeEnum.INSTEAD)
        .addPathItem(ViewEventApi.ACTION)
        .addPathItem(ATTACHMENT_DIALOG_CLOSE_HANDLER)
        .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
            .build(api -> api.closeDialogWindow(null, null))));

    View view = new View().viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, createDialogGridLayout(descriptor))
        .eventHandlers(eventHandlers);

    UiActions.add(view, actions);
    viewApi.showView(view);

    descriptor.setViewUuid(view.getUuid());
    AttachmentGridHelper.saveDescriptorToView(descriptor, viewApi);
    setGridModel(gridModel, descriptor);
  }

  @Override
  public GridModel createGridModel(List<String> columns, AttachmentGridDescriptor descriptor) {
    List<String> columnsToShow;
    if (ObjectUtils.isEmpty(columns)) {
      columnsToShow = new ArrayList<>(attachmentOrderedColumns);
    } else {
      columnsToShow = new ArrayList<>(columns);
    }


    List<String> hiddenColumnsList = new ArrayList<>();

    // ADD important columns, and hide them if needed
    if (!columnsToShow.contains(BinaryContentData.DATA_URI)) {
      columnsToShow.add(BinaryContentData.DATA_URI);
    }
    if (!columnsToShow.contains(BinaryContentData.EXTENSION)) {
      columnsToShow.add(BinaryContentData.EXTENSION);
    }
    if (!columns.contains(BinaryContentData.DATA_URI)) {
      hiddenColumnsList.add(BinaryContentData.DATA_URI);
    }
    if (!columns.contains(BinaryContentData.EXTENSION)) {
      hiddenColumnsList.add(BinaryContentData.EXTENSION);
    }

    GridModel gridModel;
    if (descriptor.getSearchIndex() != null) {
      SearchIndex<?> searchIndex =
          collectionApi.searchIndex(
              descriptor.getSearchIndex().getSchema(),
              descriptor.getSearchIndex().getName());

      gridModel = gridModelApi.createGridModel(
          searchIndex.getDefinition().getDefinition(),
          columnsToShow,
          descriptor.getGridWidgetId());
    } else {
      gridModel = gridModelApi.createGridModel(
          BinaryContentData.class,
          columnsToShow,
          descriptor.getGridWidgetId());
    }
    GridModels.hideColumns(gridModel, hiddenColumnsList);

    gridModel.getView().getDescriptor().kind(KindEnum.TABLE);

    return gridModel;
  }

  private void setGridModel(GridModel gridModel, AttachmentGridDescriptor descriptor) {
    View view = viewApi.getView(descriptor.getViewUuid());
    String gridId = descriptor.getGridWidgetId();
    List<BinaryContentData> attachmentList = descriptor.getAttachmentList();

    UUID uuid = view.getUuid();
    gridModelApi.clearCallbacks(uuid, gridId);

    gridModelApi.initGridInView(uuid, gridId, gridModel);
    gridModelApi.addGridPageCallback(view.getUuid(), gridId,
        invocationApi.builder(AttachmentGridInvocationApi.class)
            .build(a -> a.extendPageDataForAttachment(null, uuid, gridId)));
    if (ObjectUtils.isEmpty(attachmentList)) {
      attachmentList = new ArrayList<>();
    }

    setGridData(descriptor);
    AttachmentGridHelper.saveOriginalAttachmentList(descriptor, viewApi);
  }

  private void setGridData(AttachmentGridDescriptor descriptor) {

    if (descriptor.getSearchIndex() != null) {
      SearchIndex<BinaryContentData> searchIndex =
          (SearchIndex<BinaryContentData>) collectionApi.searchIndex(
              descriptor.getSearchIndex().getSchema(),
              descriptor.getSearchIndex().getName());
      gridModelApi.setData(
          descriptor.getViewUuid(),
          descriptor.getGridWidgetId(),
          searchIndex.tableDataOfObjects(descriptor.getAttachmentList().stream()));
    } else {
      gridModelApi.setData(descriptor.getViewUuid(), descriptor.getGridWidgetId(),
          BinaryContentData.class, descriptor.getAttachmentList());
    }
  }

  private SmartComponentLayoutDefinition createDialogGridLayout(
      AttachmentGridDescriptor descriptor) {
    String title = ObjectUtils.isEmpty(descriptor.getOptions().getDialogTitle())
        ? localeSettingApi.get("attachment.grid.dialog.title")
        : descriptor.getOptions().getDialogTitle();

    return ObjectLayoutBuilder.container(LayoutDirection.VERTICAL)
        .addComponentsItem(ObjectLayoutBuilder.form(LayoutDirection.HORIZONTAL,
            ObjectLayoutBuilder.label("label", title)
                .cssClass("attachmentDialogTitle")))
        .addComponentsItem(ObjectLayoutBuilder.grid(descriptor.getGridWidgetId()))
        .addComponentsItem(ObjectLayoutBuilder.toolbar(
            descriptor.getGridWidgetId() + ATTACHMENT_TOOLBAR_POSTFIX));
  }

  public UiAction getCloseViewAction(AttachmentGridDescriptor descriptor) {
    return new UiAction()
        .code(ATTACHMENT_DIALOG_CLOSE_HANDLER)
        .toolbar(descriptor.getGridWidgetId() + ATTACHMENT_TOOLBAR_POSTFIX)
        .descriptor(new UiActionDescriptor()
            .type(UiActionButtonType.FLAT)
            .title(localeSettingApi.get("close"))
            .icon("times").iconPosition(IconPosition.PRE)
            .color(UiActions.Color.SECONDARY));
  }

  @Override
  public void refreshGrid(UUID viewUuid, String gridId, List<BinaryContentData> fileList) {
    View view = viewApi.getView(viewUuid);
    AttachmentGridDescriptor descriptor =
        AttachmentGridHelper.getDescriptorFromView(view, gridId, objectApi);

    descriptor.setAttachmentList(fileList);
    setGridData(descriptor);

    AttachmentGridHelper.saveDescriptorToView(descriptor, viewApi);
  }

}
