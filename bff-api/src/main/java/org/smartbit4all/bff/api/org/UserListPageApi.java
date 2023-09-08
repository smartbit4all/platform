package org.smartbit4all.bff.api.org;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface UserListPageApi extends PageApi<Object> {

  public static final String USER_GRID = "USER_GRID";
  public static final String OPEN_USER_EDITOR_PAGE = "OPEN_USER_EDITOR_PAGE";
  public static final String ADD_USER = "ADD_USER";

  Object createPageModel(View view);

  GridPage extendPageData(GridPage page);

  @WidgetActionHandler(value = OPEN_USER_EDITOR_PAGE, widget = USER_GRID)
  void openUserEditor(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request);

  @ActionHandler(ADD_USER)
  void openAddUserDialog(UUID viewUuid,
      UiActionRequest request);

}
