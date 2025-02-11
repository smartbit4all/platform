package org.smartbit4all.bff.api.search;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel.TypeEnum;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.grid.bean.GridSelectionType;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.grid.bean.GridViewDescriptor;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.bean.ObjectHistoryIteratorData;
import org.smartbit4all.api.object.bean.ObjectHistoryRangeData;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectHistoryIterator;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.collect.Streams;

public class SearchPageApiImpl extends PageApiImpl<SearchPageModel>
    implements SearchPageApi {

  @Autowired
  protected CollectionApi collectionApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  protected InvocationApi invocationApi;

  @Autowired
  protected FilterExpressionBuilderApi filterExpressionBuilderApi;

  @Value("${searchpage.historyPageSize:50}")
  private int defaultHistoryPageSize = 50;

  protected class PageContext {

    public PageContext(UUID viewUUID) {
      super();
      this.viewUUID = viewUUID;
      view = viewApi.getView(viewUUID);
      ObjectMapHelper params = parameters(view);
      pageConfig = params.get(PARAM_SEARCHPAGECONFIG, SearchPageConfig.class);
      if (pageConfig == null) {
        pageConfig = objectApi
            .loadLatest(view.getObjectUri())
            .getObject(SearchPageConfig.class);
        params.getMap().put(PARAM_SEARCHPAGECONFIG, pageConfig);
      }
      ObjectMapHelper parameters = parameters(view);
      searchIndex = collectionApi.searchIndex(
          pageConfig.getSearchIndexSchema(),
          pageConfig.getSearchIndexName());
      uris = parameters.getAsList(PARAM_URI_LIST, URI.class);
      StoredCollectionDescriptor listDescriptor =
          parameters.get(PARAM_STORED_LIST, StoredCollectionDescriptor.class);
      if (listDescriptor != null) {
        list = collectionApi.list(listDescriptor.getSchema(), listDescriptor.getName());
      }
      selectionCallback = parameters.get(PARAM_SELECTION_CALLBACK, InvocationRequest.class);
      gridPageRenderCallback = parameters.get(
          PARAM_GRID_PAGE_RENDER_CALLBACK,
          InvocationRequest.class);
    }

    public UUID viewUUID;

    public View view;

    protected SearchIndex<?> searchIndex;

    public SearchPageConfig pageConfig;

    protected List<URI> uris;

    protected StoredList list;

    protected InvocationRequest selectionCallback;

    protected InvocationRequest gridPageRenderCallback;
  }

  public SearchPageApiImpl() {
    super(SearchPageModel.class);
  }

  @Override
  public SearchPageModel initModel(View view) {
    PageContext ctx = new PageContext(view.getUuid());

    GridModel gridModel = gridModelApi.createGridModel(
        ctx.searchIndex.getDefinition().getDefinition(),
        ctx.searchIndex.getDefinition().getDefinition().allProperties().stream()
            .map(Property::getName)
            .collect(toList()),
        ctx.searchIndex.logicalSchema(), ctx.searchIndex.name());

    if (ctx.pageConfig.getDefaultRowActions() != null) {
      gridModel.setDefaultRowActions(ctx.pageConfig.getDefaultRowActions());
    }

    final List<GridView> gridViewOptions = ctx.pageConfig.getGridViewOptions();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      gridModel.setView(gridViewOptions.get(0));
      gridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
      gridModel.setPageSize(ctx.pageConfig.getPageSize());
    }

    if (ctx.selectionCallback != null) {
      ctx.view.addActionsItem(new UiAction().code(ACTION_RETURN_SELECTED_ROWS).submit(true));
      GridViewDescriptor gridViewDescriptor = gridModel.getView().getDescriptor();
      if (gridViewDescriptor.getSelectionMode() == null) {
        gridViewDescriptor.setSelectionMode(GridSelectionMode.SINGLE);
      }
      gridViewDescriptor.setSelectionType(GridSelectionType.CHECKBOX);
      gridViewDescriptor.setPreserveSelectionOnPageChange(false);
    }

    gridModel.qualifier(ctx.pageConfig.getQualifier());
    gridModelApi.initGridInView(ctx.viewUUID, WIDGET_RESULT_GRID, gridModel);
    if (ctx.gridPageRenderCallback != null) {
      gridModelApi.addGridPageCallback(
          ctx.viewUUID,
          WIDGET_RESULT_GRID,
          ctx.gridPageRenderCallback);
    }

    // Initiate the history selection if it is set in the parameters.

    SearchPageModel model = new SearchPageModel();
    if (ctx.pageConfig.getHistoryObjectUri() != null) {


      int pageSize =
          ctx.pageConfig.getHistoryPageSize() != null ? ctx.pageConfig.getHistoryPageSize()
              : defaultHistoryPageSize;
      model
          .historyPageSize(ctx.pageConfig.getHistoryLoadAllLimit() == null
              ? pageSize
              : ctx.pageConfig.getHistoryLoadAllLimit());
      ObjectHistoryIterator historyIterator =
          objectApi.objectHistory(ctx.pageConfig.getHistoryObjectUri());

      model.historyRange(
          new ObjectHistoryRangeData().objectUri(ctx.pageConfig.getHistoryObjectUri())
              .lowerBound(new ObjectHistoryIteratorData()
                  .versionNr(ctx.pageConfig.getHistoryLowerBound() != null
                      ? ctx.pageConfig.getHistoryLowerBound()
                      : Math.max(historyIterator.getLatestVersionNr() - model.getHistoryPageSize(),
                          0)))
              .upperBound(new ObjectHistoryIteratorData()
                  .versionNr(ctx.pageConfig.getHistoryUpperBound() != null
                      ? ctx.pageConfig.getHistoryUpperBound()
                      : historyIterator.getLatestVersionNr())));
    }

    // Setup the available actions.
    if (ctx.pageConfig.getHistoryObjectUri() == null) {
      ctx.view.addActionsItem(new UiAction().code(ACTION_QUERY).submit(true));
    }
    if (ctx.pageConfig.getHistoryObjectUri() != null && (model.getHistoryRange() != null
        && !Long.valueOf(0).equals(model.getHistoryRange().getLowerBound().getVersionNr()))) {
      // In history mode we have the history control commands.
      ctx.view.addActionsItem(new UiAction().code(ACTION_HISTORY_PREV)
          .descriptor(new UiActionDescriptor().icon("arrow_back").title("Vissza")
              .iconPosition(IconPosition.PRE)));
      ctx.view.addActionsItem(new UiAction().code(ACTION_HISTORY_NEXT)
          .descriptor(new UiActionDescriptor().icon("arrow_forward").title("Előre")
              .iconPosition(IconPosition.POST)));
    }

    ctx.view.addActionsItem(new UiAction().code(ACTION_CLOSE).submit(false));
    // .addActionsItem(new UiAction().code(ACTION_CLEAR)


    FilterExpressionBuilderModel filterModel = ctx.pageConfig.getFilterModel();
    String pageTitle = ctx.pageConfig.getPageTitle();
    FilterExpressionFieldList filters = null;
    if (filterModel != null) {
      pageTitle = filterModel.getLabel();
      filterModel.label(null);
      filters = filterModel.getWorkplaceList();

      if (ctx.pageConfig.getFilterConfig() != null) {

        FilterExpressionBuilderUiModel filterExpressionBuilderUiModel =
            filterExpressionBuilderApi.createFilterBuilder(filterModel,
                ctx.pageConfig.getFilterConfig());
        filterExpressionBuilderApi.initFilterBuilderInView(view.getUuid(), FILTER_BUILDER_WIDGET_ID,
            filterExpressionBuilderUiModel);
        filterExpressionBuilderUiModel.setType(TypeEnum.COMPLEX);

      } else {
        FilterExpressionBuilderUiModel filterExpressionBuilderUiModel =
            filterExpressionBuilderApi.createFilterBuilder(filterModel,
                null);
        filterExpressionBuilderUiModel.setType(TypeEnum.SIMPLE);
        filterExpressionBuilderApi.initFilterBuilderInView(view.getUuid(), FILTER_BUILDER_WIDGET_ID,
            filterExpressionBuilderUiModel);
      }
    }

    if (!Boolean.TRUE.equals(ctx.pageConfig.getSkipInitialQuery())) {
      refreshGrid(model, ctx);
    }

    return model
        .pageTitle(pageTitle)
        .filters(filters);
  }

  /**
   * Override this to add specific list of object for the search page.
   *
   * @param ctx PageContext describing current view.
   *
   * @return
   */
  protected Stream<ObjectNode> getNodesToQuery(PageContext ctx) {
    return null;
  }

  protected void refreshGrid(SearchPageModel model, PageContext ctx) {
    TableData<?> gridContent = null;
    FilterExpressionList filters = getFiltersForRefreshGrid(model, ctx);

    Stream<ObjectNode> nodesToQuery = getNodesToQuery(ctx);
    if (nodesToQuery != null) {
      // We have an injected node stream to use.
      gridContent =
          ctx.searchIndex.executeSearchOnNodes(nodesToQuery, filters, getOrderByList(ctx));
    } else if (ctx.uris != null) {
      // We have an explicit uri list. We use it directly.
      gridContent =
          ctx.searchIndex.executeSearchOn(ctx.uris.stream(), filters, getOrderByList(ctx));
    } else if (ctx.list != null) {
      // We have a stored list the query is working on.
      gridContent =
          ctx.searchIndex.executeSearchOnNodes(ctx.list.nodesFromCache(), filters,
              getOrderByList(ctx));
    } else if (model.getHistoryRange() != null) {
      // We have an object history the query is working on.
      URI objectUri = model.getHistoryRange().getObjectUri();
      // Update the lowerBound if empty
      ObjectHistoryIterator historyIterator = objectApi.objectHistory(objectUri)
          .firstIndex(model.getHistoryRange().getLowerBound().getVersionNr())
          .lastVersion(model.getHistoryRange().getUpperBound().getVersionNr()).reverse(true)
          .useCache(true);
      gridContent =
          ctx.searchIndex.executeSearchOnNodes(Streams.stream(historyIterator), filters,
              getOrderByList(ctx));
    } else {
      // We try the database or read all.
      if (ctx.pageConfig.getFieldsToQuery() != null
          && !ctx.pageConfig.getFieldsToQuery().isEmpty()) {

        gridContent = ctx.searchIndex.executeSearch(filters, getOrderByList(ctx),
            ctx.pageConfig.getFieldsToQuery());
      } else {
        gridContent = ctx.searchIndex.executeSearch(filters, getOrderByList(ctx));
      }
    }
    setDataToGrid(ctx.view.getUuid(), ctx.searchIndex, gridContent, filters);
    if (gridContent.size() == 0) {
      model.noResultText(ctx.pageConfig.getNoResultText());
    } else {
      model.noResultText(null);
    }
  }

  protected FilterExpressionList getFiltersForRefreshGrid(SearchPageModel model, PageContext ctx) {
    FilterExpressionList filters = filterExpressionBuilderApi
        .getFilterExpressionList(ctx.viewUUID, FILTER_BUILDER_WIDGET_ID);
    return filters;
  }

  protected void setDataToGrid(UUID uuid, SearchIndex<?> searchIndex, TableData<?> gridContent,
      FilterExpressionList filters) {
    if (gridContent != null) {
      gridModelApi.setData(uuid, WIDGET_RESULT_GRID, gridContent);
    }
  }

  private final List<FilterExpressionOrderBy> getOrderByList(PageContext ctx) {
    if (ctx.pageConfig.getGridViewOptions() == null
        || ctx.pageConfig.getGridViewOptions().isEmpty()) {
      return Collections.emptyList();
    }
    return ctx.pageConfig.getGridViewOptions().get(0).getOrderByList();
  }

  @Override
  public void performQuery(UUID viewUuid, UiActionRequest request) {
    SearchPageModel model = extractClientModel(request);
    setModel(viewUuid, model);
    PageContext ctx = new PageContext(viewUuid);
    if (ctx.pageConfig.getFilterModel() == null) {
      ctx.pageConfig.filterModel(new FilterExpressionBuilderModel());
    }

    FilterExpressionFieldList filterExpressionFieldList =
        filterExpressionBuilderApi.getFilterExpressionFieldList(viewUuid, FILTER_BUILDER_WIDGET_ID);

    ctx.pageConfig.getFilterModel().workplaceList(filterExpressionFieldList);
    refreshGrid(model, ctx);
  }

  @Override
  public void performClose(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void performReturnSelectedRows(UUID viewUuid, UiActionRequest request) {
    final PageContext ctx = new PageContext(viewUuid);
    List<GridRow> selectedRows = gridModelApi.getSelectedRows(viewUuid, WIDGET_RESULT_GRID);
    InvocationRequest selectionCallback = ctx.selectionCallback;
    InvocationParameter invocationParameter = selectionCallback.getParameters().get(0);
    invocationParameter.setValue(selectedRows);
    try {
      invocationApi.invoke(selectionCallback);
    } catch (ApiNotFoundException e) {
      throw new IllegalStateException(e);
    } finally {
      invocationParameter.setValue(null);
    }
    performClose(viewUuid, request);
  }

  @Override
  public void performClear(UUID viewUuid, UiActionRequest request) {
    FilterExpressionBuilderUiModel widgetModelFromView = viewApi.getWidgetModelFromView(
        FilterExpressionBuilderUiModel.class, viewUuid, FILTER_BUILDER_WIDGET_ID);
    widgetModelFromView.getModel().getWorkplaceList().getFilters().stream()
        .forEach(field -> field.getExpressionData().getOperand2().valueAsString(""));
  }

  @Override
  public void performHistoryNext(UUID viewUuid, UiActionRequest request) {
    SearchPageModel model = extractClientModel(request);
    setModel(viewUuid, model);
    PageContext ctx = new PageContext(viewUuid);
    // TODO The filter is not working now!
    // We calculate the next history range
    Long actualLowerBound = model.getHistoryRange().getLowerBound().getVersionNr();
    Long actualUpperBound = model.getHistoryRange().getUpperBound().getVersionNr();
    Objects.requireNonNull(actualLowerBound, "History range - lower bound is missing.");
    Objects.requireNonNull(actualUpperBound, "History range - upper bound is missing.");
    ObjectHistoryIterator historyIterator =
        objectApi.objectHistory(ctx.pageConfig.getHistoryObjectUri());
    long newUpperBound = Math.min(actualUpperBound + model.getHistoryPageSize(),
        historyIterator.getLatestVersionNr());
    long shift = newUpperBound - actualUpperBound;
    if (shift > 0) {
      long newLowerBound = actualLowerBound + shift;
      model.getHistoryRange().getLowerBound().setVersionNr(newLowerBound);
      model.getHistoryRange().getUpperBound().setVersionNr(newUpperBound);
      refreshGrid(model, ctx);
    }
  }

  @Override
  public void performHistoryPrev(UUID viewUuid, UiActionRequest request) {
    SearchPageModel model = extractClientModel(request);
    setModel(viewUuid, model);
    PageContext ctx = new PageContext(viewUuid);
    // TODO The filter is not working now!
    // We calculate the next history range
    Long actualLowerBound = model.getHistoryRange().getLowerBound().getVersionNr();
    Long actualUpperBound = model.getHistoryRange().getUpperBound().getVersionNr();
    Objects.requireNonNull(actualLowerBound, "History range - lower bound is missing.");
    Objects.requireNonNull(actualUpperBound, "History range - upper bound is missing.");
    long newLowerBound = Math.max(actualLowerBound - model.getHistoryPageSize(),
        0);
    long shift = actualLowerBound - newLowerBound;
    if (shift > 0) {
      long newUpperBound = actualUpperBound - shift;
      model.getHistoryRange().getLowerBound().setVersionNr(newLowerBound);
      model.getHistoryRange().getUpperBound().setVersionNr(newUpperBound);
      refreshGrid(model, ctx);
    }
  }

  @Override
  public void refreshGridData(UUID viewUuid) {
    SearchPageModel model = getModel(viewUuid);
    refreshGrid(model, new PageContext(viewUuid));
  }

  /**
   * Creates a simple {@link SearchPageConfig} with a single GridVew in gridViewOptions, based on
   * the entityDef of specified searchIndex and columns.
   *
   * @param searchIndexSchema
   * @param searchIndexName
   * @param columns
   * @return
   */
  protected SearchPageConfig createSimpleConfig(String searchIndexSchema, String searchIndexName,
      List<String> columns, String... columnPrefix) {
    SearchIndex<?> searchIndex = collectionApi.searchIndex(
        searchIndexSchema,
        searchIndexName);
    GridView gridView = gridModelApi.createGridView(
        searchIndex.getDefinition().getDefinition(),
        columns,
        columnPrefix);

    return new SearchPageConfig()
        .searchIndexSchema(searchIndexSchema)
        .searchIndexName(searchIndexName)
        .filterModel(null)
        .gridViewOptions(Arrays.asList(gridView));
  }

}
