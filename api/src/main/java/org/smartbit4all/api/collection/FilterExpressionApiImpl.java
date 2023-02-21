package org.smartbit4all.api.collection;

import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class FilterExpressionApiImpl implements FilterExpressionApi {

  public FilterExpressionApiImpl() {
    super();
  }

  @Autowired
  private LocaleSettingApi localeApi;

  @Override
  public FilterExpressionList of(FilterExpressionFieldList filterExpressionFieldList) {
    List<FilterExpressionData> filterExpressions = filterExpressionFieldList.getFilters().stream()
        .map(FilterExpressionField::getExpressionData)
        .filter(data -> operandHasValue(data.getOperand1())
            || operandHasValue(data.getOperand2())
            || operandHasValue(data.getOperand3()))
        .collect(Collectors.toList());

    return filterExpressions.isEmpty()
        ? null
        : new FilterExpressionList().expressions(filterExpressions);
  }

  @Override
  public boolean operandHasValue(FilterExpressionOperandData operandData) {
    return operandData != null && Boolean.FALSE.equals(operandData.getIsDataName())
        && !Strings.isNullOrEmpty(operandData.getValueAsString());
  }

  @Override
  public FilterExpressionBuilderField builderFieldForString(String propertyName) {
    return new FilterExpressionBuilderField()
        .label(localeApi.get(propertyName)).fieldTemplate(
            new FilterExpressionField().label(localeApi.get(propertyName))
                .addPossibleOperationsItem(FilterExpressionOperation.EQUAL)
                .addPossibleOperationsItem(FilterExpressionOperation.NOT_EQUAL)
                .addPossibleOperationsItem(FilterExpressionOperation.LIKE)
                .addPossibleOperationsItem(FilterExpressionOperation.NOT_LIKE));
  }

}
