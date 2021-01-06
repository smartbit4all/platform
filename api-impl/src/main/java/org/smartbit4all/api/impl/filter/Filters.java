package org.smartbit4all.api.impl.filter;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
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
  public static final String MULTI_SEL = "multi.eq";
  public static final String COMBO_SEL = "combo.eq";

  public static final String LAST_WEEK = "statistics.filter.time.last_week";
  public static final String THIS_MONTH = "statistics.filter.time.this_month";
  public static final String LAST_MONTH = "statistics.filter.time.last_month";
  public static final String YESTERDAY = "statistics.filter.time.yesterday";
  public static final String TODAY = "statistics.filter.time.today";
  public static final String OTHER = "statistics.filter.time.other";

  // -----------------------------------------------------

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TransferService transferService;

  /**
   * Transform the filter using the {@link EntityManager} to access the {@link Property}s.
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

    return null;
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
      String operationCode = filterField.getOperationCode();
      URI firstSelectedValue =
          (filterField.getSelectedValues() != null && filterField.getSelectedValues().size() > 0)
              ? filterField.getSelectedValues().get(0)
              : null;
      if (DATE_INTERVAL.equals(operationCode) || (DATE_INTERVAL_CB.equals(operationCode)
          || OTHER.equals(firstSelectedValue.toString()))) {
        Property<?> property1 =
            filterField.getPropertyUri1() == null ? null
                : entityManager.property(filterField.getPropertyUri1());

        FilterOperandValue value1Operand = filterField.getValue1();
        FilterOperandValue value2Operand = filterField.getValue2();
        if (LocalDate.class.getName().equals(property1.getClass().getName())) {
          // If we have LocaDate in the filter field as values then OK. Else we have to transfer it
          // to LocaDate because the property itself is LocalDate type.

          LocalDate value1 = convertToLocalDate(value1Operand);
          LocalDate value2 = convertToLocalDate(value2Operand);

          value2 = value2.plusDays(1);

          @SuppressWarnings("unchecked")
          Property<LocalDate> actualProperty = (Property<LocalDate>) property1;
          if (value1 == null && value2 != null) {
            // Less than upper bound.
            groupClause.add(actualProperty.lt(value2));
          } else if (value1 != null && value2 == null) {
            // Greater or equal than the lower bound
            groupClause.add(actualProperty.ge(value2));
          } else if (value1 == null && value2 != null) {
            groupClause.add(actualProperty.between(value1, value2));
          }
        }
        if (LocalDateTime.class.getName().equals(property1.getClass().getName())) {
          // If we have LocaDate in the filter field as values then OK. Else we have to transfer it
          // to LocaDate because the property itself is LocalDate type.
          LocalDateTime value1 = convertToLocalDateTime(value1Operand);
          LocalDateTime value2 = convertToLocalDateTime(value2Operand);
          @SuppressWarnings("unchecked")
          Property<LocalDateTime> actualProperty = (Property<LocalDateTime>) property1;
          if (value1 == null && value2 != null) {
            // Less than upper bound.
            groupClause.add(actualProperty.lt(value2));
          } else if (value1 != null && value2 == null) {
            // Greater or equal than the lower bound
            groupClause.add(actualProperty.ge(value2));
          } else if (value1 == null && value2 != null) {
            groupClause.add(actualProperty.between(value1, value2));
          }
        }
      } else if (DATE_INTERVAL_CB.equals(operationCode) && firstSelectedValue != null) {
        // In this case we have an instruction about the interval. This section will produce the
        // LocalDate variables and later on there will be a conversion if we need LocalDateTime.
        LocalDate value1 = null;
        LocalDate value2 = null;
        LocalDate today = LocalDate.now();
        if (firstSelectedValue.toString().equals(LAST_WEEK)) {
        } else if (firstSelectedValue.toString().equals(THIS_MONTH)) {

        } else if (firstSelectedValue.toString().equals(LAST_MONTH)) {

        } else if (firstSelectedValue.toString().equals(YESTERDAY)) {

        } else if (firstSelectedValue.toString().equals(TODAY)) {

        }
      } else if (DATE_EQ.equals(operationCode)) {
      } else if (DATE_TIME_INTERVAL.equals(operationCode)) {
      } else if (DATE_TIME_INTERVAL_CB.equals(operationCode)) {
      } else if (DATE_TIME_EQ.equals(operationCode)) {
      } else if (TXT_EQ.equals(operationCode)) {
      } else if (MULTI_SEL.equals(operationCode)) {
      } else if (COMBO_SEL.equals(operationCode)) {
      }
    }
  }

  private LocalDate convertToLocalDate(FilterOperandValue value1Operand) {
    LocalDate value1 = null;
    if (LocalDate.class.getName().equals(value1Operand.getType())) {
      // TODO More sophisticated converter search.
      Converter<String, LocalDate> converter =
          transferService.converterByType(String.class, LocalDate.class);
      value1 = converter.convertTo(value1Operand.getValue());
    } else if (LocalDateTime.class.getName().equals(value1Operand.getType())) {
      Converter<String, LocalDateTime> converter =
          transferService.converterByType(String.class, LocalDateTime.class);
      // Truncate the time from the local date time.
      value1 = converter.convertTo(value1Operand.getValue()).toLocalDate();
    }
    return value1;
  }

  private LocalDateTime convertToLocalDateTime(FilterOperandValue value1Operand) {
    LocalDateTime value1 = null;
    if (LocalDate.class.getName().equals(value1Operand.getType())) {
      // TODO More sophisticated converter search.
      Converter<String, LocalDate> converter =
          transferService.converterByType(String.class, LocalDate.class);
      value1 = converter.convertTo(value1Operand.getValue()).atStartOfDay();
    } else if (LocalDateTime.class.getName().equals(value1Operand.getType())) {
      Converter<String, LocalDateTime> converter =
          transferService.converterByType(String.class, LocalDateTime.class);
      // Truncate the time from the local date time.
      value1 = converter.convertTo(value1Operand.getValue());
    }
    return value1;
  }

  public static LocalDate[] getPreviousWeekFirstDay(LocalDate date) {
    final int dayOfWeek = date.getDayOfWeek().getValue();
    final LocalDate from = date.minusDays(dayOfWeek + 6); // (dayOfWeek - 1) + 7
    final LocalDate to = date.minusDays(dayOfWeek);

    return new LocalDate[] {from, to};
  }

  public static LocalDate[] getPreviousMonth(LocalDate date) {
    final LocalDate from = date.minusDays(date.getDayOfMonth() - 1).minusMonths(1);
    final LocalDate to = from.plusMonths(1).minusDays(1);

    return new LocalDate[] {from, to};
  }

}
