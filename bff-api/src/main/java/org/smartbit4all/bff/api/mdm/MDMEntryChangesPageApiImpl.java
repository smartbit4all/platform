package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMApprovalApi;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.UiActions.UiActionBuilder;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.mdm.utility.MDMActions;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryChangesPageApiImpl extends PageApiImpl<Object>
    implements MDMEntryChangesPageApi {

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired(required = false)
  private MDMApprovalApi mdmApprovalApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  protected InvocationApi invocationApi;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    public MDMDefinition definition;
    List<MDMEntryApi> entryApisWithChanges;
    Map<String, SearchIndex<BranchedObjectEntry>> searchIndexAdminsByDescriptorName;

    public PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      setDefition(parameters);
      entryApisWithChanges = definition.getDescriptors().keySet().stream()
          .map(descriptorName -> masterDataManagementApi.getApi(definition.getName(),
              descriptorName))
          .filter(entryApi -> entryApi.getBranchingList().stream()
              .anyMatch(e -> e.getBranchingState() != BranchingStateEnum.NOP))
          .collect(toList());
      searchIndexAdminsByDescriptorName = entryApisWithChanges.stream()
          .collect(toMap(MDMEntryApi::getName,
              e -> collectionApi.searchIndex(definition.getName(),
                  e.getDescriptor().getSearchIndexForEntries(),
                  BranchedObjectEntry.class)));

      return this;
    }

    public PageContext loadOnlyDefinitionByView() {
      setDefition(parameters(view));
      return this;
    }

    private final void setDefition(ObjectMapHelper parameters) {
      definition = masterDataManagementApi
          .getDefinition(parameters.require(PARAM_MDM_DEFINITION, String.class));
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, definition.getAdminGroupName());
    }

    public boolean getBranchActive() {
      ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
      ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
      MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
      return mdmDefinitionState.getGlobalModification() != null;
    }

    public URI getApprover() {
      ObjectNode definitionNode = objectApi.loadLatest(definition.getUri());
      ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
      MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
      if (mdmDefinitionState.getGlobalModification() != null) {
        return mdmDefinitionState.getGlobalModification().getApprover();
      } else {
        return null;
      }

    }

    public View getView() {
      return view;
    }

  }

  public MDMEntryChangesPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    PageContext pageContext = new PageContext();
    pageContext.view = view;
    pageContext.loadByView();
    refreshActions(pageContext);
    createOrRefreshGrids(pageContext);

    return new HashMap<String, Object>();
  }

  // private SmartComponentLayoutDefinition createChangesLayout(PageContext ctx) {
  // SmartComponentLayoutDefinition changesLayout =
  // container(LayoutDirection.VERTICAL);
  //
  // ctx.entryApisWithChanges.forEach(entryApi -> changesLayout
  // .addComponentsItem(
  // form(LayoutDirection.VERTICAL, label(null, entryApi.getDisplayNameList())))
  // .addComponentsItem(grid(entryApi.getName())));
  //
  // return changesLayout;
  // }

  protected void refreshActions(PageContext ctx) {
    if (ctx.definition.getBranchingStrategy() == MDMBranchingStrategy.GLOBAL) {
      boolean isAdmin = ctx.checkAdmin();
      boolean branchActive = ctx.getBranchActive();
      // if API present, approving enabled
      boolean approvingEnabled = mdmApprovalApi != null;
      UiActionBuilder uiActions = UiActions.builder();
      if (approvingEnabled) {
        URI approver = ctx.getApprover();
        boolean underApproval = approver != null;
        boolean isApprover = approver != null && approver.equals(sessionApi.getUserUri());
        boolean canEdit = canEdit(isAdmin, underApproval, isApprover);

        uiActions
            .addIf(new UiAction().code(MDMActions.ACTION_START_EDITING).confirm(true),
                isAdmin, !branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_CANCEL_CHANGES).confirm(true),
                canEdit, branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_SEND_FOR_APPROVAL).confirm(true),
                isAdmin, !underApproval, branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_OK).confirm(true),
                isApprover, underApproval, branchActive)
            .addIf(
                new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_NOT_OK).confirm(true)
                    .input2Type(UiActionInputType.TEXTAREA),
                isApprover, underApproval, branchActive);
      } else {
        uiActions
            .addIf(MDMActions.ACTION_START_EDITING, isAdmin, !branchActive)
            .addIf(MDMActions.ACTION_FINALIZE_CHANGES, isAdmin, branchActive)
            .addIf(MDMActions.ACTION_CANCEL_CHANGES, isAdmin, branchActive);
      }

      ctx.view.actions(uiActions.build());
    }
  }

  protected final void createOrRefreshGrids(PageContext ctx) {
    SmartComponentLayoutDefinition changesLayout =
        container(LayoutDirection.VERTICAL);

    if (ctx.entryApisWithChanges.isEmpty()) {
      changesLayout.addComponentsItem(
          form(LayoutDirection.HORIZONTAL, label(null, localeSettingApi.get("mdm.changes.nop"))));
    } else {
      ctx.entryApisWithChanges.forEach(entryApi -> {
        GridModel entryGridModel =
            viewApi.getWidgetModelFromView(GridModel.class, ctx.view.getUuid(),
                entryApi.getName());
        if (entryGridModel == null) {
          createGridModelByEntryApi(ctx, entryApi);
        }

        List<BranchedObjectEntry> list = entryApi.getBranchingList();
        gridModelApi.setData(ctx.view.getUuid(), entryApi.getName(),
            createTableDataForGrid(ctx, entryApi, list));

        changesLayout
            .addComponentsItem(
                form(LayoutDirection.VERTICAL, label(null, entryApi.getDisplayNameList())))
            .addComponentsItem(grid(entryApi.getName()));

      });
    }

    ctx.view.putComponentLayoutsItem("changesLayout", changesLayout);
  }

  protected GridModel createGridModelByEntryApi(PageContext ctx, MDMEntryApi entryApi) {
    SearchIndex<BranchedObjectEntry> searchIndexAdmin =
        ctx.searchIndexAdminsByDescriptorName.get(entryApi.getName());
    List<String> columns =
        searchIndexAdmin.getDefinition().getDefinition().allProperties().stream()
            .map(Property::getName).collect(toList());
    GridModel entryGridModel =
        gridModelApi.createGridModel(searchIndexAdmin.getDefinition().getDefinition(),
            columns, ctx.definition.getName(), entryApi.getName());
    if (columns.contains(BranchedObjectEntry.BRANCHING_STATE)) {
      List<String> newCols = new ArrayList<>();
      newCols.add(BranchedObjectEntry.BRANCHING_STATE);
      newCols.addAll(
          columns.stream()
              .filter(col -> !BranchedObjectEntry.BRANCHING_STATE.equals(col))
              .collect(toList()));
      entryGridModel.getView().setOrderedColumnNames(newCols);
    }
    GridModels.hideColumns(entryGridModel, BranchedObjectEntry.ORIGINAL_URI,
        BranchedObjectEntry.BRANCH_URI);
    if (columns.contains(MDMConstants.PROPERTY_URI)) {
      GridModels.hideColumns(entryGridModel, MDMConstants.PROPERTY_URI);
    }

    final List<GridView> gridViewOptions = entryApi.getDescriptor().getListPageGridViews();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      entryGridModel.setView(gridViewOptions.get(0));
      entryGridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
    }

    gridModelApi.initGridInView(ctx.view.getUuid(), entryApi.getName(), entryGridModel);
    gridModelApi.addGridPageCallback(ctx.view.getUuid(), entryApi.getName(), invocationApi
        .builder(MDMEntryChangesPageApi.class)
        .build(api -> api.addWidgetEntryGridActions(null, ctx.view.getUuid())));

    return entryGridModel;
  }

  protected TableData<?> createTableDataForGrid(PageContext ctx, MDMEntryApi entryApi,
      List<BranchedObjectEntry> list) {
    String constructObjectDefinitionName =
        masterDataManagementApi.constructObjectDefinitionName(ctx.definition,
            entryApi.getDescriptor());
    ObjectDefinition<?> branchedObjectDefinition =
        objectApi.definition(constructObjectDefinitionName);

    return ctx.searchIndexAdminsByDescriptorName.get(entryApi.getName())
        .executeSearchOnNodes(list.stream().map(i -> {
          ObjectDefinition<?> objectDefinition = branchedObjectDefinition;
          return objectApi.create(ctx.definition.getName(), objectDefinition,
              objectDefinition.toMap(i));
        }), new FilterExpressionList().addExpressionsItem(
            new FilterExpressionData().currentOperation(FilterExpressionOperation.NOT_EQUAL)
                .operand1(new FilterExpressionOperandData()
                    .isDataName(true)
                    .valueAsString(BranchedObjectEntry.BRANCHING_STATE)
                    .type(FilterExpressionDataType.STRING))
                .operand2(new FilterExpressionOperandData()
                    .valueAsString(BranchingStateEnum.NOP.toString())
                    .isDataName(false)
                    .type(FilterExpressionDataType.STRING))));
  }

  protected boolean canEdit(boolean isAdmin, boolean underApproval, boolean isApprover) {
    return (isAdmin && !underApproval) || (isApprover && underApproval);
  }

  @Override
  public void startEditing(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.initiateGlobalBranch(context.definition.getName(),
        String.valueOf(System.currentTimeMillis()));
    refreshActions(context);

  }

  protected PageContext getContextByViewUUID(UUID viewUuid) {
    return getContextByViewUUID(viewUuid, false);
  }

  protected PageContext getContextByViewUUID(UUID viewUuid, boolean loadOnlyDefition) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return loadOnlyDefition ? result.loadOnlyDefinitionByView() : result.loadByView();
  }

  @Override
  public void cancelChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, true);
    masterDataManagementApi.dropGlobal(context.definition.getName());
    context.loadByView();
    refreshActions(context);
    createOrRefreshGrids(context);
  }

  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, true);
    masterDataManagementApi.mergeGlobal(context.definition.getName());
    context.loadByView();
    refreshActions(context);
    createOrRefreshGrids(context);
  }

  @Override
  public void sendForApproval(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, false);
    if (mdmApprovalApi == null) {
      throw new IllegalStateException("Az admin jóváhagyó nem elérhető!");
    }
    String definition = context.definition.getName();
    List<URI> approvers = mdmApprovalApi.getApprovers(definition);
    if (approvers == null || approvers.size() != 1) {
      throw new IllegalStateException("Az admin jóváhagyó nincs beállítva!");
    }
    masterDataManagementApi.sendForApprovalGlobal(
        context.definition.getName(),
        approvers.get(0));
    context.loadByView();
    createOrRefreshGrids(context);
    refreshActions(context);
  }

  @Override
  public void adminApproveOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, true);
    masterDataManagementApi.approvalAcceptedGlobal(context.definition.getName());
    context.loadByView();
    createOrRefreshGrids(context);
    refreshActions(context);
  }

  @Override
  public void adminApproveNotOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    // TODO send and store reason
    masterDataManagementApi.approvalRejectedGlobal(context.definition.getName());
    refreshActions(context);
  }

  @Override
  public GridPage addWidgetEntryGridActions(GridPage page, UUID viewUuid) {
    page.getRows().forEach(row -> {
      String icon;
      Map<String, Object> map = (Map<String, Object>) row.getData();
      BranchingStateEnum state = (BranchingStateEnum) map.get(BranchedObjectEntry.BRANCHING_STATE);
      switch (state) {
        case NEW:
          icon = "add_circle";
          break;
        case MODIFIED:
          icon = "tag";
          break;
        case DELETED:
          icon = "cancel";
          break;

        default:
          icon = "radio_button_unchecked";
          break;
      }
      if (icon != null) {
        row.putIconsItem(BranchedObjectEntry.BRANCHING_STATE,
            new ImageResource()
                .source("smart-icon")
                .identifier(icon));
      }
    });
    return page;
  }
}
