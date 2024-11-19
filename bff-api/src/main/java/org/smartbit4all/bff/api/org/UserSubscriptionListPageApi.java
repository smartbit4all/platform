package org.smartbit4all.bff.api.org;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.search.SearchPageApi;

public interface UserSubscriptionListPageApi extends SearchPageApi {

  GridPage onPageRender(GridPage page);

  String REVOKE = "ROW_REVOKE";

  String MODIFY = "ROW_MODIFY";

  @WidgetActionHandler(REVOKE)
  void revoke(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request);

  @WidgetActionHandler(MODIFY)
  void modify(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request);
}
