package org.smartbit4all.api.collection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldWidgetType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class FilterExpressionApiImpl implements FilterExpressionApi {

  public FilterExpressionApiImpl() {
    super();
  }

  @Autowired
  private LocaleSettingApi localeApi;

  private static List<FilterExpressionOperation> parenthesisOperands =
      Arrays.asList(FilterExpressionOperation.EXPRESSION, FilterExpressionOperation.EXISTS,
          FilterExpressionOperation.NOT_EXISTS);

  @Override
  public FilterExpressionList of(FilterExpressionFieldList filterExpressionFieldList) {
    List<FilterExpressionData> filterExpressions = filterExpressionFieldList.getFilters().stream()
        .filter(field -> field.getExpressionData() != null
            && (operandHasValue(field.getExpressionData().getOperand1())
                || operandHasValue(field.getExpressionData().getOperand2())
                || operandHasValue(field.getExpressionData().getOperand3())
                || parenthesisOperands.contains(field.getExpressionData().getCurrentOperation())))
        .map(field -> {
          if (parenthesisOperands.contains(field.getExpressionData().getCurrentOperation())) {
            field.getExpressionData().setSubExpression(of(field.getSubFieldList()));
          }
          return field;
        }).map(FilterExpressionField::getExpressionData).map(this::handleLikeExpressions)
        .collect(Collectors.toList());

    return filterExpressions.isEmpty() ? null
        : new FilterExpressionList().expressions(filterExpressions);
  }

  private FilterExpressionData handleLikeExpressions(FilterExpressionData expression) {
    if (expression.getCurrentOperation() == FilterExpressionOperation.LIKE) {
      augmentLikeOperand(expression.getOperand1());
      augmentLikeOperand(expression.getOperand2());
      augmentLikeOperand(expression.getOperand3());
    }
    return expression;
  }

  private void augmentLikeOperand(FilterExpressionOperandData operand) {
    if (operandHasValue(operand)) {
      String value = operand.getValueAsString();
      if (!value.startsWith(StringConstant.PERCENT)) {
        value = StringConstant.PERCENT + value;
      }
      if (!value.endsWith(StringConstant.PERCENT)) {
        value = value + StringConstant.PERCENT;
      }
      operand.setValueAsString(value);
    }
  }

  @Override
  public boolean operandHasValue(FilterExpressionOperandData operandData) {
    return operandData != null
        && !Boolean.TRUE.equals(operandData.getIsDataName())
        && !Strings.isNullOrEmpty(operandData.getValueAsString());
  }

  @Override
  public FilterExpressionBuilderField builderFieldForString(String propertyName) {
    return new FilterExpressionBuilderField().label(localeApi.get(propertyName))
        .fieldTemplate(new FilterExpressionField().label(localeApi.get(propertyName))
            .addPossibleOperationsItem(FilterExpressionOperation.EQUAL)
            .addPossibleOperationsItem(FilterExpressionOperation.NOT_EQUAL)
            .addPossibleOperationsItem(FilterExpressionOperation.LIKE)
            .addPossibleOperationsItem(FilterExpressionOperation.NOT_LIKE)
            .addPossibleOperationsItem(FilterExpressionOperation.IS_EMPTY)
            .addPossibleOperationsItem(FilterExpressionOperation.IS_NOT_EMPTY)
            .filterFieldType(FilterExpressionDataType.STRING)
            .widgetType(FilterExpressionFieldWidgetType.TEXT_FIELD).expressionData(
                new FilterExpressionData().currentOperation(FilterExpressionOperation.EQUAL)
                    .boolOperator(FilterExpressionBoolOperator.AND)
                    .operand1(new FilterExpressionOperandData().isDataName(Boolean.TRUE)
                        .type(FilterExpressionDataType.STRING).valueAsString(propertyName))
                    .operand2(new FilterExpressionOperandData().isDataName(Boolean.FALSE)
                        .type(FilterExpressionDataType.STRING))));
  }

}
