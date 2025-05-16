package org.smartbit4all.api.filterexpression.restserver.impl;

import java.util.UUID;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel.TypeEnum;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
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
    FilterExpressionBuilderUiModel model = filterExpressionBuilderApi.load(uuid, filterIdentifier);
    if (model == null) {
      model = new FilterExpressionBuilderUiModel()
          .type(TypeEnum.SIMPLE)
          .model(new FilterExpressionBuilderModel()
              .workplaceList(new FilterExpressionFieldList()));
    }
    return ResponseEntity.ok(model);
  }

  @Override
  public ResponseEntity<FilterExpressionBuilderUiModel> performWidgetAction(UUID uuid,
      String filterIdentifier, UiActionRequest body) throws Exception {
    return ResponseEntity
        .ok(filterExpressionBuilderApi.performWidgetActionRequest(uuid, filterIdentifier, body));
  }
}
