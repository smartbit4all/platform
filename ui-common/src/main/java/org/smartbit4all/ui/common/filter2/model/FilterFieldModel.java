package org.smartbit4all.ui.common.filter2.model;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.smartbit4all.api.filter.bean.FilterOperation;
import org.smartbit4all.api.value.bean.Value;

public class FilterFieldModel {

  private FilterFieldLabel label;
  private Boolean closeable;
  private Boolean draggable;
  private FilterOperation selectedOperation;
  private List<FilterOperation> operations = new ArrayList<>();
  private List<Value> possibleValues = new ArrayList<>();
  private FilterOperandValue value1;
  private FilterOperandValue value2;
  private FilterOperandValue value3;

  public FilterFieldLabel getLabel() {
    return label;
  }

  public void setLabel(FilterFieldLabel label) {
    this.label = label;
  }

  public Boolean getCloseable() {
    return closeable;
  }

  public void setCloseable(Boolean closeable) {
    this.closeable = closeable;
  }

  public Boolean getDraggable() {
    return draggable;
  }

  public void setDraggable(Boolean draggable) {
    this.draggable = draggable;
  }

  public FilterOperation getSelectedOperation() {
    return selectedOperation;
  }

  public void setSelectedOperation(FilterOperation selectedOperation) {
    this.selectedOperation = selectedOperation;
  }

  public List<FilterOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<FilterOperation> operations) {
    this.operations = operations;
  }

  public List<Value> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(List<Value> possibleValues) {
    this.possibleValues = possibleValues;
  }

  public FilterOperandValue getValue1() {
    return value1;
  }

  public void setValue1(FilterOperandValue value1) {
    this.value1 = value1;
  }

  public FilterOperandValue getValue2() {
    return value2;
  }

  public void setValue2(FilterOperandValue value2) {
    this.value2 = value2;
  }

  public FilterOperandValue getValue3() {
    return value3;
  }

  public void setValue3(FilterOperandValue value3) {
    this.value3 = value3;
  }

}
