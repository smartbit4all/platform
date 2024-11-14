package org.smartbit4all.api.view.grid;

import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface GridExportApi extends PrimaryApi<GridExportContributorApi> {

  String SCHEMA = "gridExport";
  String EXPORT_GRID = "exportGrid";

  BinaryContentData exportGrid(UUID viewUuid, String gridIdentifier);

  BinaryContentData exportGrid(UUID viewUuid, String gridIdentifier, String mimetype);

  void exportGridAction(UUID viewUuid, UiActionRequest request, String gridId);
}
