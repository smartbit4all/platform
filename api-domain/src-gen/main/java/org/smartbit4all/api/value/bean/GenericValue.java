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
import java.net.URI;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GenericValue
 */
@JsonPropertyOrder({
  GenericValue.URI,
  GenericValue.CODE,
  GenericValue.NAME,
  GenericValue.ICON,
  GenericValue.INACTIVE
})
@JsonTypeName("GenericValue")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GenericValue {
  public static final String URI = "uri";
  private URI uri;

  public static final String CODE = "code";
  private String code;

  public static final String NAME = "name";
  private String name;

  public static final String ICON = "icon";
  private String icon;

  public static final String INACTIVE = "inactive";
  private Boolean inactive;

  public GenericValue() { 
  }

  public GenericValue uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The uri the value.
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri the value.")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public GenericValue code(String code) {
    
    this.code = code;
    return this;
  }

   /**
   * The logical code of the value.
   * @return code
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The logical code of the value.")
  @JsonProperty(CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCode() {
    return code;
  }


  @JsonProperty(CODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCode(String code) {
    this.code = code;
  }


  public GenericValue name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The value as it is.
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The value as it is.")
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


  public GenericValue icon(String icon) {
    
    this.icon = icon;
    return this;
  }

   /**
   * The code of icon that can be associatied with this value. It may be a uri.
   * @return icon
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The code of icon that can be associatied with this value. It may be a uri.")
  @JsonProperty(ICON)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIcon() {
    return icon;
  }


  @JsonProperty(ICON)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIcon(String icon) {
    this.icon = icon;
  }


  public GenericValue inactive(Boolean inactive) {
    
    this.inactive = inactive;
    return this;
  }

   /**
   * Indicates if this value is inactive.
   * @return inactive
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Indicates if this value is inactive.")
  @JsonProperty(INACTIVE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getInactive() {
    return inactive;
  }


  @JsonProperty(INACTIVE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInactive(Boolean inactive) {
    this.inactive = inactive;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenericValue genericValue = (GenericValue) o;
    return Objects.equals(this.uri, genericValue.uri) &&
        Objects.equals(this.code, genericValue.code) &&
        Objects.equals(this.name, genericValue.name) &&
        Objects.equals(this.icon, genericValue.icon) &&
        Objects.equals(this.inactive, genericValue.inactive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, code, name, icon, inactive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenericValue {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    inactive: ").append(toIndentedString(inactive)).append("\n");
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

