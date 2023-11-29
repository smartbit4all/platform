package org.smartbit4all.api.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.meta.PropertyObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class FilterExpressionApiImpl implements FilterExpressionApi {

  private static final Logger log = LoggerFactory.getLogger(FilterExpressionApiImpl.class);

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
    if (filterExpressionFieldList == null) {
      return null;
    }
    FilterExpressionFieldList fieldList =
        objectApi.definition(FilterExpressionFieldList.class).deepCopy(filterExpressionFieldList);
    List<FilterExpressionData> filterExpressions = fieldList.getFilters().stream()
        .filter(field -> field.getExpressionData() != null
            && (operandHasValueOrValues(field.getExpressionData().getOperand1())
                || operandHasValueOrValues(field.getExpressionData().getOperand2())
                || operandHasValueOrValues(field.getExpressionData().getOperand3())
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
    if (expression.getCurrentOperation() == FilterExpressionOperation.LIKE
        || expression.getCurrentOperation() == FilterExpressionOperation.NOT_LIKE) {
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

  private boolean operandHasValues(FilterExpressionOperandData operandData) {
    return operandData != null
        && !Boolean.TRUE.equals(operandData.getIsDataName())
        && operandData.getSelectedValues() != null
        && !operandData.getSelectedValues().isEmpty();
  }

  private boolean operandHasValueOrValues(FilterExpressionOperandData operandData) {
    return operandHasValue(operandData) || operandHasValues(operandData);
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
      SearchEntityDefinition entityDef,
      SearchIndexMappingObject searchIndexMappingObject,
      Map<String, CustomExpressionMapping> searchExpressionByPropertyName) {
    return constructExpressionInner(filterExpressions,
        entityDef,
        null,
        searchIndexMappingObject, searchExpressionByPropertyName);
  }

  @Override
  public Expression constructExpression(FilterExpressionList filterExpressions,
      EntityDefinition entityDef) {
    return constructExpressionInner(filterExpressions,
        null,
        entityDef,
        null,
        null);
  }

  private Expression constructExpressionInner(FilterExpressionList filterExpressions,
      SearchEntityDefinition searchEntityDef, EntityDefinition entityDef,
      SearchIndexMappingObject searchIndexMappingObject,
      Map<String, CustomExpressionMapping> searchExpressionByPropertyName) {
    Expression currentExpression = null;
    FilterExpressionData prevFed = null;
    for (FilterExpressionData fed : filterExpressions.getExpressions()) {
      Expression exp = null;
      if (searchExpressionByPropertyName != null && searchExpressionByPropertyName != null) {
        String propertyName = getPropertyName(fed);

        if (propertyName != null && searchExpressionByPropertyName.containsKey(propertyName)) {
          Property<?> property = searchEntityDef.getDefinition().getProperty(propertyName);
          Object value = getValue(fed, property != null ? property.type() : null);
          CustomExpressionMapping customExpressionMapping =
              searchExpressionByPropertyName.get(propertyName);
          if (customExpressionMapping.complexExpressionProcessor != null) {
            exp = customExpressionMapping.complexExpressionProcessor
                .apply(value, searchEntityDef.definition).BRACKET();
          } else if (customExpressionMapping.expressionProcessor != null && property != null) {
            exp = customExpressionMapping.expressionProcessor.apply(value, property);
          } else if (customExpressionMapping.detailExpressionProcessor != null) {
            exp =
                customExpressionMapping.detailExpressionProcessor.apply(value,
                    searchIndexMappingObject);
          }
        }

      }
      if (exp == null) {
        // Construct the Expression from the FilterExpressionData
        exp =
            convertFilterExpression(fed, searchEntityDef, entityDef);
      }
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


  private Object getValue(FilterExpressionData fed, Class<?> type) {
    List<Object> values = new ArrayList<>();
    if (fed.getCurrentOperation() == FilterExpressionOperation.NOT_IN
        || fed.getCurrentOperation() == FilterExpressionOperation.IN) {
      values.addAll(valuesOf(fed.getOperand1(), type));
      values.addAll(valuesOf(fed.getOperand2(), type));
      values.addAll(valuesOf(fed.getOperand3(), type));
      return values;
    } else {
      values.add(valueOf(fed.getOperand1(), type));
      values.add(valueOf(fed.getOperand2(), type));
      values.add(valueOf(fed.getOperand3(), type));
      return values.stream().filter(p -> p != null).findFirst().orElse(null);

    }
  }



  private String getPropertyName(FilterExpressionData fed) {
    List<String> properties = new ArrayList<>();
    properties.add(propertyNameOf(fed.getOperand1()));
    properties.add(propertyNameOf(fed.getOperand2()));
    properties.add(propertyNameOf(fed.getOperand3()));
    // The first property would be great for type conversion.
    return properties.stream().filter(p -> p != null).findFirst().orElse(null);
  }

  private String propertyNameOf(FilterExpressionOperandData op) {
    if (op != null && Boolean.TRUE.equals(op.getIsDataName())) {
      return op.getValueAsString();
    }
    return null;
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

  private final Object valueOf(FilterExpressionOperandData op, Class<?> type) {
    if (op != null && Boolean.FALSE.equals(op.getIsDataName())) {
      return convertValue(op.getValueAsString(), type);
    }
    return null;
  }

  private final List<Object> valuesOf(FilterExpressionOperandData op, Class<?> type) {
    if (op != null && Boolean.FALSE.equals(op.getIsDataName())) {
      return convertValues(op.getSelectedValues(), type);
    }
    return Collections.emptyList();
  }

  private Expression convertFilterExpression(FilterExpressionData fed,
      SearchEntityDefinition searchEntityDef, EntityDefinition entityDef) {

    // expression
    if (fed.getCurrentOperation().equals(FilterExpressionOperation.EXPRESSION)) {
      // TODO detail entitydef?
      Expression innerExpression =
          constructExpressionInner(fed.getSubExpression(), searchEntityDef, entityDef,
              null, null);
      return innerExpression != null ? new ExpressionBracket(innerExpression) : null;
    }

    // exists, not_exists
    if (Arrays.asList(FilterExpressionOperation.EXISTS, FilterExpressionOperation.NOT_EXISTS)
        .contains(fed.getCurrentOperation())) {
      // The expression is simple parenthesis for the same entity definition.
      String propertyName = fed.getOperand1().getValueAsString();
      Expression existsExpression =
          constructExists(fed, propertyName, searchEntityDef);
      if (fed.getCurrentOperation() == FilterExpressionOperation.NOT_EXISTS) {
        existsExpression = existsExpression.NOT();
      }
      return existsExpression;
    }

    // this.property based expressions
    PropertyObject property = null;
    List<PropertyObject> properties = new ArrayList<>();
    properties.add(propertyOf(fed.getOperand1(), searchEntityDef, entityDef));
    properties.add(propertyOf(fed.getOperand2(), searchEntityDef, entityDef));
    properties.add(propertyOf(fed.getOperand3(), searchEntityDef, entityDef));
    // The first property would be great for type conversion.
    property = properties.stream().filter(p -> p != null).findFirst().orElse(null);
    if (property == null) {
      // no property found, no expression
      log.warn("No property found, no expression constructed from field: {}", fed);
      return null;
    }

    // handle modifiers, now as simple functions
    if (!Strings.isNullOrEmpty(fed.getModifier())) {
      property = property.function(PropertyFunction.withSelfPropertyArgument(fed.getModifier()));
    }
    // Type conversion by the type of the filter expression operand
    List<Object> values = new ArrayList<>();
    Class<?> type = property.getBasic().type();
    // list value expression
    if (fed.getCurrentOperation() == FilterExpressionOperation.IN ||
        fed.getCurrentOperation() == FilterExpressionOperation.NOT_IN) {
      values.addAll(valuesOf(fed.getOperand1(), type));
      values.addAll(valuesOf(fed.getOperand2(), type));
      values.addAll(valuesOf(fed.getOperand3(), type));
      Expression expression;
      if (values.isEmpty()) {
        expression = Expression.TRUE();
      } else {
        expression = property.in(values);
      }
      if (fed.getCurrentOperation() == FilterExpressionOperation.NOT_IN) {
        expression = expression.NOT();
      }
      return expression;
    }
    // single value expression
    values.add(valueOf(fed.getOperand1(), type));
    values.add(valueOf(fed.getOperand2(), type));
    values.add(valueOf(fed.getOperand3(), type));

    switch (fed.getCurrentOperation()) {
      case BETWEEN:
      case NOT_BETWEEN:
        Object lowerBound = values.get(1);
        Object upperBound = values.get(2);
        Expression betweenExpression = null;
        if (lowerBound != null && upperBound != null) {
          betweenExpression = property.between(lowerBound, upperBound);
        } else if (lowerBound != null) {
          betweenExpression = property.ge(lowerBound);
        } else if (upperBound != null) {
          betweenExpression = property.le(upperBound);
        }
        if (fed.getCurrentOperation() == FilterExpressionOperation.NOT_BETWEEN
            && betweenExpression != null) {
          betweenExpression = betweenExpression.NOT();
        }
        return betweenExpression;
      case EQUAL:
        return property.eq(values.get(1));
      case GREATER:
        return property.gt(values.get(1));
      case GREATER_OR_EQUAL:
        return property.ge(values.get(1));
      case IS_EMPTY:
        return property.isNull();
      case IS_NOT_EMPTY:
        return property.isNotNull();
      case LESS:
        return property.lt(values.get(1));
      case LESS_OR_EQUAL:
        return property.le(values.get(1));
      case LIKE:
        return property.like(values.get(1));
      case NOT_EQUAL:
        return property.noteq(values.get(1));
      case NOT_LIKE:
        return property.notlike(values.get(1));
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
        detailDefinition.detail, null, null, null);
    // Add the exists to the current entity and return the exists expression as is.
    return searchEntityDef.definition.exists(detailDefinition.masterJoin, existsExpression)
        .name(propertyName);
  }

  private final Object convertValue(String valueAsString, Class<?> type) {
    if (type == null) {
      return valueAsString;
    }
    if (String.class.equals(type)) {
      return valueAsString;
    }
    if (valueAsString == null) {
      return null;
    }
    return objectApi.asType(type, valueAsString);
  }

  private final List<Object> convertValues(List<String> values, Class<?> type) {
    if (type == null) {
      return (List<Object>) (Object) values;
    }
    if (String.class.equals(type)) {
      return (List<Object>) (Object) values;
    }
    if (values == null) {
      return Collections.emptyList();
    }
    return (List<Object>) objectApi.asList(type, values);
  }

}
