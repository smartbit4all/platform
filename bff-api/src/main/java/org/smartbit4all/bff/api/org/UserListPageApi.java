package org.smartbit4all.bff.api.org;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface UserListPageApi extends PageApi<Object> {

  public static final List<String> orderedColumns =
      Arrays.asList(User.NAME, User.USERNAME, User.EMAIL);

  public static final String USER_GRID = "USER_GRID";

  public static final String OPEN_USER_EDITOR = "OPEN_USER_EDITOR";

  public static final String OPEN_USER_EDITOR_DIALOG = "OPEN_ADD_USER_DIALOG";

  public static final String USER_EDITOR_PAGE = "USER_EDITOR_PAGE";

  public static final String USER_EDITOR_DIALOG = "ADD_USER_DIALOG";


  List<UiAction> GRID_ACTIONS = Arrays.asList(
      new UiAction().code(OPEN_USER_EDITOR));

  List<UiAction> USER_ADMIN_ACTIONS = Arrays.asList(
      new UiAction().code(OPEN_USER_EDITOR_DIALOG));

  Object createPageModel(View view);

  GridPage extendPageData(GridPage page);

  @WidgetActionHandler(value = OPEN_USER_EDITOR, widget = USER_GRID)
  void openUserEditor(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request);

  @ActionHandler(OPEN_USER_EDITOR_DIALOG)
  void openAddUserDialog(UUID viewUuid,
      UiActionRequest request);

}
