package org.smartbit4all.bff.api.attachmentgrid.util;

import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_GRID_DESCRIPTOR_POSTFIX;
import static org.smartbit4all.bff.api.attachmentgrid.util.AttachmentGridConstants.ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;
import org.smartbit4all.core.object.ObjectApi;

final public class AttachmentGridHelper {

  private AttachmentGridHelper() {}

  static public void saveDescriptorToView(AttachmentGridDescriptor descriptor, ViewApi viewApi) {
    View view = viewApi.getView(descriptor.getViewUuid());
    view.putParametersItem(
        descriptor.getGridWidgetId() + ATTACHMENT_GRID_DESCRIPTOR_POSTFIX,
        descriptor);
  }

  static public AttachmentGridDescriptor getDescriptorFromView(View view, String widgetId,
      ObjectApi objectApi) {
    return objectApi.asType(
        AttachmentGridDescriptor.class,
        (view.getParameters().get(
            widgetId + ATTACHMENT_GRID_DESCRIPTOR_POSTFIX)));
  }

  static public void saveOriginalAttachmentList(
      AttachmentGridDescriptor descriptor,
      ViewApi viewApi) {
    View view = viewApi.getView(descriptor.getViewUuid());
    view.putParametersItem(
        descriptor.getGridWidgetId() + ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX,
        descriptor.getAttachmentList());
  }

}
