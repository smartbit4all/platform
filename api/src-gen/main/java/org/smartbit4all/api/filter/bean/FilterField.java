package org.smartbit4all.api.filter.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * FilterField
 */

public class FilterField {
  @JsonProperty("metaName")
  private String metaName;

  @JsonProperty("operation")
  private FilterOperation operation;

  @JsonProperty("value1")
  private String value1;

  @JsonProperty("value2")
  private String value2;

  @JsonProperty("selectedValues")
  @Valid
  private List<String> selectedValues = null;

  public FilterField metaName(String metaName) {
    this.metaName = metaName;
    return this;
  }

  /**
   * Name of the referenced FilterFieldMeta.
   * 
   * @return metaName
   */
  @ApiModelProperty(value = "Name of the referenced FilterFieldMeta.")


  public String getMetaName() {
    return metaName;
  }

  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }

  public FilterField operation(FilterOperation operation) {
    this.operation = operation;
    return this;
  }

  /**
   * Get operation
   * 
   * @return operation
   */
  @ApiModelProperty(value = "")

  @Valid

  public FilterOperation getOperation() {
    return operation;
  }

  public void setOperation(FilterOperation operation) {
    this.operation = operation;
  }

  public FilterField value1(String value1) {
    this.value1 = value1;
    return this;
  }

  /**
   * Get value1
   * 
   * @return value1
   */
  @ApiModelProperty(value = "")


  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public FilterField value2(String value2) {
    this.value2 = value2;
    return this;
  }

  /**
   * Get value2
   * 
   * @return value2
   */
  @ApiModelProperty(value = "")


  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }

  public FilterField selectedValues(List<String> selectedValues) {
    this.selectedValues = selectedValues;
    return this;
  }

  public FilterField addSelectedValuesItem(String selectedValuesItem) {
    if (this.selectedValues == null) {
      this.selectedValues = new ArrayList<>();
    }
    this.selectedValues.add(selectedValuesItem);
    return this;
  }

  /**
   * Get selectedValues
   * 
   * @return selectedValues
   */
  @ApiModelProperty(value = "")


  public List<String> getSelectedValues() {
    return selectedValues;
  }

  public void setSelectedValues(List<String> selectedValues) {
    this.selectedValues = selectedValues;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterField filterField = (FilterField) o;
    return Objects.equals(this.metaName, filterField.metaName) &&
        Objects.equals(this.operation, filterField.operation) &&
        Objects.equals(this.value1, filterField.value1) &&
        Objects.equals(this.value2, filterField.value2) &&
        Objects.equals(this.selectedValues, filterField.selectedValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metaName, operation, value1, value2, selectedValues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterField {\n");

    sb.append("    metaName: ").append(toIndentedString(metaName)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    value1: ").append(toIndentedString(value1)).append("\n");
    sb.append("    value2: ").append(toIndentedString(value2)).append("\n");
    sb.append("    selectedValues: ").append(toIndentedString(selectedValues)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

