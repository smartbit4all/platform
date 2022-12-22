/*
 * View API
 * View API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.view.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.view.bean.UiActionInputType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * UiAction
 */
@JsonPropertyOrder({
  UiAction.CODE,
  UiAction.IDENTIFIER,
  UiAction.INPUT_TYPE
})
@JsonTypeName("UiAction")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class UiAction {
  public static final String CODE = "code";
  private String code;

  public static final String IDENTIFIER = "identifier";
  private String identifier;

  public static final String INPUT_TYPE = "inputType";
  private UiActionInputType inputType = UiActionInputType.NONE;

  public UiAction() { 
  }

  public UiAction code(String code) {
    
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


  public UiAction identifier(String identifier) {
    
    this.identifier = identifier;
    return this;
  }

   /**
   * Get identifier
   * @return identifier
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(IDENTIFIER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIdentifier() {
    return identifier;
  }


  @JsonProperty(IDENTIFIER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  public UiAction inputType(UiActionInputType inputType) {
    
    this.inputType = inputType;
    return this;
  }

   /**
   * Get inputType
   * @return inputType
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(INPUT_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionInputType getInputType() {
    return inputType;
  }


  @JsonProperty(INPUT_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInputType(UiActionInputType inputType) {
    this.inputType = inputType;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UiAction uiAction = (UiAction) o;
    return Objects.equals(this.code, uiAction.code) &&
        Objects.equals(this.identifier, uiAction.identifier) &&
        Objects.equals(this.inputType, uiAction.inputType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, identifier, inputType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UiAction {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    inputType: ").append(toIndentedString(inputType)).append("\n");
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

