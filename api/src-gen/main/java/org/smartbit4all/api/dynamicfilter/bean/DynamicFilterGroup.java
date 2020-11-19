package org.smartbit4all.api.dynamicfilter.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * AND / OR group of filters / groups.
 */
@ApiModel(description = "AND / OR group of filters / groups.")

public class DynamicFilterGroup   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("metaName")
  private String metaName;

  @JsonProperty("type")
  private DynamicFilterGroupType type;

  @JsonProperty("filters")
  @Valid
  private List<DynamicFilter> filters = null;

  @JsonProperty("groups")
  @Valid
  private List<DynamicFilterGroup> groups = null;

  @JsonProperty("isNegated")
  private Boolean isNegated;

  @JsonProperty("isExpanded")
  private Boolean isExpanded;

  public DynamicFilterGroup name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the group.
   * @return name
  */
  @ApiModelProperty(value = "Name of the group.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DynamicFilterGroup icon(String icon) {
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

  public DynamicFilterGroup metaName(String metaName) {
    this.metaName = metaName;
    return this;
  }

  /**
   * Name of the referenced DynamicFilterGroupMeta, if this group is created from any.
   * @return metaName
  */
  @ApiModelProperty(value = "Name of the referenced DynamicFilterGroupMeta, if this group is created from any.")


  public String getMetaName() {
    return metaName;
  }

  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }

  public DynamicFilterGroup type(DynamicFilterGroupType type) {
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

  public DynamicFilterGroup filters(List<DynamicFilter> filters) {
    this.filters = filters;
    return this;
  }

  public DynamicFilterGroup addFiltersItem(DynamicFilter filtersItem) {
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

  public List<DynamicFilter> getFilters() {
    return filters;
  }

  public void setFilters(List<DynamicFilter> filters) {
    this.filters = filters;
  }

  public DynamicFilterGroup groups(List<DynamicFilterGroup> groups) {
    this.groups = groups;
    return this;
  }

  public DynamicFilterGroup addGroupsItem(DynamicFilterGroup groupsItem) {
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

  public List<DynamicFilterGroup> getGroups() {
    return groups;
  }

  public void setGroups(List<DynamicFilterGroup> groups) {
    this.groups = groups;
  }

  public DynamicFilterGroup isNegated(Boolean isNegated) {
    this.isNegated = isNegated;
    return this;
  }

  /**
   * Get isNegated
   * @return isNegated
  */
  @ApiModelProperty(value = "")


  public Boolean getIsNegated() {
    return isNegated;
  }

  public void setIsNegated(Boolean isNegated) {
    this.isNegated = isNegated;
  }

  public DynamicFilterGroup isExpanded(Boolean isExpanded) {
    this.isExpanded = isExpanded;
    return this;
  }

  /**
   * Get isExpanded
   * @return isExpanded
  */
  @ApiModelProperty(value = "")


  public Boolean getIsExpanded() {
    return isExpanded;
  }

  public void setIsExpanded(Boolean isExpanded) {
    this.isExpanded = isExpanded;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterGroup dynamicFilterGroup = (DynamicFilterGroup) o;
    return Objects.equals(this.name, dynamicFilterGroup.name) &&
        Objects.equals(this.icon, dynamicFilterGroup.icon) &&
        Objects.equals(this.metaName, dynamicFilterGroup.metaName) &&
        Objects.equals(this.type, dynamicFilterGroup.type) &&
        Objects.equals(this.filters, dynamicFilterGroup.filters) &&
        Objects.equals(this.groups, dynamicFilterGroup.groups) &&
        Objects.equals(this.isNegated, dynamicFilterGroup.isNegated) &&
        Objects.equals(this.isExpanded, dynamicFilterGroup.isExpanded);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, icon, metaName, type, filters, groups, isNegated, isExpanded);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterGroup {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    metaName: ").append(toIndentedString(metaName)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    isNegated: ").append(toIndentedString(isNegated)).append("\n");
    sb.append("    isExpanded: ").append(toIndentedString(isExpanded)).append("\n");
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

