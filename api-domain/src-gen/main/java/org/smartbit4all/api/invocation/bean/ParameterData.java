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
 * The parameter of a given method. 
 */
@ApiModel(description = "The parameter of a given method. ")
@JsonPropertyOrder({
  ParameterData.NAME,
  ParameterData.TYPE_NAME
})
@JsonTypeName("ParameterData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ParameterData {
  public static final String NAME = "name";
  private String name;

  public static final String TYPE_NAME = "typeName";
  private String typeName;


  public ParameterData name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Unique name of the parameter.
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "Unique name of the parameter.")
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


  public ParameterData typeName(String typeName) {
    
    this.typeName = typeName;
    return this;
  }

   /**
   * The fully qualified name of the parameter data.
   * @return typeName
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The fully qualified name of the parameter data.")
  @JsonProperty(TYPE_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getTypeName() {
    return typeName;
  }


  @JsonProperty(TYPE_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParameterData parameterData = (ParameterData) o;
    return Objects.equals(this.name, parameterData.name) &&
        Objects.equals(this.typeName, parameterData.typeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, typeName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ParameterData {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    typeName: ").append(toIndentedString(typeName)).append("\n");
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
