package org.smartbit4all.bff.api.attachmentgrid;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;

public interface AttachmentGridApi {

  /**
   * Sets up an attachment grid based on the {@link AttachmentGridDescriptor}. This requires the
   * gridWidgetId to be unique and the grid needs to be placed on a
   * {@link SmartComponentLayoutDefinition}.
   * 
   * @param descriptor
   * @param gridModel
   */
  void createAttachmentGrid(AttachmentGridDescriptor descriptor, GridModel gridModel);

  /**
   * Sets up an attachment grid based on the {@link AttachmentGridDescriptor} and opens it in a
   * dialog window. This requires the gridWidgetId.
   * 
   * @param descriptor
   * @param gridModel
   */
  void createDialogAttachmentGrid(AttachmentGridDescriptor descriptor, GridModel gridModel);

  /**
   * Creates a grid model, used in AttachmentGridBuilder.
   * 
   * @param columns
   * @param descriptor
   * @return
   */
  GridModel createGridModel(List<String> columns, AttachmentGridDescriptor descriptor);

  /**
   * Sets the list into the descriptor and grid.
   * 
   * @param fileList
   */
  void refreshGrid(UUID viewUuid, String gridId, List<BinaryContentData> fileList);

}

