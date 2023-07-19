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
  GenericValue.CAPTION,
  GenericValue.ICON
})
@JsonTypeName("GenericValue")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GenericValue {
  public static final String URI = "uri";
  private URI uri;

  public static final String CODE = "code";
  private String code;

  public static final String CAPTION = "caption";
  private String caption;

  public static final String ICON = "icon";
  private String icon;

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


  public GenericValue caption(String caption) {
    
    this.caption = caption;
    return this;
  }

   /**
   * The value as it is.
   * @return caption
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The value as it is.")
  @JsonProperty(CAPTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCaption() {
    return caption;
  }


  @JsonProperty(CAPTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCaption(String caption) {
    this.caption = caption;
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
        Objects.equals(this.caption, genericValue.caption) &&
        Objects.equals(this.icon, genericValue.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, code, caption, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenericValue {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    caption: ").append(toIndentedString(caption)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
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

