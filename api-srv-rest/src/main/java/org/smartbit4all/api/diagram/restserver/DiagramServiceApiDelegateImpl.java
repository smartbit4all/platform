package org.smartbit4all.api.diagram.restserver;

import java.util.UUID;
import org.smartbit4all.api.diagram.bean.DiagramWidgetModel;
import org.smartbit4all.api.view.diagram.DiagramApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class DiagramServiceApiDelegateImpl implements DiagramServiceApiDelegate {
  @Autowired
  private DiagramApi diagramApi;

  @Override
  public ResponseEntity<DiagramWidgetModel> load(UUID uuid, String identifier) throws Exception {
    return ResponseEntity.ok(diagramApi.getModel(uuid, identifier));
  }


}
