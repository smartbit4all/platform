package org.smartbit4all.api.filterexpression.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.restserver.FilterApiDelegate;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class FilterApiDelegateImpl implements FilterApiDelegate {

  @Autowired
  private FilterExpressionBuilderApi filterExpressionBuilderApi;


  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> load(UUID uuid, String filterIdentifier)
      throws Exception {
    return ResponseEntity.ok(filterExpressionBuilderApi.load(uuid, filterIdentifier));
  }


  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> performWidgetAction(UUID uuid,
      String filterIdentifier, UiActionRequest body) throws Exception {
    return ResponseEntity
        .ok(filterExpressionBuilderApi.performWidgetActionRequest(uuid, filterIdentifier, body));
  }
}
