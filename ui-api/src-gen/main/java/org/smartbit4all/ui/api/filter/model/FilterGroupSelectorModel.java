package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.filter.model.FilterFieldSelectorModel;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterGroupSelectorModel
 */

public class FilterGroupSelectorModel   {
  @JsonProperty("labelCode")
  private String labelCode;

  @JsonProperty("iconCode")
  private String iconCode;

  @JsonProperty("groupType")
  private org.smartbit4all.api.filter.bean.FilterGroupType groupType = null;

  @JsonProperty("filters")
  @Valid
  private List<FilterFieldSelectorModel> filters = null;

  @JsonProperty("closeable")
  private Boolean closeable;

  @JsonProperty("visible")
  private Boolean visible;

  @JsonProperty("currentGroupPath")
  private String currentGroupPath;

  public FilterGroupSelectorModel labelCode(String labelCode) {
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

  public FilterGroupSelectorModel iconCode(String iconCode) {
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

  public FilterGroupSelectorModel groupType(org.smartbit4all.api.filter.bean.FilterGroupType groupType) {
    this.groupType = groupType;
    return this;
  }

  /**
   * Get groupType
   * @return groupType
  */
  @ApiModelProperty(value = "")

  @Valid

  public org.smartbit4all.api.filter.bean.FilterGroupType getGroupType() {
    return groupType;
  }

  public void setGroupType(org.smartbit4all.api.filter.bean.FilterGroupType groupType) {
    this.groupType = groupType;
  }

  public FilterGroupSelectorModel filters(List<FilterFieldSelectorModel> filters) {
    this.filters = filters;
    return this;
  }

  public FilterGroupSelectorModel addFiltersItem(FilterFieldSelectorModel filtersItem) {
    if (this.filters == null) {
      this.filters = new ArrayList<>();
    }
    this.filters.add(filtersItem);
    return this;
  }

  /**
   * Get filters
   * @return filters
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterFieldSelectorModel> getFilters() {
    return filters;
  }

  public void setFilters(List<FilterFieldSelectorModel> filters) {
    this.filters = filters;
  }

  public FilterGroupSelectorModel closeable(Boolean closeable) {
    this.closeable = closeable;
    return this;
  }

  /**
   * Get closeable
   * @return closeable
  */
  @ApiModelProperty(value = "")


  public Boolean getCloseable() {
    return closeable;
  }

  public void setCloseable(Boolean closeable) {
    this.closeable = closeable;
  }

  public FilterGroupSelectorModel visible(Boolean visible) {
    this.visible = visible;
    return this;
  }

  /**
   * Get visible
   * @return visible
  */
  @ApiModelProperty(value = "")


  public Boolean getVisible() {
    return visible;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public FilterGroupSelectorModel currentGroupPath(String currentGroupPath) {
    this.currentGroupPath = currentGroupPath;
    return this;
  }

  /**
   * Get currentGroupPath
   * @return currentGroupPath
  */
  @ApiModelProperty(value = "")


  public String getCurrentGroupPath() {
    return currentGroupPath;
  }

  public void setCurrentGroupPath(String currentGroupPath) {
    this.currentGroupPath = currentGroupPath;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterGroupSelectorModel filterGroupSelectorModel = (FilterGroupSelectorModel) o;
    return Objects.equals(this.labelCode, filterGroupSelectorModel.labelCode) &&
        Objects.equals(this.iconCode, filterGroupSelectorModel.iconCode) &&
        Objects.equals(this.groupType, filterGroupSelectorModel.groupType) &&
        Objects.equals(this.filters, filterGroupSelectorModel.filters) &&
        Objects.equals(this.closeable, filterGroupSelectorModel.closeable) &&
        Objects.equals(this.visible, filterGroupSelectorModel.visible) &&
        Objects.equals(this.currentGroupPath, filterGroupSelectorModel.currentGroupPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(labelCode, iconCode, groupType, filters, closeable, visible, currentGroupPath);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroupSelectorModel {\n");
    
    sb.append("    labelCode: ").append(toIndentedString(labelCode)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
    sb.append("    groupType: ").append(toIndentedString(groupType)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("    closeable: ").append(toIndentedString(closeable)).append("\n");
    sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
    sb.append("    currentGroupPath: ").append(toIndentedString(currentGroupPath)).append("\n");
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

