package org.smartbit4all.bff.api.search;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.SearchIndexResultPageConfig;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * A generic search result page api that accept different parameters and manages the filter
 * expressions and the result grid. We can extend this page in our project to make it more
 * sophisticated but we can pass parameters also to use the default behavior. The main extension
 * points are the filtering logic if it is not just a simple {@link SearchIndexResultPageConfig}.
 * The actions for the whole page and the grid rows.
 * 
 * @author Peter Boros
 */
public interface SearchIndexResultPageApi extends PageApi<SearchIndexResultPageConfig> {

  /**
   * The generic query action that will run the execute search with the current filter expressions.
   */
  static final String ACTION_QUERY = "QUERY";

  /**
   * The close action that will close the list.
   */
  static final String ACTION_CLOSE = "CLOSE";

  /**
   * The action that will close the list and pass the selected rows to the caller view. This
   * function requires the {@link #PARAM_SELECTION_CALLBACK} parameter with a callback
   * {@link InvocationRequest} that has a parameter with a list of object.
   */
  static final String ACTION_RETURN_SELECTED_ROWS = "RETURN_SELECTED_ROWS";

  /**
   * We can pass this parameter to this view and in this case the
   * {@link #ACTION_RETURN_SELECTED_ROWS} action will be enabled. If the action performed then the
   * callback view will be called with the selected rows.
   */
  static final String PARAM_SELECTION_CALLBACK = "SELECTION_CALLBACK";

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

}
