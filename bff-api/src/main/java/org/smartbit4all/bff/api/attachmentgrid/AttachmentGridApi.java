package org.smartbit4all.bff.api.attachmentgrid;

import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;

public interface AttachmentGridApi {

  /**
   * Sets up an attachment grid based on the {@link AttachmentGridDescriptor}. This requires the
   * gridWidgetId to be unique and the grid needs to be placed on a
   * {@link SmartComponentLayoutDefinition}.
   * 
   * @param descriptor
   */
  void createAttachmentGrid(AttachmentGridDescriptor descriptor);

  /**
   * Sets up an attachment grid based on the {@link AttachmentGridDescriptor} and opens it in a
   * dialog window. This requires the gridWidgetId.
   * 
   * @param descriptor
   */
  void createDialogAttachmentGrid(AttachmentGridDescriptor descriptor);

}

