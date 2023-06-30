package org.smartbit4all.bff.api.search;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.SearchIndexResultPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class SearchIndexResultPageApiImpl extends PageApiImpl<SearchIndexResultPageConfig>
    implements SearchIndexResultPageApi {

  @Autowired
  private CollectionApi collectionsApi;

  @Autowired
  private ViewApi viewApi;

  @Autowired
  private GridModelApi gridModelApi;

  @Autowired
  private FilterExpressionApi filterExpressionApi;

  protected class PageContext {

    public PageContext(UUID viewUUID) {
      super();
      this.viewUUID = viewUUID;
      view = viewApi.getView(viewUUID);
      pageConfig = getModel(viewUUID);
      searchIndex = collectionsApi.searchIndex(pageConfig.getSearchIndexSchema(),
          pageConfig.getSearchIndexName());
      uris = extractListParam(URI.class, PARAM_URI_LIST, view.getParameters());
      StoredCollectionDescriptor listDescriptor =
          extractParam(StoredCollectionDescriptor.class, PARAM_STORED_LIST, view.getParameters());
      if (listDescriptor != null) {
        list = collectionsApi.list(listDescriptor.getSchema(), listDescriptor.getName());
      }
      selectionCallback =
          extractParam(InvocationRequest.class, PARAM_SELECTION_CALLBACK, view.getParameters());
    }

    protected UUID viewUUID;

    protected View view;

    protected SearchIndex<?> searchIndex;

    protected SearchIndexResultPageConfig pageConfig;

    protected List<URI> uris;

    protected StoredList list;

    protected InvocationRequest selectionCallback;

  }

  public SearchIndexResultPageApiImpl() {
    super(SearchIndexResultPageConfig.class);
  }

  @Override
  public SearchIndexResultPageConfig initModel(View view) {
    return objectApi.loadLatest(view.getObjectUri()).getObject(SearchIndexResultPageConfig.class);
  }

  @Override
  public SearchIndexResultPageConfig load(UUID viewUuid) {
    PageContext ctx = new PageContext(viewUuid);

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

    refreshGrid(ctx);

    return ctx.pageConfig;
  }

  private void refreshGrid(PageContext ctx) {
    TableData<?> gridContent = null;
    FilterExpressionList filters = getFilterExpressionList(ctx);
    if (ctx.uris != null) {
      // We have an explicit uri list. We use it directly.
      gridContent = ctx.searchIndex.executeSearchOn(ctx.uris.stream(), filters);
    } else if (ctx.list != null) {
      // We have a stored list the query is working on.
      gridContent = ctx.searchIndex.executeSearchOn(ctx.list.uris().stream(), filters);
    } else {
      // We try the database or read all.
      gridContent = ctx.searchIndex.executeSearch(filters);
    }
    if (gridContent != null) {
      gridModelApi.setData(ctx.view.getUuid(), WIDGET_RESULT_GRID, gridContent);
    }
  }

  private final FilterExpressionList getFilterExpressionList(PageContext ctx) {
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