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

public class FilterGroup {
  @JsonProperty("name")
  private String name;

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

  public FilterGroup name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the group.
   * 
   * @return name
   */
  @ApiModelProperty(value = "Name of the group.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FilterGroup type(FilterGroupType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * 
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
   * 
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
   * 
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
   * 
   * @return isNegated
   */
  @ApiModelProperty(value = "")


  public Boolean getIsNegated() {
    return isNegated;
  }

  public void setIsNegated(Boolean isNegated) {
    this.isNegated = isNegated;
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
        Objects.equals(this.type, filterGroup.type) &&
        Objects.equals(this.filterFields, filterGroup.filterFields) &&
        Objects.equals(this.filterGroups, filterGroup.filterGroups) &&
        Objects.equals(this.isNegated, filterGroup.isNegated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, filterFields, filterGroups, isNegated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroup {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    filterFields: ").append(toIndentedString(filterFields)).append("\n");
    sb.append("    filterGroups: ").append(toIndentedString(filterGroups)).append("\n");
    sb.append("    isNegated: ").append(toIndentedString(isNegated)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

