package org.smartbit4all.api.dynamicfilter.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * DynamicFilter
 */

public class DynamicFilter   {
  @JsonProperty("metaName")
  private String metaName;

  @JsonProperty("operation")
  private DynamicFilterOperation operation;

  @JsonProperty("value1")
  private String value1;

  @JsonProperty("value2")
  private String value2;

  @JsonProperty("selectedValues")
  @Valid
  private List<String> selectedValues = null;

  @JsonProperty("included")
  private Boolean included;

  @JsonProperty("editable")
  private Boolean editable;

  public DynamicFilter metaName(String metaName) {
    this.metaName = metaName;
    return this;
  }

  /**
   * Name of the referenced DynamicFilterMeta.
   * @return metaName
  */
  @ApiModelProperty(value = "Name of the referenced DynamicFilterMeta.")


  public String getMetaName() {
    return metaName;
  }

  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }

  public DynamicFilter operation(DynamicFilterOperation operation) {
    this.operation = operation;
    return this;
  }

  /**
   * Get operation
   * @return operation
  */
  @ApiModelProperty(value = "")

  @Valid

  public DynamicFilterOperation getOperation() {
    return operation;
  }

  public void setOperation(DynamicFilterOperation operation) {
    this.operation = operation;
  }

  public DynamicFilter value1(String value1) {
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

  public DynamicFilter value2(String value2) {
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

  public DynamicFilter selectedValues(List<String> selectedValues) {
    this.selectedValues = selectedValues;
    return this;
  }

  public DynamicFilter addSelectedValuesItem(String selectedValuesItem) {
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


  public List<String> getSelectedValues() {
    return selectedValues;
  }

  public void setSelectedValues(List<String> selectedValues) {
    this.selectedValues = selectedValues;
  }

  public DynamicFilter included(Boolean included) {
    this.included = included;
    return this;
  }

  /**
   * Get included
   * @return included
  */
  @ApiModelProperty(value = "")


  public Boolean getIncluded() {
    return included;
  }

  public void setIncluded(Boolean included) {
    this.included = included;
  }

  public DynamicFilter editable(Boolean editable) {
    this.editable = editable;
    return this;
  }

  /**
   * Get editable
   * @return editable
  */
  @ApiModelProperty(value = "")


  public Boolean getEditable() {
    return editable;
  }

  public void setEditable(Boolean editable) {
    this.editable = editable;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilter dynamicFilter = (DynamicFilter) o;
    return Objects.equals(this.metaName, dynamicFilter.metaName) &&
        Objects.equals(this.operation, dynamicFilter.operation) &&
        Objects.equals(this.value1, dynamicFilter.value1) &&
        Objects.equals(this.value2, dynamicFilter.value2) &&
        Objects.equals(this.selectedValues, dynamicFilter.selectedValues) &&
        Objects.equals(this.included, dynamicFilter.included) &&
        Objects.equals(this.editable, dynamicFilter.editable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metaName, operation, value1, value2, selectedValues, included, editable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilter {\n");
    
    sb.append("    metaName: ").append(toIndentedString(metaName)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    value1: ").append(toIndentedString(value1)).append("\n");
    sb.append("    value2: ").append(toIndentedString(value2)).append("\n");
    sb.append("    selectedValues: ").append(toIndentedString(selectedValues)).append("\n");
    sb.append("    included: ").append(toIndentedString(included)).append("\n");
    sb.append("    editable: ").append(toIndentedString(editable)).append("\n");
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

