package org.smartbit4all.api.collection;

import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;

public interface FilterExpressionApi {

  FilterExpressionList of(FilterExpressionFieldList filterExpressionFieldList);

  boolean operandHasValue(FilterExpressionOperandData operandData);

  FilterExpressionBuilderField builderFieldForString(String propertyName);

}
