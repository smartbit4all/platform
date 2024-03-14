package org.smartbit4all.bff.api.attachment;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.attachment.bean.AttachmentList;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.UploadedFile;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;


public class AttachmentListPageApiImpl extends PageApiImpl<AttachmentList>
    implements AttachmentListPageApi {

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

  private static final List<String> ORDERED_COLUMNS = Arrays.asList(
      BinaryContentData.FILE_NAME);

  @Override
  public AttachmentList initModel(View view) {
    AttachmentList pageModel =
        objectApi.loadLatest(view.getObjectUri()).getObject(AttachmentList.class);

    initGrid(view.getUuid());
    refreshGrid(view.getUuid(), pageModel);

    UiActions.add(view, SAVE_ACTION, UPLOAD_ATTACHMENTS_ACTION, new UiAction().code(DEFAULT_CLOSE));

    return pageModel;
  }

  private void initGrid(UUID viewUuid) {
    GridModel gridModel =
        gridModelApi.createGridModel(binaryContentDataSearchIndex.getDefinition().getDefinition(),
            ORDERED_COLUMNS, BinaryContentData.class.getSimpleName());
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
      return new BinaryContentData().dataUri(dataUri).fileName(filename).created(created)
          .updated(created).mimeType(mimeType).extension(extension).size(file.getSize());
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
  public GridPage onGridPageRender(GridPage gridPage) {
    if (gridPage != null) {
      gridPage.getRows().forEach(row -> row.addActionsItem(DELETE_ATTACHMENT_ACTION));
    }
    return gridPage;
  }

}
