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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The resolver is responsibe for defining the data uri for a given invocation parameter.
 */
@ApiModel(description = "The resolver is responsibe for defining the data uri for a given invocation parameter.")
@JsonPropertyOrder({
  InvocationParameterResolver.NAME,
  InvocationParameterResolver.POSITION,
  InvocationParameterResolver.DATA_URI
})
@JsonTypeName("InvocationParameterResolver")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class InvocationParameterResolver {
  public static final String NAME = "name";
  private String name;

  public static final String POSITION = "position";
  private Integer position;

  public static final String DATA_URI = "dataUri";
  private URI dataUri;

  public InvocationParameterResolver() { 
  }

  public InvocationParameterResolver name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  public InvocationParameterResolver position(Integer position) {
    
    this.position = position;
    return this;
  }

   /**
   * Get position
   * @return position
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(POSITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getPosition() {
    return position;
  }


  @JsonProperty(POSITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPosition(Integer position) {
    this.position = position;
  }


  public InvocationParameterResolver dataUri(URI dataUri) {
    
    this.dataUri = dataUri;
    return this;
  }

   /**
   * The uri of the data entry with the scheme as the name of the related object. The path defines the data access path. We can use this URI to resolve the data with the ObjectApi. 
   * @return dataUri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The uri of the data entry with the scheme as the name of the related object. The path defines the data access path. We can use this URI to resolve the data with the ObjectApi. ")
  @JsonProperty(DATA_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getDataUri() {
    return dataUri;
  }


  @JsonProperty(DATA_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDataUri(URI dataUri) {
    this.dataUri = dataUri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvocationParameterResolver invocationParameterResolver = (InvocationParameterResolver) o;
    return Objects.equals(this.name, invocationParameterResolver.name) &&
        Objects.equals(this.position, invocationParameterResolver.position) &&
        Objects.equals(this.dataUri, invocationParameterResolver.dataUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, position, dataUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvocationParameterResolver {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
    sb.append("    dataUri: ").append(toIndentedString(dataUri)).append("\n");
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
