package org.smartbit4all.bff.api.mdm;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMApprovalApi;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.UiActions.UiActionBuilder;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.api.view.layout.SmartLayoutApi;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDisplay;
import org.smartbit4all.core.object.ObjectExtensionApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class MDMEntryListPageApiImpl extends PageApiImpl<SearchPageModel>
    implements MDMEntryListPageApi {

  private static final Logger log = LoggerFactory.getLogger(MDMEntryListPageApiImpl.class);

  private static final String PROPERTY_URI = "uri";

  private static final String VARIABLE_INACTIVES = "inactives";

  private static final String VARIABLE_ACTIVES = "actives";

  public MDMEntryListPageApiImpl() {
    super(SearchPageModel.class);
  }

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  protected InvocationApi invocationApi;

  @Autowired
  protected SmartLayoutApi smartLayoutApi;

  @Autowired
  private ObjectExtensionApi objectExtensionApi;

  @Autowired
  private ObjectLayoutApi objectLayoutApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Autowired(required = false)
  private MDMApprovalApi mdmApprovalApi;

  /**
   * The name of the default editor in the application. It is opened as editor if the editor view is
   * not set for the given entry.
   */
  private String defaultEditorViewName = MDMAdminPageApi.MDM_EDIT;

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    MDMEntryDescriptor entryDescriptor;
    public MDMDefinition definition;
    MDMEntryApi entryApi;
    SearchIndex<BranchedObjectEntry> searchIndexAdmin;
    SearchIndex<Object> searchIndexPublished;
    boolean inactives = false;
    MDMBranchingStrategy branchingStrategy;

    PageContext loadByView() {
      entryDescriptor = getEntryDescriptor(view);
      definition = getDefinition(view);
      entryApi =
          masterDataManagementApi.getApi(definition.getName(), entryDescriptor.getName());

      searchIndexAdmin =
          collectionApi.searchIndex(definition.getName(),
              entryDescriptor.getSearchIndexForEntries(),
              BranchedObjectEntry.class);
      searchIndexPublished =
          collectionApi.searchIndex(definition.getName(), entryDescriptor.getName(),
              Object.class);
      inactives = Boolean.TRUE.equals(variables(view).get(VARIABLE_INACTIVES, Boolean.class));

      branchingStrategy = entryDescriptor.getBranchingStrategy();
      if (branchingStrategy == null) {
        log.warn("branchingStrategy null, using default NONE");
        branchingStrategy = MDMBranchingStrategy.NONE;
      }

      return this;
    }

    private final MDMDefinition getDefinition(View view) {
      return extractParam(MDMDefinition.class, PARAM_MDM_DEFINITION, view.getParameters());
    }

    private final MDMEntryDescriptor getEntryDescriptor(View view) {
      return extractParam(MDMEntryDescriptor.class, PARAM_ENTRY_DESCRIPTOR, view.getParameters());
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, entryDescriptor.getAdminGroupName());
    }

    public void setInactives(boolean inactives) {
      this.inactives = inactives;
      variables(view).put(VARIABLE_INACTIVES, inactives);
    }

    ObjectDefinition<?> getBranchedObjectDefinition() {
      String constructObjectDefinitionName =
          masterDataManagementApi.constructObjectDefinitionName(definition, entryDescriptor);
      return objectApi.definition(constructObjectDefinitionName);
    }

    ObjectDefinition<?> getObjectDefinition() {
      return objectApi.definition(entryDescriptor.getTypeQualifiedName());
    }

  }

  protected PageContext getContextByViewUUID(UUID viewUuid) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return result.loadByView();
  }

  protected PageContext getContextByView(View view) {
    PageContext result = new PageContext();
    result.view = view;
    return result.loadByView();
  }

  @Override
  public SearchPageModel initModel(View view) {
    PageContext context = getContextByView(view);
    refreshActions(context);

    List<String> columns =
        context.searchIndexAdmin.getDefinition().getDefinition().allProperties().stream()
            .map(Property::getName).collect(toList());
    GridModel entryGridModel =
        gridModelApi.createGridModel(context.searchIndexAdmin.getDefinition().getDefinition(),
            columns,
            context.definition.getName(), context.entryDescriptor.getName());
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
    if (columns.contains(PROPERTY_URI)) {
      GridModels.hideColumns(entryGridModel, PROPERTY_URI);
    }
    final List<GridView> gridViewOptions = context.entryDescriptor.getListPageGridViews();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      entryGridModel.setView(gridViewOptions.get(0));
      entryGridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
    }

    gridModelApi.initGridInView(view.getUuid(), WIDGET_ENTRY_GRID, entryGridModel);
    gridModelApi.addGridPageCallback(view.getUuid(), WIDGET_ENTRY_GRID, invocationApi
        .builder(MDMEntryListPageApi.class)
        .build(api -> api.addWidgetEntryGridActions(null, view.getUuid())));

    refreshGrid(context);

    FilterExpressionFieldList filters = null;
    return new SearchPageModel()
        .pageTitle(getPageTitle(context))
        .filters(null);
  }

  private String getPageTitle(PageContext context) {
    String pageTitle = context.entryApi.getDisplayNameList();
    if (Boolean.TRUE.equals(context.inactives)) {
      pageTitle += StringConstant.SPACE_HYPHEN_SPACE
          + localeSettingApi.get(MasterDataManagementApi.SCHEMA, VARIABLE_INACTIVES);
    }
    return pageTitle;
  }

  protected void refreshActions(PageContext ctx) {
    boolean isAdmin = ctx.checkAdmin();
    boolean branchActive = ctx.entryApi.hasBranch();
    boolean inactiveEnabled = Boolean.TRUE.equals(ctx.entryDescriptor.getInactiveMgmt());
    boolean branchingEnabled = ctx.branchingStrategy != MDMBranchingStrategy.NONE;
    boolean entryEditingEnabled = branchActive || !branchingEnabled;
    UiActionBuilder uiActions = UiActions.builder()
        .add(ACTION_DO_QUERY);
    // if API present, approving enabled
    boolean approvingEnabled = mdmApprovalApi != null;
    if (approvingEnabled) {
      URI approver = ctx.entryApi.getApprover();
      boolean underApproval = approver != null;
      boolean isApprover = approver != null && approver.equals(sessionApi.getUserUri());
      boolean canEdit = canEdit(isAdmin, underApproval, isApprover);

      uiActions
          .addIf(ACTION_NEW_ENTRY, canEdit, branchActive, !ctx.inactives)
          .addIf(new UiAction().code(ACTION_START_EDITING).confirm(true),
              isAdmin, branchingEnabled, !branchActive, !ctx.inactives)
          .addIf(new UiAction().code(ACTION_CANCEL_CHANGES).confirm(true),
              canEdit, branchingEnabled, branchActive, !ctx.inactives)
          .addIf(new UiAction().code(ACTION_SEND_FOR_APPROVAL).confirm(true),
              isAdmin, !underApproval, branchingEnabled, branchActive,
              !ctx.inactives)
          .addIf(new UiAction().code(ACTION_ADMIN_APPROVE_OK).confirm(true),
              isApprover, underApproval, branchingEnabled, branchActive,
              !ctx.inactives)
          .addIf(
              new UiAction().code(ACTION_ADMIN_APPROVE_NOT_OK).confirm(true)
                  .input2Type(UiActionInputType.TEXTAREA),
              isApprover, underApproval, branchingEnabled,
              branchActive,
              !ctx.inactives);
    } else {
      uiActions
          .addIf(ACTION_NEW_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives)
          .addIf(ACTION_START_EDITING, isAdmin, branchingEnabled, !branchActive, !ctx.inactives)
          .addIf(ACTION_FINALIZE_CHANGES, isAdmin, branchingEnabled, branchActive, !ctx.inactives)
          .addIf(ACTION_CANCEL_CHANGES, isAdmin, branchingEnabled, branchActive, !ctx.inactives);
    }

    uiActions
        .addIf(
            new UiAction().code(ACTION_TOGGLE_INACTIVES).descriptor(
                new UiActionDescriptor().type(UiActionButtonType.STROKED)
                    .title(ctx.inactives
                        ? localeSettingApi.get(MasterDataManagementApi.SCHEMA, VARIABLE_ACTIVES)
                        : localeSettingApi.get(MasterDataManagementApi.SCHEMA,
                            VARIABLE_INACTIVES))),
            isAdmin, inactiveEnabled);

    ctx.view.actions(uiActions.build());
  }

  protected final void refreshGrid(PageContext ctx) {

    if (ctx.checkAdmin() && ctx.entryApi.hasBranch()) {
      List<BranchedObjectEntry> list;
      if (ctx.inactives) {
        StoredList inactiveList = ctx.entryApi.getInactiveList();
        list = inactiveList.compareWithBranch(ctx.entryApi.getBranchUri());
      } else {
        list = ctx.entryApi.getBranchingList();
      }
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_ENTRY_GRID,
          ctx.searchIndexAdmin
              .executeSearchOnNodes(list.stream().map(i -> {
                ObjectDefinition<?> objectDefinition = ctx.getBranchedObjectDefinition();
                return objectApi.create(ctx.definition.getName(), objectDefinition,
                    objectDefinition.toMap(i));
              }), null));
    } else {
      StoredList inactiveList = ctx.entryApi.getInactiveList();
      StoredList list = ctx.inactives ? inactiveList : ctx.entryApi.getList();
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_ENTRY_GRID,
          ctx.searchIndexPublished.executeSearchOnNodes(list.nodesFromCache(),
              null));
    }

  }

  @Override
  public void performDoQuery(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    refreshGrid(context);
  }

  @Override
  public void startEditing(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.initiateGlobalBranch(context.definition.getName(),
        String.valueOf(System.currentTimeMillis()));
    refreshActions(context);
    refreshGrid(context);
  }

  @Override
  public void cancelChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.dropGlobal(context.definition.getName());
    refreshActions(context);
    refreshGrid(context);
  }

  @Override
  public void newEntry(UUID viewUuid, UiActionRequest request) {
    showEditorView(
        viewUuid,
        getContextByViewUUID(viewUuid),
        new BranchedObjectEntry().branchingState(BranchingStateEnum.NEW),
        request.getCode());
  }

  private final ObjectNode createNewObject(MDMEntryDescriptor entryDescriptor) {
    // ObjectDefinition<?> definition =
    // objectApi.definition(entryDescriptor.getTypeQualifiedName());
    // return definition.newInstanceAsMap();
    final String qualifiedName = entryDescriptor.getTypeQualifiedName();
    return objectExtensionApi.newInstance(qualifiedName, "my-schema");
  }

  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.mergeGlobal(context.definition.getName());
    refreshGrid(context);
    refreshActions(context);
  }

  @Override
  public void sendForApproval(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
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
    refreshGrid(context);
    refreshActions(context);
  }

  @Override
  public void adminApproveOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.approvalAcceptedGlobal(context.definition.getName());
    refreshGrid(context);
    refreshActions(context);
  }

  @Override
  public void adminApproveNotOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    // TODO send and store reason
    masterDataManagementApi.approvalRejectedGlobal(context.definition.getName());
    refreshGrid(context);
    refreshActions(context);
  }

  @Override
  public void toggleInactives(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    context.setInactives(!context.inactives);
    getModel(viewUuid).setPageTitle(getPageTitle(context));
    refreshActions(context);
    refreshGrid(context);
  }

  @Override
  public void performEditEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    // We need to pass the override of the save action.
    performActionOnGridRow(getContextByViewUUID(viewUuid), gridId, rowId, (r, ctx) -> {
      BranchedObjectEntry branchedObjectEntry =
          objectApi.asType(BranchedObjectEntry.class, r.getData());
      showEditorView(viewUuid, ctx, branchedObjectEntry, request.getCode());
    });
  }

  protected void showEditorView(UUID viewUuid, PageContext ctx,
      BranchedObjectEntry branchedObjectEntry, String actionCode) {
    ObjectDefinition<?> objectDefinition = ctx.getObjectDefinition();

    boolean isView = ACTION_VIEW_ENTRY.equals(actionCode) ||
        ACTION_VIEW_ORIGINAL_ENTRY.equals(actionCode);

    boolean isViewOriginal = ACTION_VIEW_ORIGINAL_ENTRY.equals(actionCode);

    // Model:
    URI branchUri = isViewOriginal ? null : ctx.entryApi.getBranchUri();
    URI objectLatestUri =
        (!isViewOriginal && branchedObjectEntry.getBranchUri() != null) // branchedObjectEntry
                                                                        // contains latest uris
            ? branchedObjectEntry.getBranchUri()
            : branchedObjectEntry.getOriginalUri();

    final ObjectNode modelNode = (objectLatestUri == null)
        ? createNewObject(ctx.entryDescriptor)
        : objectApi.load(objectLatestUri, branchUri);

    // Layout:
    final ObjectLayoutDescriptor layoutDescriptor = objectExtensionApi
        .generateDefaultLayout(objectDefinition.getQualifiedName());
    final ObjectDisplay display = objectLayoutApi.getSketchDisplay(modelNode, layoutDescriptor);
    SmartLayoutDefinition layout = display.getDefaultForms().stream()
        .map(SmartLayoutDefinition::getWidgets)
        .flatMap(List::stream)
        .map(w -> w.label(localeSettingApi.get(w.getLabel())))
        .collect(collectingAndThen(toList(), new SmartLayoutDefinition()::widgets));

    List<UiAction> actions = UiActions.builder()
        .addIf(new UiAction().code(MDMEntryEditPageApi.ACTION_SAVE).submit(true), !isView)
        .add(new UiAction().code(MDMEntryEditPageApi.ACTION_CANCEL))
        .build();
    viewApi.showView(new View()
        .viewName(getEditorViewName(ctx))
        .type(ViewType.DIALOG)
        .objectUri(modelNode.getObjectUri())
        .branchUri(branchUri)
        .putLayoutsItem("layout", layout)
        .putParametersItem(PARAM_MDM_DEFINITION, ctx.definition)
        .putParametersItem(PARAM_ENTRY_DESCRIPTOR, ctx.entryDescriptor)
        .putParametersItem(PARAM_BRANCHED_OBJECT_ENTRY, branchedObjectEntry)
        .putParametersItem(PARAM_MDM_LIST_VIEW, viewUuid)
        .putParametersItem(PARAM_RAW_MODEL, modelNode.getObjectAsMap())
        .putParametersItem(PARAM_ACTION_CODE, actionCode)
        .actions(actions));
  }

  @Override
  public void performDeleteEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, row -> {
      Object oBranchUri =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI);
      if (oBranchUri != null) {
        return BranchedObjectEntry.BRANCH_URI;
      }
      return BranchedObjectEntry.ORIGINAL_URI;
    },
        (u, ctx) -> {
          ctx.entryApi.remove(u);
        });
    refreshGrid(context);
  }

  @Override
  public void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, row -> BranchedObjectEntry.BRANCH_URI,
        (u, ctx) -> ctx.entryApi.cancel(u));
    refreshGrid(context);
  }

  @Override
  public void performRestoreEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId,
        row -> {
          Object oBranchUri =
              GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI);
          if (oBranchUri != null) {
            return BranchedObjectEntry.BRANCH_URI;
          }
          return BranchedObjectEntry.ORIGINAL_URI;
        },
        (u, ctx) -> ctx.entryApi.restore(u));
    refreshGrid(context);
  }

  @Override
  public void saveObject(UUID viewUuid, URI objectUri, Object editingObject) {
    PageContext context = getContextByViewUUID(viewUuid);
    ObjectDefinition<?> objectDefinition = context.getObjectDefinition();
    Map<String, Object> editingObjectAsMap = objectDefinition.toMap(editingObject);
    ObjectNode objectNode;
    if (objectUri == null) {
      objectNode = objectApi.create(
          context.entryApi.getDescriptor().getSchema(),
          objectDefinition,
          editingObjectAsMap);
    } else {
      objectNode = objectApi.load(objectUri);
      objectNode.setValues(editingObjectAsMap);
    }
    saveObjectInternal(context, objectNode);
  }

  @Override
  public void saveObject(UUID viewUuid, ObjectNode objectNode) {
    saveObjectInternal(getContextByViewUUID(viewUuid), objectNode);
  }

  protected void saveObjectInternal(PageContext context, ObjectNode objectNode) {
    context.entryApi.save(objectNode);
    refreshGrid(context);
  }

  private final void performActionOnGridRow(PageContext context, String gridId, String rowId,
      BiConsumer<GridRow, PageContext> action) {
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, context.view.getUuid(), gridId);
    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    gridRow.ifPresent(r -> action.accept(r, context));
  }

  private final void performActionOnEntry(PageContext context, String gridId, String rowId,
      Function<GridRow, String> uriPropertyGetter, BiConsumer<URI, PageContext> action) {
    performActionOnGridRow(context, gridId, rowId, (r, ctx) -> {
      Object valueFromGridRow =
          GridModels.getValueFromGridRow(r, uriPropertyGetter.apply(r));
      URI objectUri = valueFromGridRow instanceof URI ? (URI) valueFromGridRow
          : (valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null);

      if (objectUri != null) {
        action.accept(objectUri, context);
      }
    });
  }

  public final MDMEntryListPageApi defaultEditorViewName(String defaultEditorViewName) {
    this.defaultEditorViewName = defaultEditorViewName;
    return this;
  }

  private final String getEditorViewName(PageContext context) {
    return context.entryDescriptor.getEditorViewName() == null ? defaultEditorViewName
        : context.entryDescriptor.getEditorViewName();
  }

  @Override
  public GridPage addWidgetEntryGridActions(GridPage page, UUID viewUuid) {
    PageContext ctx = getContextByViewUUID(viewUuid);
    boolean isAdmin = ctx.checkAdmin();
    boolean branchActive = ctx.entryApi.hasBranch();
    boolean branchingEnabled = ctx.branchingStrategy != MDMBranchingStrategy.NONE;
    boolean entryEditingEnabled = branchActive || !branchingEnabled;
    boolean approvingEnabled = mdmApprovalApi != null;
    URI approver = ctx.entryApi.getApprover();
    boolean underApproval = approver != null;
    boolean isApprover = approver != null && approver.equals(sessionApi.getUserUri());

    page.getRows().forEach(row -> {
      boolean isOnBranch =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI) != null;
      boolean isOnOriginal =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.ORIGINAL_URI) != null;
      Object oBranchingState =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCHING_STATE);
      boolean newOnBranch = BranchingStateEnum.NEW.equals(oBranchingState);
      boolean deletedOnBranch = BranchingStateEnum.DELETED.equals(oBranchingState);

      UiActionBuilder uiActions = UiActions.builder();
      if (approvingEnabled) {
        boolean canEdit = canEdit(isAdmin, underApproval, isApprover);
        uiActions.addIf(ACTION_RESTORE_ENTRY, canEdit, entryEditingEnabled, ctx.inactives)
            .addIf(ACTION_EDIT_ENTRY, canEdit, entryEditingEnabled, !ctx.inactives,
                !deletedOnBranch)
            .addIf(ACTION_DELETE_ENTRY, canEdit, entryEditingEnabled, !ctx.inactives, newOnBranch)
            .addIf(ACTION_INACTIVATE_ENTRY, canEdit, entryEditingEnabled, !ctx.inactives,
                !newOnBranch, !deletedOnBranch)
            .addIf(ACTION_VIEW_ORIGINAL_ENTRY, canEdit, entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch && isOnOriginal)
            .addIf(ACTION_CANCEL_DRAFT_ENTRY, canEdit, entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch)
            .addIf(ACTION_VIEW_ENTRY, (isAdmin || isApprover));
      } else {
        uiActions.addIf(ACTION_RESTORE_ENTRY, isAdmin, entryEditingEnabled, ctx.inactives)
            .addIf(ACTION_EDIT_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives,
                !deletedOnBranch)
            .addIf(ACTION_DELETE_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives, newOnBranch)
            .addIf(ACTION_INACTIVATE_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives,
                !newOnBranch, !deletedOnBranch)
            .addIf(ACTION_VIEW_ORIGINAL_ENTRY, isAdmin, entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch && isOnOriginal)
            .addIf(ACTION_CANCEL_DRAFT_ENTRY, isAdmin, entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch);
      }
      row.setActions(uiActions.build());
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

  protected boolean canEdit(boolean isAdmin, boolean underApproval, boolean isApprover) {
    return (isAdmin && !underApproval) || (isApprover && underApproval);
  }

}
