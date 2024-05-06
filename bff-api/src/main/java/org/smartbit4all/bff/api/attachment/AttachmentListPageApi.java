package org.smartbit4all.bff.api.attachment;

import java.util.UUID;
import org.smartbit4all.api.attachment.bean.AttachmentList;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface AttachmentListPageApi extends PageApi<AttachmentList> {

  String SCHEMA = "bffAttachmentList";

  String GRID_ID = "ATTACHMENT_GRID";

  String CALLBACK = "callback";

  String UPLOAD_ATTACHMENTS = "UPLOAD_ATTACHMENTS";

  String DELETE_ATTACHMENT = "DELETE_ATTACHMENT";

  String SAVE = "SAVE";

  String SAVE_AND_PERFORM_ACTION_ON_SELECTED_ATTACHMENTS =
      "SAVE_AND_PERFORM_ACTION_ON_SELECTED_ATTACHMENTS";

  String DOWNLOAD_ATTACHMENT = "DOWNLOAD_ATTACHMENT";

  @ActionHandler(SAVE)
  void save(UUID viewUuid, UiActionRequest request);

  @ActionHandler(SAVE_AND_PERFORM_ACTION_ON_SELECTED_ATTACHMENTS)
  void saveAndPerformActionOnSelected(UUID viewUuid, UiActionRequest request);

  @ActionHandler(UPLOAD_ATTACHMENTS)
  void uploadAttachments(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(widget = GRID_ID, value = DELETE_ATTACHMENT)
  void deleteAttachment(UUID viewUuid, String gridId, String nodeId,
      UiActionRequest request);

  @WidgetActionHandler(widget = GRID_ID, value = DOWNLOAD_ATTACHMENT)
  void downloadAttachment(UUID viewUuid, String gridId, String nodeId,
      UiActionRequest request);

  GridPage onGridPageRender(GridPage gridPage);

}
