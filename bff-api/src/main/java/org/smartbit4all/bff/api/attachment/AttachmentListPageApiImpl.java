package org.smartbit4all.bff.api.attachment;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.AttachmentList;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.DownloadedFile;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.UploadedFile;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;


public class AttachmentListPageApiImpl extends PageApiImpl<AttachmentList>
    implements AttachmentListPageApi {

  private static final String DOWNLOADABLE_ITEM_ID = "attachment";

  public AttachmentListPageApiImpl() {
    super(AttachmentList.class);
  }

  @Autowired
  SessionApi sessionApi;
  @Autowired
  MimeTypeApi mimeTypeApi;
  @Autowired
  GridModelApi gridModelApi;
  @Autowired
  InvocationApi invocationApi;
  @Autowired
  SearchIndex<BinaryContentData> binaryContentDataSearchIndex;

  private static final Logger log =
      LoggerFactory.getLogger(AttachmentListPageApiImpl.class);

  private static final List<String> ORDERED_COLUMNS = Arrays.asList(
      BinaryContentData.FILE_NAME);

  @Override
  public AttachmentList initModel(View view) {
    AttachmentList pageModel =
        objectApi.loadLatest(view.getObjectUri()).getObject(AttachmentList.class);

    initGrid(view.getUuid());
    refreshGrid(view.getUuid(), pageModel);

    UiActions.add(view, new UiAction().code(SAVE),
        new UiAction().code(SAVE_AND_PERFORM_ACTION_ON_SELECTED_ATTACHMENTS),
        new UiAction().code(UPLOAD_ATTACHMENTS).inputType(UiActionInputType.MULTIPLE_FILES),
        new UiAction().code(DEFAULT_CLOSE));

    return pageModel;
  }

  private void initGrid(UUID viewUuid) {
    GridModel gridModel =
        gridModelApi.createGridModel(binaryContentDataSearchIndex.getDefinition().getDefinition(),
            ORDERED_COLUMNS, BinaryContentData.class.getSimpleName());
    gridModel.getView().getDescriptor().selectionMode(GridSelectionMode.MULTIPLE);
    gridModelApi.initGridInView(viewUuid, GRID_ID, gridModel);
    gridModelApi.addGridPageCallback(viewUuid, GRID_ID,
        invocationApi.builder(AttachmentListPageApi.class)
            .build(a -> a.onGridPageRender(null)));
  }

  private void refreshGrid(UUID viewUuid, AttachmentList pageModel) {
    List<BinaryContentData> contents = pageModel.getContents();
    gridModelApi.setData(viewUuid, GRID_ID, BinaryContentData.class, contents);
  }

  @Override
  public void uploadAttachments(UUID viewUuid, UiActionRequest request) {
    AttachmentList pageModel = getModel(viewUuid);
    List<UploadedFile> inputFiles =
        actionRequestHelper(request).getAsList(UiActions.INPUT, UploadedFile.class);
    inputFiles.stream().map(file -> {
      BinaryData data = file.getData();
      String filename = file.getFilename();
      String mimeType = mimeTypeApi.getMimeType(filename);
      String extension = mimeTypeApi.getExtension(mimeType);
      data.setMimeType(mimeType);
      URI dataUri =
          objectApi.saveAsNew(SCHEMA, new BinaryDataObject(data));
      UserActivityLog created = sessionApi.createActivityLog();
      return new BinaryContentData()
          .dataUri(dataUri)
          .fileName(filename)
          .created(created)
          .updated(created)
          .mimeType(mimeType)
          .extension(extension)
          .size(file.getSize())
          .contentHash(data.hashIfPresent());
    }).forEach(pageModel::addContentsItem);
    setModel(viewUuid, pageModel);
    refreshGrid(viewUuid, pageModel);
  }

  @Override
  public void deleteAttachment(UUID viewUuid, String gridId, String nodeId,
      UiActionRequest request) {
    AttachmentList model = getModel(viewUuid);
    model.getContents().remove(Integer.parseInt(nodeId));
    setModel(viewUuid, model);
    refreshGrid(viewUuid, model);
  }

  @Override
  public void save(UUID viewUuid, UiActionRequest request) {
    AttachmentList model = getModel(viewUuid);
    if (model.getUri() == null) {
      objectApi.saveAsNew(SCHEMA, model);
    } else {
      ObjectNode listNode = objectApi.loadLatest(model.getUri());
      listNode.setValue(model.getContents(), AttachmentList.CONTENTS);
      objectApi.save(listNode);
    }
    viewApi.closeView(viewUuid);
  }

  @Override
  public void saveAndPerformActionOnSelected(UUID viewUuid, UiActionRequest request) {
    AttachmentList attachmentList = getModel(viewUuid);
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, GRID_ID);
    List<BinaryContentData> datasToWorkWith = new ArrayList<>();
    gridModel.getPage().getRows().forEach(row -> {
      if (Boolean.TRUE.equals(row.getSelected())) {
        BinaryContentData data = attachmentList.getContents().get(Integer.parseInt(row.getId()));
        datasToWorkWith.add(data);
      }
    });
    InvocationRequest callback = viewApi.getCallback(viewUuid, CALLBACK);
    try {
      invocationApi.invoke(callback, callback.getParameters().get(0).getValue(), datasToWorkWith);
    } catch (ApiNotFoundException e) {
      log.error(e.getMessage(), e);
    }
    save(viewUuid, new UiActionRequest().code(SAVE).params(Collections.emptyMap()));
  }

  @Override
  public void downloadAttachment(UUID viewUuid, String gridId, String nodeId,
      UiActionRequest request) {
    int id = Integer.parseInt(nodeId);
    BinaryContentData binaryContentData = getModel(viewUuid).getContents().get(id);
    viewApi.getView(viewUuid).putDownloadableItemsItem(DOWNLOADABLE_ITEM_ID,
        binaryContentData.getDataUri());
    viewApi.downloadFile(new DownloadedFile().uuid(viewUuid).identifier(DOWNLOADABLE_ITEM_ID)
        .filename(binaryContentData.getFileName()));
  }

  @Override
  public GridPage onGridPageRender(GridPage gridPage) {
    if (gridPage != null) {
      gridPage.getRows()
          .forEach(row -> {
            row.addActionsItem(
                new UiAction().code(DOWNLOAD_ATTACHMENT));
            row.addActionsItem(new UiAction().code(DELETE_ATTACHMENT));
          });
    }
    return gridPage;
  }

}
