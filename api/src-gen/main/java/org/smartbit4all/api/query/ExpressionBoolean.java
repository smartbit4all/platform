package org.smartbit4all.api.query;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExpressionBoolean
 */

public class ExpressionBoolean   {
  @JsonProperty("operand")
  private String operand;

  @JsonProperty("value")
  private Boolean value = true;

  public ExpressionBoolean operand(String operand) {
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

  public ExpressionBoolean value(Boolean value) {
    this.value = value;
    return this;
  }

  /**
   * The boolean value of the property
   * @return value
  */
  @ApiModelProperty(required = true, value = "The boolean value of the property")
  @NotNull


  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpressionBoolean expressionBoolean = (ExpressionBoolean) o;
    return Objects.equals(this.operand, expressionBoolean.operand) &&
        Objects.equals(this.value, expressionBoolean.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operand, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExpressionBoolean {\n");
    
    sb.append("    operand: ").append(toIndentedString(operand)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

