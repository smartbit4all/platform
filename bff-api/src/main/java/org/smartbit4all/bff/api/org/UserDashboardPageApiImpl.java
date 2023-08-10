package org.smartbit4all.bff.api.org;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.userselector.bean.UserEditingModel;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDashboardPageApiImpl extends PageApiImpl<Object> implements UserDashboardPageApi {

  @Autowired
  GridModelApi gridModelApi;

  @Autowired
  InvocationApi invocationApi;

  @Autowired
  SearchIndex<User> userSearch;

  @Autowired
  OrgApi orgApi;

  public UserDashboardPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    Object pageModel = createPageModel(view);

    view.actions(ADMIN_ACTIONS);

    gridModelApi.setDataFromUris(view.getUuid(), USER_GRID, userSearch,
        orgApi.getAllUsers().stream().map(User::getUri));

    return pageModel;
  }

  @Override
  public Object createPageModel(View view) {

    UserEditingModel pageModel = new UserEditingModel();

    GridModel userGridModel = gridModelApi.createGridModel(
        userSearch.getDefinition().getDefinition(), orderedColumns, User.class.getSimpleName());

    gridModelApi.initGridInView(view.getUuid(), USER_GRID, userGridModel);


    gridModelApi.addGridPageCallback(view.getUuid(), USER_GRID,
        invocationApi.builder(UserDashboardPageApi.class)
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
        row.actions(GRID_ACTIONS);
      }
    }

    return page;
  }


  @Override
  public void openUserEditor(UUID viewUuid, String gridId, String rowId, UiActionRequest request) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, USER_GRID);

    URI userUri = objectApi.asType(URI.class,
        GridModels.getValueFromGridRow(gridModel, rowId, User.URI));

    viewApi
        .showView(new View().viewName(getUserEditorPageName()).objectUri(userUri));
  }


  @Override
  public void openAddUserDialog(UUID viewUuid, UiActionRequest request) {
    viewApi
        .showView(new View().viewName(getUserEditorPageName()));
  }

  protected String getUserEditorPageName() {
    return OrgViewNames.USER_EDITOR_PAGE;
  }

}
