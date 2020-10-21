package org.smartbit4all.api.query.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExpressionClause
 */

public class ExpressionClause   {
  @JsonProperty("objectQName")
  private String objectQName;

  @JsonProperty("propertyName")
  private String propertyName;

  @JsonProperty("isNegate")
  private Boolean isNegate = false;

  @JsonProperty("boolOperator")
  private BoolOperator boolOperator;

  @JsonProperty("expressionClauses")
  @Valid
  private List<ExpressionClause> expressionClauses = null;

  @JsonProperty("expression2Ops")
  @Valid
  private List<Expression2Op> expression2Ops = null;

  @JsonProperty("expressionBooleans")
  @Valid
  private List<ExpressionBoolean> expressionBooleans = null;

  @JsonProperty("expressionBetweens")
  @Valid
  private List<ExpressionBetween> expressionBetweens = null;

  @JsonProperty("expressionIns")
  @Valid
  private List<ExpressionIn> expressionIns = null;

  @JsonProperty("expressionIsNulls")
  @Valid
  private List<ExpressionIsNull> expressionIsNulls = null;

  public ExpressionClause objectQName(String objectQName) {
    this.objectQName = objectQName;
    return this;
  }

  /**
   * The qualified name of the bean the expression must run on
   * @return objectQName
  */
  @ApiModelProperty(value = "The qualified name of the bean the expression must run on")


  public String getObjectQName() {
    return objectQName;
  }

  public void setObjectQName(String objectQName) {
    this.objectQName = objectQName;
  }

  public ExpressionClause propertyName(String propertyName) {
    this.propertyName = propertyName;
    return this;
  }

  /**
   * Holds the property name of the parent object when the expressionClause is embedded
   * @return propertyName
  */
  @ApiModelProperty(value = "Holds the property name of the parent object when the expressionClause is embedded")


  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public ExpressionClause isNegate(Boolean isNegate) {
    this.isNegate = isNegate;
    return this;
  }

  /**
   * If the whole clause must be negated
   * @return isNegate
  */
  @ApiModelProperty(value = "If the whole clause must be negated")


  public Boolean getIsNegate() {
    return isNegate;
  }

  public void setIsNegate(Boolean isNegate) {
    this.isNegate = isNegate;
  }

  public ExpressionClause boolOperator(BoolOperator boolOperator) {
    this.boolOperator = boolOperator;
    return this;
  }

  /**
   * Get boolOperator
   * @return boolOperator
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public BoolOperator getBoolOperator() {
    return boolOperator;
  }

  public void setBoolOperator(BoolOperator boolOperator) {
    this.boolOperator = boolOperator;
  }

  public ExpressionClause expressionClauses(List<ExpressionClause> expressionClauses) {
    this.expressionClauses = expressionClauses;
    return this;
  }

  public ExpressionClause addExpressionClausesItem(ExpressionClause expressionClausesItem) {
    if (this.expressionClauses == null) {
      this.expressionClauses = new ArrayList<>();
    }
    this.expressionClauses.add(expressionClausesItem);
    return this;
  }

  /**
   * Get expressionClauses
   * @return expressionClauses
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ExpressionClause> getExpressionClauses() {
    return expressionClauses;
  }

  public void setExpressionClauses(List<ExpressionClause> expressionClauses) {
    this.expressionClauses = expressionClauses;
  }

  public ExpressionClause expression2Ops(List<Expression2Op> expression2Ops) {
    this.expression2Ops = expression2Ops;
    return this;
  }

  public ExpressionClause addExpression2OpsItem(Expression2Op expression2OpsItem) {
    if (this.expression2Ops == null) {
      this.expression2Ops = new ArrayList<>();
    }
    this.expression2Ops.add(expression2OpsItem);
    return this;
  }

  /**
   * Get expression2Ops
   * @return expression2Ops
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<Expression2Op> getExpression2Ops() {
    return expression2Ops;
  }

  public void setExpression2Ops(List<Expression2Op> expression2Ops) {
    this.expression2Ops = expression2Ops;
  }

  public ExpressionClause expressionBooleans(List<ExpressionBoolean> expressionBooleans) {
    this.expressionBooleans = expressionBooleans;
    return this;
  }

  public ExpressionClause addExpressionBooleansItem(ExpressionBoolean expressionBooleansItem) {
    if (this.expressionBooleans == null) {
      this.expressionBooleans = new ArrayList<>();
    }
    this.expressionBooleans.add(expressionBooleansItem);
    return this;
  }

  /**
   * Get expressionBooleans
   * @return expressionBooleans
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ExpressionBoolean> getExpressionBooleans() {
    return expressionBooleans;
  }

  public void setExpressionBooleans(List<ExpressionBoolean> expressionBooleans) {
    this.expressionBooleans = expressionBooleans;
  }

  public ExpressionClause expressionBetweens(List<ExpressionBetween> expressionBetweens) {
    this.expressionBetweens = expressionBetweens;
    return this;
  }

  public ExpressionClause addExpressionBetweensItem(ExpressionBetween expressionBetweensItem) {
    if (this.expressionBetweens == null) {
      this.expressionBetweens = new ArrayList<>();
    }
    this.expressionBetweens.add(expressionBetweensItem);
    return this;
  }

  /**
   * Get expressionBetweens
   * @return expressionBetweens
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ExpressionBetween> getExpressionBetweens() {
    return expressionBetweens;
  }

  public void setExpressionBetweens(List<ExpressionBetween> expressionBetweens) {
    this.expressionBetweens = expressionBetweens;
  }

  public ExpressionClause expressionIns(List<ExpressionIn> expressionIns) {
    this.expressionIns = expressionIns;
    return this;
  }

  public ExpressionClause addExpressionInsItem(ExpressionIn expressionInsItem) {
    if (this.expressionIns == null) {
      this.expressionIns = new ArrayList<>();
    }
    this.expressionIns.add(expressionInsItem);
    return this;
  }

  /**
   * Get expressionIns
   * @return expressionIns
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ExpressionIn> getExpressionIns() {
    return expressionIns;
  }

  public void setExpressionIns(List<ExpressionIn> expressionIns) {
    this.expressionIns = expressionIns;
  }

  public ExpressionClause expressionIsNulls(List<ExpressionIsNull> expressionIsNulls) {
    this.expressionIsNulls = expressionIsNulls;
    return this;
  }

  public ExpressionClause addExpressionIsNullsItem(ExpressionIsNull expressionIsNullsItem) {
    if (this.expressionIsNulls == null) {
      this.expressionIsNulls = new ArrayList<>();
    }
    this.expressionIsNulls.add(expressionIsNullsItem);
    return this;
  }

  /**
   * Get expressionIsNulls
   * @return expressionIsNulls
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ExpressionIsNull> getExpressionIsNulls() {
    return expressionIsNulls;
  }

  public void setExpressionIsNulls(List<ExpressionIsNull> expressionIsNulls) {
    this.expressionIsNulls = expressionIsNulls;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExpressionClause expressionClause = (ExpressionClause) o;
    return Objects.equals(this.objectQName, expressionClause.objectQName) &&
        Objects.equals(this.propertyName, expressionClause.propertyName) &&
        Objects.equals(this.isNegate, expressionClause.isNegate) &&
        Objects.equals(this.boolOperator, expressionClause.boolOperator) &&
        Objects.equals(this.expressionClauses, expressionClause.expressionClauses) &&
        Objects.equals(this.expression2Ops, expressionClause.expression2Ops) &&
        Objects.equals(this.expressionBooleans, expressionClause.expressionBooleans) &&
        Objects.equals(this.expressionBetweens, expressionClause.expressionBetweens) &&
        Objects.equals(this.expressionIns, expressionClause.expressionIns) &&
        Objects.equals(this.expressionIsNulls, expressionClause.expressionIsNulls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectQName, propertyName, isNegate, boolOperator, expressionClauses, expression2Ops, expressionBooleans, expressionBetweens, expressionIns, expressionIsNulls);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExpressionClause {\n");
    
    sb.append("    objectQName: ").append(toIndentedString(objectQName)).append("\n");
    sb.append("    propertyName: ").append(toIndentedString(propertyName)).append("\n");
    sb.append("    isNegate: ").append(toIndentedString(isNegate)).append("\n");
    sb.append("    boolOperator: ").append(toIndentedString(boolOperator)).append("\n");
    sb.append("    expressionClauses: ").append(toIndentedString(expressionClauses)).append("\n");
    sb.append("    expression2Ops: ").append(toIndentedString(expression2Ops)).append("\n");
    sb.append("    expressionBooleans: ").append(toIndentedString(expressionBooleans)).append("\n");
    sb.append("    expressionBetweens: ").append(toIndentedString(expressionBetweens)).append("\n");
    sb.append("    expressionIns: ").append(toIndentedString(expressionIns)).append("\n");
    sb.append("    expressionIsNulls: ").append(toIndentedString(expressionIsNulls)).append("\n");
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

