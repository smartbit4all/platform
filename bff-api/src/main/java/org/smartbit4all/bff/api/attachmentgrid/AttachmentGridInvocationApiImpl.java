package org.smartbit4all.bff.api.attachmentgrid;

import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_DOWNLOADBLE_FILE;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_DOWNLOAD_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_OPEN_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_REFRESH_LIST_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_REMOVE_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_SAVE_LIST_HANDLER;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_TEMP_SCHEMA;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_UPLOAD_HANDLER;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mimetype.ConverterApi;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.bean.DownloadedFile;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonDescriptor;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionDialogDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.UiActionUploadDescriptor;
import org.smartbit4all.api.view.bean.UploadedFile;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.attachmentgrid.bean.AdditionalAttachmentAction;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridOptions;
import org.smartbit4all.bff.api.attachmentgrid.bean.ButtonDescriptor;
import org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridHelper;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class AttachmentGridInvocationApiImpl implements AttachmentGridInvocationApi {

  private static final Logger log = LoggerFactory.getLogger(AttachmentGridInvocationApiImpl.class);

  @Autowired(required = false)
  private ViewApi viewApi;
  @Autowired(required = false)
  private ConverterApi converterApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;
  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired
  private GridModelApi gridModelApi;
  @Autowired
  private SessionApi sessionApi;
  @Autowired
  private CollectionApi collectionApi;

  @Override
  public GridPage extendPageDataForAttachment(GridPage page, UUID viewUuid,
      String widgetId) {
    if (page == null) {
      return page;
    }
    AttachmentGridDescriptor descriptor =
        AttachmentGridHelper.getDescriptorFromView(viewApi.getView(viewUuid), widgetId, objectApi);
    AttachmentGridOptions options = descriptor.getOptions();

    for (GridRow row : page.getRows()) {

      if (Boolean.TRUE.equals(options.getIsPreviewable())) {
        row.addActionsItem(new UiAction()
            .model(true)
            .code(ATTACHMENT_OPEN_HANDLER)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get("open.attachment"))));
      }
      if (Boolean.TRUE.equals(options.getIsDownloadable())) {
        row.addActionsItem(new UiAction()
            .model(true)
            .code(ATTACHMENT_DOWNLOAD_HANDLER)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get("download.attachment"))));
      }
      if (Boolean.TRUE.equals(options.getIsEditable())) {
        row.addActionsItem(new UiAction()
            .model(true)
            .confirm(true)
            .code(ATTACHMENT_REMOVE_HANDLER)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get("remove.attachment"))
                .confirmDialog(new UiActionDialogDescriptor()
                    .title(localeSettingApi.get("remove.attachment.confirm.header"))
                    .text(localeSettingApi.get("remove.attachment.confirm.text"))
                    .actionButton(new UiActionButtonDescriptor()
                        .caption(localeSettingApi.get("remove.attachment.confirm.action"))
                        .color(UiActions.Color.PRIMARY))
                    .cancelButton(new UiActionButtonDescriptor()
                        .caption(localeSettingApi.get("remove.attachment.confirm.cancel"))
                        .color(UiActions.Color.ACCENT)))));
      }

      // Add custom actions
      if (!ObjectUtils.isEmpty(descriptor.getAdditionalActions())) {
        for (AdditionalAttachmentAction action : descriptor.getAdditionalActions()) {
          row.addActionsItem(new UiAction()
              .model(true)
              .code(localeSettingApi.get(action.getCode()))
              .descriptor(action.getDescriptor()));
        }
      }
    }
    return page;
  }

  @Override
  public void refreshGridToOriginalState(UUID viewUuid, UiActionRequest request, String widgetId) {
    View view = viewApi.getView(viewUuid);
    AttachmentGridDescriptor descriptor =
        AttachmentGridHelper.getDescriptorFromView(view, widgetId, objectApi);
    Object object = view.getParameters().get(
        descriptor.getGridWidgetId() + ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX);
    List<BinaryContentData> originalList = objectApi.asType(List.class, object);

    if (ObjectUtils.isEmpty(originalList)) {
      descriptor.setAttachmentList(new ArrayList<>());
    } else {
      descriptor.setAttachmentList(originalList);
    }

    UiActions.remove(view, getSaveListAction(descriptor));
    UiActions.add(view, getSaveListAction(descriptor).disabled(true));
    setGrid(descriptor);
  }

  @Override
  public void addAttachment(UUID viewUuid, UiActionRequest request) {

    String widgetId = request.getIdentifier();

    View view = viewApi.getView(viewUuid);
    ObjectMapHelper actionRequestHelper = actionRequestHelper(request);
    AttachmentGridDescriptor descriptor =
        AttachmentGridHelper.getDescriptorFromView(view, widgetId, objectApi);
    AttachmentGridOptions options = descriptor.getOptions();

    List<String> existingFileNames;
    if (!ObjectUtils.isEmpty(descriptor.getAttachmentList())) {
      existingFileNames = descriptor.getAttachmentList().stream().map(a -> a.getFileName())
          .collect(Collectors.toList());
    } else {
      existingFileNames = new ArrayList<>();
    }
    List<BinaryContentData> newAttachments = new ArrayList<>();
    Boolean isMultipleInput = options.getIsMultipleInput();
    if (Boolean.TRUE.equals(isMultipleInput)) {

      List<UploadedFile> uploadedFiles =
          actionRequestHelper.getAsList(UiActions.INPUT2, UploadedFile.class);
      newAttachments.addAll(uploadedFiles.stream().map(uploadedFile -> {
        BinaryContentData bCData = generateUniqueFilename(
            uploadedFile.getFilename(), existingFileNames)
                .dataUri(objectApi.saveAsNew(
                    descriptor.getLogicalSchema(), uploadedFile.getData().asObject()))
                .created(sessionApi.createActivityLog())
                .size(uploadedFile.getSize());
        existingFileNames.add(bCData.getFileName());
        return bCData;

      }).collect(Collectors.toList()));
    } else {
      UploadedFile uploadedFile =
          actionRequestHelper.get(UiActions.INPUT2, UploadedFile.class);
      BinaryContentData bCData = generateUniqueFilename(
          uploadedFile.getFilename(), existingFileNames)
              .dataUri(objectApi.saveAsNew(
                  descriptor.getLogicalSchema(), uploadedFile.getData().asObject()))
              .created(sessionApi.createActivityLog())
              .size(uploadedFile.getSize());
      existingFileNames.add(bCData.getFileName());
      newAttachments.add(bCData);
    }

    if (!ObjectUtils.isEmpty(descriptor.getAttachmentList())
        && Boolean.TRUE.equals(isMultipleInput)) {
      List<BinaryContentData> existingAttachments = descriptor.getAttachmentList();
      existingAttachments.addAll(newAttachments);
      descriptor.setAttachmentList(existingAttachments);
    } else {
      descriptor.setAttachmentList(newAttachments);
    }

    if (!options.getAutoSave()) {
      UiActions.remove(view, getSaveListAction(descriptor));
      UiActions.add(view, getSaveListAction(descriptor).disabled(false));
    }
    setGrid(descriptor);

    if (options.getAutoSave()) {
      saveListRequest(viewUuid, request, widgetId);
    }
  }

  @Override
  public void openAttachmentFromGrid(
      UUID viewUuid, String widgetId,
      String nodeId, UiActionRequest request) {

    GridModel gridModel = viewApi.getWidgetModelFromView(
        GridModel.class, viewUuid, widgetId);
    Object valueFromGridRow = GridModels.getValueFromGridRow(
        gridModel, nodeId, BinaryContentData.DATA_URI);
    URI dataUri = valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null;
    Object extensionFromGridRow = GridModels.getValueFromGridRow(
        gridModel, nodeId, BinaryContentData.EXTENSION);
    String extension = extensionFromGridRow.toString();
    Object nameFromGridRow = GridModels.getValueFromGridRow(
        gridModel, nodeId, BinaryContentData.FILE_NAME);
    String fileName = nameFromGridRow.toString();

    previewFile(viewApi.getView(viewUuid), dataUri, fileName, extension);
  }

  @Override
  public void downloadAttachmentFromGrid(
      UUID viewUuid, String widgetId,
      String nodeId, UiActionRequest request) {

    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    Object valueFromGridRow =
        GridModels.getValueFromGridRow(gridModel, nodeId, BinaryContentData.DATA_URI);
    Object fileNameObj =
        GridModels.getValueFromGridRow(gridModel, nodeId, BinaryContentData.FILE_NAME);
    String documentName = objectApi.asType(String.class, fileNameObj);
    URI dataUri =
        valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null;

    viewApi.getView(viewUuid)
        .putDownloadableItemsItem(ATTACHMENT_DOWNLOADBLE_FILE, dataUri);
    viewApi.downloadFile(new DownloadedFile().uuid(viewUuid)
        .identifier(ATTACHMENT_DOWNLOADBLE_FILE)
        .filename(documentName));
  }

  @Override
  public void removeAttachment(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    AttachmentGridDescriptor descriptor =
        AttachmentGridHelper.getDescriptorFromView(viewApi.getView(viewUuid), widgetId, objectApi);
    AttachmentGridOptions options = descriptor.getOptions();

    List<BinaryContentData> currentDocuments = descriptor.getAttachmentList();
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    Object valueFromGridRow =
        GridModels.getValueFromGridRow(gridModel, nodeId, BinaryContentData.DATA_URI);
    URI dataUri = objectApi.asType(URI.class, valueFromGridRow);

    currentDocuments.removeIf(document -> objectApi
        .equalsIgnoreVersion(dataUri, document.getDataUri()));
    descriptor.setAttachmentList(currentDocuments);

    View view = viewApi.getView(viewUuid);

    if (!options.getAutoSave()) {
      UiActions.remove(view, getSaveListAction(descriptor));
      UiActions.add(view, getSaveListAction(descriptor).disabled(false));
    }
    setGrid(descriptor);
  }

  private BinaryContentData generateUniqueFilename(String uploadedFilename,
      List<String> existingFileNames) {

    String regex = "^(.*?)(\\s*\\(\\d+\\))*\\s*\\.\\w+$";

    String baseName = uploadedFilename.replaceAll(regex, "$1");
    String extension = uploadedFilename.replaceAll("^.*\\.(.*)$", "$1");

    List<String> baseFileNames = existingFileNames.stream()
        .map(filename -> filename.replaceAll(regex, "$1"))
        .collect(Collectors.toList());

    if (!baseFileNames.contains(baseName)) {
      return new BinaryContentData()
          .fileName(baseName + "." + extension).extension(extension);
    }

    int counter = Collections.frequency(baseFileNames, baseName);
    String uniqueName = baseName + " (" + counter + ")." + extension;

    return new BinaryContentData()
        .fileName(uniqueName).extension(extension);
  }

  @Override
  public void saveListRequest(UUID viewUuid, UiActionRequest request, String widgetId) {
    View view = viewApi.getView(viewUuid);
    AttachmentGridDescriptor descriptor =
        AttachmentGridHelper.getDescriptorFromView(view, widgetId, objectApi);
    AttachmentGridOptions options = descriptor.getOptions();

    InvocationRequest invocationRequest = descriptor.getSaveRequest();
    invocationRequest.getParameters().get(0).setValue(descriptor.getAttachmentList());
    invocationRequest.getParameters().get(1).setValue(request);
    try {
      invocationApi.invoke(invocationRequest);
    } catch (ApiNotFoundException e) {
      log.error(e.getMessage(), e);
    }
    AttachmentGridHelper.saveOriginalAttachmentList(descriptor, viewApi);

    if (!options.getAutoSave()) {
      UiActions.remove(view, getSaveListAction(descriptor));
      UiActions.add(view, getSaveListAction(descriptor).disabled(true));
    }

    if (viewApi.getView(viewUuid).getType().equals(ViewType.DIALOG)
        && options.getCloseOnSave().equals(Boolean.TRUE)) {
      viewApi.closeView(viewUuid);
    }
  }

  private void previewFile(View view, URI dataUri, String fileName, String extension) {
    Optional<URI> previewableFileUri = createPreviewableFile(dataUri, extension);

    if (previewableFileUri.isPresent()) {
      viewApi.showView(new View()
          .type(ViewType.DIALOG)
          .objectUri(previewableFileUri.get())
          .viewName(PlatformViewNames.PDF_VIEWER_DIALOG));
    } else {
      log.warn("File type is not supported for preview: {}", extension);
      view.putDownloadableItemsItem(ATTACHMENT_DOWNLOADBLE_FILE, dataUri);
      viewApi.downloadFile(new DownloadedFile()
          .uuid(view.getUuid())
          .identifier(ATTACHMENT_DOWNLOADBLE_FILE)
          .filename(fileName));
    }
  }

  private Optional<URI> createPreviewableFile(URI dataUri, String extension) {
    try {
      BinaryDataObject binaryDataObject =
          objectApi.loadLatest(dataUri).getObject(BinaryDataObject.class);

      switch (extension) {
        case MimeTypeApi.PDF_EXT:
          return Optional.of(binaryDataObject.getUri());
        case MimeTypeApi.DOCX_EXT:
          URI docxConvertedUri = saveTemporaryBinaryData(
              converterApi.convert(binaryDataObject.getBinaryData(),
                  MimeTypeApi.DOCX_MIMETYPE, MimeTypeApi.PDF_MIMETYPE));
          return Optional.of(docxConvertedUri);
        case MimeTypeApi.PNG_EXT:
          URI pngConvertedUri = saveTemporaryBinaryData(
              converterApi.convert(binaryDataObject.getBinaryData(),
                  MimeTypeApi.PNG_MIMETYPE, MimeTypeApi.PDF_MIMETYPE));
          return Optional.of(pngConvertedUri);
        default:
          return Optional.empty();
      }
    } catch (Throwable t) {
      log.warn("Error creating previewable file for extension {}: {}", extension, t.getMessage());
      return Optional.empty();
    }
  }

  private URI saveTemporaryBinaryData(final BinaryData contentData) {
    return objectApi.saveAsNew(ATTACHMENT_TEMP_SCHEMA, contentData.asObject());
  }

  private ObjectMapHelper actionRequestHelper(UiActionRequest request) {
    return new ObjectMapHelper(request.getParams(), objectApi, request.getCode()
        + StringConstant.SPACE_HYPHEN_SPACE + request.getIdentifier()
        + StringConstant.SPACE_HYPHEN_SPACE + request.getPath() + " action parameters");
  }


  public UiAction getAddAttachmentAction(AttachmentGridDescriptor descriptor) {
    AttachmentGridOptions options = descriptor.getOptions();

    UiAction action = new UiAction()
        .input2Type(
            Boolean.TRUE.equals(options.getIsMultipleInput()) ? UiActionInputType.MULTIPLE_FILES
                : UiActionInputType.FILE)
        .code(ATTACHMENT_UPLOAD_HANDLER)
        .model(true)
        .identifier(descriptor.getGridWidgetId())
        .toolbar(descriptor.getGridWidgetId() + UiActions.TOOLBAR_SUFFIX)
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

    if (descriptor.getUploadButtonDescriptor() != null) {
      action = setButtonDescriptor(action, descriptor.getUploadButtonDescriptor());
    }
    return action;
  }

  public UiAction getRefreshToOriginalGridAction(AttachmentGridDescriptor descriptor) {
    UiAction action = new UiAction()
        .model(true)
        .code(ATTACHMENT_REFRESH_LIST_HANDLER)
        .toolbar(descriptor.getGridWidgetId() + UiActions.TOOLBAR_SUFFIX)
        .descriptor(new UiActionDescriptor()
            .type(UiActionButtonType.ICON)
            .icon("sync").iconPosition(IconPosition.PRE)
            .color(UiActions.Color.PRIMARY));

    if (descriptor.getRefreshButtonDescriptor() != null) {
      action = setButtonDescriptor(action, descriptor.getRefreshButtonDescriptor());
    }
    return action;
  }

  public UiAction getSaveListAction(AttachmentGridDescriptor descriptor) {

    UiAction action = new UiAction()
        .code(ATTACHMENT_SAVE_LIST_HANDLER)
        .toolbar(descriptor.getGridWidgetId() + UiActions.TOOLBAR_SUFFIX)
        .disabled(true)
        .submit(true)
        .model(true)
        .descriptor(new UiActionDescriptor()
            .type(UiActionButtonType.ICON)
            .icon("save").iconPosition(IconPosition.PRE)
            .color(UiActions.Color.PRIMARY)
            .feedbackText(localeSettingApi.get("attachment.succesful.save"))
            .feedbackType(UiActionFeedbackType.SNACKBAR));

    if (descriptor.getSaveButtonDescriptor() != null) {
      action = setButtonDescriptor(action, descriptor.getSaveButtonDescriptor());
    }
    return action;

  }

  @Override
  public List<UiAction> getUiActions(AttachmentGridDescriptor descriptor) {
    AttachmentGridOptions options = descriptor.getOptions();
    List<UiAction> actions = new ArrayList<>();

    if (Boolean.TRUE.equals(options.getIsEditable())
        && Boolean.TRUE.equals(options.getAutoSave())) {
      actions.addAll(Arrays.asList(
          getAddAttachmentAction(descriptor)));
    } else if (Boolean.TRUE.equals(options.getIsEditable())) {
      actions.addAll(Arrays.asList(
          getAddAttachmentAction(descriptor),
          getRefreshToOriginalGridAction(descriptor),
          getSaveListAction(descriptor)));
    }
    return actions;
  }

  @Override
  public List<ViewEventHandler> getEventHandlers(AttachmentGridDescriptor descriptor) {
    AttachmentGridOptions options = descriptor.getOptions();
    List<ViewEventHandler> handlers = new ArrayList<>();
    String gridId = descriptor.getGridWidgetId();

    if (Boolean.TRUE.equals(options.getIsEditable())) {

      ViewEventHandler uploadEvent = new ViewEventHandler()
          .viewEventType(ViewEventTypeEnum.INSTEAD)
          .addPathItem(ViewEventApi.ACTION)
          .addPathItem(ATTACHMENT_UPLOAD_HANDLER)
          .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
              .build(api -> api.addAttachment(null, null)));
      handlers.add(uploadEvent);
      handlers.add(createSaveModelEvent(uploadEvent));

      ViewEventHandler removeEvent = new ViewEventHandler()
          .viewEventType(ViewEventTypeEnum.INSTEAD)
          .addPathItem(ViewEventApi.WIDGET)
          .addPathItem(gridId)
          .addPathItem(ATTACHMENT_REMOVE_HANDLER)
          .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
              .build(api -> api.removeAttachment(null, gridId, null, null)));
      handlers.add(removeEvent);
      handlers.add(createSaveModelGridEvent(removeEvent));

      ViewEventHandler refreshEvent = new ViewEventHandler()
          .viewEventType(ViewEventTypeEnum.INSTEAD)
          .addPathItem(ViewEventApi.ACTION)
          .addPathItem(ATTACHMENT_REFRESH_LIST_HANDLER)
          .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
              .build(api -> api.refreshGridToOriginalState(null, null, gridId)));
      handlers.add(refreshEvent);
      handlers.add(createSaveModelEvent(refreshEvent));

      ViewEventHandler saveListEvent = new ViewEventHandler()
          .viewEventType(ViewEventTypeEnum.INSTEAD)
          .addPathItem(ViewEventApi.ACTION)
          .addPathItem(ATTACHMENT_SAVE_LIST_HANDLER)
          .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
              .build(api -> api.saveListRequest(null, null, gridId)));
      handlers.add(saveListEvent);
      handlers.add(createSaveModelEvent(saveListEvent));
    }

    if (Boolean.TRUE.equals(options.getIsDownloadable())) {
      handlers.add(new ViewEventHandler()
          .viewEventType(ViewEventTypeEnum.INSTEAD)
          .addPathItem(ViewEventApi.WIDGET)
          .addPathItem(gridId)
          .addPathItem(ATTACHMENT_DOWNLOAD_HANDLER)
          .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
              .build(api -> api.downloadAttachmentFromGrid(null, gridId, null, null))));
    }
    if (Boolean.TRUE.equals(options.getIsPreviewable())) {
      handlers.add(new ViewEventHandler()
          .viewEventType(ViewEventTypeEnum.INSTEAD)
          .addPathItem(ViewEventApi.WIDGET)
          .addPathItem(gridId)
          .addPathItem(ATTACHMENT_OPEN_HANDLER)
          .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
              .build(api -> api.openAttachmentFromGrid(null, gridId, null, null))));
    }

    if (!ObjectUtils.isEmpty(descriptor.getAdditionalActions())) {
      for (AdditionalAttachmentAction action : descriptor.getAdditionalActions()) {
        ViewEventHandler event = new ViewEventHandler()
            .viewEventType(ViewEventTypeEnum.INSTEAD)
            .addPathItem(ViewEventApi.WIDGET)
            .addPathItem(gridId)
            .addPathItem(action.getCode())
            .invocationRequest(action.getActionInvocationRequest());
        handlers.add(event);
        handlers.add(createSaveModelGridEvent(event));
      }
    }

    return handlers;
  }

  private void setGrid(AttachmentGridDescriptor descriptor) {

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
      gridModelApi.setData(
          descriptor.getViewUuid(),
          descriptor.getGridWidgetId(),
          BinaryContentData.class,
          descriptor.getAttachmentList());
    }

    gridModelApi.setPageSize(
        descriptor.getViewUuid(),
        descriptor.getGridWidgetId(),
        (model) -> {
          if (Boolean.FALSE.equals(model.getPaginator())) {
            return model.getTotalRowCount();
          } else {
            return model.getPageSize();
          }
        });
    AttachmentGridHelper.saveDescriptorToView(descriptor, viewApi);
  }

  @Override
  public void closeDialogWindow(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void saveModel(UUID viewUuid, UiActionRequest request) {
    if (actionRequestHelper(request).get(UiActions.MODEL, Object.class) != null) {
      viewApi.getView(viewUuid)
          .setModel(actionRequestHelper(request)
              .get(UiActions.MODEL, Object.class));
    }
  }

  @Override
  public void saveModel(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    saveModel(viewUuid, request);
  }

  private ViewEventHandler createSaveModelEvent(ViewEventHandler event) {
    return new ViewEventHandler()
        .viewEventType(ViewEventTypeEnum.BEFORE)
        .path(event.getPath())
        .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
            .build(api -> api.saveModel(null, null)));
  }

  private ViewEventHandler createSaveModelGridEvent(ViewEventHandler event) {
    return new ViewEventHandler()
        .viewEventType(ViewEventTypeEnum.BEFORE)
        .path(event.getPath())
        .invocationRequest(invocationApi.builder(AttachmentGridInvocationApi.class)
            .build(api -> api.saveModel(null, null, null, null)));
  }

  private UiAction setButtonDescriptor(UiAction action, ButtonDescriptor bDescriptor) {
    if (bDescriptor.getToolbar() != null) {
      action.toolbar(bDescriptor.getToolbar());
    }
    if (bDescriptor.getIdentifier() != null) {
      action.identifier(bDescriptor.getIdentifier());
    }
    if (bDescriptor.getParams() != null) {
      action.params(bDescriptor.getParams());
    }

    if (bDescriptor.getDescriptor() != null) {
      Map<String, Object> oldDesc = objectApi.create(null, action.getDescriptor()).getObjectAsMap();
      Map<String, Object> newDesc =
          objectApi.create(null, bDescriptor.getDescriptor()).getObjectAsMap();


      oldDesc.forEach((key, value) -> {
        if (value != null) {
          newDesc.merge(key, value, (v1, v2) -> v1 != null ? v1 : v2);
        }
      });
      UiActionDescriptor newDescAction = objectApi.asType(UiActionDescriptor.class, newDesc);

      action.setDescriptor(newDescAction);
    }

    return action;
  }
}
