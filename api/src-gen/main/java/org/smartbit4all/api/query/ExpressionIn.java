package org.smartbit4all.api.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExpressionIn
 */

public class ExpressionIn   {
  @JsonProperty("operand")
  private String operand;

  @JsonProperty("typeQName")
  private String typeQName;

  @JsonProperty("values")
  @Valid
  private List<String> values = new ArrayList<>();

  public ExpressionIn operand(String operand) {
    this.operand = operand;
    return this;
  }

  /**
   * The name of the property
   * @return operand
  */
  @ApiModelProperty(required = true, value = "The name of the property")
  @NotNull


  public String getOperand() {
    return operand;
  }

  public void setOperand(String operand) {
    this.operand = operand;
  }

  public ExpressionIn typeQName(String typeQName) {
    this.typeQName = typeQName;
    return this;
  }

  /**
   * The qualified name of the type of the value
   * @return typeQName
  */
  @ApiModelProperty(required = true, value = "The qualified name of the type of the value")
  @NotNull


  public String getTypeQName() {
    return typeQName;
  }

  public void setTypeQName(String typeQName) {
    this.typeQName = typeQName;
  }

  public ExpressionIn values(List<String> values) {
    this.values = values;
    return this;
  }

  public ExpressionIn addValuesItem(String valuesItem) {
    this.values.add(valuesItem);
    return this;
  }

  /**
   * The string form of values
   * @return values
  */
  @ApiModelProperty(required = true, value = "The string form of values")
  @NotNull


  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpressionIn expressionIn = (ExpressionIn) o;
    return Objects.equals(this.operand, expressionIn.operand) &&
        Objects.equals(this.typeQName, expressionIn.typeQName) &&
        Objects.equals(this.values, expressionIn.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operand, typeQName, values);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExpressionIn {\n");
    
    sb.append("    operand: ").append(toIndentedString(operand)).append("\n");
    sb.append("    typeQName: ").append(toIndentedString(typeQName)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
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

