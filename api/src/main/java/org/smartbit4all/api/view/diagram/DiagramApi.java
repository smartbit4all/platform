package org.smartbit4all.api.view.diagram;

import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.diagram.bean.DiagramModel;

public interface DiagramApi {

  void initDiagramInView(UUID viewUuid, String diagramId, DiagramModel diagramModel);

  DiagramModel getModel(UUID viewUuid, String diagramId);

  void setModel(UUID viewUuid, String diagramId, DiagramModel diagramModel);

  BinaryContentData getPicture(UUID viewUuid, String diagramId);
}
