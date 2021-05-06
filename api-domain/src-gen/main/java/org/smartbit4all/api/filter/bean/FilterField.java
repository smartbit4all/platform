package org.smartbit4all.api.filter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterOperandValue;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterField
 */

public class FilterField   {
  @JsonProperty("propertyUri1")
  private URI propertyUri1;

  @JsonProperty("propertyUri2")
  private URI propertyUri2;

  @JsonProperty("propertyUri3")
  private URI propertyUri3;

  @JsonProperty("operationCode")
  private String operationCode;

  @JsonProperty("value1")
  private FilterOperandValue value1;

  @JsonProperty("value2")
  private FilterOperandValue value2;

  @JsonProperty("value3")
  private FilterOperandValue value3;

  @JsonProperty("selectedValues")
  @Valid
  private List<URI> selectedValues = null;

  public FilterField propertyUri1(URI propertyUri1) {
    this.propertyUri1 = propertyUri1;
    return this;
  }

  /**
   * Property identifier, specifies which property should be used in this filter.
   * @return propertyUri1
  */
  @ApiModelProperty(value = "Property identifier, specifies which property should be used in this filter.")

  @Valid

  public URI getPropertyUri1() {
    return propertyUri1;
  }

  public void setPropertyUri1(URI propertyUri1) {
    this.propertyUri1 = propertyUri1;
  }

  public FilterField propertyUri2(URI propertyUri2) {
    this.propertyUri2 = propertyUri2;
    return this;
  }

  /**
   * Property identifier, specifies which property should be used in this filter.
   * @return propertyUri2
  */
  @ApiModelProperty(value = "Property identifier, specifies which property should be used in this filter.")

  @Valid

  public URI getPropertyUri2() {
    return propertyUri2;
  }

  public void setPropertyUri2(URI propertyUri2) {
    this.propertyUri2 = propertyUri2;
  }

  public FilterField propertyUri3(URI propertyUri3) {
    this.propertyUri3 = propertyUri3;
    return this;
  }

  /**
   * Property identifier, specifies which property should be used in this filter.
   * @return propertyUri3
  */
  @ApiModelProperty(value = "Property identifier, specifies which property should be used in this filter.")

  @Valid

  public URI getPropertyUri3() {
    return propertyUri3;
  }

  public void setPropertyUri3(URI propertyUri3) {
    this.propertyUri3 = propertyUri3;
  }

  public FilterField operationCode(String operationCode) {
    this.operationCode = operationCode;
    return this;
  }

  /**
   * Operation code, specifies the operator of the condition.
   * @return operationCode
  */
  @ApiModelProperty(value = "Operation code, specifies the operator of the condition.")


  public String getOperationCode() {
    return operationCode;
  }

  public void setOperationCode(String operationCode) {
    this.operationCode = operationCode;
  }

  public FilterField value1(FilterOperandValue value1) {
    this.value1 = value1;
    return this;
  }

  /**
   * Get value1
   * @return value1
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterOperandValue getValue1() {
    return value1;
  }

  public void setValue1(FilterOperandValue value1) {
    this.value1 = value1;
  }

  public FilterField value2(FilterOperandValue value2) {
    this.value2 = value2;
    return this;
  }

  /**
   * Get value2
   * @return value2
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterOperandValue getValue2() {
    return value2;
  }

  public void setValue2(FilterOperandValue value2) {
    this.value2 = value2;
  }

  public FilterField value3(FilterOperandValue value3) {
    this.value3 = value3;
    return this;
  }

  /**
   * Get value3
   * @return value3
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterOperandValue getValue3() {
    return value3;
  }

  public void setValue3(FilterOperandValue value3) {
    this.value3 = value3;
  }

  public FilterField selectedValues(List<URI> selectedValues) {
    this.selectedValues = selectedValues;
    return this;
  }

  public FilterField addSelectedValuesItem(URI selectedValuesItem) {
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

  public List<URI> getSelectedValues() {
    return selectedValues;
  }

  public void setSelectedValues(List<URI> selectedValues) {
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
    return Objects.equals(this.propertyUri1, filterField.propertyUri1) &&
        Objects.equals(this.propertyUri2, filterField.propertyUri2) &&
        Objects.equals(this.propertyUri3, filterField.propertyUri3) &&
        Objects.equals(this.operationCode, filterField.operationCode) &&
        Objects.equals(this.value1, filterField.value1) &&
        Objects.equals(this.value2, filterField.value2) &&
        Objects.equals(this.value3, filterField.value3) &&
        Objects.equals(this.selectedValues, filterField.selectedValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyUri1, propertyUri2, propertyUri3, operationCode, value1, value2, value3, selectedValues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterField {\n");
    
    sb.append("    propertyUri1: ").append(toIndentedString(propertyUri1)).append("\n");
    sb.append("    propertyUri2: ").append(toIndentedString(propertyUri2)).append("\n");
    sb.append("    propertyUri3: ").append(toIndentedString(propertyUri3)).append("\n");
    sb.append("    operationCode: ").append(toIndentedString(operationCode)).append("\n");
    sb.append("    value1: ").append(toIndentedString(value1)).append("\n");
    sb.append("    value2: ").append(toIndentedString(value2)).append("\n");
    sb.append("    value3: ").append(toIndentedString(value3)).append("\n");
    sb.append("    selectedValues: ").append(toIndentedString(selectedValues)).append("\n");
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

