package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The meta data of the detail inside an entity. It defines the operation mode of the multiple instance management ui. (like a grid or something...)
 */
@ApiModel(description = "The meta data of the detail inside an entity. It defines the operation mode of the multiple instance management ui. (like a grid or something...)")

public class DetailWidgetDescriptor   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("name")
  private String name;

  @JsonProperty("label")
  private String label;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("entityUri")
  private URI entityUri;

  public DetailWidgetDescriptor uri(URI uri) {
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

  public DetailWidgetDescriptor name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DetailWidgetDescriptor label(String label) {
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

  public DetailWidgetDescriptor icon(String icon) {
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

  public DetailWidgetDescriptor entityUri(URI entityUri) {
    this.entityUri = entityUri;
    return this;
  }

  /**
   * Get entityUri
   * @return entityUri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getEntityUri() {
    return entityUri;
  }

  public void setEntityUri(URI entityUri) {
    this.entityUri = entityUri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DetailWidgetDescriptor detailWidgetDescriptor = (DetailWidgetDescriptor) o;
    return Objects.equals(this.uri, detailWidgetDescriptor.uri) &&
        Objects.equals(this.name, detailWidgetDescriptor.name) &&
        Objects.equals(this.label, detailWidgetDescriptor.label) &&
        Objects.equals(this.icon, detailWidgetDescriptor.icon) &&
        Objects.equals(this.entityUri, detailWidgetDescriptor.entityUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, label, icon, entityUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DetailWidgetDescriptor {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    entityUri: ").append(toIndentedString(entityUri)).append("\n");
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

