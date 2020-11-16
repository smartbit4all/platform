package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupType;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DynamicFilterGroupDescriptor
 */

public class DynamicFilterGroupDescriptor   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("type")
  private DynamicFilterGroupType type;

  @JsonProperty("isDefaultSelected")
  private Boolean isDefaultSelected;

  public DynamicFilterGroupDescriptor name(String name) {
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

  public DynamicFilterGroupDescriptor icon(String icon) {
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

  public DynamicFilterGroupDescriptor type(DynamicFilterGroupType type) {
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

  public DynamicFilterGroupDescriptor isDefaultSelected(Boolean isDefaultSelected) {
    this.isDefaultSelected = isDefaultSelected;
    return this;
  }

  /**
   * Get isDefaultSelected
   * @return isDefaultSelected
  */
  @ApiModelProperty(value = "")


  public Boolean getIsDefaultSelected() {
    return isDefaultSelected;
  }

  public void setIsDefaultSelected(Boolean isDefaultSelected) {
    this.isDefaultSelected = isDefaultSelected;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterGroupDescriptor dynamicFilterGroupDescriptor = (DynamicFilterGroupDescriptor) o;
    return Objects.equals(this.name, dynamicFilterGroupDescriptor.name) &&
        Objects.equals(this.icon, dynamicFilterGroupDescriptor.icon) &&
        Objects.equals(this.type, dynamicFilterGroupDescriptor.type) &&
        Objects.equals(this.isDefaultSelected, dynamicFilterGroupDescriptor.isDefaultSelected);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, icon, type, isDefaultSelected);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterGroupDescriptor {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    isDefaultSelected: ").append(toIndentedString(isDefaultSelected)).append("\n");
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

