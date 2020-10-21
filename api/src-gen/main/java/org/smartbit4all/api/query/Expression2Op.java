package org.smartbit4all.api.query;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Expression2Op
 */

public class Expression2Op   {
  @JsonProperty("operator")
  private Operator operator;

  @JsonProperty("operand")
  private String operand;

  @JsonProperty("literal")
  private LiteralValue literal;

  public Expression2Op operator(Operator operator) {
    this.operator = operator;
    return this;
  }

  /**
   * Get operator
   * @return operator
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public Operator getOperator() {
    return operator;
  }

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  public Expression2Op operand(String operand) {
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

  public Expression2Op literal(LiteralValue literal) {
    this.literal = literal;
    return this;
  }

  /**
   * Get literal
   * @return literal
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public LiteralValue getLiteral() {
    return literal;
  }

  public void setLiteral(LiteralValue literal) {
    this.literal = literal;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Expression2Op expression2Op = (Expression2Op) o;
    return Objects.equals(this.operator, expression2Op.operator) &&
        Objects.equals(this.operand, expression2Op.operand) &&
        Objects.equals(this.literal, expression2Op.literal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, operand, literal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Expression2Op {\n");
    
    sb.append("    operator: ").append(toIndentedString(operator)).append("\n");
    sb.append("    operand: ").append(toIndentedString(operand)).append("\n");
    sb.append("    literal: ").append(toIndentedString(literal)).append("\n");
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

