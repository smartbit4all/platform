package org.smartbit4all.api.collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
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
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class FilterExpressionApiImpl implements FilterExpressionApi {

  public FilterExpressionApiImpl() {
    super();
  }

  @Autowired
  private LocaleSettingApi localeApi;

  @Autowired
  private ObjectApi objectApi;

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

  @Override
  public Expression constructExpression(FilterExpressionList filterExpressions,
      SearchEntityDefinition entityDef) {
    return constructExpressionInner(filterExpressions, entityDef, null);
  }

  @Override
  public Expression constructExpression(FilterExpressionList filterExpressions,
      EntityDefinition entityDef) {
    return constructExpressionInner(filterExpressions, null, entityDef);
  }

  private Expression constructExpressionInner(FilterExpressionList filterExpressions,
      SearchEntityDefinition searchEntityDef, EntityDefinition entityDef) {
    Expression currentExpression = null;
    FilterExpressionData prevFed = null;
    for (FilterExpressionData fed : filterExpressions.getExpressions()) {
      // Construct the Expression from the FilterExpressionData
      Expression exp = convertFilterExpression(fed, searchEntityDef, entityDef);
      if (exp != null) {
        if (currentExpression != null && prevFed != null) {
          if (prevFed.getBoolOperator() == FilterExpressionBoolOperator.OR) {
            currentExpression = currentExpression.OR(exp);
          } else {
            currentExpression = currentExpression.AND(exp);
          }
        } else {
          currentExpression = exp;
        }
        prevFed = fed;
      }
    }
    return currentExpression;
  }

  private final PropertyObject propertyOf(FilterExpressionOperandData op,
      SearchEntityDefinition searchEntityDef, EntityDefinition entityDef) {
    if (op != null && Boolean.TRUE.equals(op.getIsDataName())) {
      if (searchEntityDef != null) {
        return searchEntityDef.definition.getPropertyObject(op.getValueAsString());
      }
      return entityDef.getPropertyObject(op.getValueAsString());
    }
    return null;
  }

  private final Object valueOf(FilterExpressionOperandData op, PropertyObject property)
      throws IOException {
    if (op != null && Boolean.FALSE.equals(op.getIsDataName())) {
      return convertValue(op.getValueAsString(), property);
    }
    return null;
  }

  private Expression convertFilterExpression(FilterExpressionData fed,
      SearchEntityDefinition searchEntityDef, EntityDefinition entityDef) {
    String propertyName;
    PropertyObject property = null;
    List<PropertyObject> properties = new ArrayList<>();

    if (fed.getCurrentOperation().equals(FilterExpressionOperation.EXPRESSION)) {
      // TODO detail entitydef
      Expression innerExpression =
          constructExpressionInner(fed.getSubExpression(), searchEntityDef, entityDef);
      return innerExpression != null ? new ExpressionBracket(innerExpression) : null;
    }

    if (Arrays.asList(FilterExpressionOperation.EXISTS, FilterExpressionOperation.NOT_EXISTS)
        .contains(fed.getCurrentOperation())) {
      propertyName = fed.getOperand1().getValueAsString();
    } else {
      properties.add(propertyOf(fed.getOperand1(), searchEntityDef, entityDef));
      properties.add(propertyOf(fed.getOperand2(), searchEntityDef, entityDef));
      properties.add(propertyOf(fed.getOperand3(), searchEntityDef, entityDef));
      // The first property would be great for type conversion.
      property = properties.stream().filter(p -> p != null).findFirst().get();
      propertyName = property.getName();
    }
    // Type conversion by the type of the filter expression operand
    List<Object> values = new ArrayList<>();
    try {
      values.add(valueOf(fed.getOperand1(), property));
      values.add(valueOf(fed.getOperand2(), property));
      values.add(valueOf(fed.getOperand3(), property));
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Unable to convert the string value of the filter expression to "
              + property.getBasic().type(),
          e);
    }

    switch (fed.getCurrentOperation()) {
      case BETWEEN:
        Object lowerBound = values.get(1);
        Object upperBound = values.get(2);
        if (lowerBound != null && upperBound != null) {
          return properties.get(0).between(lowerBound, upperBound);
        }
        if (lowerBound != null) {
          return properties.get(0).ge(lowerBound);
        }
        if (upperBound != null) {
          return properties.get(0).le(upperBound);
        }
        return null;
      case EQUAL:
        return properties.get(0).eq(values.get(1));
      case EXISTS:
        // The expression is simple parenthesis for the same entity definition.
        return constructExists(fed, propertyName, searchEntityDef);
      case EXPRESSION:
        // The expression is simple parenthesis for the same entity definition.
        Expression innerExpression =
            constructExpressionInner(fed.getSubExpression(), searchEntityDef, entityDef);
        return innerExpression != null ? new ExpressionBracket(innerExpression) : null;
      case GREATER:
        return properties.get(0).gt(values.get(1));
      case GREATER_OR_EQUAL:
        return properties.get(0).ge(values.get(1));
      case IS_EMPTY:
        return properties.get(0).isNull();
      case IS_NOT_EMPTY:
        return properties.get(0).isNotNull();
      case LESS:
        return properties.get(0).lt(values.get(1));
      case LESS_OR_EQUAL:
        return properties.get(0).le(values.get(1));
      case LIKE:
        return properties.get(0).like(values.get(1));
      case NOT_BETWEEN:
        return properties.get(0).between(values.get(1), values.get(2)).NOT();
      case NOT_EQUAL:
        return properties.get(0).noteq(values.get(1));
      case NOT_EXISTS:
        return constructExists(fed, propertyName, searchEntityDef).NOT();
      case NOT_LIKE:
        return properties.get(0).notlike(values.get(1));
      default:
        break;

    }
    return null;
  }

  private final Expression constructExists(FilterExpressionData fed, String propertyName,
      SearchEntityDefinition searchEntityDef) {
    // SearchIndexMappingObject detailMapping =
    // ((SearchIndexMappingObject) mappingsByPropertyName.get(propertyName));
    DetailDefinition detailDefinition = searchEntityDef.detailsByName.get(propertyName);
    Expression existsExpression = constructExpressionInner(fed.getSubExpression(),
        detailDefinition.detail, null);
    // Add the exists to the current entity and return the exists expression as is.
    return searchEntityDef.definition.exists(detailDefinition.masterJoin, existsExpression)
        .name(propertyName);
  }

  private final Object convertValue(String valueAsString, Property<?> property)
      throws IOException {
    Class<?> type;
    if (property instanceof PropertyObject) {
      type = ((PropertyObject) property).getBasic().type();
    } else {
      type = property.type();
    }
    if (String.class.equals(type)) {
      return valueAsString;
    }
    if (valueAsString == null) {
      return null;
    }
    return objectApi.asType(type, valueAsString);
  }

}
