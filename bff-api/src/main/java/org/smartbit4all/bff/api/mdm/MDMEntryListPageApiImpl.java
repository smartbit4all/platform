package org.smartbit4all.bff.api.mdm;

import java.net.URI;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMEntryInstance;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class MDMEntryListPageApiImpl extends PageApiImpl<MDMEntryDescriptor>
    implements MDMEntryListPageApi {

  private static final String PROPERTY_URI = "uri";

  public MDMEntryListPageApiImpl() {
    super(MDMEntryDescriptor.class);
  }

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private GridModelApi gridModelApi;

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  /**
   * The name of the default editor in the application. It is opened as editor if the editor view is
   * not set for the given entry.
   */
  private String defaultEditorViewName;

  private class PageContext {

    View view;
    MDMEntryDescriptor entryDescriptor;
    MDMDefinition definition;
    MDMEntryApi entryApi;
    SearchIndex<MDMEntryInstance> searchIndex;

    PageContext loadByView() {
      entryDescriptor = getEntryDescriptor(view);
      definition = getDefinition(view);
      entryApi =
          masterDataManagementApi.getApi(definition.getName(), entryDescriptor.getName());

      searchIndex =
          collectionApi.searchIndex(definition.getName(), entryDescriptor.getName(),
              MDMEntryInstance.class);

      return this;
    }

    private final MDMDefinition getDefinition(View view) {
      return extractParam(MDMDefinition.class, MDM_DEFINITION, view.getParameters());
    }

    private final MDMEntryDescriptor getEntryDescriptor(View view) {
      return extractParam(MDMEntryDescriptor.class, ENTRY_DESCRIPTOR, view.getParameters());
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
        gridModelApi.createGridModel(context.searchIndex.getDefinition().getDefinition(),
            context.searchIndex.getDefinition().getDefinition().allProperties().stream()
                .map(Property::getName).collect(toList()),
            context.definition.getName(), context.entryDescriptor.getName());
    gridModelApi.initGridInView(view.getUuid(), ENTRY_GRID, entryGridModel);


    return context.entryDescriptor;
  }

  private final void refreshGrid(PageContext ctx) {
    // TODO tricky-dicky with the draft and published objects.
    gridModelApi.setData(ctx.view.getUuid(), ENTRY_GRID,
        ctx.searchIndex.executeSearchOn(ctx.entryApi.getPublishedList().uris().stream(),
            null));
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
  }

  @Override
  public void newEntry(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    context.entryApi.publishCurrentModifications();
  }

  @Override
  public void performEditEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    performActionOnEntry(getContextByViewUUID(viewUuid), gridId, rowId, request, (u, ctx) -> {
      viewApi.showView(
          new View()
              .viewName(ctx.entryDescriptor.getEditorViewName() == null ? defaultEditorViewName
                  : ctx.entryDescriptor.getEditorViewName())
              .putParametersItem(MDM_DEFINITION, ctx.definition)
              .putParametersItem(ENTRY_DESCRIPTOR, ctx.entryDescriptor).objectUri(u));
    });
  }

  @Override
  public void performDeleteEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    performActionOnEntry(getContextByViewUUID(viewUuid), gridId, rowId, request,
        (u, ctx) -> {
          // entryApi.delete
        });
  }

  @Override
  public void performCancelDraftEntry(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    performActionOnEntry(context, gridId, rowId, request, (u, ctx) -> ctx.entryApi.cancelDraft(u));
  }

  private final void performActionOnEntry(PageContext context, String gridId, String rowId,
      UiActionRequest request,
      BiConsumer<URI, PageContext> action) {
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, context.view.getUuid(), ENTRY_GRID);
    Object valueFromGridRow =
        GridModels.getValueFromGridRow(gridModel, rowId, PROPERTY_URI);
    URI objectUri =
        valueFromGridRow instanceof String ? URI.create((String) valueFromGridRow) : null;

    if (objectUri != null) {
      action.accept(objectUri, context);
    }

  }

  public final MDMEntryListPageApi defaultEditorViewName(String defaultEditorViewName) {
    this.defaultEditorViewName = defaultEditorViewName;
    return this;
  }

}
