package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryListPageApiImpl extends PageApiImpl<MDMEntryDescriptor>
    implements MDMEntryListPageApi {

  private static final Logger log = LoggerFactory.getLogger(MDMEntryListPageApiImpl.class);

  private static final String PROPERTY_URI = "uri";

  public MDMEntryListPageApiImpl() {
    super(MDMEntryDescriptor.class);
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

  /**
   * The name of the default editor in the application. It is opened as editor if the editor view is
   * not set for the given entry.
   */
  private String defaultEditorViewName;

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  private class PageContext {

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

    boolean checkAdmin() {
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

  private PageContext getContextByViewUUID(UUID viewUuid) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return result.loadByView();
  }

  private PageContext getContextByView(View view) {
    PageContext result = new PageContext();
    result.view = view;
    return result.loadByView();
  }

  @Override
  public MDMEntryDescriptor initModel(View view) {
    PageContext context = getContextByView(view);

    GridModel entryGridModel =
        gridModelApi.createGridModel(context.searchIndexAdmin.getDefinition().getDefinition(),
            context.searchIndexAdmin.getDefinition().getDefinition().allProperties().stream()
                .map(Property::getName).collect(toList()),
            context.definition.getName(), context.entryDescriptor.getName());
    gridModelApi.initGridInView(view.getUuid(), WIDGET_ENTRY_GRID, entryGridModel);

    refreshGrid(context);

    return context.entryDescriptor;
  }

  private final void refreshGrid(PageContext ctx) {

    if (ctx.checkAdmin()) {
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_ENTRY_GRID,
          ctx.searchIndexAdmin.executeSearchOnNodes(ctx.entryApi.getAllEntries().stream().map(i -> {
            ObjectDefinition<?> objectDefinition = ctx.getBranchedObjectDefinition();
            return objectApi.create(ctx.definition.getName(), objectDefinition,
                objectDefinition.toMap(i));
          }), null));

    } else {
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_ENTRY_GRID,
          ctx.searchIndexPublished.executeSearchOn(ctx.entryApi.getPublishedList().uris().stream(),
              null));
    }

  }

  @Override
  public void performDoQuery(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    refreshGrid(context);
  }

  @Override
  public void cancelChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    context.entryApi.cancelCurrentModifications();
    refreshGrid(context);
  }

  @Override
  public void newEntry(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    // We need to pass the override of the save action.
    viewApi.showView(
        new View()
            .viewName(getEditorViewName(context))
            .putParametersItem(PARAM_MDM_DEFINITION, context.definition)
            .putParametersItem(PARAM_ENTRY_DESCRIPTOR, context.entryDescriptor)
            .model(createNewObject(context.entryDescriptor))

    );
  }

  private final Map<String, Object> createNewObject(MDMEntryDescriptor entryDescriptor) {
    Class<?> clazz;
    try {
      clazz = Class.forName(entryDescriptor.getTypeQualifiedName());
    } catch (ClassNotFoundException e1) {
      clazz = Object.class;
    }
    Object modelObject;
    try {
      modelObject = clazz.newInstance();
    } catch (IllegalAccessException | InstantiationException e) {
      modelObject = new Object();
    }
    ObjectNode model = objectApi.create(entryDescriptor.getSchema(), modelObject);
    return model.getObjectAsMap();
  }



  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    context.entryApi.publishCurrentModifications();
    refreshGrid(context);
  }

  @Override
  public void performEditEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    // We need to pass the override of the save action.
    performActionOnGridRow(getContextByViewUUID(viewUuid), gridId, rowId, (r, ctx) -> {
      BranchedObjectEntry branchedObjectEntry =
          objectApi.asType(BranchedObjectEntry.class, r.getData());
      viewApi.showView(
          new View()
              .viewName(getEditorViewName(ctx))
              .type(ViewType.DIALOG)
              .putParametersItem(PARAM_MDM_DEFINITION, ctx.definition)
              .putParametersItem(PARAM_ENTRY_DESCRIPTOR, ctx.entryDescriptor)
              .putParametersItem(PARAM_BRANCHED_OBJECT_ENTRY, branchedObjectEntry)
              .putParametersItem(PARAM_MDM_LIST_VIEW, viewUuid)
              .actions(UiActions.builder().add(MDMEntryEditPageApi.ACTION_SAVE)
                  .add(MDMEntryEditPageApi.ACTION_CANCEL).build())
              .model(constructEditingObject(branchedObjectEntry)));
    });
  }

  private final Object constructEditingObject(
      BranchedObjectEntry branchedObjectEntry) {
    URI uri = branchedObjectEntry.getBranchUri() != null ? branchedObjectEntry.getBranchUri()
        : branchedObjectEntry.getOriginalUri();
    return objectApi.load(uri).getObjectAsMap();
  }

  @Override
  public void performDeleteEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId,
        (u, ctx) -> {
          ctx.entryApi.deleteObject(u);
        });
    refreshGrid(context);
  }

  @Override
  public void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, (u, ctx) -> ctx.entryApi.cancelDraft(u));
    refreshGrid(context);
  }

  @Override
  public void saveNewObject(UUID viewUuid, Object editingObject) {
    PageContext context = getContextByViewUUID(viewUuid);
    ObjectDefinition<?> objectDefinition = context.getObjectDefinition();
    context.entryApi.saveAsDraft(objectDefinition, objectDefinition.toMap(editingObject));
    refreshGrid(context);
  }

  @Override
  public void saveModificationObject(UUID viewUuid, Object editingObject,
      BranchedObjectEntry branchedObjectEntry) {
    PageContext context = getContextByViewUUID(viewUuid);
    ObjectDefinition<?> objectDefinition = context.getObjectDefinition();
    context.entryApi.saveAsDraft(objectDefinition, objectDefinition.toMap(editingObject));
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
      BiConsumer<URI, PageContext> action) {
    performActionOnGridRow(context, gridId, rowId, (r, ctx) -> {
      Object valueFromGridRow =
          GridModels.getValueFromGridRow(r, PROPERTY_URI);
      URI objectUri = valueFromGridRow instanceof URI ? (URI) valueFromGridRow
          : (valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null);

      if (objectUri != null) {
        action.accept(objectUri, context);
      }
    });

    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, context.view.getUuid(), gridId);
    Object valueFromGridRow =
        GridModels.getValueFromGridRow(gridModel, rowId, PROPERTY_URI);
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

}
