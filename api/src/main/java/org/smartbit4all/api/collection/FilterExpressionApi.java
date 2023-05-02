package org.smartbit4all.api.collection;

import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;

public interface FilterExpressionApi {

  FilterExpressionList of(FilterExpressionFieldList filterExpressionFieldList);

  boolean operandHasValue(FilterExpressionOperandData operandData);

  FilterExpressionBuilderField builderFieldForString(String propertyName);

  Expression constructExpression(FilterExpressionList filterExpressions,
      EntityDefinition entityDef);

  Expression constructExpression(FilterExpressionList filterExpressions,
      SearchEntityDefinition entityDef);

}
