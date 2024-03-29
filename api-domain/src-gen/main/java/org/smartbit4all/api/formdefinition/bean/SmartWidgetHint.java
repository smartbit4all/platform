/*
 * Form layout definition
 * Contains form layout definition objects.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.formdefinition.bean;

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
 * SmartWidgetHint
 */
@JsonPropertyOrder({
  SmartWidgetHint.TEXT,
  SmartWidgetHint.POSITION
})
@JsonTypeName("SmartWidgetHint")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SmartWidgetHint {
  public static final String TEXT = "text";
  private String text;

  /**
   * Gets or Sets position
   */
  public enum PositionEnum {
    LABEL("UNDER_LABEL"),
    
    INPUT("UNDER_INPUT");

    private String value;

    PositionEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PositionEnum fromValue(String value) {
      for (PositionEnum b : PositionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String POSITION = "position";
  private PositionEnum position;

  public SmartWidgetHint() { 
  }

  public SmartWidgetHint text(String text) {
    
    this.text = text;
    return this;
  }

   /**
   * Get text
   * @return text
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(TEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getText() {
    return text;
  }


  @JsonProperty(TEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setText(String text) {
    this.text = text;
  }


  public SmartWidgetHint position(PositionEnum position) {
    
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

  public PositionEnum getPosition() {
    return position;
  }


  @JsonProperty(POSITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPosition(PositionEnum position) {
    this.position = position;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SmartWidgetHint smartWidgetHint = (SmartWidgetHint) o;
    return Objects.equals(this.text, smartWidgetHint.text) &&
        Objects.equals(this.position, smartWidgetHint.position);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, position);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SmartWidgetHint {\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
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

