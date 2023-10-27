package org.smartbit4all.api.view.filterexpression;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * @author Dániel Papp
 * 
 *         UiAction codes for FilterExpressionBuilderApi, keep this file synchronized with
 *         filterExpressionBuilderApiActions.ts inside ng-client npm package
 */
public interface FilterExpressionBuilderApi {
  public static final String SCHEMA = "filterExpressionBuilder";


  FilterExpressionFieldList getFilterExpressionFieldList(UUID viewUuid, String gridId);

  FilterExpressionBuilderUiModel createFilterBuilder(FilterExpressionBuilderModel model,
      FilterExpressionBuilderApiConfig config);

  void initFilterBuilderInView(UUID viewUuid, String gridId, FilterExpressionBuilderUiModel model);

  FilterExpressionBuilderUiModel load(UUID viewUuid, String filterIdentifier);

  FilterExpressionBuilderUiModel performWidgetActionRequest(UUID viewUuid, String filterIdentifier,
      UiActionRequest request);

  public final UiAction OPEN_FILTER_GROUPS_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.OPEN_FILTER_GROUPS);
  public final UiAction CLOSE_FILTER_GROUPS_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.CLOSE_FILTER_GROUPS);
  public final UiAction ADD_BRACKET_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.ADD_BRACKET);
  public final UiAction ADD_FILTER_EXPRESSION_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.ADD_FILTER_EXPRESSION);
  public final UiAction REDO_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.REDO).confirm(true);
  public final UiAction FILTER_GROUPS_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.FILTER_GROUPS);


  public final List<UiAction> UI_ACTIONS_CLOSED = Arrays.asList(
      CLOSE_FILTER_GROUPS_ACTION,
      ADD_BRACKET_ACTION,
      REDO_ACTION);

  public final List<UiAction> UI_ACTIONS_OPENED = Arrays.asList(
      OPEN_FILTER_GROUPS_ACTION,
      ADD_BRACKET_ACTION,
      REDO_ACTION);

  public final UiAction SAVE_EXPRESSION_DATA_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.SAVE_EXPRESSION_DATA);

  public final UiAction REMOVE_FILTER_EXPRESSION_ACTION =
      new UiAction().code(FilterExpressionBuilderApiActions.REMOVE_FILTER_EXPRESSION).confirm(true);

  public final List<UiAction> SELECTED_EXPRESSION_ACTIONS =
      Arrays.asList(SAVE_EXPRESSION_DATA_ACTION, REMOVE_FILTER_EXPRESSION_ACTION);
}
