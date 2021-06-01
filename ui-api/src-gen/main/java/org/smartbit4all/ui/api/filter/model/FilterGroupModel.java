package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.filter.model.FilterFieldModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupLabel;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterGroupModel
 */

public class FilterGroupModel   {
  @JsonProperty("groupType")
  private org.smartbit4all.api.filter.bean.FilterGroupType groupType = null;

  @JsonProperty("label")
  private FilterGroupLabel label;

  @JsonProperty("closeable")
  private Boolean closeable;

  @JsonProperty("root")
  private Boolean root;

  @JsonProperty("visible")
  private Boolean visible;

  @JsonProperty("active")
  private Boolean active;

  @JsonProperty("childGroupAllowed")
  private Boolean childGroupAllowed;

  @JsonProperty("groupTypeChangeEnabled")
  private Boolean groupTypeChangeEnabled;

  @JsonProperty("negated")
  private Boolean negated;

  @JsonProperty("groups")
  @Valid
  private List<FilterGroupModel> groups = null;

  @JsonProperty("filters")
  @Valid
  private List<FilterFieldModel> filters = null;

  public FilterGroupModel groupType(org.smartbit4all.api.filter.bean.FilterGroupType groupType) {
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

  public FilterGroupModel label(FilterGroupLabel label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterGroupLabel getLabel() {
    return label;
  }

  public void setLabel(FilterGroupLabel label) {
    this.label = label;
  }

  public FilterGroupModel closeable(Boolean closeable) {
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

  public FilterGroupModel root(Boolean root) {
    this.root = root;
    return this;
  }

  /**
   * Get root
   * @return root
  */
  @ApiModelProperty(value = "")


  public Boolean getRoot() {
    return root;
  }

  public void setRoot(Boolean root) {
    this.root = root;
  }

  public FilterGroupModel visible(Boolean visible) {
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

  public FilterGroupModel active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
  */
  @ApiModelProperty(value = "")


  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public FilterGroupModel childGroupAllowed(Boolean childGroupAllowed) {
    this.childGroupAllowed = childGroupAllowed;
    return this;
  }

  /**
   * Get childGroupAllowed
   * @return childGroupAllowed
  */
  @ApiModelProperty(value = "")


  public Boolean getChildGroupAllowed() {
    return childGroupAllowed;
  }

  public void setChildGroupAllowed(Boolean childGroupAllowed) {
    this.childGroupAllowed = childGroupAllowed;
  }

  public FilterGroupModel groupTypeChangeEnabled(Boolean groupTypeChangeEnabled) {
    this.groupTypeChangeEnabled = groupTypeChangeEnabled;
    return this;
  }

  /**
   * Get groupTypeChangeEnabled
   * @return groupTypeChangeEnabled
  */
  @ApiModelProperty(value = "")


  public Boolean getGroupTypeChangeEnabled() {
    return groupTypeChangeEnabled;
  }

  public void setGroupTypeChangeEnabled(Boolean groupTypeChangeEnabled) {
    this.groupTypeChangeEnabled = groupTypeChangeEnabled;
  }

  public FilterGroupModel negated(Boolean negated) {
    this.negated = negated;
    return this;
  }

  /**
   * Get negated
   * @return negated
  */
  @ApiModelProperty(value = "")


  public Boolean getNegated() {
    return negated;
  }

  public void setNegated(Boolean negated) {
    this.negated = negated;
  }

  public FilterGroupModel groups(List<FilterGroupModel> groups) {
    this.groups = groups;
    return this;
  }

  public FilterGroupModel addGroupsItem(FilterGroupModel groupsItem) {
    if (this.groups == null) {
      this.groups = new ArrayList<>();
    }
    this.groups.add(groupsItem);
    return this;
  }

  /**
   * Get groups
   * @return groups
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterGroupModel> getGroups() {
    return groups;
  }

  public void setGroups(List<FilterGroupModel> groups) {
    this.groups = groups;
  }

  public FilterGroupModel filters(List<FilterFieldModel> filters) {
    this.filters = filters;
    return this;
  }

  public FilterGroupModel addFiltersItem(FilterFieldModel filtersItem) {
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

  public List<FilterFieldModel> getFilters() {
    return filters;
  }

  public void setFilters(List<FilterFieldModel> filters) {
    this.filters = filters;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterGroupModel filterGroupModel = (FilterGroupModel) o;
    return Objects.equals(this.groupType, filterGroupModel.groupType) &&
        Objects.equals(this.label, filterGroupModel.label) &&
        Objects.equals(this.closeable, filterGroupModel.closeable) &&
        Objects.equals(this.root, filterGroupModel.root) &&
        Objects.equals(this.visible, filterGroupModel.visible) &&
        Objects.equals(this.active, filterGroupModel.active) &&
        Objects.equals(this.childGroupAllowed, filterGroupModel.childGroupAllowed) &&
        Objects.equals(this.groupTypeChangeEnabled, filterGroupModel.groupTypeChangeEnabled) &&
        Objects.equals(this.negated, filterGroupModel.negated) &&
        Objects.equals(this.groups, filterGroupModel.groups) &&
        Objects.equals(this.filters, filterGroupModel.filters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupType, label, closeable, root, visible, active, childGroupAllowed, groupTypeChangeEnabled, negated, groups, filters);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroupModel {\n");
    
    sb.append("    groupType: ").append(toIndentedString(groupType)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    closeable: ").append(toIndentedString(closeable)).append("\n");
    sb.append("    root: ").append(toIndentedString(root)).append("\n");
    sb.append("    visible: ").append(toIndentedString(visible)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    childGroupAllowed: ").append(toIndentedString(childGroupAllowed)).append("\n");
    sb.append("    groupTypeChangeEnabled: ").append(toIndentedString(groupTypeChangeEnabled)).append("\n");
    sb.append("    negated: ").append(toIndentedString(negated)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
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

