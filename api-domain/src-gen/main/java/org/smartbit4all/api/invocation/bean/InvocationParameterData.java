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
import org.smartbit4all.api.invocation.bean.InvocationParameterKind;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * InvocationParameterData
 */
@JsonPropertyOrder({
  InvocationParameterData.KIND,
  InvocationParameterData.TYPE_CLASS,
  InvocationParameterData.VALUE
})
@JsonTypeName("InvocationParameterData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class InvocationParameterData {
  public static final String KIND = "kind";
  private InvocationParameterKind kind;

  public static final String TYPE_CLASS = "typeClass";
  private String typeClass;

  public static final String VALUE = "value";
  private String value;


  public InvocationParameterData kind(InvocationParameterKind kind) {
    
    this.kind = kind;
    return this;
  }

   /**
   * Get kind
   * @return kind
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public InvocationParameterKind getKind() {
    return kind;
  }


  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKind(InvocationParameterKind kind) {
    this.kind = kind;
  }


  public InvocationParameterData typeClass(String typeClass) {
    
    this.typeClass = typeClass;
    return this;
  }

   /**
   * The fully qualified type class name of the parameter.
   * @return typeClass
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The fully qualified type class name of the parameter.")
  @JsonProperty(TYPE_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTypeClass() {
    return typeClass;
  }


  @JsonProperty(TYPE_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTypeClass(String typeClass) {
    this.typeClass = typeClass;
  }


  public InvocationParameterData value(String value) {
    
    this.value = value;
    return this;
  }

   /**
   * The value of the property
   * @return value
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The value of the property")
  @JsonProperty(VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getValue() {
    return value;
  }


  @JsonProperty(VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationParameterData invocationParameterData = (InvocationParameterData) o;
    return Objects.equals(this.kind, invocationParameterData.kind) &&
        Objects.equals(this.typeClass, invocationParameterData.typeClass) &&
        Objects.equals(this.value, invocationParameterData.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, typeClass, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationParameterData {\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    typeClass: ").append(toIndentedString(typeClass)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

