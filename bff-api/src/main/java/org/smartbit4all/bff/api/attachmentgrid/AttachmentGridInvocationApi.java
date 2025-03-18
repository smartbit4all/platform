package org.smartbit4all.bff.api.attachmentgrid;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;

public interface AttachmentGridInvocationApi {

  GridPage extendPageDataForAttachment(GridPage page, UUID viewUuid,
      String widgetId);

  void addAttachment(UUID viewUuid, UiActionRequest request);

  void refreshGridToOriginalState(UUID viewUuid, UiActionRequest request, String widgetId);

  void openAttachmentFromGrid(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  void downloadAttachmentFromGrid(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  void removeAttachment(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request);

  void saveListRequest(UUID viewUuid, UiActionRequest request, String widgetId);

  void closeDialogWindow(UUID viewUuid, UiActionRequest request);

  void saveModel(UUID viewUuid, UiActionRequest request);

  void saveModel(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request);

  public List<UiAction> getUiActions(AttachmentGridDescriptor descriptor);

  public List<ViewEventHandler> getEventHandlers(AttachmentGridDescriptor descriptor);
}
