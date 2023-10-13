package org.smartbit4all.bff.api.mdm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
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

    Set<String> propertiesToRemove = new HashSet<>(
        Arrays.asList(BranchedObjectEntry.ORIGINAL_URI, BranchedObjectEntry.BRANCH_URI));

    GridModel entryGridModel =
        gridModelApi.createGridModel(context.searchIndexAdmin.getDefinition().getDefinition(),
            context.searchIndexAdmin.getDefinition().getDefinition().allProperties().stream()
                .filter(p -> !propertiesToRemove.contains(p.getName()))
                .map(Property::getName).collect(toList()),
            context.definition.getName(), context.entryDescriptor.getName());

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

  private void refreshActions(PageContext ctx) {
    boolean isAdmin = ctx.checkAdmin();
    boolean branchActive = ctx.entryApi.hasBranch();
    boolean inactiveEnabled = Boolean.TRUE.equals(ctx.entryDescriptor.getInactiveMgmt());
    boolean branchingEnabled = ctx.branchingStrategy != MDMBranchingStrategy.NONE;
    boolean entryEditingEnabled = branchActive || !branchingEnabled;

    UiActionBuilder uiActions = UiActions.builder()
        .add(ACTION_DO_QUERY)
        .addIf(ACTION_NEW_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives)
        .addIf(ACTION_START_EDITING, isAdmin, branchingEnabled, !branchActive, !ctx.inactives)
        .addIf(ACTION_FINALIZE_CHANGES, isAdmin, branchingEnabled, branchActive, !ctx.inactives)
        .addIf(ACTION_CANCEL_CHANGES, isAdmin, branchingEnabled, branchActive, !ctx.inactives)
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

  private final void refreshGrid(PageContext ctx) {

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
          ctx.searchIndexPublished.executeSearchOn(list.uris().stream(),
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
        new BranchedObjectEntry().branchingState(BranchingStateEnum.NEW));
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
      showEditorView(viewUuid, ctx, branchedObjectEntry);
    });
  }

  protected void showEditorView(UUID viewUuid, PageContext ctx,
      BranchedObjectEntry branchedObjectEntry) {
    ObjectDefinition<?> objectDefinition = ctx.getObjectDefinition();

    // Model:
    URI branchUri = ctx.entryApi.getBranchUri();
    URI objectLatestUri = getEditingObjectUri(branchedObjectEntry); // branchedObjectEntry contains
                                                                    // latest uris
    final ObjectNode modelNode = (objectLatestUri == null)
        ? createNewObject(ctx.entryDescriptor)
        : objectApi.load(objectLatestUri, branchUri);
    // objectUri, branchUri
    // Layout:
    final ObjectLayoutDescriptor layoutDescriptor = objectExtensionApi
        .generateDefaultLayout(objectDefinition.getQualifiedName());
    final ObjectDisplay display = objectLayoutApi.getSketchDisplay(modelNode, layoutDescriptor);
    SmartLayoutDefinition layout = display.getDefaultForms().stream()
        .map(SmartLayoutDefinition::getWidgets)
        .flatMap(List::stream)
        .collect(collectingAndThen(toList(), new SmartLayoutDefinition()::widgets));

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
        .actions(Arrays.asList(
            new UiAction().code(MDMEntryEditPageApi.ACTION_SAVE).submit(true),
            new UiAction().code(MDMEntryEditPageApi.ACTION_CANCEL)))
        .putParametersItem(PARAM_RAW_MODEL, modelNode.getObjectAsMap()));
  }

  private URI getEditingObjectUri(BranchedObjectEntry branchedObjectEntry) {
    return (branchedObjectEntry.getBranchUri() != null)
        ? branchedObjectEntry.getBranchUri()
        : branchedObjectEntry.getOriginalUri();
  }

  @Override
  public void performDeleteEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, BranchedObjectEntry.ORIGINAL_URI,
        (u, ctx) -> {
          ctx.entryApi.remove(u);
        });
    refreshGrid(context);
  }

  @Override
  public void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, BranchedObjectEntry.BRANCH_URI,
        (u, ctx) -> ctx.entryApi.cancel(u));
    refreshGrid(context);
  }

  @Override
  public void performRestoreEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId,
        context.entryApi.getBranchUri() != null ? BranchedObjectEntry.BRANCH_URI
            : ObjectDefinition.URI_PROPERTY,
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
      String uriProperty, BiConsumer<URI, PageContext> action) {
    performActionOnGridRow(context, gridId, rowId, (r, ctx) -> {
      Object valueFromGridRow =
          GridModels.getValueFromGridRow(r, uriProperty);
      URI objectUri = valueFromGridRow instanceof URI ? (URI) valueFromGridRow
          : (valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null);

      if (objectUri != null) {
        action.accept(objectUri, context);
      }
    });

    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, context.view.getUuid(), gridId);
    Object valueFromGridRow =
        GridModels.getValueFromGridRow(gridModel, rowId, uriProperty);
    URI objectUri = valueFromGridRow instanceof URI ? (URI) valueFromGridRow
        : (valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null);

    if (objectUri != null) {
      action.accept(objectUri, context);
    }
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

    page.getRows().forEach(row -> {
      row.setActions(UiActions.builder()
          // .add("VIEW")
          .addIf(ACTION_RESTORE_ENTRY, isAdmin, ctx.inactives)
          .add(ACTION_EDIT_ENTRY) // isAdmin, entryEditingEnabled, !ctx.inactives)
          .addIf(ACTION_DELETE_ENTRY, isAdmin, entryEditingEnabled, !ctx.inactives)
          // TODO check if draft present..
          .addIf(ACTION_CANCEL_DRAFT_ENTRY, isAdmin, entryEditingEnabled, branchingEnabled,
              !ctx.inactives)
          .build());
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
