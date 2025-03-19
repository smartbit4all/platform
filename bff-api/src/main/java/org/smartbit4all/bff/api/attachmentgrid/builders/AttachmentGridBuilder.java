package org.smartbit4all.bff.api.attachmentgrid.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.collection.bean.SearchIndexDescriptor;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridApi;
import org.smartbit4all.bff.api.attachmentgrid.bean.AdditionalAttachmentAction;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridDescriptor;
import org.smartbit4all.bff.api.attachmentgrid.bean.AttachmentGridOptions;
import org.smartbit4all.bff.api.attachmentgrid.bean.ButtonDescriptor;

public abstract class AttachmentGridBuilder<T extends AttachmentGridBuilder<T>> {
  protected final AttachmentGridApi attachmentGridApi;

  protected String logicalSchema;
  protected String gridWidgetId;
  protected InvocationRequest saveRequest;
  protected List<BinaryContentData> attachmentList;
  protected SearchIndexDescriptor searchIndex;

  protected List<AdditionalAttachmentAction> additionalActions;
  protected List<String> columns;

  protected ButtonDescriptor saveButtonDescriptor;
  protected ButtonDescriptor uploadButtonDescriptor;
  protected ButtonDescriptor refreshButtonDescriptor;

  protected AttachmentGridOptions options;
  protected UnaryOperator<GridModel> beforeInitCallback;

  protected AttachmentGridBuilder(AttachmentGridApi attachmentGridApi) {
    this.attachmentGridApi = attachmentGridApi;
    this.options = new AttachmentGridOptions();
    this.additionalActions = new ArrayList<>();
    this.attachmentList = new ArrayList<>();
    this.columns = new ArrayList<>();
  }

  public static AttachmentGridDialogBuilder createDialog(AttachmentGridApi attachmentGridApi) {
    AttachmentGridDialogBuilder builder = new AttachmentGridDialogBuilder(attachmentGridApi);
    return builder;
  }

  public static AttachmentGridStandardBuilder createStandard(AttachmentGridApi attachmentGridApi) {
    AttachmentGridStandardBuilder builder = new AttachmentGridStandardBuilder(attachmentGridApi);
    return builder;
  }

  public T attachmentList(List<BinaryContentData> attachmentList) {
    Objects.requireNonNull(attachmentList, "attachmentList cannot be null!");
    this.attachmentList = attachmentList;
    return self();
  }

  public T searchIndex(SearchIndexDescriptor searchIndex) {
    Objects.requireNonNull(searchIndex, "searchIndex cannot be null!");
    this.searchIndex = searchIndex;
    return self();
  }

  public T logicalSchema(String logicalSchema) {
    Objects.requireNonNull(logicalSchema, "logicalSchema cannot be null!");
    this.logicalSchema = logicalSchema;
    return self();
  }

  public T gridWidgetId(String gridWidgetId) {
    Objects.requireNonNull(gridWidgetId, "gridWidgetId cannot be null!");
    this.gridWidgetId = gridWidgetId;
    return self();
  }

  public T saveRequest(InvocationRequest saveRequest) {
    Objects.requireNonNull(saveRequest, "saveRequest cannot be null!");
    this.saveRequest = saveRequest;
    return self();
  }

  public T additionalActions(List<AdditionalAttachmentAction> additionalActions) {
    Objects.requireNonNull(additionalActions, "additionalActions cannot be null!");
    for (AdditionalAttachmentAction additionalAction : additionalActions) {
      Objects.requireNonNull(additionalAction, "additionalAction cannot be null!");
      Objects.requireNonNull(additionalAction.getCode(), "additionalAction.code cannot be null!");
      Objects.requireNonNull(additionalAction.getActionInvocationRequest(),
          "additionalAction.actionInvocationRequest cannot be null!");
    }
    this.additionalActions = additionalActions;
    return self();
  }

  public T addAdditionalActions(AdditionalAttachmentAction additionalAction) {
    Objects.requireNonNull(additionalAction, "additionalAction cannot be null!");
    Objects.requireNonNull(additionalAction.getCode(), "additionalAction.code cannot be null!");
    Objects.requireNonNull(additionalAction.getActionInvocationRequest(),
        "additionalAction.actionInvocationRequest cannot be null!");
    this.additionalActions.add(additionalAction);
    return self();
  }

  public T columns(List<String> columns) {
    Objects.requireNonNull(columns, "columns cannot be null!");
    this.columns = columns;
    return self();
  }

  public T saveButtonDescriptor(ButtonDescriptor saveButtonDescriptor) {
    Objects.requireNonNull(saveButtonDescriptor, "saveButtonDescriptor cannot be null!");
    this.saveButtonDescriptor = saveButtonDescriptor;
    return self();
  }

  public T uploadButtonDescriptor(ButtonDescriptor uploadButtonDescriptor) {
    Objects.requireNonNull(uploadButtonDescriptor, "uploadButtonDescriptor cannot be null!");
    this.uploadButtonDescriptor = uploadButtonDescriptor;
    return self();
  }

  public T refreshButtonDescriptor(ButtonDescriptor refreshButtonDescriptor) {
    Objects.requireNonNull(refreshButtonDescriptor, "refreshButtonDescriptor cannot be null!");
    this.refreshButtonDescriptor = refreshButtonDescriptor;
    return self();
  }

  public T options(AttachmentGridOptions options) {
    Objects.requireNonNull(options, "options cannot be null!");
    this.options = options;
    return self();
  }

  public T previewable(Boolean value) {
    Objects.requireNonNull(value, "value cannot be null!");
    this.options.isPreviewable(value);
    return self();
  }

  public T downloadable(Boolean value) {
    Objects.requireNonNull(value, "value cannot be null!");
    this.options.isDownloadable(value);
    return self();
  }

  public T autoSave(Boolean value) {
    Objects.requireNonNull(value, "value cannot be null!");
    this.options.autoSave(value);
    return self();
  }

  public T multipleInput(Boolean value) {
    Objects.requireNonNull(value, "value cannot be null!");
    this.options.isMultipleInput(value);
    return self();
  }

  public T editable(Boolean value) {
    Objects.requireNonNull(value, "value cannot be null!");
    this.options.isEditable(value);
    return self();
  }

  public T beforeInitCallback(UnaryOperator<GridModel> beforeInitCallback) {
    Objects.requireNonNull(beforeInitCallback, "beforeInitCallback cannot be null!");
    this.beforeInitCallback = beforeInitCallback;
    return self();
  }

  protected GridModel getGridModel(AttachmentGridDescriptor descriptor) {
    Objects.requireNonNull(descriptor, "descriptor cannot be null!");
    GridModel gridModel = attachmentGridApi.createGridModel(this.columns, descriptor);

    if (this.beforeInitCallback != null) {
      return this.beforeInitCallback.apply(gridModel);
    }
    return gridModel;
  }

  protected void checkParameters() {
    Objects.requireNonNull(this.gridWidgetId, "gridWidgetId cannot be null!");
    Objects.requireNonNull(this.logicalSchema, "logicalSchema cannot be null!");

    if (this.options.getIsEditable()) {
      Objects.requireNonNull(this.saveRequest, "saveRequest cannot be null!");
      InvocationParameter invocationParameter = this.saveRequest.getParameters().get(0);
      if (!invocationParameter.getTypeClass().equals("java.util.List")) {
        throw new IllegalArgumentException(
            "SaveRequest first parameter should be List<BinaryContentData>");
      }
    }
  }

  protected AttachmentGridDescriptor build() {
    this.checkParameters();
    AttachmentGridDescriptor descriptor = new AttachmentGridDescriptor()
        .gridWidgetId(this.gridWidgetId)
        .attachmentList(this.attachmentList)
        .searchIndex(this.searchIndex)
        .logicalSchema(this.logicalSchema)
        .saveRequest(this.saveRequest)
        .additionalActions(this.additionalActions)
        .saveButtonDescriptor(this.saveButtonDescriptor)
        .uploadButtonDescriptor(this.uploadButtonDescriptor)
        .refreshButtonDescriptor(this.refreshButtonDescriptor)
        .options(this.options)
        .columns(this.columns);
    return descriptor;
  }

  protected abstract T self();

  public abstract void run();
}
