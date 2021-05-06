package org.smartbit4all.api.filter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterFieldMeta;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterGroupMeta
 */

public class FilterGroupMeta   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("filterFieldMetas")
  @Valid
  private List<FilterFieldMeta> filterFieldMetas = null;

  @JsonProperty("labelCode")
  private String labelCode;

  @JsonProperty("iconCode")
  private String iconCode;

  @JsonProperty("style")
  private String style;

  @JsonProperty("type")
  private FilterGroupType type;

  public FilterGroupMeta id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of the filter group metadata. Not mandatory, specify only if in use.
   * @return id
  */
  @ApiModelProperty(value = "Identifier of the filter group metadata. Not mandatory, specify only if in use.")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FilterGroupMeta filterFieldMetas(List<FilterFieldMeta> filterFieldMetas) {
    this.filterFieldMetas = filterFieldMetas;
    return this;
  }

  public FilterGroupMeta addFilterFieldMetasItem(FilterFieldMeta filterFieldMetasItem) {
    if (this.filterFieldMetas == null) {
      this.filterFieldMetas = new ArrayList<>();
    }
    this.filterFieldMetas.add(filterFieldMetasItem);
    return this;
  }

  /**
   * Get filterFieldMetas
   * @return filterFieldMetas
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterFieldMeta> getFilterFieldMetas() {
    return filterFieldMetas;
  }

  public void setFilterFieldMetas(List<FilterFieldMeta> filterFieldMetas) {
    this.filterFieldMetas = filterFieldMetas;
  }

  public FilterGroupMeta labelCode(String labelCode) {
    this.labelCode = labelCode;
    return this;
  }

  /**
   * Get labelCode
   * @return labelCode
  */
  @ApiModelProperty(value = "")


  public String getLabelCode() {
    return labelCode;
  }

  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }

  public FilterGroupMeta iconCode(String iconCode) {
    this.iconCode = iconCode;
    return this;
  }

  /**
   * Get iconCode
   * @return iconCode
  */
  @ApiModelProperty(value = "")


  public String getIconCode() {
    return iconCode;
  }

  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }

  public FilterGroupMeta style(String style) {
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

  public FilterGroupMeta type(FilterGroupType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterGroupType getType() {
    return type;
  }

  public void setType(FilterGroupType type) {
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
    FilterGroupMeta filterGroupMeta = (FilterGroupMeta) o;
    return Objects.equals(this.id, filterGroupMeta.id) &&
        Objects.equals(this.filterFieldMetas, filterGroupMeta.filterFieldMetas) &&
        Objects.equals(this.labelCode, filterGroupMeta.labelCode) &&
        Objects.equals(this.iconCode, filterGroupMeta.iconCode) &&
        Objects.equals(this.style, filterGroupMeta.style) &&
        Objects.equals(this.type, filterGroupMeta.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, filterFieldMetas, labelCode, iconCode, style, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroupMeta {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    filterFieldMetas: ").append(toIndentedString(filterFieldMetas)).append("\n");
    sb.append("    labelCode: ").append(toIndentedString(labelCode)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
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

