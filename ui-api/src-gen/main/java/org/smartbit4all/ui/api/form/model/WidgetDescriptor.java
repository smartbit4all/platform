package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.WidgetType;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Represents a widget for the EntityFormDescriptor. It might have many types and all of them must be impemented by the form display engine.
 */
@ApiModel(description = "Represents a widget for the EntityFormDescriptor. It might have many types and all of them must be impemented by the form display engine.")

public class WidgetDescriptor   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("label")
  private String label;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("propertyUris")
  @Valid
  private List<URI> propertyUris = null;

  @JsonProperty("formDescriptorUri")
  private URI formDescriptorUri;

  @JsonProperty("widgetType")
  private WidgetType widgetType;

  public WidgetDescriptor uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public WidgetDescriptor label(String label) {
    this.label = label;
    return this;
  }

  /**
   * The label of the widget on the default language (english) that can be translated by the platform.
   * @return label
  */
  @ApiModelProperty(value = "The label of the widget on the default language (english) that can be translated by the platform.")


  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public WidgetDescriptor icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * The icon code for the icon to show. The display must be implemented in the form display engine.
   * @return icon
  */
  @ApiModelProperty(value = "The icon code for the icon to show. The display must be implemented in the form display engine.")


  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public WidgetDescriptor propertyUris(List<URI> propertyUris) {
    this.propertyUris = propertyUris;
    return this;
  }

  public WidgetDescriptor addPropertyUrisItem(URI propertyUrisItem) {
    if (this.propertyUris == null) {
      this.propertyUris = new ArrayList<>();
    }
    this.propertyUris.add(propertyUrisItem);
    return this;
  }

  /**
   * The list of property uris for the widget we have. Could be one or more depending on the widget type.
   * @return propertyUris
  */
  @ApiModelProperty(value = "The list of property uris for the widget we have. Could be one or more depending on the widget type.")

  @Valid

  public List<URI> getPropertyUris() {
    return propertyUris;
  }

  public void setPropertyUris(List<URI> propertyUris) {
    this.propertyUris = propertyUris;
  }

  public WidgetDescriptor formDescriptorUri(URI formDescriptorUri) {
    this.formDescriptorUri = formDescriptorUri;
    return this;
  }

  /**
   * The uri of the EntityFoirmDescriptor related to the given widget. In case of reference or detail it can must be set to identify the form to show when constructing a new instance.
   * @return formDescriptorUri
  */
  @ApiModelProperty(value = "The uri of the EntityFoirmDescriptor related to the given widget. In case of reference or detail it can must be set to identify the form to show when constructing a new instance.")

  @Valid

  public URI getFormDescriptorUri() {
    return formDescriptorUri;
  }

  public void setFormDescriptorUri(URI formDescriptorUri) {
    this.formDescriptorUri = formDescriptorUri;
  }

  public WidgetDescriptor widgetType(WidgetType widgetType) {
    this.widgetType = widgetType;
    return this;
  }

  /**
   * Get widgetType
   * @return widgetType
  */
  @ApiModelProperty(value = "")

  @Valid

  public WidgetType getWidgetType() {
    return widgetType;
  }

  public void setWidgetType(WidgetType widgetType) {
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
    WidgetDescriptor widgetDescriptor = (WidgetDescriptor) o;
    return Objects.equals(this.uri, widgetDescriptor.uri) &&
        Objects.equals(this.label, widgetDescriptor.label) &&
        Objects.equals(this.icon, widgetDescriptor.icon) &&
        Objects.equals(this.propertyUris, widgetDescriptor.propertyUris) &&
        Objects.equals(this.formDescriptorUri, widgetDescriptor.formDescriptorUri) &&
        Objects.equals(this.widgetType, widgetDescriptor.widgetType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, label, icon, propertyUris, formDescriptorUri, widgetType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WidgetDescriptor {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    propertyUris: ").append(toIndentedString(propertyUris)).append("\n");
    sb.append("    formDescriptorUri: ").append(toIndentedString(formDescriptorUri)).append("\n");
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

