package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Defines a possible way of using a filter field. For example: exact match, like, sounds like, interval, etc.
 */
@ApiModel(description = "Defines a possible way of using a filter field. For example: exact match, like, sounds like, interval, etc.")

public class DynamicFilterOperation   {
  @JsonProperty("filterView")
  private String filterView;

  @JsonProperty("field1")
  private String field1;

  @JsonProperty("field2")
  private String field2;

  @JsonProperty("possibleValues")
  private URI possibleValues;

  @JsonProperty("code")
  private String code;

  @JsonProperty("displayValue")
  private String displayValue;

  @JsonProperty("icon")
  private String icon;

  public DynamicFilterOperation filterView(String filterView) {
    this.filterView = filterView;
    return this;
  }

  /**
   * Declarative name of a DynamicFilterOperationUI, which will handle this operation.
   * @return filterView
  */
  @ApiModelProperty(value = "Declarative name of a DynamicFilterOperationUI, which will handle this operation.")


  public String getFilterView() {
    return filterView;
  }

  public void setFilterView(String filterView) {
    this.filterView = filterView;
  }

  public DynamicFilterOperation field1(String field1) {
    this.field1 = field1;
    return this;
  }

  /**
   * Get field1
   * @return field1
  */
  @ApiModelProperty(value = "")


  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public DynamicFilterOperation field2(String field2) {
    this.field2 = field2;
    return this;
  }

  /**
   * Get field2
   * @return field2
  */
  @ApiModelProperty(value = "")


  public String getField2() {
    return field2;
  }

  public void setField2(String field2) {
    this.field2 = field2;
  }

  public DynamicFilterOperation possibleValues(URI possibleValues) {
    this.possibleValues = possibleValues;
    return this;
  }

  /**
   * Get possibleValues
   * @return possibleValues
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(URI possibleValues) {
    this.possibleValues = possibleValues;
  }

  public DynamicFilterOperation code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  */
  @ApiModelProperty(value = "")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public DynamicFilterOperation displayValue(String displayValue) {
    this.displayValue = displayValue;
    return this;
  }

  /**
   * Get displayValue
   * @return displayValue
  */
  @ApiModelProperty(value = "")


  public String getDisplayValue() {
    return displayValue;
  }

  public void setDisplayValue(String displayValue) {
    this.displayValue = displayValue;
  }

  public DynamicFilterOperation icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Get icon
   * @return icon
  */
  @ApiModelProperty(value = "")


  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterOperation dynamicFilterOperation = (DynamicFilterOperation) o;
    return Objects.equals(this.filterView, dynamicFilterOperation.filterView) &&
        Objects.equals(this.field1, dynamicFilterOperation.field1) &&
        Objects.equals(this.field2, dynamicFilterOperation.field2) &&
        Objects.equals(this.possibleValues, dynamicFilterOperation.possibleValues) &&
        Objects.equals(this.code, dynamicFilterOperation.code) &&
        Objects.equals(this.displayValue, dynamicFilterOperation.displayValue) &&
        Objects.equals(this.icon, dynamicFilterOperation.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterView, field1, field2, possibleValues, code, displayValue, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterOperation {\n");
    
    sb.append("    filterView: ").append(toIndentedString(filterView)).append("\n");
    sb.append("    field1: ").append(toIndentedString(field1)).append("\n");
    sb.append("    field2: ").append(toIndentedString(field2)).append("\n");
    sb.append("    possibleValues: ").append(toIndentedString(possibleValues)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    displayValue: ").append(toIndentedString(displayValue)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
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

