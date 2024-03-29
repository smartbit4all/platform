/*
 * Navigation API
 * Navigation API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.ui.api.navigation.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.ui.api.navigation.model.MessageResultType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * MessageResult
 */
@JsonPropertyOrder({
  MessageResult.CODE,
  MessageResult.LABEL,
  MessageResult.ICON,
  MessageResult.TYPE
})
@JsonTypeName("MessageResult")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MessageResult {
  public static final String CODE = "code";
  private String code;

  public static final String LABEL = "label";
  private String label;

  public static final String ICON = "icon";
  private String icon;

  public static final String TYPE = "type";
  private MessageResultType type;

  public MessageResult() { 
  }

  public MessageResult code(String code) {
    
    this.code = code;
    return this;
  }

   /**
   * Get code
   * @return code
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
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


  public MessageResult label(String label) {
    
    this.label = label;
    return this;
  }

   /**
   * Get label
   * @return label
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLabel() {
    return label;
  }


  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLabel(String label) {
    this.label = label;
  }


  public MessageResult icon(String icon) {
    
    this.icon = icon;
    return this;
  }

   /**
   * Get icon
   * @return icon
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
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


  public MessageResult type(MessageResultType type) {
    
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public MessageResultType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setType(MessageResultType type) {
    this.type = type;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageResult messageResult = (MessageResult) o;
    return Objects.equals(this.code, messageResult.code) &&
        Objects.equals(this.label, messageResult.label) &&
        Objects.equals(this.icon, messageResult.icon) &&
        Objects.equals(this.type, messageResult.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, label, icon, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageResult {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

