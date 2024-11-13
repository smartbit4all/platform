package org.smartbit4all.api.view.grid;

import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.grid.bean.GridModel;

public interface GridExportContributorApi extends ContributionApi {

  BinaryContentData exportGrid(GridModel gridModel, String gridId);

  boolean supportsMimetype(String mimetype);

}
