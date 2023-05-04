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
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBoolOperator;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The expression data is the decriptor of an expression stands for an equal, like etc. and also can be a bracket where  we have embedded expressions. 
 */
@ApiModel(description = "The expression data is the decriptor of an expression stands for an equal, like etc. and also can be a bracket where  we have embedded expressions. ")
@JsonPropertyOrder({
  FilterExpressionData.OPERAND1,
  FilterExpressionData.OPERAND2,
  FilterExpressionData.OPERAND3,
  FilterExpressionData.CURRENT_OPERATION,
  FilterExpressionData.BOOL_OPERATOR,
  FilterExpressionData.SUB_EXPRESSION
})
@JsonTypeName("FilterExpressionData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterExpressionData {
  public static final String OPERAND1 = "operand1";
  private FilterExpressionOperandData operand1;

  public static final String OPERAND2 = "operand2";
  private FilterExpressionOperandData operand2;

  public static final String OPERAND3 = "operand3";
  private FilterExpressionOperandData operand3;

  public static final String CURRENT_OPERATION = "currentOperation";
  private FilterExpressionOperation currentOperation;

  public static final String BOOL_OPERATOR = "boolOperator";
  private FilterExpressionBoolOperator boolOperator;

  public static final String SUB_EXPRESSION = "subExpression";
  private FilterExpressionList subExpression;

  public FilterExpressionData() { 
  }

  public FilterExpressionData operand1(FilterExpressionOperandData operand1) {
    
    this.operand1 = operand1;
    return this;
  }

   /**
   * Get operand1
   * @return operand1
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OPERAND1)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionOperandData getOperand1() {
    return operand1;
  }


  @JsonProperty(OPERAND1)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperand1(FilterExpressionOperandData operand1) {
    this.operand1 = operand1;
  }


  public FilterExpressionData operand2(FilterExpressionOperandData operand2) {
    
    this.operand2 = operand2;
    return this;
  }

   /**
   * Get operand2
   * @return operand2
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OPERAND2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionOperandData getOperand2() {
    return operand2;
  }


  @JsonProperty(OPERAND2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperand2(FilterExpressionOperandData operand2) {
    this.operand2 = operand2;
  }


  public FilterExpressionData operand3(FilterExpressionOperandData operand3) {
    
    this.operand3 = operand3;
    return this;
  }

   /**
   * Get operand3
   * @return operand3
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OPERAND3)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionOperandData getOperand3() {
    return operand3;
  }


  @JsonProperty(OPERAND3)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperand3(FilterExpressionOperandData operand3) {
    this.operand3 = operand3;
  }


  public FilterExpressionData currentOperation(FilterExpressionOperation currentOperation) {
    
    this.currentOperation = currentOperation;
    return this;
  }

   /**
   * Get currentOperation
   * @return currentOperation
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CURRENT_OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionOperation getCurrentOperation() {
    return currentOperation;
  }


  @JsonProperty(CURRENT_OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCurrentOperation(FilterExpressionOperation currentOperation) {
    this.currentOperation = currentOperation;
  }


  public FilterExpressionData boolOperator(FilterExpressionBoolOperator boolOperator) {
    
    this.boolOperator = boolOperator;
    return this;
  }

   /**
   * Get boolOperator
   * @return boolOperator
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(BOOL_OPERATOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionBoolOperator getBoolOperator() {
    return boolOperator;
  }


  @JsonProperty(BOOL_OPERATOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBoolOperator(FilterExpressionBoolOperator boolOperator) {
    this.boolOperator = boolOperator;
  }


  public FilterExpressionData subExpression(FilterExpressionList subExpression) {
    
    this.subExpression = subExpression;
    return this;
  }

   /**
   * Get subExpression
   * @return subExpression
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SUB_EXPRESSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionList getSubExpression() {
    return subExpression;
  }


  @JsonProperty(SUB_EXPRESSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubExpression(FilterExpressionList subExpression) {
    this.subExpression = subExpression;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpressionData filterExpressionData = (FilterExpressionData) o;
    return Objects.equals(this.operand1, filterExpressionData.operand1) &&
        Objects.equals(this.operand2, filterExpressionData.operand2) &&
        Objects.equals(this.operand3, filterExpressionData.operand3) &&
        Objects.equals(this.currentOperation, filterExpressionData.currentOperation) &&
        Objects.equals(this.boolOperator, filterExpressionData.boolOperator) &&
        Objects.equals(this.subExpression, filterExpressionData.subExpression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operand1, operand2, operand3, currentOperation, boolOperator, subExpression);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpressionData {\n");
    sb.append("    operand1: ").append(toIndentedString(operand1)).append("\n");
    sb.append("    operand2: ").append(toIndentedString(operand2)).append("\n");
    sb.append("    operand3: ").append(toIndentedString(operand3)).append("\n");
    sb.append("    currentOperation: ").append(toIndentedString(currentOperation)).append("\n");
    sb.append("    boolOperator: ").append(toIndentedString(boolOperator)).append("\n");
    sb.append("    subExpression: ").append(toIndentedString(subExpression)).append("\n");
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

