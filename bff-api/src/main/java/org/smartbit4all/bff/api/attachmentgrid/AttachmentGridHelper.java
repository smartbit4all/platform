package org.smartbit4all.bff.api.attachmentgrid;

import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;
import org.smartbit4all.core.object.ObjectApi;

final class AttachmentGridHelper {

  public static final String ATTACHMENT_GRID_DESCRIPTOR_POSTFIX =
      "_ATTACHMENT_GRID_DESCRIPTOR_POSTFIX";
  public static final String ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX =
      "_ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX";
  public static final String ATTACHMENT_TOOLBAR_POSTFIX =
      "_ATTACHMENT_TOOLBAR_POSTFIX";

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

  static public void getOriginalAttachmentList(
      AttachmentGridDescriptor descriptor,
      ViewApi viewApi) {
    View view = viewApi.getView(descriptor.getViewUuid());
    view.putParametersItem(
        descriptor.getGridWidgetId() + ATTACHMENT_GRID_ORIGINAL_LIST_POSTFIX,
        descriptor.getAttachmentList());
  }

  static public void setGrid(
      AttachmentGridDescriptor descriptor,
      GridModelApi gridModelApi, ViewApi viewApi) {

    gridModelApi.setData(
        descriptor.getViewUuid(),
        descriptor.getGridWidgetId(),
        BinaryContentData.class,
        descriptor.getAttachmentList());

    if (descriptor.getPageSize() != null) {
      gridModelApi.setPageSize(
          descriptor.getViewUuid(),
          descriptor.getGridWidgetId(),
          (m) -> descriptor.getPageSize());
    } else if (!descriptor.getIsPaginatorEnabled() && descriptor.getPageSize() == null) {
      gridModelApi.setPageSize(
          descriptor.getViewUuid(),
          descriptor.getGridWidgetId(),
          (m) -> m.getTotalRowCount());
    }
    saveDescriptorToView(descriptor, viewApi);
  }

}
