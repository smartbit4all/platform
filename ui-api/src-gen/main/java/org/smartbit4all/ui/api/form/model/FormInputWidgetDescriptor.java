package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Represents a descriptor, which contains the type of the widget and the URIs to the properties
 */
@ApiModel(description = "Represents a descriptor, which contains the type of the widget and the URIs to the properties")

public class FormInputWidgetDescriptor   {
  @JsonProperty("label")
  private String label;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("uris")
  @Valid
  private List<URI> uris = null;

  /**
   * The management code for the input widget relies on this enum.
   */
  public enum WidgetTypeEnum {
    TEXT("text"),
    
    TEXT_INTERVAL("text_interval"),
    
    COMBOBOX("combobox"),
    
    DATE("date"),
    
    DATE_INTERVAL("date_interval"),
    
    NUMBER("number"),
    
    NUMBER_INTERVAL("number_interval");

    private String value;

    WidgetTypeEnum(String value) {
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
    public static WidgetTypeEnum fromValue(String value) {
      for (WidgetTypeEnum b : WidgetTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("widgetType")
  private WidgetTypeEnum widgetType;

  public FormInputWidgetDescriptor label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  @ApiModelProperty(value = "")


  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public FormInputWidgetDescriptor icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Get icon
   * @return icon
  */
  @ApiModelProperty(value = "")


  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public FormInputWidgetDescriptor uris(List<URI> uris) {
    this.uris = uris;
    return this;
  }

  public FormInputWidgetDescriptor addUrisItem(URI urisItem) {
    if (this.uris == null) {
      this.uris = new ArrayList<>();
    }
    this.uris.add(urisItem);
    return this;
  }

  /**
   * The list of property uris for the widget we have. Could be one or more depending on the widget type.
   * @return uris
  */
  @ApiModelProperty(value = "The list of property uris for the widget we have. Could be one or more depending on the widget type.")

  @Valid

  public List<URI> getUris() {
    return uris;
  }

  public void setUris(List<URI> uris) {
    this.uris = uris;
  }

  public FormInputWidgetDescriptor widgetType(WidgetTypeEnum widgetType) {
    this.widgetType = widgetType;
    return this;
  }

  /**
   * The management code for the input widget relies on this enum.
   * @return widgetType
  */
  @ApiModelProperty(value = "The management code for the input widget relies on this enum.")


  public WidgetTypeEnum getWidgetType() {
    return widgetType;
  }

  public void setWidgetType(WidgetTypeEnum widgetType) {
    this.widgetType = widgetType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FormInputWidgetDescriptor formInputWidgetDescriptor = (FormInputWidgetDescriptor) o;
    return Objects.equals(this.label, formInputWidgetDescriptor.label) &&
        Objects.equals(this.icon, formInputWidgetDescriptor.icon) &&
        Objects.equals(this.uris, formInputWidgetDescriptor.uris) &&
        Objects.equals(this.widgetType, formInputWidgetDescriptor.widgetType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, icon, uris, widgetType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FormInputWidgetDescriptor {\n");
    
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    uris: ").append(toIndentedString(uris)).append("\n");
    sb.append("    widgetType: ").append(toIndentedString(widgetType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

