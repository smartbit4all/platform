package org.smartbit4all.bff.api.assoc;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectNode;

public interface AssociationGridApi {

  void initGridInView(
      View view,
      String associationConf,
      URI objectUri,
      String gridIdentifier);

  void initGridInView(
      View view,
      String associationConf,
      ObjectNode objectNode,
      String gridIdentifier);

  boolean isActionSupported(View view, UiActionRequest request);

  void performAction(View view, UiActionRequest request);

  void updateGrid(List<GridRow> gridRows, String configName, UUID viewUuid);

  // GridPage onGridPageRender(GridPage gridPage, AssociationGridConfig config);
}
