package org.smartbit4all.bff.api.assoc;

import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.core.object.ObjectNode;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public interface AssociationGridApi {

  void initGridInView(
      UUID viewUuid,
      String associationConf,
      URI objectUri,
      String gridIdentifier);

  void initGridInView(
      UUID viewUuid,
      String associationConf,
      ObjectNode objectNode,
      String gridIdentifier);

  GridPage onGridPageRender(GridPage gridPage, List<String> widgetActionCodes);
}
