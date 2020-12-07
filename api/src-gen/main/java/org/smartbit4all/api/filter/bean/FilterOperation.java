package org.smartbit4all.api.filter.bean;

import java.net.URI;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Defines a possible way of using a filter field. For example: exact match, like, sounds like,
 * interval, etc.
 */
@ApiModel(
    description = "Defines a possible way of using a filter field. For example: exact match, like, sounds like, interval, etc.")

public class FilterOperation {
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

  public FilterOperation filterView(String filterView) {
    this.filterView = filterView;
    return this;
  }

  /**
   * Declarative name of a FilterOperationUI, which will handle this operation.
   * 
   * @return filterView
   */
  @ApiModelProperty(
      value = "Declarative name of a FilterOperationUI, which will handle this operation.")


  public String getFilterView() {
    return filterView;
  }

  public void setFilterView(String filterView) {
    this.filterView = filterView;
  }

  public FilterOperation field1(String field1) {
    this.field1 = field1;
    return this;
  }

  /**
   * Get field1
   * 
   * @return field1
   */
  @ApiModelProperty(value = "")


  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public FilterOperation field2(String field2) {
    this.field2 = field2;
    return this;
  }

  /**
   * Get field2
   * 
   * @return field2
   */
  @ApiModelProperty(value = "")


  public String getField2() {
    return field2;
  }

  public void setField2(String field2) {
    this.field2 = field2;
  }

  public FilterOperation possibleValues(URI possibleValues) {
    this.possibleValues = possibleValues;
    return this;
  }

  /**
   * Get possibleValues
   * 
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

  public FilterOperation code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * 
   * @return code
   */
  @ApiModelProperty(value = "")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public FilterOperation displayValue(String displayValue) {
    this.displayValue = displayValue;
    return this;
  }

  /**
   * Get displayValue
   * 
   * @return displayValue
   */
  @ApiModelProperty(value = "")


  public String getDisplayValue() {
    return displayValue;
  }

  public void setDisplayValue(String displayValue) {
    this.displayValue = displayValue;
  }

  public FilterOperation icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Get icon
   * 
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
    FilterOperation filterOperation = (FilterOperation) o;
    return Objects.equals(this.filterView, filterOperation.filterView) &&
        Objects.equals(this.field1, filterOperation.field1) &&
        Objects.equals(this.field2, filterOperation.field2) &&
        Objects.equals(this.possibleValues, filterOperation.possibleValues) &&
        Objects.equals(this.code, filterOperation.code) &&
        Objects.equals(this.displayValue, filterOperation.displayValue) &&
        Objects.equals(this.icon, filterOperation.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterView, field1, field2, possibleValues, code, displayValue, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterOperation {\n");

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
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

