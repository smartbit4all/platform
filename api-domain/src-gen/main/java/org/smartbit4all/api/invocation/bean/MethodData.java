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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.invocation.bean.ParameterData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The method of an api that can be called. 
 */
@ApiModel(description = "The method of an api that can be called. ")
@JsonPropertyOrder({
  MethodData.URI,
  MethodData.NAME,
  MethodData.RETURN_TYPE,
  MethodData.PARAMETERS
})
@JsonTypeName("MethodData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MethodData {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String RETURN_TYPE = "returnType";
  private String returnType;

  public static final String PARAMETERS = "parameters";
  private List<ParameterData> parameters = new ArrayList<>();


  public MethodData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public MethodData name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
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


  public MethodData returnType(String returnType) {
    
    this.returnType = returnType;
    return this;
  }

   /**
   * The qualified name of the type / class or structure. Migth be empty if there is no return value at all.
   * @return returnType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The qualified name of the type / class or structure. Migth be empty if there is no return value at all.")
  @JsonProperty(RETURN_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getReturnType() {
    return returnType;
  }


  @JsonProperty(RETURN_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }


  public MethodData parameters(List<ParameterData> parameters) {
    
    this.parameters = parameters;
    return this;
  }

  public MethodData addParametersItem(ParameterData parametersItem) {
    this.parameters.add(parametersItem);
    return this;
  }

   /**
   * The parameters of the given method.
   * @return parameters
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The parameters of the given method.")
  @JsonProperty(PARAMETERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<ParameterData> getParameters() {
    return parameters;
  }


  @JsonProperty(PARAMETERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setParameters(List<ParameterData> parameters) {
    this.parameters = parameters;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MethodData methodData = (MethodData) o;
    return Objects.equals(this.uri, methodData.uri) &&
        Objects.equals(this.name, methodData.name) &&
        Objects.equals(this.returnType, methodData.returnType) &&
        Objects.equals(this.parameters, methodData.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, returnType, parameters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MethodData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    returnType: ").append(toIndentedString(returnType)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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
