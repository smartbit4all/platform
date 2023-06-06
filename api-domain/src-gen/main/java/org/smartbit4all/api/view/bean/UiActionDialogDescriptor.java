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
import org.smartbit4all.api.view.bean.UiActionButtonDescriptor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Describes the look of a dialog that is related to a specific UiAction. 
 */
@ApiModel(description = "Describes the look of a dialog that is related to a specific UiAction. ")
@JsonPropertyOrder({
  UiActionDialogDescriptor.TITLE,
  UiActionDialogDescriptor.PLACEHOLDER,
  UiActionDialogDescriptor.TEXT,
  UiActionDialogDescriptor.MASK,
  UiActionDialogDescriptor.ACTION_BUTTON,
  UiActionDialogDescriptor.CANCEL_BUTTON
})
@JsonTypeName("UiActionDialogDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class UiActionDialogDescriptor {
  public static final String TITLE = "title";
  private String title;

  public static final String PLACEHOLDER = "placeholder";
  private String placeholder;

  public static final String TEXT = "text";
  private String text;

  public static final String MASK = "mask";
  private String mask;

  public static final String ACTION_BUTTON = "actionButton";
  private UiActionButtonDescriptor actionButton;

  public static final String CANCEL_BUTTON = "cancelButton";
  private UiActionButtonDescriptor cancelButton;

  public UiActionDialogDescriptor() { 
  }

  public UiActionDialogDescriptor title(String title) {
    
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(TITLE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getTitle() {
    return title;
  }


  @JsonProperty(TITLE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setTitle(String title) {
    this.title = title;
  }


  public UiActionDialogDescriptor placeholder(String placeholder) {
    
    this.placeholder = placeholder;
    return this;
  }

   /**
   * Get placeholder
   * @return placeholder
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PLACEHOLDER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPlaceholder() {
    return placeholder;
  }


  @JsonProperty(PLACEHOLDER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }


  public UiActionDialogDescriptor text(String text) {
    
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


  public UiActionDialogDescriptor mask(String mask) {
    
    this.mask = mask;
    return this;
  }

   /**
   * Get mask
   * @return mask
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(MASK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getMask() {
    return mask;
  }


  @JsonProperty(MASK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMask(String mask) {
    this.mask = mask;
  }


  public UiActionDialogDescriptor actionButton(UiActionButtonDescriptor actionButton) {
    
    this.actionButton = actionButton;
    return this;
  }

   /**
   * Get actionButton
   * @return actionButton
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(ACTION_BUTTON)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public UiActionButtonDescriptor getActionButton() {
    return actionButton;
  }


  @JsonProperty(ACTION_BUTTON)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setActionButton(UiActionButtonDescriptor actionButton) {
    this.actionButton = actionButton;
  }


  public UiActionDialogDescriptor cancelButton(UiActionButtonDescriptor cancelButton) {
    
    this.cancelButton = cancelButton;
    return this;
  }

   /**
   * Get cancelButton
   * @return cancelButton
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CANCEL_BUTTON)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public UiActionButtonDescriptor getCancelButton() {
    return cancelButton;
  }


  @JsonProperty(CANCEL_BUTTON)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCancelButton(UiActionButtonDescriptor cancelButton) {
    this.cancelButton = cancelButton;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UiActionDialogDescriptor uiActionDialogDescriptor = (UiActionDialogDescriptor) o;
    return Objects.equals(this.title, uiActionDialogDescriptor.title) &&
        Objects.equals(this.placeholder, uiActionDialogDescriptor.placeholder) &&
        Objects.equals(this.text, uiActionDialogDescriptor.text) &&
        Objects.equals(this.mask, uiActionDialogDescriptor.mask) &&
        Objects.equals(this.actionButton, uiActionDialogDescriptor.actionButton) &&
        Objects.equals(this.cancelButton, uiActionDialogDescriptor.cancelButton);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, placeholder, text, mask, actionButton, cancelButton);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UiActionDialogDescriptor {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    placeholder: ").append(toIndentedString(placeholder)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    mask: ").append(toIndentedString(mask)).append("\n");
    sb.append("    actionButton: ").append(toIndentedString(actionButton)).append("\n");
    sb.append("    cancelButton: ").append(toIndentedString(cancelButton)).append("\n");
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

