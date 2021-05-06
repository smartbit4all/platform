package org.smartbit4all.api.filter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filter.bean.FilterGroupMeta;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterConfig
 */

public class FilterConfig   {
  @JsonProperty("filterGroupMetas")
  @Valid
  private List<FilterGroupMeta> filterGroupMetas = null;

  @JsonProperty("defaultFilterStyle")
  private String defaultFilterStyle;

  @JsonProperty("defaultFilterGroupStyle")
  private String defaultFilterGroupStyle;

  public FilterConfig filterGroupMetas(List<FilterGroupMeta> filterGroupMetas) {
    this.filterGroupMetas = filterGroupMetas;
    return this;
  }

  public FilterConfig addFilterGroupMetasItem(FilterGroupMeta filterGroupMetasItem) {
    if (this.filterGroupMetas == null) {
      this.filterGroupMetas = new ArrayList<>();
    }
    this.filterGroupMetas.add(filterGroupMetasItem);
    return this;
  }

  /**
   * Get filterGroupMetas
   * @return filterGroupMetas
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterGroupMeta> getFilterGroupMetas() {
    return filterGroupMetas;
  }

  public void setFilterGroupMetas(List<FilterGroupMeta> filterGroupMetas) {
    this.filterGroupMetas = filterGroupMetas;
  }

  public FilterConfig defaultFilterStyle(String defaultFilterStyle) {
    this.defaultFilterStyle = defaultFilterStyle;
    return this;
  }

  /**
   * Get defaultFilterStyle
   * @return defaultFilterStyle
  */
  @ApiModelProperty(value = "")


  public String getDefaultFilterStyle() {
    return defaultFilterStyle;
  }

  public void setDefaultFilterStyle(String defaultFilterStyle) {
    this.defaultFilterStyle = defaultFilterStyle;
  }

  public FilterConfig defaultFilterGroupStyle(String defaultFilterGroupStyle) {
    this.defaultFilterGroupStyle = defaultFilterGroupStyle;
    return this;
  }

  /**
   * Get defaultFilterGroupStyle
   * @return defaultFilterGroupStyle
  */
  @ApiModelProperty(value = "")


  public String getDefaultFilterGroupStyle() {
    return defaultFilterGroupStyle;
  }

  public void setDefaultFilterGroupStyle(String defaultFilterGroupStyle) {
    this.defaultFilterGroupStyle = defaultFilterGroupStyle;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterConfig filterConfig = (FilterConfig) o;
    return Objects.equals(this.filterGroupMetas, filterConfig.filterGroupMetas) &&
        Objects.equals(this.defaultFilterStyle, filterConfig.defaultFilterStyle) &&
        Objects.equals(this.defaultFilterGroupStyle, filterConfig.defaultFilterGroupStyle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterGroupMetas, defaultFilterStyle, defaultFilterGroupStyle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterConfig {\n");
    
    sb.append("    filterGroupMetas: ").append(toIndentedString(filterGroupMetas)).append("\n");
    sb.append("    defaultFilterStyle: ").append(toIndentedString(defaultFilterStyle)).append("\n");
    sb.append("    defaultFilterGroupStyle: ").append(toIndentedString(defaultFilterGroupStyle)).append("\n");
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

