package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.VectorDBApi;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.grid.bean.GridSelectionType;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.mdm.MDMApprovalApi;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MDMModificationApi;
import org.smartbit4all.api.mdm.MDMSetupApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationItem;
import org.smartbit4all.api.mdm.bean.MDMModificationItem.StateEnum;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.UiActions.UiActionBuilder;
import org.smartbit4all.api.view.ViewContexts;
import org.smartbit4all.api.view.ViewPublisherApi;
import org.smartbit4all.api.view.bean.DeviceInfo;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.UploadedFile;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.api.view.layout.SmartLayoutApi;
import org.smartbit4all.bff.api.mdm.relation.MDMRelationEditorService;
import org.smartbit4all.bff.api.search.SearchPageApi;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDisplay;
import org.smartbit4all.core.object.ObjectExtensionApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.google.common.collect.Lists;

public class MDMEntryListPageApiImpl extends PageApiImpl<SearchPageModel>
    implements MDMEntryListPageApi {

  private static final Logger log = LoggerFactory.getLogger(MDMEntryListPageApiImpl.class);

  private static final String VARIABLE_INACTIVES = "inactives";

  private static final String VARIABLE_ACTIVES = "actives";

  protected static Function<GridRow, String> branchedObjectUriGetter = row -> {
    Object oBranchUri =
        GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI);
    if (oBranchUri != null) {
      return BranchedObjectEntry.BRANCH_URI;
    }
    return BranchedObjectEntry.ORIGINAL_URI;
  };

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
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  VectorDBApi vectorDBApi;

  @Autowired
  protected FilterExpressionApi filterExpressionApi;

  @Autowired
  protected FilterExpressionBuilderApi filterExpressionBuilderApi;

  @Autowired(required = false)
  private MDMApprovalApi mdmApprovalApi;

  @Autowired
  private ViewPublisherApi viewPublisherApi;

  @Autowired
  private MDMRelationEditorService mdmRelationEditorService;

  @Autowired
  private MDMSetupApi mdmSetupApi;

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
    MDMDefinition definition;
    URI mdmBranch;
    MDMModificationApi modificationApi;
    MDMEntryApi entryApi;
    MDMEntryApi vectorEntryApi;
    MDMEntryApi embeddingEntryApi;
    SearchIndex<BranchedObjectEntry> searchIndexAdmin;
    SearchIndex<Object> searchIndexPublished;
    boolean inactives = false;
    MDMBranchingStrategy branchingStrategy;
    FilterExpressionBuilderModel filterModel;

    PageContext loadByView() {
      entryDescriptor = getEntryDescriptor(getView());
      definition = getDefinition(getView());
      modificationApi = masterDataManagementApi
          .getModificationApiForUser(definition.getName(), sessionApi.getUserUri());
      mdmBranch = modificationApi == null ? null : modificationApi.getModification().getBranchUri();
      entryApi =
          masterDataManagementApi.getApi(getDefinition().getName(), getEntryDescriptor().getName(),
              mdmBranch);
      vectorEntryApi =
          masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
              PlatformApiConfig.VECTOR_DB_CONNECTIONS, null); // TODO mdmBranch?
      embeddingEntryApi =
          masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
              PlatformApiConfig.EMBEDDING_CONNECTIONS, null); // TODO mdmBranch?

      searchIndexAdmin =
          collectionApi.searchIndex(getDefinition().getName(),
              getEntryDescriptor().getSearchIndexForEntries(),
              BranchedObjectEntry.class);
      searchIndexPublished =
          collectionApi.searchIndex(getDefinition().getName(), getEntryDescriptor().getName(),
              Object.class);
      inactives = Boolean.TRUE.equals(variables(getView()).get(VARIABLE_INACTIVES, Boolean.class));

      branchingStrategy = getEntryDescriptor().getBranchingStrategy();
      if (getBranchingStrategy() == null) {
        log.warn("branchingStrategy null, using default NONE");
        branchingStrategy = MDMBranchingStrategy.NONE;
      }


      filterModel = entryDescriptor.getFilterModel();
      if (isAdmin()) {
        FilterExpressionBuilderModel filterModelAdmin =
            entryDescriptor.getFilterModelAdmin();
        if (filterModelAdmin != null) {
          filterModel = filterModelAdmin;
        }
      }

      return this;
    }

    private final MDMDefinition getDefinition(View view) {
      return extractParam(MDMDefinition.class, PARAM_MDM_DEFINITION, view.getParameters());
    }

    private final MDMEntryDescriptor getEntryDescriptor(View view) {
      return extractParam(MDMEntryDescriptor.class, PARAM_ENTRY_DESCRIPTOR, view.getParameters());
    }

    public boolean isAdmin() {
      return OrgUtils.securityPredicate(sessionApi, getEntryDescriptor().getAdminGroupName());
    }

    public boolean isAdminApprover() {
      return OrgUtils.securityPredicate(sessionApi, getDefinition().getAdminApproverGroupName());
    }

    public void setInactives(boolean inactives) {
      this.inactives = inactives;
      variables(getView()).put(VARIABLE_INACTIVES, inactives);
    }

    ObjectDefinition<?> getBranchedObjectDefinition() {
      String constructObjectDefinitionName =
          masterDataManagementApi.constructObjectDefinitionName(getDefinition(),
              getEntryDescriptor());
      return objectApi.definition(constructObjectDefinitionName);
    }

    public View getView() {
      return view;
    }

    public MDMEntryApi getEntryApi() {
      return entryApi;
    }

    public URI getApprover() {
      if (modificationApi == null) {
        return null;
      }
      return modificationApi.getModification().getApprover();
    }

    public MDMEntryDescriptor getEntryDescriptor() {
      return entryDescriptor;
    }

    public FilterExpressionBuilderModel getFilterModel() {
      return filterModel;
    }

    public MDMDefinition getDefinition() {
      return definition;
    }

    public URI getMdmBranch() {
      return mdmBranch;
    }

    public boolean isUnderApproval() {
      return getApprover() != null;
    }

    public boolean isCurrentApprover() {
      URI approver = getApprover();
      return approver != null && approver.equals(sessionApi.getUserUri());
    }

    public MDMModificationApi getModificationApi() {
      return modificationApi;
    }

    public MDMBranchingStrategy getBranchingStrategy() {
      return branchingStrategy;
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

    List<String> columns;
    List<String> searchIndexColumns =
        context.searchIndexAdmin.getDefinition().getDefinition().allProperties().stream()
            .map(Property::getName).collect(toList());
    if (context.entryDescriptor.getTableColumns() != null) {
      List<String> tableColumns = context.entryDescriptor.getTableColumns().stream()
          .map(MDMTableColumnDescriptor::getName)
          .filter(col -> searchIndexColumns.contains(col)) // valid columnNames only!
          .collect(toList());

      List<String> extraColumns = searchIndexColumns.stream()
          .filter(col -> !tableColumns.contains(col))
          .collect(toList());
      columns = new ArrayList<>();
      columns.addAll(tableColumns);
      columns.addAll(extraColumns);
    } else {
      columns = searchIndexColumns;
    }
    createGridModel(view, context, columns);
    initFilterModel(context);
    refreshGrid(context);

    return new SearchPageModel()
        .pageTitle(getPageTitle(context));
  }

  protected GridModel createGridModel(View view, PageContext context, List<String> columns) {

    boolean isAdmin = context.isAdmin();
    boolean branchActive = context.getEntryApi().hasBranch();
    boolean branchingEnabled = context.getBranchingStrategy() != MDMBranchingStrategy.NONE;
    boolean entryEditingEnabled = branchActive || !branchingEnabled;

    GridModel entryGridModel =
        gridModelApi.createGridModel(context.searchIndexAdmin.getDefinition().getDefinition(),
            columns,
            context.getDefinition().getName(), context.getEntryDescriptor().getName());
    GridModels.hideColumns(entryGridModel, BranchedObjectEntry.ORIGINAL_URI,
        BranchedObjectEntry.BRANCH_URI);

    if (columns.contains(MDMConstants.PROPERTY_URI)) {
      GridModels.hideColumns(entryGridModel, MDMConstants.PROPERTY_URI);
    }
    if (columns.contains(MDMConstants.PROPERTY_URI)) {
      GridModels.hideColumns(entryGridModel, MDMConstants.PROPERTY_URI);
    }
    if (columns.contains(MDMModificationItem.STATE)) {
      GridModels.hideColumns(entryGridModel, MDMModificationItem.STATE);
    }
    if (columns.contains(MDMDefinitionOption.STATE_NAME)) {
      GridModels.hideColumns(entryGridModel, MDMDefinitionOption.STATE_NAME);
    }
    // if we want to show it at all, it's value should be there
    // if (context.getModificationApi() == null) {
    // GridModels.hideColumns(entryGridModel, MDMDefinitionOption.STATE_NAME);
    // }
    final List<GridView> gridViewOptions = context.getEntryDescriptor().getListPageGridViews();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      entryGridModel.setView(gridViewOptions.get(0));
      entryGridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
    }

    if (Boolean.TRUE.equals(isAdmin) && Boolean.TRUE.equals(entryEditingEnabled)) {
      entryGridModel.getView().getDescriptor().selectionMode(GridSelectionMode.MULTIPLE);
      entryGridModel.getView().getDescriptor().selectionType(GridSelectionType.CHECKBOX);
    }

    entryGridModel.qualifier(context.entryDescriptor.getName());
    gridModelApi.initGridInView(view.getUuid(), WIDGET_ENTRY_GRID, entryGridModel);
    // BranchedObjectEntry.BRANCHING_STATE should be handled after init
    if (columns.contains(BranchedObjectEntry.BRANCHING_STATE)) {
      if (context.mdmBranch == null) {
        GridModels.hideColumns(entryGridModel, BranchedObjectEntry.BRANCHING_STATE);
      } else {
        List<String> currentColumns = entryGridModel.getView().getOrderedColumnNames();
        List<String> newCols = new ArrayList<>();
        newCols.add(BranchedObjectEntry.BRANCHING_STATE);
        newCols.addAll(
            currentColumns.stream()
                .filter(col -> !BranchedObjectEntry.BRANCHING_STATE.equals(col))
                .collect(toList()));
        entryGridModel.getView().setOrderedColumnNames(newCols);
        entryGridModel.getView().getDescriptor().getColumns().stream()
            .filter(col -> BranchedObjectEntry.BRANCHING_STATE.equals(col.getPropertyName()))
            .forEach(col -> col.alwaysShow(true));
      }
    }

    gridModelApi.addGridPageCallback(view.getUuid(), WIDGET_ENTRY_GRID, invocationApi
        .builder(MDMEntryListPageApi.class)
        .build(api -> api.addWidgetEntryGridActions(null, view.getUuid())));

    gridModelApi.addSelectionChangeListener(view.getUuid(), WIDGET_ENTRY_GRID,
        invocationApi.builder(MDMEntryListPageApi.class)
            .build(api -> api.handleGridSelectionChange(
                view.getUuid(),
                WIDGET_ENTRY_GRID)));

    return entryGridModel;
  }

  private void initFilterModel(PageContext ctx) {

    FilterExpressionBuilderModel filterModel = ctx.getFilterModel();
    if (filterModel != null) {
      FilterExpressionBuilderUiModel filterExpressionBuilderUiModel =
          filterExpressionBuilderApi.createFilterBuilder(filterModel, null);
      filterExpressionBuilderApi.initFilterBuilderInView(ctx.getView().getUuid(),
          SearchPageApi.FILTER_BUILDER_WIDGET_ID,
          filterExpressionBuilderUiModel);
    }
  }

  private String getPageTitle(PageContext context) {
    String pageTitle = context.getEntryApi().getDisplayNameList();
    if (Boolean.TRUE.equals(context.inactives)) {
      pageTitle += StringConstant.SPACE_HYPHEN_SPACE
          + localeSettingApi.get(MasterDataManagementApi.SCHEMA, VARIABLE_INACTIVES);
    }
    return pageTitle;
  }

  protected void refreshActions(PageContext ctx) {
    boolean isAdmin = ctx.isAdmin();
    boolean branchActive = ctx.getEntryApi().hasBranch();
    boolean inactiveEnabled = Boolean.TRUE.equals(ctx.getEntryDescriptor().getInactiveMgmt());
    boolean branchingEnabled = ctx.getBranchingStrategy() != MDMBranchingStrategy.NONE;
    boolean entryEditingEnabled = branchActive || !branchingEnabled;
    UiActionBuilder uiActions = UiActions.builder()
        .add(ACTION_DO_QUERY);
    boolean approvingEnabled = mdmApprovalApi != null;
    boolean isEntryEditable =
        Boolean.TRUE.equals(ctx.getEntryDescriptor(ctx.view).getIsValueSet());

    boolean isValueApiPresent = !ObjectUtils.isEmpty(vectorDBApi.getContributionApis());

    boolean isImportable = Boolean.TRUE.equals(ctx.entryDescriptor.getImportable());


    // if API present, approving enabled
    if (approvingEnabled) {
      boolean underApproval = ctx.isUnderApproval();
      boolean isApprover = ctx.isCurrentApprover();
      boolean canEdit = canEdit(isAdmin, underApproval, isApprover);

      uiActions
          .addIf(ACTION_NEW_ENTRY, canEdit, branchActive, !ctx.inactives)
          .addIf(new UiAction().code(ACTION_SHOW_ENTRY_DESCRIPTOR_PAGE),
              canEdit, branchActive, isEntryEditable)
          .addIf(new UiAction().code(ACTION_RECREATE_INDEX),
              isValueApiPresent, canEdit, branchActive, isEntryEditable);
    } else {
      uiActions
          .addIf(ACTION_NEW_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives)
          .addIf(new UiAction().code(ACTION_SHOW_ENTRY_DESCRIPTOR_PAGE),
              branchActive, isEntryEditable)
          .addIf(new UiAction().code(ACTION_RECREATE_INDEX),
              isValueApiPresent, branchActive, isEntryEditable);
    }

    uiActions.addIf(new UiAction().code(ACTION_IMPORT_ENTRIES)
        .inputType(UiActionInputType.FILE)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(ACTION_IMPORT_ENTRIES))
            .color(UiActions.Color.PRIMARY)
            .type(UiActionButtonType.RAISED)
            .feedbackType(UiActionFeedbackType.NONE)),
        isAdmin, isImportable, entryEditingEnabled);

    uiActions
        .addIf(
            new UiAction().code(ACTION_TOGGLE_INACTIVES)
                .descriptor(actionToggleInactivesDescriptor(ctx)),
            isAdmin, inactiveEnabled);

    ctx.getView().actions(uiActions.build());
  }

  protected UiActionDescriptor actionToggleInactivesDescriptor(PageContext ctx) {
    return new UiActionDescriptor().type(UiActionButtonType.STROKED)
        .title(ctx.inactives
            ? localeSettingApi.get(MasterDataManagementApi.SCHEMA, VARIABLE_ACTIVES)
            : localeSettingApi.get(MasterDataManagementApi.SCHEMA,
                VARIABLE_INACTIVES));
  }

  @Override
  public void entryDescriptorPageCallback(UUID viewUuid) {
    PageContext ctx = getContextByViewUUID(viewUuid);
    refreshActions(ctx);
  }

  protected final void refreshGrid(PageContext ctx) {
    TableData<?> data;
    if ((ctx.isAdmin() || ctx.isAdminApprover()) && ctx.getEntryApi().hasBranch()) {
      List<BranchedObjectEntry> list;
      if (ctx.inactives) {
        StoredList inactiveList = ctx.getEntryApi().getInactiveList();
        list = inactiveList.compareWithBranch(ctx.getEntryApi().getBranchUri());
      } else {
        list = ctx.getEntryApi().getBranchingList();
      }
      data = createTableDataForAdminGrid(ctx, list);
    } else {
      StoredList inactiveList = ctx.getEntryApi().getInactiveList();
      StoredList list = ctx.inactives ? inactiveList : ctx.getEntryApi().getList();
      data = createTableDataForPublishedGrid(ctx, list);
    }

    data = postProcessTableDataForGrid(ctx, data);
    gridModelApi.setData(ctx.getView().getUuid(), WIDGET_ENTRY_GRID, data);

  }



  protected TableData<?> postProcessTableDataForGrid(PageContext ctx, TableData<?> data) {
    return data;
  }

  protected TableData<?> createTableDataForAdminGrid(PageContext ctx,
      List<BranchedObjectEntry> list) {
    FilterExpressionList filters = null;
    filters = createFilterExpressionIfPresent(ctx, filters);
    return ctx.searchIndexAdmin
        .executeSearchOnNodes(list.stream().map(i -> {
          ObjectDefinition<?> objectDefinition = ctx.getBranchedObjectDefinition();
          return objectApi.create(ctx.getDefinition().getName(), objectDefinition,
              objectDefinition.toMap(i));
        }), filters);
  }

  protected TableData<?> createTableDataForPublishedGrid(PageContext ctx,
      StoredList list) {
    FilterExpressionList filters = null;
    filters = createFilterExpressionIfPresent(ctx, filters);
    return ctx.searchIndexPublished.executeSearchOnNodes(list.nodesFromCache(), filters);
  }

  private FilterExpressionList createFilterExpressionIfPresent(PageContext ctx,
      FilterExpressionList filters) {
    if (ctx.getFilterModel() != null) {
      filters = filterExpressionBuilderApi.getFilterExpressionList(ctx.getView().getUuid(),
          SearchPageApi.FILTER_BUILDER_WIDGET_ID);
    }
    return filters;
  }

  @Override
  public void performDoQuery(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
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
  public void toggleInactives(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    context.setInactives(!context.inactives);
    getModel(viewUuid).setPageTitle(getPageTitle(context));
    refreshActions(context);

    clearSelection(viewUuid);
    refreshGrid(context);
  }

  @Override
  public void performEditEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    // We need to pass the override of the save action.
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnGridRow(context, gridId, rowId, (r, ctx) -> {
      BranchedObjectEntry branchedObjectEntry =
          objectApi.asType(BranchedObjectEntry.class, r.getData());
      showEditorView(viewUuid, ctx, branchedObjectEntry, request.getCode());
      fireActionPerformed(getUriFromGridRow(branchedObjectUriGetter, r), request, context);
    });
  }

  protected void showEditorView(UUID viewUuid, PageContext ctx,
      BranchedObjectEntry branchedObjectEntry, String actionCode) {
    ObjectDefinition<?> objectDefinition = ctx.getEntryApi().getObjectDefinition();

    boolean isView = ACTION_VIEW_ENTRY.equals(actionCode) ||
        ACTION_VIEW_ORIGINAL_ENTRY.equals(actionCode);

    boolean isViewOriginal = ACTION_VIEW_ORIGINAL_ENTRY.equals(actionCode);

    // Model:
    URI branchUri = isViewOriginal ? null : ctx.getEntryApi().getBranchUri();
    URI objectLatestUri =
        (!isViewOriginal && branchedObjectEntry.getBranchUri() != null) // branchedObjectEntry
                                                                        // contains latest uris
            ? branchedObjectEntry.getBranchUri()
            : branchedObjectEntry.getOriginalUri();

    final ObjectNode modelNode = (objectLatestUri == null)
        ? createNewObject(ctx.getEntryDescriptor())
        : objectApi.load(objectLatestUri, branchUri);

    // Layout:
    final ObjectLayoutDescriptor layoutDescriptor = objectExtensionApi
        .generateDefaultLayout(objectDefinition.getQualifiedName());
    final ObjectDisplay display = objectLayoutApi.getSketchDisplay(modelNode, layoutDescriptor);
    SmartLayoutDefinition layout = display.getDefaultForms().stream()
        .map(SmartLayoutDefinition::getWidgets)
        .flatMap(List::stream)
        .map(w -> {
          if (SmartFormWidgetType.CONTAINER.equals(w.getType())) {
            List<SmartWidgetDefinition> containersToCheck = Lists.newArrayList(w);
            while (!containersToCheck.isEmpty()) {
              List<SmartWidgetDefinition> newContainersToCheck = new ArrayList<>();
              for (SmartWidgetDefinition container : containersToCheck) {
                for (SmartWidgetDefinition component : container.getChildrenComponents()) {
                  if (component.getType() == SmartFormWidgetType.CONTAINER) {
                    newContainersToCheck.add(component);
                  }
                  component.label(
                      localeSettingApi.get(ctx.getEntryApi().getName(),
                          objectDefinition.getClazz().getSimpleName(), component.getLabel()));
                }
              }
              containersToCheck.clear();
              containersToCheck.addAll(newContainersToCheck);
            }
          }

          return w.label(localeSettingApi.get(ctx.getEntryApi().getName(),
              objectDefinition.getClazz().getSimpleName(), w.getLabel()));
        })
        .collect(collectingAndThen(toList(), new SmartLayoutDefinition()::widgets));

    List<UiAction> actions = UiActions.builder()
        .addIf(new UiAction().code(MDMEntryEditPageApi.ACTION_SAVE).submit(true), !isView)
        .add(new UiAction().code(MDMEntryEditPageApi.ACTION_CANCEL))
        .build();
    View view = new View()
        .viewName(getEditorViewName(ctx))
        .type(ViewType.DIALOG)
        .objectUri(modelNode.getObjectUri())
        .branchUri(branchUri)
        .putLayoutsItem(LAYOUT_EDITOR_FORM, layout)
        .putParametersItem(PARAM_MDM_DEFINITION, ctx.getDefinition())
        .putParametersItem(PARAM_ENTRY_DESCRIPTOR, ctx.getEntryDescriptor())
        .putParametersItem(PARAM_BRANCHED_OBJECT_ENTRY, branchedObjectEntry)
        .putParametersItem(PARAM_MDM_LIST_VIEW_UUID, viewUuid)
        .putParametersItem(PARAM_RAW_MODEL, modelNode.getObjectAsMap())
        .putParametersItem(PARAM_ACTION_CODE, actionCode)
        .actions(actions);

    MDMEntryDescriptor descriptor = ctx.entryDescriptor;
    if (!ObjectUtils.isEmpty(descriptor.getEditorParameters())) {
      view.getParameters().putAll(descriptor.getEditorParameters());
    }

    viewApi.showView(view);
  }

  @Override
  public void performDeleteEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, branchedObjectUriGetter,
        (u, ctx) -> {
          ctx.getEntryApi().remove(u);
          fireActionPerformed(u, request, context);
        });
    refreshGrid(context);
  }

  private void fireActionPerformed(URI entryUri, UiActionRequest request, PageContext ctx) {
    fireActionPerformed(entryUri, request, ctx, null, null);
  }

  private void fireActionPerformed(URI entryUri, UiActionRequest request, PageContext ctx,
      Object prevModel, Object nextModel) {
    String entryName = getEntryName(ctx, objectApi.load(entryUri));
    viewPublisherApi.fireActionPerformed(ctx.view, request,
        ctx.modificationApi != null ? ctx.modificationApi.getModification().getId() : "",
        entryName,
        prevModel,
        nextModel);
  }

  private String getEntryName(PageContext ctx, ObjectNode entryNode) {
    List<String> displayNamePropertyPath = ctx.getEntryDescriptor().getDisplayNamePropertyPath();
    String entryName = "unknown";
    if (displayNamePropertyPath != null && !displayNamePropertyPath.isEmpty()) {
      entryName = entryNode.getValueAsString(
          displayNamePropertyPath.toArray(new String[displayNamePropertyPath.size()]));
    }
    return entryName;
  }

  @Override
  public void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, row -> BranchedObjectEntry.BRANCH_URI,
        (u, ctx) -> {
          cancelDraftEntryInner(u, ctx);
          fireActionPerformed(u, request, context);
        });
    refreshGrid(context);
  }

  protected boolean cancelDraftEntryInner(URI u, PageContext ctx) {
    return ctx.getEntryApi().cancel(u);
  }

  @Override
  public void performRestoreEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, branchedObjectUriGetter,
        (u, ctx) -> {
          ctx.getEntryApi().restore(u);
          fireActionPerformed(u, request, context);
        });
    refreshGrid(context);
  }

  @Override
  public void showEntryDescriptorPage(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    View view = viewApi.getView(viewUuid);
    MDMEntryDescriptor entryDescriptor = context.entryApi.getDescriptor();
    MDMDefinition mdmDefinition = context.getDefinition(view);
    // TODO refresh the actions on the sidebar
    InvocationRequest refreshCallBack = invocationApi.builder(MDMEntryListPageApi.class)
        .build(api -> api.entryDescriptorPageCallback(viewUuid));
    viewApi.showView(
        new View()
            .viewName(MDMConstants.MDM_ENTRY_DESCRIPTOR)
            .branchUri(context.mdmBranch)
            .type(ViewType.DIALOG)
            .putParametersItem(MDMEntryDescriptorPageApi.PARAM_MDM_ENTRY_DESCRIPTOR,
                entryDescriptor)
            .putParametersItem(MDMEntryDescriptorPageApi.PARAM_MDM_DEFINITION,
                mdmDefinition.getName())
            .putCallbacksItem(MDMEntryDescriptorPageApi.CALLBACK_REFRESH_ACTIONS, refreshCallBack));
  }

  @Override
  public void recreateIndex(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    View view = viewApi.getView(viewUuid);
    MDMEntryDescriptor entryDescriptor = context.getEntryDescriptor(view);
    MDMDefinition mdmDefinition = context.getDefinition(view);
    MDMEntryApi entryApi =
        masterDataManagementApi.getApi(mdmDefinition.getName(), entryDescriptor.getName(),
            context.mdmBranch);
    entryApi.updateAllIndices();
  }

  @Override
  public void importEntries(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid);
    UploadedFile uploadedFile =
        actionRequestHelper(request).get(UiActions.INPUT, UploadedFile.class);
    mdmSetupApi
        .importEntriesFromCsvFile(ctx.definition.getName(), ctx.entryDescriptor.getName(),
            uploadedFile.getData(), ctx.entryDescriptor.getCsvSeparator(), ctx.mdmBranch);
  }

  @Override
  public void saveObject(UUID viewUuid, URI objectUri, Object editingObject, View editorView,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    ObjectNode objectNode = createObjectNodeToSave(objectUri, editingObject, context);

    saveObjectInternal(context, objectNode, editorView, request);
  }

  private ObjectNode createObjectNodeToSave(URI objectUri, Object editingObject,
      PageContext context) {
    ObjectDefinition<?> objectDefinition = context.getEntryApi().getObjectDefinition();
    Map<String, Object> editingObjectAsMap = objectDefinition.toMap(editingObject);
    ObjectNode objectNode;
    if (objectUri == null) {
      objectNode = objectApi.create(
          context.getEntryApi().getDescriptor().getSchema(),
          objectDefinition,
          editingObjectAsMap);
    } else {
      objectNode = objectApi.load(objectUri, context.getMdmBranch());
      objectNode.setValues(editingObjectAsMap);
    }
    return objectNode;
  }

  @Override
  public void saveObject(UUID viewUuid, ObjectNode objectNode, View editorView,
      UiActionRequest request) {
    saveObjectInternal(getContextByViewUUID(viewUuid), objectNode, editorView, request);
  }

  @Override
  public void saveObject(View view, URI objectUri, Object editingObject, View editorView,
      UiActionRequest request) {
    PageContext context = getContextByView(view);
    ObjectNode objectNode = createObjectNodeToSave(objectUri, editingObject, context);
    saveObjectInternal(context, objectNode, editorView, request);
  }

  @Override
  public void saveObject(View view, ObjectNode objectNode, View editorView,
      UiActionRequest request) {
    saveObjectInternal(getContextByView(view), objectNode, editorView, request);
  }

  protected void saveObjectInternal(PageContext context, ObjectNode objectNode, View editorView,
      UiActionRequest request) {
    mdmRelationEditorService.setRelationsInHost(editorView, objectNode);
    context.getEntryApi().save(objectNode);
    if (editorView == null || request == null) {
      log.warn("cannot fire action performed on save [{}] type entry",
          context.getEntryApi().getName());
    } else {
      String entryName = getEntryName(context, objectNode);
      Object initialEditorModel = editorView.getParameters().get(ViewContexts.INITIAL_MODEL);
      if (initialEditorModel == null) {
        log.warn("cannot fire action performed on save [{}] type entry, initialEditorModel is null",
            context.getEntryApi().getName());
      } else {
        Map<String, Object> initalEditorModelAsMap = initialEditorModel instanceof Map
            ? (Map<String, Object>) initialEditorModel
            : objectApi.getDefaultSerializer().toMap(initialEditorModel);
        Map<String, Object> editorModelAsMap = editorView.getModel() instanceof Map
            ? (Map<String, Object>) editorView.getModel()
            : objectApi.getDefaultSerializer().toMap(editorView.getModel());

        editorModelAsMap.put(MDMEntryApi.Props.CREATED,
            initalEditorModelAsMap.get(MDMEntryApi.Props.CREATED));
        editorModelAsMap.put(MDMEntryApi.Props.UPDATED,
            initalEditorModelAsMap.get(MDMEntryApi.Props.UPDATED));
        editorView.setModel(editorModelAsMap);

        viewPublisherApi.fireActionPerformed(editorView, request,
            context.modificationApi != null ? context.modificationApi.getModification().getId()
                : "",
            entryName);
      }
    }

    // refresh if it's not a placeholder view to save
    if (context.getView().getUuid() != null) {
      refreshGrid(context);
    }
  }

  private final void performActionOnGridRow(PageContext context, String gridId, String rowId,
      BiConsumer<GridRow, PageContext> action) {
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, context.getView().getUuid(), gridId);
    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    gridRow.ifPresent(r -> action.accept(r, context));
  }

  protected final void performActionOnEntry(PageContext context, String gridId, String rowId,
      Function<GridRow, String> uriPropertyGetter, BiConsumer<URI, PageContext> action) {
    performActionOnGridRow(context, gridId, rowId, (r, ctx) -> {
      URI objectUri = getUriFromGridRow(uriPropertyGetter, r);
      if (objectUri != null) {
        action.accept(objectUri, context);
      }
    });
  }

  protected URI getUriFromGridRow(Function<GridRow, String> uriPropertyGetter, GridRow r) {
    Object valueFromGridRow =
        GridModels.getValueFromGridRow(r, uriPropertyGetter.apply(r));
    return valueFromGridRow instanceof URI ? (URI) valueFromGridRow
        : (valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null);
  }

  public final MDMEntryListPageApi defaultEditorViewName(String defaultEditorViewName) {
    this.defaultEditorViewName = defaultEditorViewName;
    return this;
  }

  private final String getEditorViewName(PageContext context) {
    return context.getEntryDescriptor().getEditorViewName() == null ? defaultEditorViewName
        : context.getEntryDescriptor().getEditorViewName();
  }

  @Override
  public GridPage addWidgetEntryGridActions(GridPage page, UUID viewUuid) {
    PageContext ctx = getContextByViewUUID(viewUuid);
    return addWidgetEntryGridActionsInner(page, ctx);
  }

  protected GridPage addWidgetEntryGridActionsInner(GridPage page, PageContext ctx) {
    boolean isAdmin = ctx.isAdmin();
    boolean branchActive = ctx.getEntryApi().hasBranch();
    boolean branchingEnabled = ctx.getBranchingStrategy() != MDMBranchingStrategy.NONE;
    boolean entryEditingEnabled = branchActive || !branchingEnabled;
    boolean approvingEnabled = mdmApprovalApi != null;

    page.getRows().forEach(row -> {
      boolean isOnBranch =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI) != null;
      boolean isOnOriginal =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.ORIGINAL_URI) != null;
      Object oBranchingState =
          GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCHING_STATE);
      boolean newOnBranch = BranchingStateEnum.NEW.equals(oBranchingState);
      boolean deletedOnBranch = BranchingStateEnum.DELETED.equals(oBranchingState);
      boolean isNewEntry =
          Objects.equals(GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCHING_STATE),
              BranchedObjectEntry.BranchingStateEnum.NEW);

      boolean inactive = !ctx.inactives && !newOnBranch && deletedOnBranch;

      if (Boolean.TRUE.equals(isAdmin)
          && Boolean.TRUE.equals(entryEditingEnabled)
          && Boolean.TRUE.equals(inactive)) {
        row.selectable(Boolean.FALSE);
      }

      UiActionBuilder uiActions = UiActions.builder();
      if (approvingEnabled) {
        boolean canEdit = canEdit(isAdmin, ctx.isUnderApproval(), ctx.isCurrentApprover());
        uiActions
            .addIf(createUiActionWithDescriptor(ACTION_RESTORE_ENTRY), canEdit, entryEditingEnabled,
                ctx.inactives)
            .addIf(createUiActionWithDescriptor(ACTION_EDIT_ENTRY), canEdit, entryEditingEnabled,
                !ctx.inactives,
                !deletedOnBranch)
            .addIf(createUiActionWithDescriptor(ACTION_DELETE_ENTRY), canEdit, entryEditingEnabled,
                !ctx.inactives, newOnBranch)
            .addIf(createUiActionWithDescriptor(ACTION_INACTIVATE_ENTRY), canEdit,
                entryEditingEnabled, !ctx.inactives,
                !newOnBranch, !deletedOnBranch)
            .addIf(createUiActionWithDescriptor(ACTION_CANCEL_DRAFT_ENTRY), canEdit,
                entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch, !isNewEntry)
            .addIf(createUiActionWithDescriptor(ACTION_VIEW_ORIGINAL_ENTRY),
                (isAdmin || ctx.isCurrentApprover()),
                entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch && isOnOriginal)
            .addIf(createUiActionWithDescriptor(ACTION_VIEW_ENTRY),
                (isAdmin || ctx.isCurrentApprover()));
      } else {
        uiActions
            .addIf(createUiActionWithDescriptor(ACTION_VIEW_ENTRY),
                isAdmin, !entryEditingEnabled)
            .addIf(createUiActionWithDescriptor(ACTION_RESTORE_ENTRY), isAdmin, entryEditingEnabled,
                ctx.inactives)
            .addIf(createUiActionWithDescriptor(ACTION_EDIT_ENTRY), isAdmin, entryEditingEnabled,
                !ctx.inactives,
                !deletedOnBranch)
            .addIf(createUiActionWithDescriptor(ACTION_DELETE_ENTRY), isAdmin, entryEditingEnabled,
                !ctx.inactives, newOnBranch)
            .addIf(createUiActionWithDescriptor(ACTION_INACTIVATE_ENTRY), isAdmin,
                entryEditingEnabled, !ctx.inactives,
                !newOnBranch, !deletedOnBranch)
            .addIf(createUiActionWithDescriptor(ACTION_VIEW_ORIGINAL_ENTRY), isAdmin,
                entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch && isOnOriginal)
            .addIf(createUiActionWithDescriptor(ACTION_CANCEL_DRAFT_ENTRY), isAdmin,
                entryEditingEnabled, branchingEnabled,
                !ctx.inactives, isOnBranch, !isNewEntry);
      }
      row.setActions(uiActions.build());
      String icon;
      Map<String, Object> map = (Map<String, Object>) row.getData();
      BranchingStateEnum state = (BranchingStateEnum) map.get(BranchedObjectEntry.BRANCHING_STATE);
      icon = setIconToEntry(ctx.view.getUuid(), state);
      if (icon != null) {
        row.putIconsItem(BranchedObjectEntry.BRANCHING_STATE,
            Arrays.asList(new ImageResource()
                .source("smart-icon")
                .identifier(icon)));
      }

      boolean entryDescHasStateColumn = ctx.entryDescriptor.getTableColumns().stream()
          .anyMatch(desc -> MDMModificationItem.STATE.equals(desc.getName()));
      if (entryDescHasStateColumn) {
        Object branchedObjectUriRaw =
            GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI);
        URI objectUri = branchedObjectUriRaw != null
            ? URI.create(branchedObjectUriRaw.toString())
            : null;
        Map<String, MDMModificationItem> modificationItems = ctx.getModificationApi() != null
            ? ctx.getModificationApi().getModification().getModificationItems()
            : null;
        if (entryDescHasStateColumn && objectUri != null && modificationItems != null
            && modificationItems.containsKey(objectUri.toString())) {
          StateEnum itemState = modificationItems.get(objectUri.toString()).getState();
          map.put(MDMModification.STATE, itemState);
          map.put(MDMDefinitionOption.STATE_NAME, localeSettingApi.get(itemState));
        }
      }
    });
    return page;
  }

  protected String setIconToEntry(UUID viewUuid, BranchingStateEnum state) {
    String icon;
    DeviceInfo deviceInfo = getDeviceInfo(viewUuid);
    String componentLibrary = null;
    if (deviceInfo != null) {
      componentLibrary = deviceInfo.getComponentLibrary();
    }
    if (componentLibrary == null) {
      componentLibrary = StringConstant.EMPTY;
    }

    switch (state) {
      case NEW:
        icon = getNewSateIcon(viewUuid, componentLibrary);
        break;
      case MODIFIED:
        icon = getModifiedSateIcon(viewUuid, componentLibrary);
        break;
      case DELETED:
        icon = getDeletedSateIcon(viewUuid, componentLibrary);
        break;
      default:
        icon = getDefaulSateIcon(viewUuid, componentLibrary);
        break;
    }
    return icon;
  }

  protected String getNewSateIcon(UUID viewUuid, String componentLibrary) {
    return componentLibrary.equals(UiActions.ComponentLibrary.PRIMENG)
        ? "plus-circle"
        : "add_circle";
  }

  protected String getModifiedSateIcon(UUID viewUuid, String componentLibrary) {
    return componentLibrary.equals(UiActions.ComponentLibrary.PRIMENG)
        ? "hashtag"
        : "tag";
  }

  protected String getDeletedSateIcon(UUID viewUuid, String componentLibrary) {
    return componentLibrary.equals(UiActions.ComponentLibrary.PRIMENG)
        ? "times-circle"
        : "cancel";
  }

  protected String getDefaulSateIcon(UUID viewUuid, String componentLibrary) {
    return componentLibrary.equals(UiActions.ComponentLibrary.PRIMENG)
        ? "circle"
        : "radio_button_unchecked";
  }

  private UiAction createUiActionWithDescriptor(String actionCode) {
    return new UiAction().code(actionCode).descriptor(
        new UiActionDescriptor()
            .title(localeSettingApi.get(MDMEntryListPageApi.class.getSimpleName(), actionCode)));
  }

  protected boolean canEdit(boolean isAdmin, boolean underApproval, boolean isApprover) {
    return (isAdmin && !underApproval) || (isApprover && underApproval);
  }

  @Override
  public void activateSelected(UUID viewUuid, UiActionRequest request) {
    setActiveState(viewUuid, request, Boolean.TRUE);
  }

  @Override
  public void inactivateSelected(UUID viewUuid, UiActionRequest request) {
    setActiveState(viewUuid, request, Boolean.FALSE);
  }

  protected void setActiveState(UUID viewUuid, UiActionRequest request, Boolean activate) {
    List<GridRow> selectedRows = gridModelApi.getSelectedRows(viewUuid, WIDGET_ENTRY_GRID);
    PageContext context = getContextByViewUUID(viewUuid);

    selectedRows.stream()
        // .filter(row -> {
        //
        // Object oBranchingState =
        // GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCHING_STATE);
        // boolean newOnBranch = BranchingStateEnum.NEW.equals(oBranchingState);
        // boolean deletedOnBranch = BranchingStateEnum.DELETED.equals(oBranchingState);
        //
        // boolean inactive = !newOnBranch && deletedOnBranch;
        // if (context.inactives) {
        //
        // return inactive;
        // } else {
        //
        // return !inactive;
        // }
        // })
        .map(row -> getUriFromGridRow(branchedObjectUriGetter, row))
        .forEach(uri -> {
          if (Boolean.TRUE.equals(activate)) {
            context.getEntryApi().restore(uri);
          } else if (Boolean.FALSE.equals(activate)) {
            context.getEntryApi().remove(uri);
          }
          fireActionPerformed(uri, request, context);
        });

    clearSelection(viewUuid);
    refreshGrid(context);
  }

  @Override
  public void handleGridSelectionChange(UUID viewUuid, String gridId) {
    View view = viewApi.getView(viewUuid);
    List<GridRow> selectedRows = gridModelApi.getSelectedRows(viewUuid, gridId);

    if (ObjectUtils.isEmpty(selectedRows)) {
      UiActions.remove(view,
          ACTIVATE_SELECTED,
          INACTIVATE_SELECTED);
      return;
    }

    PageContext context = getContextByViewUUID(viewUuid);
    if (Boolean.TRUE.equals(context.inactives)) {
      UiActions.add(view, ACTION_ACTIVATE_SELECTED.get()
          .descriptor(getActivateSelectedDescriptor()));
    } else {
      UiActions.add(view, ACTION_INACTIVATE_SELECTED.get()
          .descriptor(getInactivateSelectedDescriptor()));
    }
  }

  protected UiActionDescriptor getActivateSelectedDescriptor() {
    return new UiActionDescriptor()
        .color(UiActions.Color.PRIMARY)
        .type(UiActionButtonType.RAISED)
        .title(localeSettingApi.get(ACTIVATE_SELECTED));
  }

  protected UiActionDescriptor getInactivateSelectedDescriptor() {
    return new UiActionDescriptor()
        .color(UiActions.Color.PRIMARY)
        .type(UiActionButtonType.RAISED)
        .title(localeSettingApi.get(INACTIVATE_SELECTED));
  }


  protected void clearSelection(UUID viewUuid) {
    gridModelApi.selectAllRow(viewUuid, WIDGET_ENTRY_GRID, false);
  }

}
