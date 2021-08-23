package org.smartbit4all.api.filter.util;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.COMBO_SEL;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DATE_EQ;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DATE_INTERVAL;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DATE_INTERVAL_CB;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DATE_TIME_EQ;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DATE_TIME_INTERVAL;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DATE_TIME_INTERVAL_CB;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DET_COMBO_SEL;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DET_MULTI_SEL;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DET_NUMBER_EQ;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DET_TXT_EQ;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DET_TXT_LIKE;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.DET_TXT_LIKE_MIN;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.MULTI_SEL;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.NUMBER_EQ;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.TXT_EQ;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.TXT_LIKE;
import static org.smartbit4all.api.filter.util.Filters.OperationCode.TXT_LIKE_MIN;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This utility is responsible for compile the filter api objects to Expression.
 * 
 * TODO How to load?
 * 
 * @author Peter Boros
 */
@Service
public class Filters {

  //@// @formatter:off
  public static enum OperationCode {
    DATE_INTERVAL("date.interval"),
    DATE_INTERVAL_CB("date.interval.cb"),
    DATE_EQ("date.eq"),
    DATE_TIME_INTERVAL("date.time.interval"),
    DATE_TIME_INTERVAL_CB("date.time.interval.cb"),
    DATE_TIME_EQ("date.time.eq"),
    NUMBER_EQ("number.eq"),
    TXT_EQ("txt.eq"), TXT_LIKE("txt.like"),
    TXT_LIKE_MIN("txt.like.min"),
    MULTI_SEL("multi.eq"), COMBO_SEL("combo.eq"),
    DET_NUMBER_EQ("detail.number.eq"),
    DET_TXT_EQ("detail.txt.eq"), DET_TXT_LIKE("detail.txt.like"),
    DET_TXT_LIKE_MIN("detail.txt.like.min"),
    DET_MULTI_SEL("detail.multi.eq"), DET_COMBO_SEL("detail.combo.eq");

 // @formatter:on
    
    private String value;

    private OperationCode(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    static OperationCode getEnumInstance(String operationCode) {
      if (operationCode == null) {
        return null;
      }
      return Arrays.stream(OperationCode.values())
          .filter(c -> c.getValue().equals(operationCode)).findFirst().orElse(null);
    }
  }

  private static enum TimeFilterOptions {
    LAST_WEEK, THIS_MONTH, LAST_MONTH, YESTERDAY, TODAY, OTHER, LAST_FIVE_YEARS
  }

  private static final Map<OperationCode, Function<FilterField, Expression>> expressionFactoryByOperatationCodes =
      new HashMap<>();

  {
    expressionFactoryByOperatationCodes.put(DATE_INTERVAL, this::createDateIntervalClause);
    expressionFactoryByOperatationCodes.put(DATE_TIME_INTERVAL, this::createDateIntervalClause);
    expressionFactoryByOperatationCodes.put(DATE_INTERVAL_CB, this::createDateIntervalCbClause);
    expressionFactoryByOperatationCodes.put(DATE_TIME_INTERVAL_CB,
        this::createDateIntervalCbClause);
    expressionFactoryByOperatationCodes.put(DATE_EQ, this::createDateEqClause);
    expressionFactoryByOperatationCodes.put(DATE_TIME_EQ, this::createDateTimeEqClause);
    expressionFactoryByOperatationCodes.put(NUMBER_EQ, this::createTxtEqClause);
    expressionFactoryByOperatationCodes.put(TXT_EQ, this::createTxtEqClause);
    expressionFactoryByOperatationCodes.put(TXT_LIKE, this::createTxtLikeClause);
    expressionFactoryByOperatationCodes.put(TXT_LIKE_MIN, this::createTxtLikeMinClause);
    expressionFactoryByOperatationCodes.put(MULTI_SEL, this::createMultiSelClause);
    expressionFactoryByOperatationCodes.put(COMBO_SEL, this::createComboSelClause);

    expressionFactoryByOperatationCodes.put(DET_NUMBER_EQ,
        createDetExpression(this::createTxtEqClause));
    expressionFactoryByOperatationCodes.put(DET_TXT_EQ,
        createDetExpression(this::createTxtEqClause));
    expressionFactoryByOperatationCodes.put(DET_TXT_LIKE,
        createDetExpression(this::createTxtLikeClause));
    expressionFactoryByOperatationCodes.put(DET_TXT_LIKE_MIN,
        createDetExpression(this::createTxtLikeMinClause));
    expressionFactoryByOperatationCodes.put(DET_MULTI_SEL,
        createDetExpression(this::createMultiSelClause));
    expressionFactoryByOperatationCodes.put(DET_COMBO_SEL,
        createDetExpression(this::createComboSelClause));
  }

  // -----------------------------------------------------

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TransferService transferService;

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
      Collection<? extends FilterExpressionHandler> filterExceptionChangeHandlers) {
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
      Collection<? extends FilterExpressionHandler> filterExceptionChangeHandlers) {
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
      Collection<? extends FilterExpressionHandler> filterExceptionChangeHandlers) {

    if (filterExceptionChangeHandlers != null && !filterExceptionChangeHandlers.isEmpty()) {
      FilterExpressionHandler changeHandler = filterExceptionChangeHandlers.stream()
          .filter(h -> h.supports(filterField))
          .findFirst().orElse(null);
      if (changeHandler != null) {
        return changeHandler.createExpression(filterField);
      }
    }

    Expression expressionOfField = null;
    String operationCode = filterField.getOperationCode();
    OperationCode opertaionCodeEnum = OperationCode.getEnumInstance(operationCode);

    Function<FilterField, Expression> expressionFactory =
        expressionFactoryByOperatationCodes.get(opertaionCodeEnum);
    if (expressionFactory != null) {
      expressionOfField = expressionFactory.apply(filterField);
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
    Expression expressionOfField = null;
    FilterOperandValue filterOperandValue = filterField.getValue1();
    String value = filterOperandValue == null ? null : filterOperandValue.getValue();
    if (value != null && !value.isEmpty() && value.length() >= 3) {
      value = "%" + value + "%";
      Class<?> type = getValueType(filterOperandValue);
      Property<?> property = getProperty(filterField.getPropertyUri1());
      expressionOfField = getTypedExpression(type, property, value, Property::like);
    }
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
  
  private Function<FilterField, Expression> createDetExpression(
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
     * expression we need it in the target property's type.
     * We convert it in 2 steps: sting -> operand's type -> property's type.
     * This way it is possible to use custom type conversion logic by setting a specific type name 
     * to the operand object. 
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
    if(fromType.equals(toType)) {
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
    LocalTime endTime = LocalTime.of(23, 59, 59);
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
        return dateTimeProperty.between(filterDate.atStartOfDay(), filterDate.plusDays(1l).atStartOfDay());
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
  
  private <T> Expression createDateIntervalExpression(Property<?> property,
      FilterOperandValue value1Operand, FilterOperandValue value2Operand,
      Function<FilterOperandValue, T> operandConverter) {
    T value1 = operandConverter.apply(value1Operand);
    T value2 = operandConverter.apply(value2Operand);
    @SuppressWarnings("unchecked")
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

  public static LocalDate[] getPreviousWeekFirstDay(LocalDate date) {
    final int dayOfWeek = date.getDayOfWeek().getValue();
    final LocalDate from = date.minusDays((long) dayOfWeek + 6); // (dayOfWeek - 1) + 7
    final LocalDate to = date.minusDays(dayOfWeek);

    return new LocalDate[] {from, to};
  }

  public static LocalDate[] getPreviousMonth(LocalDate date) {
    final LocalDate from = date.minusDays((long) date.getDayOfMonth() - 1).minusMonths(1);
    final LocalDate to = from.plusMonths(1).minusDays(1);

    return new LocalDate[] {from, to};
  }

  /**
   * Returns if the group has any filterField among its own filterFields or in any of its
   * filterGroups, which has some value.
   * 
   * @param group
   * @return
   */
  public static boolean hasAnyFilterField(FilterGroup group) {
    if (group == null) {
      return false;
    }
    List<FilterGroup> filterGroups = group.getFilterGroups();
    if (filterGroups != null) {
      for (FilterGroup filterGroup : filterGroups) {
        if (hasAnyFilterField(filterGroup)) {
          return true;
        }
      }
    }
    List<FilterField> filterFields = group.getFilterFields();
    if (filterFields != null) {
      for (FilterField filterField : filterFields) {
        if (hasAnyValue(filterField)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean hasAnyValue(FilterField filter) {
    if (filter == null) {
      return false;
    }
    if (isValuePresent(filter.getValue1())) {
      return true;
    }
    if (isValuePresent(filter.getValue2())) {
      return true;
    }
    if (isValuePresent(filter.getValue3())) {
      return true;
    }
    if (filter.getSelectedValues() != null && !filter.getSelectedValues().isEmpty()) {
      return true;
    }
    return false;
  }

  public static boolean isValuePresent(FilterOperandValue value) {
    return value != null && value.getValue() != null;
  }

  public static boolean isIntervalValid(FilterField timeFilter) {
    if (!hasAnyValue(timeFilter)) {
      return false;
    }
    FilterOperandValue valueFrom = timeFilter.getValue1();
    FilterOperandValue valueTo = timeFilter.getValue2();
    if (!isValuePresent(valueFrom) && !isValuePresent(valueTo)) {
      // from and to empty, option must not be other!
      TimeFilterOptions timeFilterOption =
          TimeFilterOptions.valueOf(timeFilter.getValue3().getValue());
      if (timeFilterOption == TimeFilterOptions.OTHER) {
        return false;
      }
    }
    return true;
  }

  public static FilterGroup changeFilterProperties(FilterGroup filterGroup,
      ArrayList<? extends FilterPropertyChangeHandler> propertyChangeHandlers) {
    if (propertyChangeHandlers == null || propertyChangeHandlers.isEmpty()) {
      throw new IllegalArgumentException("propertyChangeHandlers can not be null nor empty!");
    }
    FilterGroup changedGroup = new FilterGroup();
    changedGroup.setIsNegated(filterGroup.getIsNegated());
    changedGroup.setName(filterGroup.getName());
    changedGroup.setType(filterGroup.getType());

    List<FilterGroup> subGroups = filterGroup.getFilterGroups();
    if (subGroups != null) {
      subGroups.forEach(subGroup -> {
        changedGroup.addFilterGroupsItem(changeFilterProperties(subGroup, propertyChangeHandlers));
      });
    }

    List<FilterField> filterFields = filterGroup.getFilterFields();
    if (filterFields != null && !filterFields.isEmpty()) {
      for (FilterField field : filterFields) {
        FilterField changedField = new FilterField();
        changedField.setOperationCode(field.getOperationCode());
        changedField.setSelectedValues(field.getSelectedValues());
        changedField.setValue1(field.getValue1());
        changedField.setValue2(field.getValue2());
        changedField.setValue3(field.getValue3());

        changedField
            .setPropertyUri1(changePropertyUri(field.getPropertyUri1(), propertyChangeHandlers));
        changedField
            .setPropertyUri2(changePropertyUri(field.getPropertyUri2(), propertyChangeHandlers));
        changedField
            .setPropertyUri3(changePropertyUri(field.getPropertyUri3(), propertyChangeHandlers));

        changedGroup.addFilterFieldsItem(changedField);
      }
    }

    return changedGroup;
  }

  private static URI changePropertyUri(URI propertyUriToChange,
      ArrayList<? extends FilterPropertyChangeHandler> propertyChangeHandlers) {
    FilterPropertyChangeHandler handler = propertyChangeHandlers.stream()
        .filter(h -> h.supports(propertyUriToChange)).findFirst().orElse(null);
    if (handler == null) {
      return propertyUriToChange;
    }
    return handler.changeUri(propertyUriToChange);
  }

  public static interface FilterPropertyChangeHandler {

    boolean supports(URI propertyUri);

    URI changeUri(URI propertyUriToChange);

  }

  public static abstract class FilterPropertyChangeHandlerBase
      implements FilterPropertyChangeHandler {

    @Override
    public final boolean supports(URI propertyUri) {
      return true;
    }

  }

  public static interface FilterExpressionHandler {

    boolean supports(FilterField filterField);

    Expression createExpression(FilterField filterField);

  }

  public static abstract class FilterExpressionHandlerBase
      implements FilterExpressionHandler {

    @Override
    public final boolean supports(FilterField filterField) {
      return true;
    }

  }

}
