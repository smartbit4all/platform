/*
 * value api
 * value api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.value.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetExpression;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * ValueSetOperand
 */
@JsonPropertyOrder({
  ValueSetOperand.NAMESPACE,
  ValueSetOperand.NAME,
  ValueSetOperand.DATA,
  ValueSetOperand.EXPRESSION
})
@JsonTypeName("ValueSetOperand")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ValueSetOperand {
  public static final String NAMESPACE = "namespace";
  private String namespace;

  public static final String NAME = "name";
  private String name;

  public static final String DATA = "data";
  private ValueSetDefinitionData data;

  public static final String EXPRESSION = "expression";
  private ValueSetExpression expression;

  public ValueSetOperand() { 
  }

  public ValueSetOperand namespace(String namespace) {
    
    this.namespace = namespace;
    return this;
  }

   /**
   * The namespace of the referred value set. 
   * @return namespace
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The namespace of the referred value set. ")
  @JsonProperty(NAMESPACE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getNamespace() {
    return namespace;
  }


  @JsonProperty(NAMESPACE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }


  public ValueSetOperand name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The name of the referred value set. 
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The name of the referred value set. ")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setName(String name) {
    this.name = name;
  }


  public ValueSetOperand data(ValueSetDefinitionData data) {
    
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ValueSetDefinitionData getData() {
    return data;
  }


  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setData(ValueSetDefinitionData data) {
    this.data = data;
  }


  public ValueSetOperand expression(ValueSetExpression expression) {
    
    this.expression = expression;
    return this;
  }

   /**
   * Get expression
   * @return expression
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(EXPRESSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ValueSetExpression getExpression() {
    return expression;
  }


  @JsonProperty(EXPRESSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpression(ValueSetExpression expression) {
    this.expression = expression;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValueSetOperand valueSetOperand = (ValueSetOperand) o;
    return Objects.equals(this.namespace, valueSetOperand.namespace) &&
        Objects.equals(this.name, valueSetOperand.name) &&
        Objects.equals(this.data, valueSetOperand.data) &&
        Objects.equals(this.expression, valueSetOperand.expression);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespace, name, data, expression);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValueSetOperand {\n");
    sb.append("    namespace: ").append(toIndentedString(namespace)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    expression: ").append(toIndentedString(expression)).append("\n");
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

