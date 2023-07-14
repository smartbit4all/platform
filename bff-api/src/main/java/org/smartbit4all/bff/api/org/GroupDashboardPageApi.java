package org.smartbit4all.bff.api.org;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface GroupDashboardPageApi extends PageApi<Object> {
  public static final List<String> orderedColumns =
      Arrays.asList(Group.TITLE, Group.KIND_CODE, Group.DESCRIPTION);

  public static final String GROUP_GRID = "USER_GRID";

  public static final String OPEN_GROUP_EDITOR = "OPEN_USER_EDITOR";

  public static final String OPEN_GROUP_EDITOR_DIALOG = "OPEN_ADD_USER_DIALOG";


  List<UiAction> GRID_ACTIONS = Arrays.asList(
      new UiAction().code(OPEN_GROUP_EDITOR));

  List<UiAction> ADMIN_ACTIONS = Arrays.asList(
      new UiAction().code(OPEN_GROUP_EDITOR_DIALOG));

  Object createPageModel(View view);

  GridPage extendPageData(GridPage page);

  @WidgetActionHandler(value = OPEN_GROUP_EDITOR, widget = GROUP_GRID)
  void openUserEditor(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request);

  @ActionHandler(OPEN_GROUP_EDITOR_DIALOG)
  void openAddUserDialog(UUID viewUuid,
      UiActionRequest request);


}
