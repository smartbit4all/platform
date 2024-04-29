package org.smartbit4all.bff.api.org;

import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.bff.api.search.SearchPageApi;

public interface UserSubscriptionListPageApi extends SearchPageApi {

  GridPage onPageRender(GridPage page);

}
