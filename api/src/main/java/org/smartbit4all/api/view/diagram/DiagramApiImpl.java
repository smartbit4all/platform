package org.smartbit4all.api.view.diagram;

import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.diagram.bean.DiagramModel;
import org.smartbit4all.api.diagram.bean.DiagramWidgetModel;
import org.smartbit4all.api.view.ViewApi;
import org.springframework.beans.factory.annotation.Autowired;

public class DiagramApiImpl implements DiagramApi {

  @Autowired(required = false)
  private ViewApi viewApi;

  @Override
  public void initDiagramInView(UUID viewUuid, String diagramId, DiagramModel diagramModel) {

    Objects.requireNonNull(viewUuid, "viewUuid cannot be null!");
    Objects.requireNonNull(diagramId, "diagramId cannot be null!");
    Objects.requireNonNull(diagramModel, "diagramModel cannot be null!");

    DiagramWidgetModel diagramWidgetModel = new DiagramWidgetModel()
        .diagramModel(diagramModel)
        .viewUuid(viewUuid)
        .identifier(diagramId);

    viewApi.setWidgetModelInView(DiagramWidgetModel.class, viewUuid, diagramId, diagramWidgetModel);
  }


  @Override
  public DiagramWidgetModel getModel(UUID viewUuid, String diagramId) {
    return getModelInner(viewUuid, diagramId);
  }

  @Override
  public void setModel(UUID viewUuid, String diagramId, DiagramModel diagramModel) {
    setModelInner(viewUuid, diagramId, diagramModel);
  }

  @Override
  public BinaryContentData getPicture(UUID viewUuid, String diagramId) {
    // TODO Auto-generated method stub
    return null;
  }


  private DiagramWidgetModel getModelInner(UUID viewUuid, String diagramId) {
    return viewApi.getWidgetModelFromView(DiagramWidgetModel.class, viewUuid, diagramId);
  }

  private void setModelInner(UUID viewUuid, String diagramId, DiagramModel diagramModel) {

    DiagramWidgetModel diagramWidgetModel = getModelInner(viewUuid, diagramId);
    diagramWidgetModel.diagramModel(diagramModel);
    viewApi.setWidgetModelInView(DiagramWidgetModel.class, viewUuid, diagramId, diagramWidgetModel);
  }
}
