/*
 * Filter API 2
 * Filter API 2
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.filterexpression.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderGroup;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The filter field model contains a list of groups that form a tree structure for the available filter fields. It contains the groups of available fields and the workplace root list that conatins the already added filters. On the builder side  we refer the unique identifiers of the field lists and fields. 
 */
@ApiModel(description = "The filter field model contains a list of groups that form a tree structure for the available filter fields. It contains the groups of available fields and the workplace root list that conatins the already added filters. On the builder side  we refer the unique identifiers of the field lists and fields. ")
@JsonPropertyOrder({
  FilterExpressionBuilderModel.LABEL,
  FilterExpressionBuilderModel.GROUPS,
  FilterExpressionBuilderModel.WORKPLACE_LIST
})
@JsonTypeName("FilterExpressionBuilderModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterExpressionBuilderModel {
  public static final String LABEL = "label";
  private String label;

  public static final String GROUPS = "groups";
  private List<FilterExpressionBuilderGroup> groups = null;

  public static final String WORKPLACE_LIST = "workplaceList";
  private FilterExpressionFieldList workplaceList;

  public FilterExpressionBuilderModel() { 
  }

  public FilterExpressionBuilderModel label(String label) {
    
    this.label = label;
    return this;
  }

   /**
   * Get label
   * @return label
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLabel() {
    return label;
  }


  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLabel(String label) {
    this.label = label;
  }


  public FilterExpressionBuilderModel groups(List<FilterExpressionBuilderGroup> groups) {
    
    this.groups = groups;
    return this;
  }

  public FilterExpressionBuilderModel addGroupsItem(FilterExpressionBuilderGroup groupsItem) {
    if (this.groups == null) {
      this.groups = new ArrayList<>();
    }
    this.groups.add(groupsItem);
    return this;
  }

   /**
   * Get groups
   * @return groups
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(GROUPS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<FilterExpressionBuilderGroup> getGroups() {
    return groups;
  }


  @JsonProperty(GROUPS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setGroups(List<FilterExpressionBuilderGroup> groups) {
    this.groups = groups;
  }


  public FilterExpressionBuilderModel workplaceList(FilterExpressionFieldList workplaceList) {
    
    this.workplaceList = workplaceList;
    return this;
  }

   /**
   * Get workplaceList
   * @return workplaceList
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(WORKPLACE_LIST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionFieldList getWorkplaceList() {
    return workplaceList;
  }


  @JsonProperty(WORKPLACE_LIST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setWorkplaceList(FilterExpressionFieldList workplaceList) {
    this.workplaceList = workplaceList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpressionBuilderModel filterExpressionBuilderModel = (FilterExpressionBuilderModel) o;
    return Objects.equals(this.label, filterExpressionBuilderModel.label) &&
        Objects.equals(this.groups, filterExpressionBuilderModel.groups) &&
        Objects.equals(this.workplaceList, filterExpressionBuilderModel.workplaceList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, groups, workplaceList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpressionBuilderModel {\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    workplaceList: ").append(toIndentedString(workplaceList)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
