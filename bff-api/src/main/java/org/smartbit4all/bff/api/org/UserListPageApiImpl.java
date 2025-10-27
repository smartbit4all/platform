package org.smartbit4all.bff.api.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.ApiDescriptor;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationRegisterApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.bean.ObjectContainer;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectLayoutBuilder;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.annotation.Autowired;

public class UserListPageApiImpl extends PageApiImpl<Object> implements UserListPageApi {

  @Autowired
  protected GridModelApi gridModelApi;
  @Autowired
  protected InvocationApi invocationApi;
  @Autowired
  protected SearchIndex<User> userSearch;
  @Autowired
  protected InvocationRegisterApi invocationRegisterApi;
  @Autowired
  protected OrgApi orgApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  public UserListPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    ObjectContainer pageModel = new ObjectContainer();
    initGrid(view);
    refreshGrid(view.getUuid());


    view.actions(getUserListActions(view.getUuid()));
    ApiDescriptor registrationApi =
        invocationRegisterApi.getApi(ACCOUNT_REGISTRATION_UI_API_INTERFACE_NAME,
            ACCOUNT_REGISTRATION_UI_API_INTERFACE_NAME);
    if (registrationApi != null) {
      UiActions.add(view, INVITE_USER_ACTION.apply(localeSettingApi));
    }
    view.putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT,
        ObjectLayoutBuilder.grid(USER_GRID));
    return pageModel;
  }

  @Override
  public void initGrid(View view) {
    GridModel userGridModel = gridModelApi.createGridModel(
        userSearch.getDefinition().getDefinition(), getGridColumns(),
        User.class.getSimpleName());
    userGridModel.paginator(true);
    GridModels.hideColumns(userGridModel, List.of(User.URI));
    gridModelApi.initGridInView(view.getUuid(), USER_GRID, userGridModel);
    gridModelApi.addGridPageCallback(view.getUuid(), USER_GRID,
        invocationApi.builder(UserListPageApi.class)
            .build(a -> a.extendPageData(null)));
  }

  @Override
  public GridPage extendPageData(GridPage page) {
    if (page.getRows() != null) {
      for (GridRow row : page.getRows()) {
        addActionsToRow(row);
      }
    }
    return page;
  }

  protected void addActionsToRow(GridRow row) {
    row.getActions().addAll(getUserRowActions(row));
  }

  protected void refreshGrid(UUID viewUuid) {
    TableData<?> tableData =
        userSearch.executeSearch(new FilterExpressionList(), Collections.emptyList());
    gridModelApi.setData(viewUuid, USER_GRID, tableData);
    // gridModelApi.setDataFromUris(viewUuid, USER_GRID, userSearch,
    // orgApi.getAllUsers().stream().map(User::getUri));
  }

  @Override
  public void openUserEditor(UUID viewUuid, String gridId, String rowId, UiActionRequest request) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, USER_GRID);
    URI userUri = objectApi.asType(URI.class,
        GridModels.getValueFromGridRow(gridModel, rowId, User.URI));
    viewApi.showView(new View().viewName(getUserEditorPageName()).objectUri(userUri));
  }


  @Override
  public void openAddUserDialog(UUID viewUuid, UiActionRequest request) {
    viewApi.showView(new View().viewName(getUserEditorPageName()));
  }

  @Override
  public void inviteUser(UUID viewUuid, UiActionRequest request) {
    try {
      invocationApi.invoke(
          new InvocationRequest().name(REGISTRATION_UI_API_NAME)
              .interfaceClass(ACCOUNT_REGISTRATION_UI_API_INTERFACE_NAME)
              .methodName(OPEN_REGISTRATION_PAGE)
              .addParametersItem(new InvocationParameter()
                  .name("viewType")
                  .typeClass(ViewType.class.getName())),
          ViewType.DIALOG);
    } catch (ApiNotFoundException e) {
      e.printStackTrace();
    }

  }

  protected String getUserEditorPageName() {
    return OrgViewNames.USER_EDITOR_PAGE;
  }

  protected List<String> getGridColumns() {
    return Arrays.asList(User.URI, User.NAME, User.USERNAME, User.EMAIL, User.INACTIVE);
  }

  protected List<UiAction> getUserRowActions(GridRow row) {
    boolean isInactive = Boolean.TRUE.equals(GridModels.getValueFromGridRow(row, User.INACTIVE));
    if (isInactive) {
      return Arrays.asList(new UiAction().code(ACTIVATE_USER).confirm(true));
    } else {
      return Arrays.asList(new UiAction().code(OPEN_USER_EDITOR_PAGE),
          new UiAction().code(DEACTIVATE_USER).confirm(true));
    }
  }

  protected List<UiAction> getUserListActions(UUID viewUuid) {
    List<UiAction> actions = new ArrayList<>();
    actions.add(new UiAction().code(ADD_USER));
    return actions;
  }

  @Override
  public void activateUser(UUID viewUuid, String gridId, String rowId, UiActionRequest request) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, USER_GRID);
    URI userUri = objectApi.asType(URI.class,
        GridModels.getValueFromGridRow(gridModel, rowId, User.URI));
    orgApi.restoreDeletedUser(userUri);
    refreshGrid(viewUuid);
  }

  @Override
  public void deactivateUser(UUID viewUuid, String gridId, String rowId, UiActionRequest request) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, USER_GRID);
    URI userUri = objectApi.asType(URI.class,
        GridModels.getValueFromGridRow(gridModel, rowId, User.URI));
    orgApi.removeUser(userUri);
    refreshGrid(viewUuid);
  }

}
