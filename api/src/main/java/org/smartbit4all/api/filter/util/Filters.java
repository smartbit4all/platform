package org.smartbit4all.api.filter.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterField;
import org.smartbit4all.api.filter.bean.FilterGroup;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.domain.meta.Expression;

public class Filters {

  private Filters() {
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
      FilterService.TimeFilterOptions timeFilterOption =
          FilterService.TimeFilterOptions.valueOf(timeFilter.getValue3().getValue());
      if (timeFilterOption == FilterService.TimeFilterOptions.OTHER) {
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
