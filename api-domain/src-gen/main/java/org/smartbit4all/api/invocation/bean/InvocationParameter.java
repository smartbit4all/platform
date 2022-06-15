/*
 * invocation api
 * The invocation api is a generic possibility to call remote apis.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.invocation.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * InvocationParameter
 */
@JsonPropertyOrder({
  InvocationParameter.NAME,
  InvocationParameter.VALUE,
  InvocationParameter.TYPE_CLASS
})
@JsonTypeName("InvocationParameter")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class InvocationParameter {
  public static final String NAME = "name";
  private String name;

  public static final String VALUE = "value";
  private Object value;

  public static final String TYPE_CLASS = "typeClass";
  private String typeClass;


  public InvocationParameter name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The symbolic name of the parameter. Must be set and unique inside an invocation request template.
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The symbolic name of the parameter. Must be set and unique inside an invocation request template.")
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


  public InvocationParameter value(Object value) {
    
    this.value = value;
    return this;
  }

   /**
   * The value of the object.
   * @return value
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The value of the object.")
  @JsonProperty(VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getValue() {
    return value;
  }


  @JsonProperty(VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValue(Object value) {
    this.value = value;
  }


  public InvocationParameter typeClass(String typeClass) {
    
    this.typeClass = typeClass;
    return this;
  }

   /**
   * The fully qualified type class name of the parameter.
   * @return typeClass
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The fully qualified type class name of the parameter.")
  @JsonProperty(TYPE_CLASS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getTypeClass() {
    return typeClass;
  }


  @JsonProperty(TYPE_CLASS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setTypeClass(String typeClass) {
    this.typeClass = typeClass;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationParameter invocationParameter = (InvocationParameter) o;
    return Objects.equals(this.name, invocationParameter.name) &&
        Objects.equals(this.value, invocationParameter.value) &&
        Objects.equals(this.typeClass, invocationParameter.typeClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, typeClass);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationParameter {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    typeClass: ").append(toIndentedString(typeClass)).append("\n");
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
