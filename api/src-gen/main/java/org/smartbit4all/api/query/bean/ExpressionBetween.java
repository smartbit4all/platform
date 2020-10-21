package org.smartbit4all.api.query.bean;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExpressionBetween
 */

public class ExpressionBetween   {
  @JsonProperty("operand")
  private String operand;

  @JsonProperty("lowerBound")
  private LiteralValue lowerBound;

  @JsonProperty("upperBound")
  private LiteralValue upperBound;

  public ExpressionBetween operand(String operand) {
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

  public ExpressionBetween lowerBound(LiteralValue lowerBound) {
    this.lowerBound = lowerBound;
    return this;
  }

  /**
   * Get lowerBound
   * @return lowerBound
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public LiteralValue getLowerBound() {
    return lowerBound;
  }

  public void setLowerBound(LiteralValue lowerBound) {
    this.lowerBound = lowerBound;
  }

  public ExpressionBetween upperBound(LiteralValue upperBound) {
    this.upperBound = upperBound;
    return this;
  }

  /**
   * Get upperBound
   * @return upperBound
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public LiteralValue getUpperBound() {
    return upperBound;
  }

  public void setUpperBound(LiteralValue upperBound) {
    this.upperBound = upperBound;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpressionBetween expressionBetween = (ExpressionBetween) o;
    return Objects.equals(this.operand, expressionBetween.operand) &&
        Objects.equals(this.lowerBound, expressionBetween.lowerBound) &&
        Objects.equals(this.upperBound, expressionBetween.upperBound);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operand, lowerBound, upperBound);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExpressionBetween {\n");
    
    sb.append("    operand: ").append(toIndentedString(operand)).append("\n");
    sb.append("    lowerBound: ").append(toIndentedString(lowerBound)).append("\n");
    sb.append("    upperBound: ").append(toIndentedString(upperBound)).append("\n");
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

