package org.smartbit4all.api.query.bean;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExpressionIsNull
 */

public class ExpressionIsNull   {
  @JsonProperty("operand")
  private String operand;

  public ExpressionIsNull operand(String operand) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpressionIsNull expressionIsNull = (ExpressionIsNull) o;
    return Objects.equals(this.operand, expressionIsNull.operand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operand);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExpressionIsNull {\n");
    
    sb.append("    operand: ").append(toIndentedString(operand)).append("\n");
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

