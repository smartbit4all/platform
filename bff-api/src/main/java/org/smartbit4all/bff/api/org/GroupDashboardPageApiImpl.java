package org.smartbit4all.bff.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.groupselector.bean.GroupEditingModel;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.springframework.beans.factory.annotation.Autowired;

public class GroupDashboardPageApiImpl extends PageApiImpl<Object>
    implements GroupDashboardPageApi {

  @Autowired
  GridModelApi gridModelApi;

  @Autowired
  InvocationApi invocationApi;

  @Autowired
  SearchIndex<Group> groupSearch;

  @Autowired
  OrgApi orgApi;


  public GroupDashboardPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    Object pageModel = createPageModel(view);

    view.actions(getGroupListActions());

    gridModelApi.setDataFromUris(view.getUuid(), GROUP_GRID, groupSearch,
        orgApi.getAllGroups().stream().map(Group::getUri));

    return pageModel;
  }

  @Override
  public Object createPageModel(View view) {

    GroupEditingModel pageModel = new GroupEditingModel();

    GridModel groupGridModel = gridModelApi.createGridModel(
        groupSearch.getDefinition().getDefinition(), getGridColumns(), Group.class.getSimpleName());

    gridModelApi.initGridInView(view.getUuid(), GROUP_GRID, groupGridModel);

    gridModelApi.addGridPageCallback(view.getUuid(), GROUP_GRID,
        invocationApi.builder(GroupDashboardPageApi.class)
            .build(a -> a.extendPageData(null)));
    return pageModel;
  }

  @Override
  public GridPage extendPageData(GridPage page) {
    if (page == null) {
      return page;
    }

    if (page.getRows() != null) {
      for (GridRow row : page.getRows()) {

        boolean isBuiltIn =
            objectApi.asType(Boolean.class, GridModels.getValueFromGridRow(row, "builtIn"));

        if (!isBuiltIn) {
          addActionsToRow(row);
        } else {
          row.actions(new ArrayList<>());
        }
      }
    }

    return page;
  }

  protected void addActionsToRow(GridRow row) {
    row.actions(getGroupRowActions());
  }


  @Override
  public void openGroupEditor(UUID viewUuid, String gridId, String rowId, UiActionRequest request) {

    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, GROUP_GRID);

    URI groupUri = objectApi.asType(URI.class,
        GridModels.getValueFromGridRow(gridModel, rowId, Group.URI));

    viewApi
        .showView(new View().viewName(getGroupEditorName()).objectUri(groupUri));
  }


  @Override
  public void openAddGroupDialog(UUID viewUuid, UiActionRequest request) {
    viewApi
        .showView(new View().viewName(getGroupEditorName()));
  }

  protected String getGroupEditorName() {
    return OrgViewNames.GROUP_EDITOR_PAGE;
  }

  /**
   * @return the column names to show on ui in order
   */
  protected List<String> getGridColumns() {
    return Arrays.asList(User.NAME, User.USERNAME, User.EMAIL);
  }

  protected List<UiAction> getGroupRowActions() {
    return Arrays.asList(
        new UiAction().code(OPEN_GROUP_EDITOR));
  }

  protected List<UiAction> getGroupListActions() {
    return Arrays.asList(
        new UiAction().code(ADD_GROUP));
  }

}
