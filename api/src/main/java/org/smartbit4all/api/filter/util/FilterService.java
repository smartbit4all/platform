package org.smartbit4all.api.filter.util;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.value.ValueUris;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.service.transfer.convert.Converter;

/**
 * This service is responsible for compile the filter api objects to Expression.
 * 
 */
public class FilterService {

  private static final Logger log = LoggerFactory.getLogger(FilterService.TimeFilterOptions.class);

  static enum TimeFilterOptions {
    LAST_WEEK, THIS_MONTH, LAST_MONTH, YESTERDAY, TODAY, THIS_YEAR, OTHER, LAST_FIVE_YEARS
  }

  private Map<String, Function<FilterField, Expression>> expressionFactoryByOperatationCodes;

  private EntityManager entityManager;

  private TransferService transferService;

  public FilterService(EntityManager entityManager, TransferService transferService) {
    this.entityManager = entityManager;
    this.transferService = transferService;
    initDefaultOperationFactories();
  }

  private void initDefaultOperationFactories() {
    expressionFactoryByOperatationCodes = new HashMap<>();
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DATE_INTERVAL,
        this::createDateIntervalClause);
    expressionFactoryByOperatationCodes.put(
        DefaultFilterOperationCodes.OPERATION_DATE_TIME_INTERVAL, this::createDateIntervalClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DATE_INTERVAL_CB,
        this::createDateIntervalCbClause);
    expressionFactoryByOperatationCodes.put(
        DefaultFilterOperationCodes.OPERATION_DATE_TIME_INTERVAL_CB,
        this::createDateIntervalCbClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DATE_EQ,
        this::createDateEqClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DATE_TIME_EQ,
        this::createDateTimeEqClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_NUMBER_EQ,
        this::createTxtEqClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_TXT_EQ,
        this::createTxtEqClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_TXT_LIKE,
        this::createTxtLikeClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_MULTI_SEL,
        this::createMultiSelClause);
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_COMBO_SEL,
        this::createComboSelClause);

    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DET_NUMBER_EQ,
        createDetExpression(this::createTxtEqClause));
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DET_TXT_EQ,
        createDetExpression(this::createTxtEqClause));
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DET_TXT_LIKE,
        createDetExpression(this::createTxtLikeClause));
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DET_MULTI_SEL,
        createDetExpression(this::createMultiSelClause));
    expressionFactoryByOperatationCodes.put(DefaultFilterOperationCodes.OPERATION_DET_COMBO_SEL,
        createDetExpression(this::createComboSelClause));
  }

  public void addCustomExpressionFactoryForOperationCode(String operationCode,
      Function<FilterField, Expression> factory) {
    expressionFactoryByOperatationCodes.put(operationCode, factory);
  }

  /**
   * Transforms the filter using the {@link EntityManager} to access the {@link Property}s.
   * 
   * @param filterGroup Assume that the filter must refer to one and only one
   *        {@link EntityDefinition}.
   * @return The expression transformed from the filter. If the filter is null or empty then we get
   *         null.
   */
  public Expression expression(FilterGroup filterGroup) {
    return expression(filterGroup, null);
  }

  public Expression expression(FilterGroup filterGroup,
      Collection<? extends Filters.FilterExpressionHandler> filterExceptionChangeHandlers) {
    if (filterGroup == null || ((filterGroup.getFilterFields() == null
        || filterGroup.getFilterFields().isEmpty())
        && (filterGroup.getFilterGroups() == null || filterGroup.getFilterGroups().isEmpty()))) {
      return null;
    }
    // For the root group we create the proper clause to add the sub groups and the filter fields
    // one by one. The root group is not compiled for bracket. If it has more sub groups then they
    // will be brackets at all.
    ExpressionClause starterClause =
        filterGroup.getType() == FilterGroupType.OR ? Expression.createOrClause()
            : Expression.createAndClause();
    recurseGroups(filterGroup, starterClause, filterExceptionChangeHandlers);

    return starterClause.expressions().isEmpty() ? null : starterClause;
  }

  /**
   * @param filterGroup
   * @param filterExceptionChangeHandlers
   * @param groupStarterClause
   */
  private final void recurseGroups(FilterGroup filterGroup, ExpressionClause groupClause,
      Collection<? extends Filters.FilterExpressionHandler> filterExceptionChangeHandlers) {
    // First add the sub groups as brackets.
    List<FilterGroup> groups = filterGroup.getFilterGroups() == null ? Collections.emptyList()
        : filterGroup.getFilterGroups();
    for (FilterGroup subGroup : groups) {
      ExpressionClause subGroupClause =
          subGroup.getType() == FilterGroupType.OR ? Expression.createOrClause()
              : Expression.createAndClause();
      recurseGroups(subGroup, subGroupClause, filterExceptionChangeHandlers);
      if (!subGroupClause.expressions().isEmpty()) {
        groupClause.add(subGroupClause.BRACKET());
      }
    }

    // Now we add all the filters from the current group as simple expressions
    List<FilterField> filterFields = filterGroup.getFilterFields() == null ? Collections.emptyList()
        : filterGroup.getFilterFields();
    for (FilterField filterField : filterFields) {

      Expression expressionOfField = expressionOfField(filterField, filterExceptionChangeHandlers);

      if (expressionOfField != null) {
        groupClause.add(expressionOfField);
      }
    }
  }

  public Expression expressionOfField(FilterField filterField) {
    return expressionOfField(filterField, null);
  }

  public Expression expressionOfField(FilterField filterField,
      Collection<? extends Filters.FilterExpressionHandler> filterExceptionChangeHandlers) {

    if (filterExceptionChangeHandlers != null && !filterExceptionChangeHandlers.isEmpty()) {
      Filters.FilterExpressionHandler changeHandler = filterExceptionChangeHandlers.stream()
          .filter(h -> h.supports(filterField))
          .findFirst().orElse(null);
      if (changeHandler != null) {
        return changeHandler.createExpression(filterField);
      }
    }

    Expression expressionOfField = null;
    String operationCode = filterField.getOperationCode();

    Function<FilterField, Expression> expressionFactory =
        expressionFactoryByOperatationCodes.get(operationCode);
    if (expressionFactory != null) {
      expressionOfField = expressionFactory.apply(filterField);
    } else {
      log.warn("There is no expression factory registered for operation code {}! "
          + "No expressions will be created for this operation!", operationCode);
    }

    return expressionOfField;
  }

  private Expression createTxtLikeClause(FilterField filterField) {
    Expression expressionOfField = null;
    FilterOperandValue filterOperandValue = filterField.getValue1();
    String value = filterOperandValue == null ? null : filterOperandValue.getValue();
    if (value != null && !value.isEmpty()) {
      value = "%" + value + "%";
      Class<?> type = getValueType(filterOperandValue);
      Property<?> property = getProperty(filterField.getPropertyUri1());
      expressionOfField = getTypedExpression(type, property, value, Property::like);
    }
    return expressionOfField;
  }

  private Expression createTxtLikeMinClause(FilterField filterField) {
    return createCustomExpression(filterField, FilterField::getValue1, FilterField::getPropertyUri1,
        Property::like, o -> {
          String value = o.getValue();
          if (value != null && !value.isEmpty() && value.length() >= 3) {
            value = "%" + value + "%";
            return value;
          }
          return null;
        });
  }

  /**
   * Creates an expression based on the specified parameters of the filterField.</br>
   * This method does not use the serviec's registered factories.
   * 
   * @param <PT> the type of the property
   * @param filterField The filterField that holds all the data to create the expression
   * @param operandGetter A function to give which operand to use
   * @param propUriGetter A function to give which property uri to use
   * @param valueGetter A function to give the value to use based on the operand
   * @param exp The expression to apply
   * @return
   */
  public <PT> Expression createCustomExpression(
      FilterField filterField,
      Function<FilterField, FilterOperandValue> operandGetter,
      Function<FilterField, URI> propUriGetter,
      BiFunction<Property<PT>, PT, Expression> exp,
      Function<FilterOperandValue, String> valueGetter) {
    Expression expressionOfField = null;
    FilterOperandValue filterOperandValue = operandGetter.apply(filterField);
    if (filterOperandValue == null) {
      return null;
    }
    String value = valueGetter.apply(filterOperandValue);
    if (value == null) {
      return null;
    }
    Class<?> type = getValueType(filterOperandValue);
    Property<?> property = getProperty(propUriGetter.apply(filterField));
    expressionOfField = getTypedExpression(type, property, value, exp);
    return expressionOfField;
  }

  private Expression createTxtEqClause(FilterField filterField) {
    Expression expressionOfField = null;
    FilterOperandValue filterOperandValue = filterField.getValue1();
    String value = filterOperandValue == null ? null : filterOperandValue.getValue();
    if (value != null && !value.isEmpty()) {
      Class<?> operandValueType = getValueType(filterOperandValue);
      Property<?> property = getProperty(filterField.getPropertyUri1());
      expressionOfField = getTypedExpression(operandValueType, property, value, Property::eq);
    }
    return expressionOfField;
  }

  public Function<FilterField, Expression> createDetExpression(
      Function<FilterField, Expression> originalFactory) {
    return filterField -> {
      Expression expressionOfField = null;
      Expression expressionOfDetail = originalFactory.apply(filterField);
      if (expressionOfDetail != null) {
        EntityDefinition masterEntityDef = getEntityDef(filterField.getPropertyUri2());
        // TODO Use a master reference path uri and use EntityManager.getJoinPath(URI).
        Property<?> fkProp = getProperty(filterField.getPropertyUri3());
        if (fkProp instanceof PropertyRef<?>) {
          expressionOfField =
              masterEntityDef.exists(((PropertyRef) fkProp).getJoinPath(), expressionOfDetail);
        }
      }
      return expressionOfField;
    };
  }

  private Class<?> getValueType(FilterOperandValue value) {
    String value1TypeName = value.getType();
    try {
      return Class.forName(value1TypeName);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(
          "There is no valid class for name given in the filter field: " + value1TypeName);
    }
  }

  @SuppressWarnings("unchecked")
  private <OT, PT> Expression getTypedExpression(Class<OT> operandType, Property<?> property,
      String operandValue, BiFunction<Property<PT>, PT, Expression> exp) {
    /*
     * Here we have the value as a String and its type class from the operand object. To create the
     * expression we need it in the target property's type. We convert it in 2 steps: sting ->
     * operand's type -> property's type. This way it is possible to use custom type conversion
     * logic by setting a specific type name to the operand object.
     */
    Property<PT> typedProperty = (Property<PT>) property;
    OT operandTypedValue = convertValueToType(operandValue, operandType);
    PT propertyTypedValue = null;
    try {
      propertyTypedValue = convertValueToType(operandTypedValue, operandType, typedProperty.type());
    } catch (Exception e) {
      // FIXME shouldn't it be a checked exception?
      throw new IllegalArgumentException(
          "The operand value can not be converted to the target property's type", e);
    }
    return exp.apply(typedProperty, propertyTypedValue);
  }

  private Expression createMultiSelClause(FilterField filterField) {
    Expression expressionOfField = null;
    Property<?> property = getProperty(filterField.getPropertyUri1());
    List<URI> selectedValues = filterField.getSelectedValues();
    if (selectedValues != null && !selectedValues.isEmpty()) {
      expressionOfField = createInExpression(property, property.type(), selectedValues);
    }
    return expressionOfField;
  }

  @SuppressWarnings("unchecked") // type casts are based on the propertyType parameter and are
                                 // checked by that
  private <T> Expression createInExpression(Property<?> property, Class<T> propertyType,
      List<URI> selectedValues) {
    List<T> values = selectedValues.stream()
        .map(uri -> convertValueUriId(propertyType, uri))
        .collect(Collectors.toList());
    return ((Property<T>) property).in(values);
  }

  private <T> T convertValueUriId(Class<T> propertyType, URI uri) {
    String value = ValueUris.getValueId(uri);
    return convertValueToType(value, propertyType);
  }

  @SuppressWarnings("unchecked")
  private <FT, TT> TT convertValueToType(FT value, Class<FT> fromType, Class<TT> toType) {
    if (fromType.equals(toType)) {
      return (TT) value;
    }
    Converter<FT, TT> valueConverter = transferService.converterByType(fromType, toType);
    if (valueConverter != null) {
      return valueConverter.convertTo(value);
    } else if (String.class == toType) {
      return (TT) value.toString();
    } else {
      throw new RuntimeException(
          "Unable to typecast the selected values to create expression for the configured property!");
    }
  }

  private <T> T convertValueToType(String value, Class<T> type) {
    return convertValueToType(value, String.class, type);
  }

  private Expression createComboSelClause(FilterField filterField) {
    Expression expressionOfField = null;
    Property<?> property = getProperty(filterField.getPropertyUri1());
    List<URI> selectedValues = filterField.getSelectedValues();
    if (selectedValues != null && !selectedValues.isEmpty()) {
      URI selectedValue = selectedValues.get(0);
      expressionOfField = createEqExpression(property, property.type(), selectedValue);
    }
    return expressionOfField;
  }

  @SuppressWarnings("unchecked")
  private <T> Expression createEqExpression(Property<?> property, Class<T> propertyType,
      URI selectedValueUri) {
    T value = convertValueUriId(propertyType, selectedValueUri);
    return ((Property<T>) property).eq(value);
  }

  private Property<?> getProperty(URI propertyUri) {
    Objects.requireNonNull(propertyUri, "The 'propertyUri' parameter can not be null!");
    Property<?> property = entityManager.property(propertyUri);
    if (property == null) {
      throw new IllegalStateException("There is no property registered for uri: " + propertyUri);
    }
    return property;
  }

  private EntityDefinition getEntityDef(URI entityDefUri) {
    Objects.requireNonNull(entityDefUri, "The 'entityDefUri' parameter can not be null!");
    EntityDefinition entityDef = entityManager.definition(entityDefUri);
    if (entityDef == null) {
      throw new IllegalStateException("There is no entityDef registered for uri: " + entityDefUri);
    }
    return entityDef;
  }

  private Expression createDateIntervalCbClause(FilterField filterField) {
    // In this case we have an instruction about the interval. This section will produce the
    // LocalDate variables and later on there will be a conversion if we need LocalDateTime.
    LocalTime now = LocalTime.now();
    LocalDate today = LocalDate.now();
    LocalTime startTime = LocalTime.of(0, 0);
    LocalTime endTime = LocalTime.MAX;
    LocalDate startDate = null;
    LocalDate endDate = null;

    FilterOperandValue operandValue3 = filterField.getValue3();
    TimeFilterOptions selectedTimeOption = TimeFilterOptions.valueOf(operandValue3.getValue());
    switch (selectedTimeOption) {
      case LAST_MONTH:
        startDate = today.with(DAY_OF_MONTH, 1).minus(1, MONTHS);
        endDate = today.with(DAY_OF_MONTH, 1).minusDays(1);
        break;
      case LAST_WEEK:
        startDate = today.with(DayOfWeek.MONDAY).minus(1, WEEKS);
        endDate = today.with(DayOfWeek.MONDAY).minusDays(1);
        break;
      case THIS_MONTH:
        startDate = today.with(DAY_OF_MONTH, 1);
        endDate = today;
        endTime = now;
        break;
      case TODAY:
        startDate = today;
        endDate = today;
        endTime = now;
        break;
      case YESTERDAY:
        startDate = today.minusDays(1);
        endDate = today.minusDays(1);
        break;
      case THIS_YEAR:
        startDate = today.with(DAY_OF_YEAR, 1);
        endDate = today;
        endTime = now;
        break;
      case LAST_FIVE_YEARS:
        startDate = today.minusYears(5);
        endDate = today;
        endTime = now;
        break;
      case OTHER:
      default:
        return createDateIntervalClause(filterField);
    }
    setOperandDateValue(filterField.getValue1(), startDate, startTime);
    setOperandDateValue(filterField.getValue2(), endDate, endTime);
    Expression expressionOfField = createDateIntervalClause(filterField);
    return expressionOfField;
  }

  private void setOperandDateValue(FilterOperandValue operand, LocalDate date, LocalTime time) {
    if (LocalDate.class.getName().equals(operand.getType())) {
      operand.setValue(date.toString());
    } else {
      operand.setValue(LocalDateTime.of(date, time).toString());
    }
  }

  private Expression createDateEqClause(FilterField filterField) {
    Property<?> property =
        filterField.getPropertyUri1() == null ? null
            : entityManager.property(filterField.getPropertyUri1());

    if (property != null) {
      FilterOperandValue valueOperand = filterField.getValue1();
      Class<?> propertyType = property.type();
      if (LocalDate.class.equals(propertyType)) {
        return createEqExpression(property, valueOperand, this::convertToLocalDate);
      }
      if (LocalDateTime.class.equals(propertyType)) {
        // the filter is a date to march, but the queried property is localDateTime.
        // in this case we need to create a dateTime interval expression for the given day
        LocalDate filterDate = convertToLocalDate(valueOperand);
        @SuppressWarnings("unchecked")
        Property<LocalDateTime> dateTimeProperty = (Property<LocalDateTime>) property;
        return dateTimeProperty.between(filterDate.atStartOfDay(),
            filterDate.plusDays(1l).atStartOfDay());
      }
    }
    return null;
  }

  private Expression createDateTimeEqClause(FilterField filterField) {
    Property<?> property =
        filterField.getPropertyUri1() == null ? null
            : entityManager.property(filterField.getPropertyUri1());

    if (property != null) {
      FilterOperandValue valueOperand = filterField.getValue1();
      Class<?> propertyType = property.type();
      if (LocalDate.class.equals(propertyType)) {
        return createEqExpression(property, valueOperand, this::convertToLocalDate);
      }
      if (LocalDateTime.class.equals(propertyType)) {
        LocalDateTime filterDateTime = convertToLocalDateTime(valueOperand);
        @SuppressWarnings("unchecked")
        Property<LocalDateTime> dateTimeProperty = (Property<LocalDateTime>) property;
        LocalDateTime startMin = filterDateTime.truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime endMin = filterDateTime.plusMinutes(1l).truncatedTo(ChronoUnit.MINUTES);
        return dateTimeProperty.between(startMin, endMin);
      }
    }
    return null;
  }

  private <T> Expression createEqExpression(Property<?> property,
      FilterOperandValue valueOperand, Function<FilterOperandValue, T> operandConverter) {
    T value = operandConverter.apply(valueOperand);
    if (value != null) {
      @SuppressWarnings("unchecked")
      Property<T> actualProperty = (Property<T>) property;
      return actualProperty.eq(value);
    }
    return null;
  }

  private Expression createDateIntervalClause(FilterField filterField) {
    Property<?> property1 =
        filterField.getPropertyUri1() == null ? null
            : entityManager.property(filterField.getPropertyUri1());

    if (property1 != null) {
      FilterOperandValue value1Operand = filterField.getValue1();
      FilterOperandValue value2Operand = filterField.getValue2();
      Class<?> propertyType = property1.type();
      if (LocalDate.class.equals(propertyType)) {
        // If we have LocaDate in the filter field as values then OK. Else we have to transfer it
        // to LocaDate.
        return createDateIntervalExpression(property1, value1Operand, value2Operand,
            this::convertToLocalDate);
      }
      if (LocalDateTime.class.equals(propertyType)) {
        return createDateIntervalExpression(property1, value1Operand, value2Operand,
            this::convertToLocalDateTime);
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T> Expression createDateIntervalExpression(Property<?> property,
      FilterOperandValue value1Operand, FilterOperandValue value2Operand,
      Function<FilterOperandValue, T> operandConverter) {
    T value1 = operandConverter.apply(value1Operand);
    T value2 = operandConverter.apply(value2Operand);
    if (property.type().equals(LocalDateTime.class)) {
      if (value2 != null) {
        value2 = (T) ((LocalDateTime) value2).withSecond(59).withNano(999999999);
      }
    }
    Property<T> actualProperty = (Property<T>) property;
    if (value1 == null && value2 != null) {
      // Less than upper bound.
      return actualProperty.lt(value2);
    } else if (value1 != null && value2 == null) {
      // Greater or equal than the lower bound
      return actualProperty.ge(value1);
    } else if (value1 != null && value2 != null) {
      return actualProperty.between(value1, value2);
    }
    return null;
  }

  public LocalDate convertToLocalDate(FilterOperandValue valueOperand) {
    LocalDate value = null;
    if (valueOperand == null) {
      return value;
    }
    if (LocalDate.class.getName().equals(valueOperand.getType())) {
      // TODO More sophisticated converter search.
      Converter<String, LocalDate> converter =
          transferService.converterByType(String.class, LocalDate.class);
      value = converter.convertTo(valueOperand.getValue());
    } else if (LocalDateTime.class.getName().equals(valueOperand.getType())) {
      Converter<String, LocalDateTime> converter =
          transferService.converterByType(String.class, LocalDateTime.class);
      // Truncate the time from the local date time.
      value = converter.convertTo(valueOperand.getValue()).toLocalDate();
    }
    return value;
  }

  public LocalDateTime convertToLocalDateTime(FilterOperandValue valueOperand) {
    if (valueOperand == null || valueOperand.getValue() == null) {
      return null;
    }
    LocalDateTime value = null;
    if (LocalDate.class.getName().equals(valueOperand.getType())) {
      // TODO More sophisticated converter search.
      Converter<String, LocalDate> converter =
          transferService.converterByType(String.class, LocalDate.class);
      value = converter.convertTo(valueOperand.getValue()).atStartOfDay();
    } else if (LocalDateTime.class.getName().equals(valueOperand.getType())) {
      Converter<String, LocalDateTime> converter =
          transferService.converterByType(String.class, LocalDateTime.class);
      // Truncate the time from the local date time.
      value = converter.convertTo(valueOperand.getValue());
    }
    return value;
  }

}
