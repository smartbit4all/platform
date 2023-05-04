/*
 * Filter API 2
 * Filter API 2
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.filterexpression.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The value of an expression operand. The type is named and the value itself is string formatted by the definition of the type. 
 */
@ApiModel(description = "The value of an expression operand. The type is named and the value itself is string formatted by the definition of the type. ")
@JsonPropertyOrder({
  FilterExpressionOperandData.IS_DATA_NAME,
  FilterExpressionOperandData.VALUE_AS_STRING,
  FilterExpressionOperandData.SELECTED_VALUES,
  FilterExpressionOperandData.TYPE
})
@JsonTypeName("FilterExpressionOperandData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterExpressionOperandData {
  public static final String IS_DATA_NAME = "isDataName";
  private Boolean isDataName = true;

  public static final String VALUE_AS_STRING = "valueAsString";
  private String valueAsString;

  public static final String SELECTED_VALUES = "selectedValues";
  private List<String> selectedValues = null;

  public static final String TYPE = "type";
  private FilterExpressionDataType type;

  public FilterExpressionOperandData() { 
  }

  public FilterExpressionOperandData isDataName(Boolean isDataName) {
    
    this.isDataName = isDataName;
    return this;
  }

   /**
   * True if the given operand refers to property at the moment. The value as string is a property name.
   * @return isDataName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "True if the given operand refers to property at the moment. The value as string is a property name.")
  @JsonProperty(IS_DATA_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getIsDataName() {
    return isDataName;
  }


  @JsonProperty(IS_DATA_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIsDataName(Boolean isDataName) {
    this.isDataName = isDataName;
  }


  public FilterExpressionOperandData valueAsString(String valueAsString) {
    
    this.valueAsString = valueAsString;
    return this;
  }

   /**
   * Get valueAsString
   * @return valueAsString
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(VALUE_AS_STRING)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getValueAsString() {
    return valueAsString;
  }


  @JsonProperty(VALUE_AS_STRING)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValueAsString(String valueAsString) {
    this.valueAsString = valueAsString;
  }


  public FilterExpressionOperandData selectedValues(List<String> selectedValues) {
    
    this.selectedValues = selectedValues;
    return this;
  }

  public FilterExpressionOperandData addSelectedValuesItem(String selectedValuesItem) {
    if (this.selectedValues == null) {
      this.selectedValues = new ArrayList<>();
    }
    this.selectedValues.add(selectedValuesItem);
    return this;
  }

   /**
   * Get selectedValues
   * @return selectedValues
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTED_VALUES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getSelectedValues() {
    return selectedValues;
  }


  @JsonProperty(SELECTED_VALUES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectedValues(List<String> selectedValues) {
    this.selectedValues = selectedValues;
  }


  public FilterExpressionOperandData type(FilterExpressionDataType type) {
    
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionDataType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setType(FilterExpressionDataType type) {
    this.type = type;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpressionOperandData filterExpressionOperandData = (FilterExpressionOperandData) o;
    return Objects.equals(this.isDataName, filterExpressionOperandData.isDataName) &&
        Objects.equals(this.valueAsString, filterExpressionOperandData.valueAsString) &&
        Objects.equals(this.selectedValues, filterExpressionOperandData.selectedValues) &&
        Objects.equals(this.type, filterExpressionOperandData.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isDataName, valueAsString, selectedValues, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpressionOperandData {\n");
    sb.append("    isDataName: ").append(toIndentedString(isDataName)).append("\n");
    sb.append("    valueAsString: ").append(toIndentedString(valueAsString)).append("\n");
    sb.append("    selectedValues: ").append(toIndentedString(selectedValues)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

