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
import org.smartbit4all.api.view.bean.MessageOption;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * MessageResult
 */
@JsonPropertyOrder({
  MessageResult.SELECTED_OPTION,
  MessageResult.ADDITIONAL_DATA
})
@JsonTypeName("MessageResult")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MessageResult {
  public static final String SELECTED_OPTION = "selectedOption";
  private MessageOption selectedOption;

  public static final String ADDITIONAL_DATA = "additionalData";
  private Object additionalData;


  public MessageResult selectedOption(MessageOption selectedOption) {
    
    this.selectedOption = selectedOption;
    return this;
  }

   /**
   * Get selectedOption
   * @return selectedOption
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(SELECTED_OPTION)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public MessageOption getSelectedOption() {
    return selectedOption;
  }


  @JsonProperty(SELECTED_OPTION)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSelectedOption(MessageOption selectedOption) {
    this.selectedOption = selectedOption;
  }


  public MessageResult additionalData(Object additionalData) {
    
    this.additionalData = additionalData;
    return this;
  }

   /**
   * Get additionalData
   * @return additionalData
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ADDITIONAL_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getAdditionalData() {
    return additionalData;
  }


  @JsonProperty(ADDITIONAL_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAdditionalData(Object additionalData) {
    this.additionalData = additionalData;
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
    return Objects.equals(this.selectedOption, messageResult.selectedOption) &&
        Objects.equals(this.additionalData, messageResult.additionalData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectedOption, additionalData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageResult {\n");
    sb.append("    selectedOption: ").append(toIndentedString(selectedOption)).append("\n");
    sb.append("    additionalData: ").append(toIndentedString(additionalData)).append("\n");
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

