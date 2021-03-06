package org.smartbit4all.api.impl.filter;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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

  // Constants for the filter field operators.
  public static final String DATE_INTERVAL = "date.interval";
  public static final String DATE_INTERVAL_CB = "date.interval.cb";
  public static final String DATE_EQ = "date.eq";
  public static final String DATE_TIME_INTERVAL = "date.time.interval";
  public static final String DATE_TIME_INTERVAL_CB = "date.time.interval.cb";
  public static final String DATE_TIME_EQ = "date.time.eq";
  public static final String TXT_EQ = "txt.eq";
  public static final String TXT_LIKE = "txt.like";
  public static final String MULTI_SEL = "multi.eq";
  public static final String COMBO_SEL = "combo.eq";

  private static enum TimeFilterOptions {
    LAST_WEEK, THIS_MONTH, LAST_MONTH, YESTERDAY, TODAY, OTHER
  }

  public static final String LAST_WEEK = "statistics.filter.time.last_week";
  public static final String THIS_MONTH = "statistics.filter.time.this_month";
  public static final String LAST_MONTH = "statistics.filter.time.last_month";
  public static final String YESTERDAY = "statistics.filter.time.yesterday";
  public static final String TODAY = "statistics.filter.time.today";
  public static final String OTHER = "OTHER";

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
    if (filterGroup == null || ((filterGroup.getFilterFields() == null
        || filterGroup.getFilterFields().isEmpty())
        && (filterGroup.getFilterGroups() == null || filterGroup.getFilterGroups().isEmpty()))) {
      return null;
    }
    // For the root group we create the proper clause to add the sub groups and the filter fields
    // one by one. The root group is not compiled for bracket. If it has more sub groups then they
    // will be brackets at all.
    ExpressionClause starterClause =
        filterGroup.getType() == FilterGroupType.AND ? Expression.createAndClause()
            : Expression.createOrClause();
    recurseGroups(filterGroup, starterClause);

    return starterClause.expressions().isEmpty() ? null : starterClause;
  }

  /**
   * @param filterGroup
   * @param groupStarterClause
   */
  private final void recurseGroups(FilterGroup filterGroup, ExpressionClause groupClause) {
    // First add the sub groups as brackets.
    List<FilterGroup> groups = filterGroup.getFilterGroups() == null ? Collections.emptyList()
        : filterGroup.getFilterGroups();
    for (FilterGroup subGroup : groups) {
      ExpressionClause subGroupClause =
          subGroup.getType() == FilterGroupType.AND ? Expression.createAndClause()
              : Expression.createOrClause();
      groupClause.add(subGroupClause.BRACKET());
      recurseGroups(subGroup, subGroupClause);
    }

    // Now we add all the filters from the current group as simple expressions
    List<FilterField> filterFields = filterGroup.getFilterFields() == null ? Collections.emptyList()
        : filterGroup.getFilterFields();
    for (FilterField filterField : filterFields) {

      Expression expressionOfField = expressionOfField(filterField);

      if (expressionOfField != null) {
        groupClause.add(expressionOfField);
      }
    }
  }

  public Expression expressionOfField(FilterField filterField) {
    Expression expressionOfField = null;

    String operationCode = filterField.getOperationCode();

    if (DATE_INTERVAL.equals(operationCode) || DATE_TIME_INTERVAL.equals(operationCode)) {
      expressionOfField = createDateIntervalClause(filterField);

    } else if (DATE_INTERVAL_CB.equals(operationCode)
        || DATE_TIME_INTERVAL_CB.equals(operationCode)) {
      expressionOfField = createDateIntervalCbClause(filterField);

    } else if (DATE_EQ.equals(operationCode) || DATE_TIME_EQ.equals(operationCode)) {
      expressionOfField = createDateEqClause(filterField);

    } else if (TXT_EQ.equals(operationCode)) {
      expressionOfField = createTxtEqClause(filterField);

    } else if (TXT_LIKE.equals(operationCode)) {
      expressionOfField = createTxtLikeClause(filterField);

    } else if (MULTI_SEL.equals(operationCode)) {
      expressionOfField = createMultiSelClause(filterField);
    } else if (COMBO_SEL.equals(operationCode)) {
      expressionOfField = createComboSelClause(filterField);
    }
    return expressionOfField;
  }

  private Expression createTxtLikeClause(FilterField filterField) {
    Expression expressionOfField = null;
    FilterOperandValue filterOperandValue = filterField.getValue1();
    String value = filterOperandValue.getValue();
    if (value != null && !value.isEmpty()) {
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
    String value = filterOperandValue.getValue();
    if (value != null && !value.isEmpty()) {
      Class<?> type = getValueType(filterOperandValue);
      Property<?> property = getProperty(filterField.getPropertyUri1());
      expressionOfField = getTypedExpression(type, property, value, Property::eq);
    }
    return expressionOfField;
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
  private <T> Expression getTypedExpression(Class<T> type, Property<?> property, String value,
      BiFunction<Property<T>, T, Expression> exp) {
    Property<T> propertyAsString = (Property<T>) property;
    T typedValue = convertValueToType(value, type);
    return exp.apply(propertyAsString, typedValue);
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
  
  @SuppressWarnings("unchecked") // type casts are based on the propertyType parameter and are checked by that
  private <T> Expression createInExpression(Property<?> property, Class<T> propertyType, List<URI> selectedValues) {
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
  private <T> T convertValueToType(String value, Class<T> type) {
    Converter<String, T> valueConverter = transferService.converterByType(String.class, type);
    if(valueConverter != null) {
      return valueConverter.convertTo(value);
    } else if(String.class == type){
      return (T) value;
    } else {
      throw new RuntimeException("Unable to typecast the selected values to create expression for the configured property!");
    }
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
  private <T> Expression createEqExpression(Property<?> property, Class<T> propertyType, URI selectedValueUri) {
    T value = convertValueUriId(propertyType, selectedValueUri);
    return ((Property<T>) property).eq(value);
  }

  private Property<?> getProperty(URI propertyUri) {
    Objects.requireNonNull(propertyUri, "The 'propertyUri' parameter can not be null!");
    Property<?> property = entityManager.property(propertyUri);
    if(property == null) {
      throw new IllegalStateException("There is no property registered for uri: " + propertyUri);
    }
    return property;
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
        return createEqExpression(property, valueOperand, this::convertToLocalDateTime);
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
        // to LocaDate because the property itself is LocalDate type.
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

  private LocalDate convertToLocalDate(FilterOperandValue valueOperand) {
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

  private LocalDateTime convertToLocalDateTime(FilterOperandValue valueOperand) {
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

}
