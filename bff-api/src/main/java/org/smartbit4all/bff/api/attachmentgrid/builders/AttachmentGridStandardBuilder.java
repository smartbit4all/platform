package org.smartbit4all.bff.api.attachmentgrid.builders;

import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridApi;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;

public class AttachmentGridStandardBuilder
    extends AttachmentGridBuilder<AttachmentGridStandardBuilder> {

  protected AttachmentGridStandardBuilder(AttachmentGridApi attachmentGridApi) {
    super(attachmentGridApi);
  }

  private UUID viewUUID;

  @Override
  protected AttachmentGridStandardBuilder self() {
    return this;
  }

  public AttachmentGridStandardBuilder viewUUID(UUID viewUUID) {
    Objects.requireNonNull(viewUUID, "viewUUID cannot be null!");
    this.viewUUID = viewUUID;
    return self();
  }

  @Override
  protected void checkParameters() {
    Objects.requireNonNull(this.viewUUID, "viewUUID cannot be null!");
    super.checkParameters();
  }

  @Override
  protected AttachmentGridDescriptor build() {
    AttachmentGridDescriptor descriptor = super.build();
    return descriptor.viewUuid(this.viewUUID);
  }

  @Override
  public void run() {
    AttachmentGridDescriptor descriptor = this.build();
    GridModel gridModel = this.getGridModel(descriptor);
    attachmentGridApi.createAttachmentGrid(descriptor, gridModel);
  }

}
