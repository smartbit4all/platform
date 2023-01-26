package org.smartbit4all.api.collection;

import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import com.google.common.base.Strings;

public abstract class FilterExpressions {

  public static FilterExpressionList of(FilterExpressionFieldList filterExpressionFieldList) {
    List<FilterExpressionData> filterExpressions = filterExpressionFieldList.getFilters().stream()
        .map(FilterExpressionField::getExpressionData)
        .filter(data -> operandHasValue(data.getOperand1())
            || operandHasValue(data.getOperand2())
            || operandHasValue(data.getOperand3()))
        .collect(Collectors.toList());

    return new FilterExpressionList().expressions(filterExpressions);
  }

  private static boolean operandHasValue(FilterExpressionOperandData operandData) {
    return operandData != null && Boolean.FALSE.equals(operandData.getIsDataName())
        && !Strings.isNullOrEmpty(operandData.getValueAsString());
  }

  private FilterExpressions() {}

}
