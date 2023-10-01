package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
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
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MDMEntryApiImpl;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
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
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryListPageApiImpl extends PageApiImpl<SearchPageModel>
    implements MDMEntryListPageApi {

  private static final Logger log = LoggerFactory.getLogger(MDMEntryListPageApiImpl.class);

  private static final String PROPERTY_URI = "uri";

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
    refreshActions(view, context);

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
    gridModelApi.addGridRowCallback(view.getUuid(), WIDGET_ENTRY_GRID, invocationApi
        .builder(MDMEntryListPageApi.class)
        .build(api -> api.addWidgetEntryGridActions(null, view.getUuid())));

    refreshGrid(context);

    String pageTitle = context.entryApi.getDisplayNameList();
    FilterExpressionFieldList filters = null;
    return new SearchPageModel()
        .pageTitle(pageTitle)
        .filters(null);
  }

  private void refreshActions(View view, PageContext context) {
    boolean isAdmin = context.checkAdmin();
    boolean hasBranch = context.entryApi.hasBranch();
    List<UiAction> uiActions = UiActions.builder()
        .add(ACTION_DO_QUERY)
        .addIf(ACTION_NEW_ENTRY, isAdmin)
        .addIf(ACTION_START_EDITING, isAdmin, !hasBranch)
        .addIf(ACTION_FINALIZE_CHANGES, isAdmin, hasBranch)
        .addIf(ACTION_CANCEL_CHANGES, isAdmin, hasBranch)
        .build();

    view.actions(uiActions);
  }

  private final void refreshGrid(PageContext ctx) {

    if (ctx.checkAdmin() && ctx.entryApi.hasBranch()) {
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_ENTRY_GRID,
          ctx.searchIndexAdmin
              .executeSearchOnNodes(ctx.entryApi.getBranchingList().stream().map(i -> {
                ObjectDefinition<?> objectDefinition = ctx.getBranchedObjectDefinition();
                return objectApi.create(ctx.definition.getName(), objectDefinition,
                    objectDefinition.toMap(i));
              }), null));

    } else {
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_ENTRY_GRID,
          ctx.searchIndexPublished.executeSearchOn(ctx.entryApi.getList().uris().stream(),
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
    refreshActions(context.view, context);
    refreshGrid(context);
  }

  @Override
  public void cancelChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.dropGlobal(context.definition.getName());
    refreshActions(context.view, context);
    refreshGrid(context);
  }

  @Override
  public void newEntry(UUID viewUuid, UiActionRequest request) {
    showEditorView(viewUuid, getContextByViewUUID(viewUuid), null);
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
    refreshActions(context.view, context);
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
    final ObjectNode modelNode = (branchedObjectEntry == null)
        ? createNewObject(ctx.entryDescriptor)
        : objectApi.load(getEditingObjectUri(branchedObjectEntry));
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
  public void saveObject(UUID viewUuid, URI objectUri, Object editingObject,
      BranchedObjectEntry branchedObjectEntry) {
    PageContext context = getContextByViewUUID(viewUuid);
    ObjectDefinition<?> objectDefinition = context.getObjectDefinition();
    Map<String, Object> editingObjectAsMap = objectDefinition.toMap(editingObject);
    URI uri = objectApi.asType(URI.class, editingObjectAsMap.get(MDMEntryApiImpl.uriPath[0]));
    ObjectNode objectNode;
    if (uri == null) {
      objectNode = objectApi.create(
          context.entryApi.getDescriptor().getSchema(),
          objectDefinition,
          editingObjectAsMap);
    } else {
      objectNode = objectApi.load(uri);
      objectNode.setValues(editingObjectAsMap);
    }
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
  public GridRow addWidgetEntryGridActions(GridRow row, UUID viewUuid) {
    PageContext ctx = getContextByViewUUID(viewUuid);
    if (ctx.checkAdmin()) {
      row.addActionsItem(new UiAction().code(ACTION_EDIT_ENTRY))
          .addActionsItem(new UiAction().code(ACTION_DELETE_ENTRY));
      if (ctx.entryApi.hasBranch()) {
        row.addActionsItem(new UiAction().code(ACTION_CANCEL_DRAFT_ENTRY));
      }
    }
    return row;
  }

}
