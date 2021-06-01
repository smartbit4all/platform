package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.filter.model.FilterFieldLabel;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterFieldModel
 */

public class FilterFieldModel   {
  @JsonProperty("selectorId")
  private String selectorId;

  @JsonProperty("label")
  private FilterFieldLabel label;

  @JsonProperty("closeable")
  private Boolean closeable;

  @JsonProperty("draggable")
  private Boolean draggable;

  @JsonProperty("selectedOperation")
  private org.smartbit4all.api.filter.bean.FilterOperation selectedOperation = null;

  @JsonProperty("operations")
  @Valid
  private List<org.smartbit4all.api.filter.bean.FilterOperation> operations = null;

  @JsonProperty("possibleValues")
  @Valid
  private List<org.smartbit4all.api.value.bean.Value> possibleValues = null;

  @JsonProperty("selectedValues")
  @Valid
  private List<org.smartbit4all.api.value.bean.Value> selectedValues = null;

  @JsonProperty("selectedValue")
  private org.smartbit4all.api.value.bean.Value selectedValue = null;

  @JsonProperty("value1")
  private String value1;

  @JsonProperty("value2")
  private String value2;

  @JsonProperty("value3")
  private String value3;

  public FilterFieldModel selectorId(String selectorId) {
    this.selectorId = selectorId;
    return this;
  }

  /**
   * Get selectorId
   * @return selectorId
  */
  @ApiModelProperty(value = "")


  public String getSelectorId() {
    return selectorId;
  }

  public void setSelectorId(String selectorId) {
    this.selectorId = selectorId;
  }

  public FilterFieldModel label(FilterFieldLabel label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterFieldLabel getLabel() {
    return label;
  }

  public void setLabel(FilterFieldLabel label) {
    this.label = label;
  }

  public FilterFieldModel closeable(Boolean closeable) {
    this.closeable = closeable;
    return this;
  }

  /**
   * Get closeable
   * @return closeable
  */
  @ApiModelProperty(value = "")


  public Boolean getCloseable() {
    return closeable;
  }

  public void setCloseable(Boolean closeable) {
    this.closeable = closeable;
  }

  public FilterFieldModel draggable(Boolean draggable) {
    this.draggable = draggable;
    return this;
  }

  /**
   * Get draggable
   * @return draggable
  */
  @ApiModelProperty(value = "")


  public Boolean getDraggable() {
    return draggable;
  }

  public void setDraggable(Boolean draggable) {
    this.draggable = draggable;
  }

  public FilterFieldModel selectedOperation(org.smartbit4all.api.filter.bean.FilterOperation selectedOperation) {
    this.selectedOperation = selectedOperation;
    return this;
  }

  /**
   * Get selectedOperation
   * @return selectedOperation
  */
  @ApiModelProperty(value = "")

  @Valid

  public org.smartbit4all.api.filter.bean.FilterOperation getSelectedOperation() {
    return selectedOperation;
  }

  public void setSelectedOperation(org.smartbit4all.api.filter.bean.FilterOperation selectedOperation) {
    this.selectedOperation = selectedOperation;
  }

  public FilterFieldModel operations(List<org.smartbit4all.api.filter.bean.FilterOperation> operations) {
    this.operations = operations;
    return this;
  }

  public FilterFieldModel addOperationsItem(org.smartbit4all.api.filter.bean.FilterOperation operationsItem) {
    if (this.operations == null) {
      this.operations = new ArrayList<>();
    }
    this.operations.add(operationsItem);
    return this;
  }

  /**
   * Get operations
   * @return operations
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<org.smartbit4all.api.filter.bean.FilterOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<org.smartbit4all.api.filter.bean.FilterOperation> operations) {
    this.operations = operations;
  }

  public FilterFieldModel possibleValues(List<org.smartbit4all.api.value.bean.Value> possibleValues) {
    this.possibleValues = possibleValues;
    return this;
  }

  public FilterFieldModel addPossibleValuesItem(org.smartbit4all.api.value.bean.Value possibleValuesItem) {
    if (this.possibleValues == null) {
      this.possibleValues = new ArrayList<>();
    }
    this.possibleValues.add(possibleValuesItem);
    return this;
  }

  /**
   * Get possibleValues
   * @return possibleValues
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<org.smartbit4all.api.value.bean.Value> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(List<org.smartbit4all.api.value.bean.Value> possibleValues) {
    this.possibleValues = possibleValues;
  }

  public FilterFieldModel selectedValues(List<org.smartbit4all.api.value.bean.Value> selectedValues) {
    this.selectedValues = selectedValues;
    return this;
  }

  public FilterFieldModel addSelectedValuesItem(org.smartbit4all.api.value.bean.Value selectedValuesItem) {
    if (this.selectedValues == null) {
      this.selectedValues = new ArrayList<>();
    }
    this.selectedValues.add(selectedValuesItem);
    return this;
  }

  /**
   * Get selectedValues
   * @return selectedValues
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<org.smartbit4all.api.value.bean.Value> getSelectedValues() {
    return selectedValues;
  }

  public void setSelectedValues(List<org.smartbit4all.api.value.bean.Value> selectedValues) {
    this.selectedValues = selectedValues;
  }

  public FilterFieldModel selectedValue(org.smartbit4all.api.value.bean.Value selectedValue) {
    this.selectedValue = selectedValue;
    return this;
  }

  /**
   * Get selectedValue
   * @return selectedValue
  */
  @ApiModelProperty(value = "")

  @Valid

  public org.smartbit4all.api.value.bean.Value getSelectedValue() {
    return selectedValue;
  }

  public void setSelectedValue(org.smartbit4all.api.value.bean.Value selectedValue) {
    this.selectedValue = selectedValue;
  }

  public FilterFieldModel value1(String value1) {
    this.value1 = value1;
    return this;
  }

  /**
   * Get value1
   * @return value1
  */
  @ApiModelProperty(value = "")


  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public FilterFieldModel value2(String value2) {
    this.value2 = value2;
    return this;
  }

  /**
   * Get value2
   * @return value2
  */
  @ApiModelProperty(value = "")


  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }

  public FilterFieldModel value3(String value3) {
    this.value3 = value3;
    return this;
  }

  /**
   * Get value3
   * @return value3
  */
  @ApiModelProperty(value = "")


  public String getValue3() {
    return value3;
  }

  public void setValue3(String value3) {
    this.value3 = value3;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterFieldModel filterFieldModel = (FilterFieldModel) o;
    return Objects.equals(this.selectorId, filterFieldModel.selectorId) &&
        Objects.equals(this.label, filterFieldModel.label) &&
        Objects.equals(this.closeable, filterFieldModel.closeable) &&
        Objects.equals(this.draggable, filterFieldModel.draggable) &&
        Objects.equals(this.selectedOperation, filterFieldModel.selectedOperation) &&
        Objects.equals(this.operations, filterFieldModel.operations) &&
        Objects.equals(this.possibleValues, filterFieldModel.possibleValues) &&
        Objects.equals(this.selectedValues, filterFieldModel.selectedValues) &&
        Objects.equals(this.selectedValue, filterFieldModel.selectedValue) &&
        Objects.equals(this.value1, filterFieldModel.value1) &&
        Objects.equals(this.value2, filterFieldModel.value2) &&
        Objects.equals(this.value3, filterFieldModel.value3);
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectorId, label, closeable, draggable, selectedOperation, operations, possibleValues, selectedValues, selectedValue, value1, value2, value3);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterFieldModel {\n");
    
    sb.append("    selectorId: ").append(toIndentedString(selectorId)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    closeable: ").append(toIndentedString(closeable)).append("\n");
    sb.append("    draggable: ").append(toIndentedString(draggable)).append("\n");
    sb.append("    selectedOperation: ").append(toIndentedString(selectedOperation)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    possibleValues: ").append(toIndentedString(possibleValues)).append("\n");
    sb.append("    selectedValues: ").append(toIndentedString(selectedValues)).append("\n");
    sb.append("    selectedValue: ").append(toIndentedString(selectedValue)).append("\n");
    sb.append("    value1: ").append(toIndentedString(value1)).append("\n");
    sb.append("    value2: ").append(toIndentedString(value2)).append("\n");
    sb.append("    value3: ").append(toIndentedString(value3)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

