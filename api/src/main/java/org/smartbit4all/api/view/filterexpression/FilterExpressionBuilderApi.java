package org.smartbit4all.api.view.filterexpression;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.view.bean.UiActionRequest;

public interface FilterExpressionBuilderApi {
  public static final String SCHEMA = "filterExpressionBuilderUiApi";

  FilterExpressionBuilderUiModel createFilterBuilder(FilterExpressionBuilderModel model);

  void initFilterBuilderInView(UUID viewUuid, String gridId, FilterExpressionBuilderUiModel model);

  FilterExpressionBuilderUiModel load(UUID viewUuid, String filterIdentifier);

  void filterGroups(UUID viewUuid, UiActionRequest request);

  void selectField(UUID viewUuid, UiActionRequest request);

  void addBracket(UUID viewUuid, UiActionRequest request);

  void resetFilterWorkspace(UUID viewUuid, UiActionRequest request);

  void removeFilterExpression(UUID viewUuid, UiActionRequest request);

  FilterExpressionBuilderUiModel addFilterExpression(UUID viewUuid, UiActionRequest request);

  void updateExpressionData(UUID viewUuid, UiActionRequest request);
}
