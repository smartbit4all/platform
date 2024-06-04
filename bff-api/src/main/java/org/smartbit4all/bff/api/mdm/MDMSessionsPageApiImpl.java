package org.smartbit4all.bff.api.mdm;

import static org.smartbit4all.core.utility.StringConstant.joinDot;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.config.PlatformSearchIndexConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.mdm.MDMActions;
import org.smartbit4all.api.mdm.MDMModificationApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationState;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.Style;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.search.SearchPageApiImpl;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class MDMSessionsPageApiImpl extends SearchPageApiImpl
    implements MDMSessionsPageApi {

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class SessionsPageContext {

    View view;
    MDMDefinition definition;

    public SessionsPageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      setDefinition(parameters);
      return this;
    }

    private final void setDefinition(ObjectMapHelper parameters) {
      definition = masterDataManagementApi
          .getDefinition(parameters.require(PARAM_MDM_DEFINITION, String.class));
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, getDefinition().getAdminGroupName());
    }

    public boolean getBranchActive() {
      MDMModification modification =
          masterDataManagementApi.getModificationEditingByUser(getDefinition().getName(),
              sessionApi.getUserUri());
      return modification != null;
    }

    public View getView() {
      return view;
    }

    public MDMDefinition getDefinition() {
      return definition;
    }

    public boolean isAdmin() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminGroupName());
    }

    public boolean isAdminApprover() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminApproverGroupName());
    }

  }

  protected SessionsPageContext getContextByViewUUID(UUID viewUuid) {
    SessionsPageContext result = new SessionsPageContext();
    result.view = viewApi.getView(viewUuid);
    return result.loadByView();
  }

  protected SessionsPageContext getContextByView(View view) {
    SessionsPageContext result = new SessionsPageContext();
    result.view = view;
    return result.loadByView();
  }


  @Override
  public SearchPageModel initModel(View view) {

    SessionsPageContext pageContext = new SessionsPageContext();
    pageContext.view = view;
    pageContext.loadByView();

    List<String> columns = Arrays.asList(
        MDMModification.NAME,
        PlatformSearchIndexConfig.STATE_NAME,
        joinDot(MDMModification.CREATED, UserActivityLog.NAME),
        joinDot(MDMModification.CREATED, UserActivityLog.TIMESTAMP),
        joinDot(MDMModification.SENT_TO_APPROVAL, UserActivityLog.NAME),
        joinDot(MDMModification.SENT_TO_APPROVAL, UserActivityLog.TIMESTAMP),
        MDMModification.APPROVER,
        joinDot(MDMModification.APPROVED, UserActivityLog.NAME),
        joinDot(MDMModification.APPROVED, UserActivityLog.TIMESTAMP),
        MDMModification.ID,
        MDMModification.BRANCH_URI);
    SearchPageConfig searchPageConfig = createSimpleConfig(
        PlatformApiConfig.DEFAULT_SCHEME,
        MDMModification.class.getSimpleName(),
        columns,
        MDMModification.class.getSimpleName());

    searchPageConfig.filterModel(createFilterExpressionBuilderModel());

    ObjectMapHelper params = parameters(view);
    params.put(PARAM_SEARCHPAGECONFIG, searchPageConfig);
    params.put(PARAM_GRID_PAGE_RENDER_CALLBACK,
        invocationApi.builder(MDMSessionsPageApi.class)
            .build(api -> api.onGridPageRender(null, view.getUuid())));
    SearchPageModel model = super.initModel(view);
    model.setPageTitle(null);
    view.getActions().removeIf(a -> ACTION_CLOSE.equals(a.getCode()));
    view.getActions().removeIf(a -> ACTION_QUERY.equals(a.getCode()));
    if (pageContext.checkAdmin()) {
      view.addActionsItem(new UiAction().code(MDMActions.ACTION_START_EDITING)
          .inputType(UiActionInputType.TEXTFIELD));
    }
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, view.getUuid(), WIDGET_RESULT_GRID);
    GridModels.hideColumns(gridModel, MDMModification.ID, MDMModification.BRANCH_URI);
    gridModel.setPaginator(true);

    view.style(new Style().addClassesToAddItem("mdm-session-page"));
    return model;
  }

  protected FilterExpressionBuilderModel createFilterExpressionBuilderModel() {
    return new FilterExpressionBuilderModel();
  }

  @Override
  protected Stream<ObjectNode> getNodesToQuery(PageContext ctx) {
    SessionsPageContext myCtx = getContextByView(ctx.view);
    ObjectNodeReference stateRef =
        objectApi.loadLatest(myCtx.definition.getUri()).ref(MDMDefinition.STATE);
    if (stateRef.isEmpty()) {
      // possible?
      return Stream.empty();
    }
    ObjectNode stateNode = stateRef.get();
    return getNodesToQueryByDefitinionState(ctx, stateNode);
  }

  protected Stream<ObjectNode> getNodesToQueryByDefitinionState(PageContext ctx,
      ObjectNode stateNode) {

    return stateNode
        .getValueAsList(MDMModification.class, MDMDefinitionState.ACTIVE_MODIFICATIONS)
        .stream()
        .map(mod -> objectApi.create(PlatformApiConfig.DEFAULT_SCHEME, mod));
  }

  @Override
  public void startEditing(UUID viewUuid, UiActionRequest request) {
    String name = getNameFromRequest(request);
    SessionsPageContext ctx = getContextByViewUUID(viewUuid);
    masterDataManagementApi.initiateModificationBranch(ctx.definition.getName(),
        name);
    refreshGridData(viewUuid);
  }

  private String getNameFromRequest(UiActionRequest request) {
    String name = (String) request.getParams().get(UiActions.INPUT);
    if (Strings.isNullOrEmpty(name)) {
      throw new IllegalArgumentException("A név kitöltése kötelező!");
    }
    return name;
  }

  @Override
  public void openEditing(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    SessionsPageContext ctx = getContextByViewUUID(viewUuid);
    MDMModificationApi modificationApi = getModificationApi(viewUuid, widgetId, nodeId, ctx);
    modificationApi.startEditing();
    closeOrRefreshPage(ctx);
  }

  private MDMModificationApi getModificationApi(UUID viewUuid, String widgetId, String nodeId,
      SessionsPageContext ctx) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    String modificationId =
        (String) GridModels.getValueFromGridRow(gridModel, nodeId, MDMModification.ID);
    MDMModificationApi modificationApi =
        masterDataManagementApi.getModificationApi(ctx.definition.getName(), modificationId);
    if (modificationApi == null) {
      throw new IllegalStateException("Modification is not available");
    }
    return modificationApi;
  }

  @Override
  public void renameEditing(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    String name = getNameFromRequest(request);
    SessionsPageContext ctx = getContextByViewUUID(viewUuid);
    MDMModificationApi modificationApi = getModificationApi(viewUuid, widgetId, nodeId, ctx);
    modificationApi.renameEditing(name);
    ctx.loadByView();
    refreshGridData(ctx.view.getUuid());
  }

  @Override
  public GridPage onGridPageRender(GridPage page, UUID viewUuid) {
    SessionsPageContext ctx = getContextByViewUUID(viewUuid);
    boolean isAdmin = ctx.isAdmin();
    for (GridRow row : page.getRows()) {
      MDMModificationState modificationState = MDMModificationState
          .fromValue(GridModels.getValueFromGridRow(row, MDMModification.STATE).toString());
      if (MDMModificationState.APPROVED != modificationState
          && MDMModificationState.DISPOSED != modificationState) {
        row.addActionsItem(new UiAction().code(MDMActions.ACTION_OPEN_EDITING));
        if (isAdmin) {
          row.addActionsItem(new UiAction()
              .code(MDMActions.ACTION_RENAME_EDITING)
              .inputType(UiActionInputType.TEXTFIELD));
        }
      }

    }
    return page;
  }

  protected void closeOrRefreshPage(SessionsPageContext ctx) {
    if (ctx.definition.getBranchingStrategy() == MDMBranchingStrategy.STRICT_PARALLEL) {
      showAdminPage(ctx);
    } else {
      ctx.loadByView();
      refreshGridData(ctx.view.getUuid());
    }
  }

  protected void showAdminPage(SessionsPageContext ctx) {
    viewApi.showView(new View()
        .viewName("Admin")
        .putParametersItem(MDMAdminPageApi.PARAM_MDM_DEFINITION,
            ctx.definition.getName()));
  }

}
