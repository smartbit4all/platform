package org.smartbit4all.api.filter.bean;

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

public class FilterGroup   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("icon")
  private String icon;

  @JsonProperty("metaName")
  private String metaName;

  @JsonProperty("type")
  private FilterGroupType type;

  @JsonProperty("filterFields")
  @Valid
  private List<FilterField> filterFields = null;

  @JsonProperty("filterGroups")
  @Valid
  private List<FilterGroup> filterGroups = null;

  @JsonProperty("isNegated")
  private Boolean isNegated;

  @JsonProperty("isExpanded")
  private Boolean isExpanded;

  public FilterGroup name(String name) {
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

  public FilterGroup icon(String icon) {
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

  public FilterGroup metaName(String metaName) {
    this.metaName = metaName;
    return this;
  }

  /**
   * Name of the referenced FilterGroupMeta, if this group is created from any.
   * @return metaName
  */
  @ApiModelProperty(value = "Name of the referenced FilterGroupMeta, if this group is created from any.")


  public String getMetaName() {
    return metaName;
  }

  public void setMetaName(String metaName) {
    this.metaName = metaName;
  }

  public FilterGroup type(FilterGroupType type) {
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

  public FilterGroup filterFields(List<FilterField> filterFields) {
    this.filterFields = filterFields;
    return this;
  }

  public FilterGroup addFilterFieldsItem(FilterField filterFieldsItem) {
    if (this.filterFields == null) {
      this.filterFields = new ArrayList<>();
    }
    this.filterFields.add(filterFieldsItem);
    return this;
  }

  /**
   * Get filterFields
   * @return filterFields
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterField> getFilterFields() {
    return filterFields;
  }

  public void setFilterFields(List<FilterField> filterFields) {
    this.filterFields = filterFields;
  }

  public FilterGroup filterGroups(List<FilterGroup> filterGroups) {
    this.filterGroups = filterGroups;
    return this;
  }

  public FilterGroup addFilterGroupsItem(FilterGroup filterGroupsItem) {
    if (this.filterGroups == null) {
      this.filterGroups = new ArrayList<>();
    }
    this.filterGroups.add(filterGroupsItem);
    return this;
  }

  /**
   * Get filterGroups
   * @return filterGroups
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterGroup> getFilterGroups() {
    return filterGroups;
  }

  public void setFilterGroups(List<FilterGroup> filterGroups) {
    this.filterGroups = filterGroups;
  }

  public FilterGroup isNegated(Boolean isNegated) {
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

  public FilterGroup isExpanded(Boolean isExpanded) {
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
    FilterGroup filterGroup = (FilterGroup) o;
    return Objects.equals(this.name, filterGroup.name) &&
        Objects.equals(this.icon, filterGroup.icon) &&
        Objects.equals(this.metaName, filterGroup.metaName) &&
        Objects.equals(this.type, filterGroup.type) &&
        Objects.equals(this.filterFields, filterGroup.filterFields) &&
        Objects.equals(this.filterGroups, filterGroup.filterGroups) &&
        Objects.equals(this.isNegated, filterGroup.isNegated) &&
        Objects.equals(this.isExpanded, filterGroup.isExpanded);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, icon, metaName, type, filterFields, filterGroups, isNegated, isExpanded);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroup {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    metaName: ").append(toIndentedString(metaName)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    filterFields: ").append(toIndentedString(filterFields)).append("\n");
    sb.append("    filterGroups: ").append(toIndentedString(filterGroups)).append("\n");
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

