package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * DynamicFilterGroupMeta
 */

public class DynamicFilterGroupMeta   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("style")
  private String style;

  @JsonProperty("type")
  private DynamicFilterGroupType type;

  public DynamicFilterGroupMeta name(String name) {
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

  public DynamicFilterGroupMeta icon(String icon) {
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

  public DynamicFilterGroupMeta style(String style) {
    this.style = style;
    return this;
  }

  /**
   * Get style
   * @return style
  */
  @ApiModelProperty(value = "")


  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public DynamicFilterGroupMeta type(DynamicFilterGroupType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @ApiModelProperty(value = "")

  @Valid

  public DynamicFilterGroupType getType() {
    return type;
  }

  public void setType(DynamicFilterGroupType type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterGroupMeta dynamicFilterGroupMeta = (DynamicFilterGroupMeta) o;
    return Objects.equals(this.name, dynamicFilterGroupMeta.name) &&
        Objects.equals(this.icon, dynamicFilterGroupMeta.icon) &&
        Objects.equals(this.style, dynamicFilterGroupMeta.style) &&
        Objects.equals(this.type, dynamicFilterGroupMeta.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, icon, style, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterGroupMeta {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

