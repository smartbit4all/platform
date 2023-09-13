package org.smartbit4all.bff.api.search;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchPageApiImpl extends PageApiImpl<SearchPageModel>
    implements SearchPageApi {

  /**
   * SearchPageConfig variable which is used by this page.
   */
  public static final String VAR_SEARCHPAGECONFIG = "VAR_SEARCHPAGECONFIG";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private GridModelApi gridModelApi;

  @Autowired
  private FilterExpressionApi filterExpressionApi;

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
      searchIndex = collectionApi.searchIndex(pageConfig.getSearchIndexSchema(),
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

    protected UUID viewUUID;

    protected View view;

    protected SearchIndex<?> searchIndex;

    protected SearchPageConfig pageConfig;

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

    // Setup the available actions.
    ctx.view.addActionsItem(new UiAction().code(ACTION_QUERY).submit(true))
        .addActionsItem(new UiAction().code(ACTION_CLOSE).submit(false));
    if (ctx.selectionCallback != null) {
      ctx.view.addActionsItem(new UiAction().code(ACTION_RETURN_SELECTED_ROWS).submit(true));
    }

    GridModel gridModel =
        gridModelApi.createGridModel(ctx.searchIndex.getDefinition().getDefinition(),
            ctx.searchIndex.getDefinition().getDefinition().allProperties().stream()
                .map(Property::getName).collect(toList()),
            ctx.searchIndex.logicalSchema(), ctx.searchIndex.name());
    gridModelApi.initGridInView(ctx.viewUUID, WIDGET_RESULT_GRID, gridModel);
    if (ctx.gridPageRenderCallback != null) {
      gridModelApi.addGridPageCallback(
          ctx.viewUUID,
          WIDGET_RESULT_GRID,
          ctx.gridPageRenderCallback);
    }

    refreshGrid(ctx);

    FilterExpressionBuilderModel filterModel = ctx.pageConfig.getFilterModel();
    String pageTitle = "";
    FilterExpressionFieldList filters = null;
    if (filterModel != null) {
      pageTitle = filterModel.getLabel();
      filters = filterModel.getWorkplaceList();
    }
    // if (filters == null) {
    // filters = ctx.searchIndex.allFilterFields();
    // }
    return new SearchPageModel()
        .pageTitle(pageTitle)
        .filters(filters);
  }

  private void refreshGrid(PageContext ctx) {
    TableData<?> gridContent = null;
    FilterExpressionList filters = getFilterExpressionList(ctx);

    if (ctx.uris != null) {
      // We have an explicit uri list. We use it directly.
      gridContent =
          ctx.searchIndex.executeSearchOn(ctx.uris.stream(), filters, getOrderByList(ctx));
    } else if (ctx.list != null) {
      // We have a stored list the query is working on.
      gridContent =
          ctx.searchIndex.executeSearchOn(ctx.list.uris().stream(), filters, getOrderByList(ctx));
    } else {
      // We try the database or read all.
      gridContent = ctx.searchIndex.executeSearch(filters, getOrderByList(ctx));
    }
    if (gridContent != null) {
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_RESULT_GRID, gridContent);
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
    refreshGrid(new PageContext(viewUuid));
  }

  @Override
  public void performClose(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void performReturnSelectedRows(UUID viewUuid, UiActionRequest request) {

  }

}