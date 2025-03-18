package org.smartbit4all.bff.api.attachmentgrid.builders;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridApi;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;

public class AttachmentGridDialogBuilder
    extends AttachmentGridBuilder<AttachmentGridDialogBuilder> {

  private static final Logger log = LoggerFactory.getLogger(AttachmentGridDialogBuilder.class);

  protected AttachmentGridDialogBuilder(AttachmentGridApi attachmentGridApi) {
    super(attachmentGridApi);
  }

  public AttachmentGridDialogBuilder dialogTitle(String dialogTitle) {
    Objects.requireNonNull(dialogTitle, "dialogTitle cannot be null!");
    this.options.dialogTitle(dialogTitle);
    return self();
  }

  public AttachmentGridDialogBuilder closeOnSave(Boolean value) {
    Objects.requireNonNull(value, "value cannot be null!");
    this.options.closeOnSave(value);
    return self();
  }

  @Override
  protected AttachmentGridDialogBuilder self() {
    return this;
  }

  @Override
  protected void checkParameters() {
    super.checkParameters();
  }

  @Override
  protected AttachmentGridDescriptor build() {
    AttachmentGridDescriptor descriptor = super.build();
    return descriptor;
  }

  @Override
  public void run() {
    AttachmentGridDescriptor descriptor = this.build();
    GridModel gridModel = this.getGridModel(descriptor);
    attachmentGridApi.createDialogAttachmentGrid(descriptor, gridModel);
  }
}
