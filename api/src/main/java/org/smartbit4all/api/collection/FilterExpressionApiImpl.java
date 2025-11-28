package org.smartbit4all.api.collection;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.meta.PropertyObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;
import jakarta.annotation.Nullable;

public class FilterExpressionApiImpl implements FilterExpressionApi {

  private static final Logger log = LoggerFactory.getLogger(FilterExpressionApiImpl.class);

  private static List<FilterExpressionOperation> unaryOperations =
      Arrays.asList(FilterExpressionOperation.IS_EMPTY, FilterExpressionOperation.IS_NOT_EMPTY);

  private static List<FilterExpressionOperation> parenthesisOperations =
      Arrays.asList(FilterExpressionOperation.EXPRESSION, FilterExpressionOperation.EXISTS,
          FilterExpressionOperation.NOT_EXISTS);

  private static final String LOCALE_PREFIX = "filter-description";

  @Autowired
  private LocaleSettingApi localeApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

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
                || parenthesisOperations.contains(field.getExpressionData().getCurrentOperation())
                || unaryOperations.contains(field.getExpressionData().getCurrentOperation())))
        .map(field -> {
          if (parenthesisOperations.contains(field.getExpressionData().getCurrentOperation())) {
            field.getExpressionData().setSubExpression(of(field.getSubFieldList()));
          }
          if (unaryOperations.contains(field.getExpressionData().getCurrentOperation())) {
            field.getExpressionData().operand2(null);
          }
          return field;
        }).map(FilterExpressionField::getExpressionData).map(this::handleLikeExpressions)
        .collect(Collectors.toList());

    return filterExpressions.isEmpty()
        ? null
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
  public List<String> describe(FilterExpressionFieldList filterExpressionFieldList) {
    return describe(filterExpressionFieldList, null);
  }

  @Override
  public List<String> describe(FilterExpressionFieldList filterExpressionFieldList, String locale) {
    final Locale loc;
    if (Strings.isNullOrEmpty(locale)) {
      if (sessionApi == null) {
        throw new IllegalStateException("Session API is not available in the application context!");
      }

      loc = sessionApi.getLocale();
    } else {
      loc = Locale.forLanguageTag(locale);
    }


    if (filterExpressionFieldList == null
        || filterExpressionFieldList.getFilters() == null
        || filterExpressionFieldList.getFilters().isEmpty()) {
      return Collections.singletonList(localeApi.get(loc, LOCALE_PREFIX, "no-filters"));
    }

    final List<String> result = new ArrayList<>();
    for (final var filter : filterExpressionFieldList.getFilters()) {
      final String label = filter.getLabel();
      final FilterExpressionOperandData userData = getOperandWithValues(filter);
      if (userData == null) {
        continue;
      }

      final FilterExpressionData expr = filter.getExpressionData();
      final var description = switch (expr.getCurrentOperation()) {
        case EQUAL -> describeUserData(loc, "equal", label, userData.getValueAsString());
        case NOT_EQUAL -> describeUserData(loc, "not-equal", label, userData.getValueAsString());
        case BETWEEN -> describeUserDataBetween(
            loc, "between", label,
            expr.getOperand2().getValueAsString(),
            expr.getOperand3().getValueAsString());
        case NOT_BETWEEN -> describeUserDataBetween(
            loc, "not-between", label,
            expr.getOperand2().getValueAsString(),
            expr.getOperand3().getValueAsString());
        case GREATER -> describeUserData(loc, "greater", label, userData.getSelectedValues());
        case GREATER_OR_EQUAL -> describeUserData(
            loc, "greatereq", label,
            userData.getValueAsString());
        case LESS -> describeUserData(loc, "less", label, userData.getValueAsString());
        case LESS_OR_EQUAL -> describeUserData(loc, "lesseq", label, userData.getValueAsString());
        case IN -> describeUserData(loc, "in", label, userData.getSelectedValues());
        case NOT_IN -> describeUserData(loc, "not-in", label, userData.getSelectedValues());
        case LIKE -> describeUserData(loc, "like", label, userData.getValueAsString());
        case NOT_LIKE -> describeUserData(loc, "not-like", label, userData.getValueAsString());
        case IS_EMPTY -> describeUserData(loc, "empty", label, userData.getValueAsString());
        case IS_NOT_EMPTY -> describeUserData(loc, "not-empty", label, userData.getValueAsString());
        // these are special cases, even nested in case of expression. Going to revisit this later:
        case EXISTS, NOT_EXISTS, EXPRESSION -> Optional.<String>empty();
      };
      description.ifPresent(result::add);
    }
    return result.isEmpty()
        ? Collections.singletonList(localeApi.get(loc, LOCALE_PREFIX, "no-filters"))
        : result;
  }

  private Optional<String> describeUserData(Locale locale, String key, String label, String value) {
    return describeUserData(locale, key, label, Collections.singletonList(value));
  }

  private Optional<String> describeUserData(Locale locale, String key, String label,
      List<String> values) {
    if (values == null || values.isEmpty()) {
      return Optional.empty();
    }

    String valueToSet = values.stream()
        .filter(it -> !Strings.isNullOrEmpty(it))
        .collect(Collectors.joining(StringConstant.COMMA_SPACE));
    if (Strings.isNullOrEmpty(valueToSet)) {
      return Optional.empty();
    }

    return Optional.of(MessageFormat.format(
        localeApi.get(locale, LOCALE_PREFIX, key),
        label,
        valueToSet));
  }

  private Optional<String> describeUserDataBetween(
      Locale locale,
      String key,
      String label,
      String from,
      String to) {
    if (Strings.isNullOrEmpty(from) && Strings.isNullOrEmpty(to)) {
      return Optional.empty();
    }
    from = Strings.isNullOrEmpty(from) ? "..." : tryParseDateStr(locale, from);
    to = Strings.isNullOrEmpty(to) ? "..." : tryParseDateStr(locale, to);

    String valueToSet = from + StringConstant.SPACE_HYPHEN_SPACE + to;
    return Optional.of(MessageFormat.format(
        localeApi.get(locale, LOCALE_PREFIX, key),
        label,
        valueToSet));
  }

  private String tryParseDateStr(Locale locale, String dateStr) {
    try {
      final LocalDate odt = OffsetDateTime.parse(dateStr)
          .atZoneSameInstant(ZoneId.systemDefault())
          .toLocalDate();
      return DateTimeFormatter
          .ofLocalizedDate(FormatStyle.LONG)
          .withLocale(locale)
          .format(odt);
    } catch (Exception e) {
      try {
        final LocalDateTime ldt = LocalDateTime.parse(dateStr);
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale).format(ldt);
      } catch (Exception e2) {
        try {
          final LocalDate ld = LocalDate.parse(dateStr);
          return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale).format(ld);
        } catch (Exception e3) {
          return dateStr;
        }
      }
    }
  }

  @Override
  public boolean isPristine(FilterExpressionFieldList filterExpressionFieldList) {
    if (filterExpressionFieldList == null || filterExpressionFieldList.getFilters() == null
        || filterExpressionFieldList.getFilters().isEmpty()) {
      return true;
    }

    final var filters = filterExpressionFieldList.getFilters();
    return 0L == filters.stream()
        .map(this::getOperandWithValues)
        .filter(Objects::nonNull)
        .count();
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
        && (!ObjectUtils.isEmpty(operandData.getSelectedValues())
            || !ObjectUtils.isEmpty(operandData.getSelectedObjects()));
  }

  private boolean operandHasValueOrValues(FilterExpressionOperandData operandData) {
    return operandHasValue(operandData) || operandHasValues(operandData);
  }

  private @Nullable FilterExpressionOperandData getOperandWithValues(
      final FilterExpressionField field) {
    if (field == null) {
      return null;
    }

    final var expr = field.getExpressionData();
    if (expr == null) {
      return null;
    }

    final List<Function<FilterExpressionData, FilterExpressionOperandData>> accessors = List.of(
        FilterExpressionData::getOperand1,
        FilterExpressionData::getOperand2,
        FilterExpressionData::getOperand3);
    return accessors.stream()
        .map(it -> it.apply(expr))
        .filter(this::operandHasValueOrValues)
        .findFirst()
        .orElse(null);
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
    if (filterExpressions != null) {
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
                  .apply(value, searchEntityDef.definition, searchIndexMappingObject).BRACKET();
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
              convertFilterExpression(fed, searchEntityDef, entityDef, searchIndexMappingObject,
                  searchExpressionByPropertyName);
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
    } else if (fed.getCurrentOperation() == FilterExpressionOperation.BETWEEN) {
      values.add(valueOf(fed.getOperand2(), type));
      values.add(valueOf(fed.getOperand3(), type));
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
      if (!ObjectUtils.isEmpty(op.getSelectedValues())) {
        return convertValues(op.getSelectedValues(), type);
      }
      // need some selectionDefinition here...
      if (!ObjectUtils.isEmpty(op.getSelectedObjects())) {
        Object first = op.getSelectedObjects().get(0);
        if (first instanceof GenericValue) {
          List<String> uris = op.getSelectedObjects().stream()
              .map(value -> ((GenericValue) value).getUri())
              .filter(Objects::nonNull)
              .map(URI::toString)
              .collect(toList());
          return convertValues(uris, type);
        } else if (first instanceof Value) {
          List<String> uris = op.getSelectedObjects().stream()
              .map(value -> ((Value) value).getObjectUri())
              .filter(Objects::nonNull)
              .map(URI::toString)
              .collect(toList());
          return convertValues(uris, type);
        } else if (first instanceof Map) {
          List<String> uris = op.getSelectedObjects().stream()
              .map(value -> {
                Map<?, ?> map = (Map<?, ?>) value;
                if (map.containsKey(GenericValue.URI)) {
                  return objectApi.asType(URI.class, map.get(GenericValue.URI));
                }
                if (map.containsKey(Value.OBJECT_URI)) {
                  return objectApi.asType(URI.class, map.get(Value.OBJECT_URI));
                }
                return null;
              })
              .filter(Objects::nonNull)
              .map(URI::toString)
              .collect(toList());
          return convertValues(uris, type);
        }
      }
      return convertValues(op.getSelectedValues(), type);
    }
    return Collections.emptyList();
  }

  private Expression convertFilterExpression(FilterExpressionData fed,
      SearchEntityDefinition searchEntityDef, EntityDefinition entityDef,
      SearchIndexMappingObject searchIndexMappingObject,
      Map<String, CustomExpressionMapping> searchExpressionByPropertyName) {

    // expression
    if (fed.getCurrentOperation().equals(FilterExpressionOperation.EXPRESSION)) {
      // TODO detail entitydef?

      Expression innerExpression =
          constructExpressionInner(fed.getSubExpression(), searchEntityDef, entityDef,
              searchIndexMappingObject, searchExpressionByPropertyName);


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
