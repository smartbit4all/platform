package org.smartbit4all.bff.api.search;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
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
import org.smartbit4all.api.view.bean.UiAction;
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

  /**
   * SearchPageConfig variable which is used by this page.
   */
  public static final String VAR_SEARCHPAGECONFIG = "VAR_SEARCHPAGECONFIG";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  private FilterExpressionApi filterExpressionApi;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private FilterExpressionBuilderApi filterExpressionBuilderApi;

  @Value("${searchpage.historyPageSize:50}")
  private int defaultHistoryPageSize = 50;

  protected class PageContext {

    public PageContext(UUID viewUUID) {
      super();
      this.viewUUID = viewUUID;
      view = viewApi.getView(viewUUID);
      ObjectMapHelper variables = variables(view);
      pageConfig = variables.get(VAR_SEARCHPAGECONFIG, SearchPageConfig.class);
      if (pageConfig == null) {
        pageConfig = objectApi
            .loadLatest(view.getObjectUri())
            .getObject(SearchPageConfig.class);
        variables.getMap().put(VAR_SEARCHPAGECONFIG, pageConfig);
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
      filterExpressionBuilderModel =
          parameters.get(PARAM_FILTER_MODEL, FilterExpressionBuilderModel.class);
    }

    protected UUID viewUUID;

    protected View view;

    protected SearchIndex<?> searchIndex;

    protected SearchPageConfig pageConfig;

    protected List<URI> uris;

    protected StoredList list;

    protected InvocationRequest selectionCallback;

    protected InvocationRequest gridPageRenderCallback;

    protected FilterExpressionBuilderModel filterExpressionBuilderModel;
  }

  public SearchPageApiImpl() {
    super(SearchPageModel.class);
  }

  @Override
  public SearchPageModel initModel(View view) {
    PageContext ctx = new PageContext(view.getUuid());

    // Setup the available actions.
    if (ctx.pageConfig.getHistoryObjectUri() == null) {
      ctx.view.addActionsItem(new UiAction().code(ACTION_QUERY).submit(true));
    } else {
      // In history mode we have the history control commands.
    }

    ctx.view.addActionsItem(new UiAction().code(ACTION_CLOSE).submit(false))
    // .addActionsItem(new UiAction().code(ACTION_CLEAR)
    ;

    GridModel gridModel = gridModelApi.createGridModel(
        ctx.searchIndex.getDefinition().getDefinition(),
        ctx.searchIndex.getDefinition().getDefinition().allProperties().stream()
            .map(Property::getName)
            .collect(toList()),
        ctx.searchIndex.logicalSchema(), ctx.searchIndex.name());

    final List<GridView> gridViewOptions = ctx.pageConfig.getGridViewOptions();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      gridModel.setView(gridViewOptions.get(0));
      gridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
    }

    if (ctx.selectionCallback != null) {
      ctx.view.addActionsItem(new UiAction().code(ACTION_RETURN_SELECTED_ROWS).submit(true));
      GridViewDescriptor gridViewDescriptor = gridModel.getView().getDescriptor();
      gridViewDescriptor.setSelectionMode(GridSelectionMode.SINGLE);
      gridViewDescriptor.setSelectionType(GridSelectionType.CHECKBOX);
      gridViewDescriptor.setPreserveSelectionOnPageChange(false);
    }

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

      model.historyPageSize(
          ctx.pageConfig.getHistoryPageSize() != null ? ctx.pageConfig.getHistoryPageSize()
              : defaultHistoryPageSize);
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

    refreshGrid(model, ctx);

    FilterExpressionBuilderModel filterModel = ctx.pageConfig.getFilterModel();
    String pageTitle = "";
    FilterExpressionFieldList filters = null;
    if (filterModel != null) {
      pageTitle = filterModel.getLabel();
      filterModel.label(null);
      filters = filterModel.getWorkplaceList();
      // TODO pass FilterExpressionBuilderApiConfig
      FilterExpressionBuilderUiModel filterExpressionBuilderUiModel =
          filterExpressionBuilderApi.createFilterBuilder(filterModel, null);

      if (Objects.nonNull(filterExpressionBuilderUiModel.getModel().getGroups())
          && !filterExpressionBuilderUiModel.getModel().getGroups().isEmpty()) {
        filterExpressionBuilderUiModel.setType(TypeEnum.COMPLEX);
        filterExpressionBuilderUiModel.showGroups(true);
        filterExpressionBuilderUiModel.readOnly(false);
      } else {
        filterExpressionBuilderUiModel.setType(TypeEnum.SIMPLE);
      }

      filterExpressionBuilderApi.initFilterBuilderInView(view.getUuid(), FILTER_BUILDER_WIDGET_ID,
          filterExpressionBuilderUiModel);
    }


    return model
        .pageTitle(pageTitle)
        .filters(filters);
  }

  /**
   * Override this to add specific list of object for the search page.
   *
   * @return
   */
  protected Stream<ObjectNode> getNodesToQuery() {
    return null;
  }

  protected void refreshGrid(SearchPageModel model, PageContext ctx) {
    TableData<?> gridContent = null;
    FilterExpressionList filters = getFilterExpressionList(ctx);

    Stream<ObjectNode> nodesToQuery = getNodesToQuery();
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
      gridContent = ctx.searchIndex.executeSearch(filters, getOrderByList(ctx));
    }
    setDataToGrid(ctx.view.getUuid(), ctx.searchIndex, gridContent);
  }

  protected void setDataToGrid(UUID uuid, SearchIndex<?> searchIndex, TableData<?> gridContent) {
    if (gridContent != null) {
      gridModelApi.setData(uuid, WIDGET_RESULT_GRID, gridContent);
    }
  }

  private final FilterExpressionList getFilterExpressionList(PageContext ctx) {
    if (ctx.pageConfig.getFilterModel() == null) {
      return null;
    }
    FilterExpressionList filterList =
        filterExpressionApi.of(ctx.pageConfig.getFilterModel().getWorkplaceList());
    if (filterList != null) {
      if (ctx.pageConfig.getFilterModel().getDefaultFilters() != null) {
        // Append the default filters to the
        filterList.getExpressions()
            .addAll(ctx.pageConfig.getFilterModel().getDefaultFilters().getExpressions());
      }
    } else {
      filterList = ctx.pageConfig.getFilterModel().getDefaultFilters();
    }
    return filterList;
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

}
