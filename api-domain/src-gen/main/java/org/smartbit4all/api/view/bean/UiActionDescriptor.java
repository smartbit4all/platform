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
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDialogDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionTooltip;
import org.smartbit4all.api.view.bean.UiActionUploadDescriptor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Describes the appearance (as a button) and the properties of the related dialogs of a UiAction. 
 */
@ApiModel(description = "Describes the appearance (as a button) and the properties of the related dialogs of a UiAction. ")
@JsonPropertyOrder({
  UiActionDescriptor.TITLE,
  UiActionDescriptor.TYPE,
  UiActionDescriptor.COLOR,
  UiActionDescriptor.ICON,
  UiActionDescriptor.ICON_COLOR,
  UiActionDescriptor.ICON_POSITION,
  UiActionDescriptor.DIALOG,
  UiActionDescriptor.CONFIRM_DIALOG,
  UiActionDescriptor.INPUT_DIALOG,
  UiActionDescriptor.INPUT2_DIALOG,
  UiActionDescriptor.FEEDBACK_TYPE,
  UiActionDescriptor.FEEDBACK_TEXT,
  UiActionDescriptor.UPLOAD,
  UiActionDescriptor.TOOLTIP
})
@JsonTypeName("UiActionDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class UiActionDescriptor {
  public static final String TITLE = "title";
  private String title;

  public static final String TYPE = "type";
  private UiActionButtonType type;

  public static final String COLOR = "color";
  private String color;

  public static final String ICON = "icon";
  private String icon;

  public static final String ICON_COLOR = "iconColor";
  private String iconColor;

  public static final String ICON_POSITION = "iconPosition";
  private IconPosition iconPosition;

  public static final String DIALOG = "dialog";
  private UiActionDialogDescriptor dialog;

  public static final String CONFIRM_DIALOG = "confirmDialog";
  private UiActionDialogDescriptor confirmDialog;

  public static final String INPUT_DIALOG = "inputDialog";
  private UiActionDialogDescriptor inputDialog;

  public static final String INPUT2_DIALOG = "input2Dialog";
  private UiActionDialogDescriptor input2Dialog;

  public static final String FEEDBACK_TYPE = "feedbackType";
  private UiActionFeedbackType feedbackType;

  public static final String FEEDBACK_TEXT = "feedbackText";
  private String feedbackText;

  public static final String UPLOAD = "upload";
  private UiActionUploadDescriptor upload;

  public static final String TOOLTIP = "tooltip";
  private UiActionTooltip tooltip;

  public UiActionDescriptor() { 
  }

  public UiActionDescriptor title(String title) {
    
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


  public UiActionDescriptor type(UiActionButtonType type) {
    
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public UiActionButtonType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setType(UiActionButtonType type) {
    this.type = type;
  }


  public UiActionDescriptor color(String color) {
    
    this.color = color;
    return this;
  }

   /**
   * Get color
   * @return color
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(COLOR)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getColor() {
    return color;
  }


  @JsonProperty(COLOR)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setColor(String color) {
    this.color = color;
  }


  public UiActionDescriptor icon(String icon) {
    
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


  public UiActionDescriptor iconColor(String iconColor) {
    
    this.iconColor = iconColor;
    return this;
  }

   /**
   * Get iconColor
   * @return iconColor
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ICON_COLOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIconColor() {
    return iconColor;
  }


  @JsonProperty(ICON_COLOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIconColor(String iconColor) {
    this.iconColor = iconColor;
  }


  public UiActionDescriptor iconPosition(IconPosition iconPosition) {
    
    this.iconPosition = iconPosition;
    return this;
  }

   /**
   * Get iconPosition
   * @return iconPosition
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ICON_POSITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public IconPosition getIconPosition() {
    return iconPosition;
  }


  @JsonProperty(ICON_POSITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIconPosition(IconPosition iconPosition) {
    this.iconPosition = iconPosition;
  }


  public UiActionDescriptor dialog(UiActionDialogDescriptor dialog) {
    
    this.dialog = dialog;
    return this;
  }

   /**
   * Get dialog
   * @return dialog
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionDialogDescriptor getDialog() {
    return dialog;
  }


  @JsonProperty(DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDialog(UiActionDialogDescriptor dialog) {
    this.dialog = dialog;
  }


  public UiActionDescriptor confirmDialog(UiActionDialogDescriptor confirmDialog) {
    
    this.confirmDialog = confirmDialog;
    return this;
  }

   /**
   * Get confirmDialog
   * @return confirmDialog
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CONFIRM_DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionDialogDescriptor getConfirmDialog() {
    return confirmDialog;
  }


  @JsonProperty(CONFIRM_DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setConfirmDialog(UiActionDialogDescriptor confirmDialog) {
    this.confirmDialog = confirmDialog;
  }


  public UiActionDescriptor inputDialog(UiActionDialogDescriptor inputDialog) {
    
    this.inputDialog = inputDialog;
    return this;
  }

   /**
   * Get inputDialog
   * @return inputDialog
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(INPUT_DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionDialogDescriptor getInputDialog() {
    return inputDialog;
  }


  @JsonProperty(INPUT_DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInputDialog(UiActionDialogDescriptor inputDialog) {
    this.inputDialog = inputDialog;
  }


  public UiActionDescriptor input2Dialog(UiActionDialogDescriptor input2Dialog) {
    
    this.input2Dialog = input2Dialog;
    return this;
  }

   /**
   * Get input2Dialog
   * @return input2Dialog
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(INPUT2_DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionDialogDescriptor getInput2Dialog() {
    return input2Dialog;
  }


  @JsonProperty(INPUT2_DIALOG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInput2Dialog(UiActionDialogDescriptor input2Dialog) {
    this.input2Dialog = input2Dialog;
  }


  public UiActionDescriptor feedbackType(UiActionFeedbackType feedbackType) {
    
    this.feedbackType = feedbackType;
    return this;
  }

   /**
   * Get feedbackType
   * @return feedbackType
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(FEEDBACK_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionFeedbackType getFeedbackType() {
    return feedbackType;
  }


  @JsonProperty(FEEDBACK_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFeedbackType(UiActionFeedbackType feedbackType) {
    this.feedbackType = feedbackType;
  }


  public UiActionDescriptor feedbackText(String feedbackText) {
    
    this.feedbackText = feedbackText;
    return this;
  }

   /**
   * Get feedbackText
   * @return feedbackText
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(FEEDBACK_TEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFeedbackText() {
    return feedbackText;
  }


  @JsonProperty(FEEDBACK_TEXT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFeedbackText(String feedbackText) {
    this.feedbackText = feedbackText;
  }


  public UiActionDescriptor upload(UiActionUploadDescriptor upload) {
    
    this.upload = upload;
    return this;
  }

   /**
   * Get upload
   * @return upload
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(UPLOAD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionUploadDescriptor getUpload() {
    return upload;
  }


  @JsonProperty(UPLOAD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUpload(UiActionUploadDescriptor upload) {
    this.upload = upload;
  }


  public UiActionDescriptor tooltip(UiActionTooltip tooltip) {
    
    this.tooltip = tooltip;
    return this;
  }

   /**
   * Get tooltip
   * @return tooltip
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(TOOLTIP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UiActionTooltip getTooltip() {
    return tooltip;
  }


  @JsonProperty(TOOLTIP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTooltip(UiActionTooltip tooltip) {
    this.tooltip = tooltip;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UiActionDescriptor uiActionDescriptor = (UiActionDescriptor) o;
    return Objects.equals(this.title, uiActionDescriptor.title) &&
        Objects.equals(this.type, uiActionDescriptor.type) &&
        Objects.equals(this.color, uiActionDescriptor.color) &&
        Objects.equals(this.icon, uiActionDescriptor.icon) &&
        Objects.equals(this.iconColor, uiActionDescriptor.iconColor) &&
        Objects.equals(this.iconPosition, uiActionDescriptor.iconPosition) &&
        Objects.equals(this.dialog, uiActionDescriptor.dialog) &&
        Objects.equals(this.confirmDialog, uiActionDescriptor.confirmDialog) &&
        Objects.equals(this.inputDialog, uiActionDescriptor.inputDialog) &&
        Objects.equals(this.input2Dialog, uiActionDescriptor.input2Dialog) &&
        Objects.equals(this.feedbackType, uiActionDescriptor.feedbackType) &&
        Objects.equals(this.feedbackText, uiActionDescriptor.feedbackText) &&
        Objects.equals(this.upload, uiActionDescriptor.upload) &&
        Objects.equals(this.tooltip, uiActionDescriptor.tooltip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, type, color, icon, iconColor, iconPosition, dialog, confirmDialog, inputDialog, input2Dialog, feedbackType, feedbackText, upload, tooltip);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UiActionDescriptor {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    color: ").append(toIndentedString(color)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    iconColor: ").append(toIndentedString(iconColor)).append("\n");
    sb.append("    iconPosition: ").append(toIndentedString(iconPosition)).append("\n");
    sb.append("    dialog: ").append(toIndentedString(dialog)).append("\n");
    sb.append("    confirmDialog: ").append(toIndentedString(confirmDialog)).append("\n");
    sb.append("    inputDialog: ").append(toIndentedString(inputDialog)).append("\n");
    sb.append("    input2Dialog: ").append(toIndentedString(input2Dialog)).append("\n");
    sb.append("    feedbackType: ").append(toIndentedString(feedbackType)).append("\n");
    sb.append("    feedbackText: ").append(toIndentedString(feedbackText)).append("\n");
    sb.append("    upload: ").append(toIndentedString(upload)).append("\n");
    sb.append("    tooltip: ").append(toIndentedString(tooltip)).append("\n");
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

