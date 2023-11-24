package org.smartbit4all.bff.api.org;

import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

public interface GroupListPageApi extends PageApi<Object> {

  public static final String GROUP_GRID = "GROUP_GRID";
  public static final String OPEN_GROUP_EDITOR = "OPEN_GROUP_EDITOR";
  public static final String ADD_GROUP = "ADD_GROUP";

  Object createPageModel(View view);

  GridPage extendPageData(GridPage page);

  @WidgetActionHandler(value = OPEN_GROUP_EDITOR, widget = GROUP_GRID)
  void openGroupEditor(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request);

  @ActionHandler(ADD_GROUP)
  void openAddGroupDialog(UUID viewUuid,
      UiActionRequest request);


}
