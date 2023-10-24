package org.smartbit4all.api.view.filterexpression;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.view.bean.UiActionRequest;

public class FilterExpressionBuilderApiImpl implements FilterExpressionBuilderApi {

  @Override
  public FilterExpressionBuilderUiModel createFilterBuilder(FilterExpressionBuilderModel model) {
    return new FilterExpressionBuilderUiModel().model(model);
  }

  @Override
  public void initFilterBuilderInView(UUID viewUuid, String gridId,
      FilterExpressionBuilderUiModel model) {
    // TODO Auto-generated method stub

  }

  @Override
  public FilterExpressionBuilderUiModel load(UUID viewUuid, String filterIdentifier) {
    return new FilterExpressionBuilderUiModel();
  }

  @Override
  public void filterGroups(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

  @Override
  public void selectField(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addBracket(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

  @Override
  public void resetFilterWorkspace(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeFilterExpression(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

  @Override
  public FilterExpressionBuilderUiModel addFilterExpression(UUID viewUuid,
      UiActionRequest request) {
    return new FilterExpressionBuilderUiModel();

  }

  @Override
  public void updateExpressionData(UUID viewUuid, UiActionRequest request) {
    // TODO Auto-generated method stub

  }

}
