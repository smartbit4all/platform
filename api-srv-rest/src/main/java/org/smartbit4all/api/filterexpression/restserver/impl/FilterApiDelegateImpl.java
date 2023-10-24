package org.smartbit4all.api.filterexpression.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.restserver.FilterApiDelegate;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class FilterApiDelegateImpl implements FilterApiDelegate {

  @Autowired
  private FilterExpressionBuilderApi filterExpressionBuilderApi;

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> addBracket(UUID uuid,
      String filterIdentifier, FilterExpressionBuilderUiModel filterExpressionBuilderUiModel)
      throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.addBracket(uuid, filterIdentifier,
        filterExpressionBuilderUiModel);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> addFilterExpression(UUID uuid,
      String filterIdentifier, FilterExpressionBuilderField filterExpressionBuilderField)
      throws Exception {
    return ResponseEntity.ok(filterExpressionBuilderApi.addFilterExpression(uuid, null));
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> filterGroups(UUID uuid,
      String filterIdentifier, FilterExpressionFieldList filterExpressionFieldList)
      throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.filterGroups(uuid, filterIdentifier, filterExpressionFieldList);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> load(UUID uuid, String filterIdentifier)
      throws Exception {
    return ResponseEntity.ok(filterExpressionBuilderApi.load(uuid, filterIdentifier));
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> removeFilterExpression(UUID uuid,
      String filterIdentifier, FilterExpressionField filterExpressionField) throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.removeFilterExpression(uuid, filterIdentifier,
        filterExpressionField);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> resetFilterWorkspace(UUID uuid,
      String filterIdentifier, FilterExpressionBuilderUiModel filterExpressionBuilderUiModel)
      throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.resetFilterWorkspace(uuid, filterIdentifier,
        filterExpressionBuilderUiModel);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> selectField(UUID uuid,
      String filterIdentifier, FilterExpressionField filterExpressionField) throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.selectField(uuid, filterIdentifier, filterExpressionField);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> update(UUID uuid, String filterIdentifier,
      FilterExpressionBuilderUiModel filterExpressionBuilderUiModel) throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.update(uuid, filterIdentifier, filterExpressionBuilderUiModel);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> updateFilterExpression(UUID uuid,
      String filterIdentifier, FilterExpressionField filterExpressionField) throws Exception {
    // TODO Auto-generated method stub
    return FilterApiDelegate.super.updateFilterExpression(uuid, filterIdentifier,
        filterExpressionField);
  }

}
