package org.smartbit4all.bff.api.search;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;

/**
 * A generic search result page api that accept different parameters and manages the filter
 * expressions and the result grid. We can extend this page in our project to make it more
 * sophisticated but we can pass parameters also to use the default behavior. The main extension
 * points are the filtering logic if it is not just a simple {@link SearchPageConfig}. The actions
 * for the whole page and the grid rows.
 *
 * @author Peter Boros
 */
public interface SearchPageApi extends PageApi<SearchPageModel> {

  /**
   * The generic query action that will run the execute search with the current filter expressions.
   */
  static final String ACTION_QUERY = "QUERY";

  static final String ACTION_CLEAR = "ACTION_CLEAR";

  /**
   * The close action that will close the list.
   */
  static final String ACTION_CLOSE = "CLOSE_SEARCH_PAGE";

  /**
   * The generic action that will page to the previous history page.
   */
  static final String ACTION_HISTORY_PREV = "HISTORY_PREV_PAGE";

  /**
   * The generic action that will page to the next history page.
   */
  static final String ACTION_HISTORY_NEXT = "HISTORY_NEXT_PAGE";

  /**
   * The generic action that will double the page size of the history range.
   */
  static final String ACTION_HISTORY_EXPAND = "HISTORY_EXPAND_RANGE";

  /**
   * The action that will close the list and pass the selected rows to the caller view.
   * 
   * <p>
   * This function requires the {@link #PARAM_SELECTION_CALLBACK} parameter with a callback
   * {@link InvocationRequest} The <strong>first parameter</strong> of the supplied invocation
   * request must be a {@code List} of {@link GridRow}s, subsequent parameters are left untouched.
   * 
   * <p>
   * An example selection callback may be defined as such:
   * 
   * <pre>
   * <code>
   * view.putParametersItem(
   *    SearchPageApi.PARAM_SELECTION_CALLBACK,
   *    invocationApi
          .builder(MyInterface.class)
          .build(a -> a.onRowSelected(Invocations.listOf(Collections.emptyList(), GridRow.class))));
   * </code>
   * </pre>
   * 
   * <p>
   * Failing to provide an appropriate callback will result in an unchecked exception being thrown.
   */
  static final String ACTION_RETURN_SELECTED_ROWS = "RETURN_SELECTED_ROWS";

  /**
   * We can pass this parameter to this view and in this case the
   * {@link #ACTION_RETURN_SELECTED_ROWS} action will be enabled. If the action performed then the
   * callback view will be called with the selected rows.
   * 
   * <p>
   * On the exact signature of the callback, please refer to {@link #ACTION_RETURN_SELECTED_ROWS}.
   */
  static final String PARAM_SELECTION_CALLBACK = "SELECTION_CALLBACK";

  /**
   * The grid page rendering callback parameter.
   *
   * <p>
   * An {@link InvocationRequest} is expected to be passed under this parameter key. The request
   * must accept a {@link GridPage} as its first parameter, and must return a non-null
   * {@link GridPage}. The primary use of this invocation request is to set {@link UiAction}s for
   * each of row of the View's grid.
   */
  static final String PARAM_GRID_PAGE_RENDER_CALLBACK = "GRID_PAGE_RENDER_CALLBACK";

  /**
   * The FilterBuilderModel rendering callback parameter.
   *
   * <p>
   * An {@link FilterExpressionBuilderModel} is expected to be passed under this parameter key. If
   * the FilterExpressionBuilderModel is provided the page renders the filter editor widget
   */
  static final String PARAM_FILTER_MODEL = "PARAM_FILTER_MODEL";

  static final String FILTER_BUILDER_WIDGET_ID = "FILTER_BUILDER_WIDGET";

  /**
   * The identifier of the grid that contains the result of the search index query.
   */
  static final String WIDGET_RESULT_GRID = "RESULT_GRID";

  /**
   * The uri list that is the source of the search. In this case this is not a database search but
   * an object based search.
   */
  static final String PARAM_URI_LIST = "URI_LIST";

  /**
   * The uri list that is the source of the search. In this case this is not a database search but
   * an object based search. This must be a
   */
  static final String PARAM_STORED_LIST = "STORED_LIST";

  /**
   * Executes the query action and refresh the result grid.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_QUERY)
  void performQuery(UUID viewUuid, UiActionRequest request);

  /**
   * It closes the view and return to the parent.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_CLOSE)
  void performClose(UUID viewUuid, UiActionRequest request);

  /**
   * Cancel the editing of the given object. Normally it closes the view and return to the parent.
   *
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_RETURN_SELECTED_ROWS)
  void performReturnSelectedRows(UUID viewUuid, UiActionRequest request);

  @ActionHandler(ACTION_CLEAR)
  void performClear(UUID viewUuid, UiActionRequest request);

}
