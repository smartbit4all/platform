package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOperation;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Defines a possible way of using a filter field. For example: exact match, like, sounds like, interval, etc.
 */
@ApiModel(description = "Defines a possible way of using a filter field. For example: exact match, like, sounds like, interval, etc.")

public class DynamicFilterOption   {
  @JsonProperty("filterView")
  private String filterView;

  @JsonProperty("operation")
  private DynamicFilterOperation operation;

  @JsonProperty("field1")
  private String field1;

  @JsonProperty("field2")
  private String field2;

  @JsonProperty("possibleValues")
  private URI possibleValues;

  public DynamicFilterOption filterView(String filterView) {
    this.filterView = filterView;
    return this;
  }

  /**
   * Declarative name of a DynamicFilterOptionUI, which will handle this option.
   * @return filterView
  */
  @ApiModelProperty(value = "Declarative name of a DynamicFilterOptionUI, which will handle this option.")


  public String getFilterView() {
    return filterView;
  }

  public void setFilterView(String filterView) {
    this.filterView = filterView;
  }

  public DynamicFilterOption operation(DynamicFilterOperation operation) {
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

  public DynamicFilterOption field1(String field1) {
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

  public DynamicFilterOption field2(String field2) {
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

  public DynamicFilterOption possibleValues(URI possibleValues) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterOption dynamicFilterOption = (DynamicFilterOption) o;
    return Objects.equals(this.filterView, dynamicFilterOption.filterView) &&
        Objects.equals(this.operation, dynamicFilterOption.operation) &&
        Objects.equals(this.field1, dynamicFilterOption.field1) &&
        Objects.equals(this.field2, dynamicFilterOption.field2) &&
        Objects.equals(this.possibleValues, dynamicFilterOption.possibleValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterView, operation, field1, field2, possibleValues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterOption {\n");
    
    sb.append("    filterView: ").append(toIndentedString(filterView)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    field1: ").append(toIndentedString(field1)).append("\n");
    sb.append("    field2: ").append(toIndentedString(field2)).append("\n");
    sb.append("    possibleValues: ").append(toIndentedString(possibleValues)).append("\n");
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

