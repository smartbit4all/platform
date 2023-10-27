package org.smartbit4all.api.view.filterexpression;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface FilterExpressionBuilderApi {
  public static final String SCHEMA = "filterExpressionBuilder";

  FilterExpressionFieldList getFilterExpressionFieldList(UUID viewUuid, String gridId);

  FilterExpressionBuilderUiModel createFilterBuilder(FilterExpressionBuilderModel model);

  void initFilterBuilderInView(UUID viewUuid, String gridId, FilterExpressionBuilderUiModel model);

  FilterExpressionBuilderUiModel load(UUID viewUuid, String filterIdentifier);

  FilterExpressionBuilderUiModel performWidgetActionRequest(UUID viewUuid, String filterIdentifier,
      UiActionRequest request);

  String OPEN_FILTER_GROUPS = "OPEN_FILTER_GROUPS";
  String CLOSE_FILTER_GROUPS = "CLOSE_FILTER_GROUPS";
  String ADD_BRACKET = "ADD_BRACKET";
  String ADD_FILTER_EXPRESSION = "ADD_FILTER_EXPRESSION";
  String REMOVE_FILTER_EXPRESSION = "REMOVE_FILTER_EXPRESSION";
  String REDO = "REDO";
  String FILTER_GROUPS = "FILTER_GROUPS";
  String SELECT_FIELD = "SELECT_FIELD";
  String UPDATE_FILTER_EXPRESSION = "UPDATE_FILTER_EXPRESSION";

  public final UiAction OPEN_FILTER_GROUPS_ACTION = new UiAction().code(OPEN_FILTER_GROUPS);
  public final UiAction CLOSE_FILTER_GROUPS_ACTION = new UiAction().code(CLOSE_FILTER_GROUPS);
  public final UiAction ADD_BRACKET_ACTION = new UiAction().code(ADD_BRACKET).submit(true);
  public final UiAction ADD_FILTER_EXPRESSION_ACTION =
      new UiAction().code(ADD_FILTER_EXPRESSION).submit(true);
  public final UiAction REDO_ACTION = new UiAction().code(REDO).confirm(true);
  public final UiAction FILTER_GROUPS_ACTION = new UiAction().code(FILTER_GROUPS);


  public final List<UiAction> UI_ACTIONS_CLOSED = Arrays.asList(
      CLOSE_FILTER_GROUPS_ACTION,
      ADD_BRACKET_ACTION,
      REDO_ACTION);

  public final List<UiAction> UI_ACTIONS_OPENED = Arrays.asList(
      OPEN_FILTER_GROUPS_ACTION,
      ADD_BRACKET_ACTION,
      REDO_ACTION);
  public static final String SAVE_EXPRESSION_DATA = "SAVE_EXPRESSION_DATA";
  public final UiAction SAVE_EXPRESSION_DATA_ACTION =
      new UiAction().code(SAVE_EXPRESSION_DATA).submit(true);

  public final UiAction REMOVE_FILTER_EXPRESSION_ACTION =
      new UiAction().code(REMOVE_FILTER_EXPRESSION).confirm(true);

  public final List<UiAction> SELECTED_EXPRESSION_ACTIONS =
      Arrays.asList(SAVE_EXPRESSION_DATA_ACTION, REMOVE_FILTER_EXPRESSION_ACTION);
}
