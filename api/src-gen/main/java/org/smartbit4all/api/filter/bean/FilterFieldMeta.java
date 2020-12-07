package org.smartbit4all.api.filter.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Descriptor of a possible filter field. This field doesn&#39;t have to correspond to an existing
 * entity&#39;s property, it is simple a way of filtering.
 */
@ApiModel(
    description = "Descriptor of a possible filter field. This field doesn't have to correspond to an existing entity's property, it is simple a way of filtering.")

public class FilterFieldMeta {
  @JsonProperty("name")
  private String name;

  @JsonProperty("groupName")
  private String groupName;

  @JsonProperty("style")
  private String style;

  @JsonProperty("operations")
  @Valid
  private List<FilterOperation> operations = null;

  public FilterFieldMeta name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the filterable field.
   * 
   * @return name
   */
  @ApiModelProperty(value = "Name of the filterable field.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FilterFieldMeta groupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  /**
   * Get groupName
   * 
   * @return groupName
   */
  @ApiModelProperty(value = "")


  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public FilterFieldMeta style(String style) {
    this.style = style;
    return this;
  }

  /**
   * Get style
   * 
   * @return style
   */
  @ApiModelProperty(value = "")


  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public FilterFieldMeta operations(List<FilterOperation> operations) {
    this.operations = operations;
    return this;
  }

  public FilterFieldMeta addOperationsItem(FilterOperation operationsItem) {
    if (this.operations == null) {
      this.operations = new ArrayList<>();
    }
    this.operations.add(operationsItem);
    return this;
  }

  /**
   * Get operations
   * 
   * @return operations
   */
  @ApiModelProperty(value = "")

  @Valid

  public List<FilterOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<FilterOperation> operations) {
    this.operations = operations;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterFieldMeta filterFieldMeta = (FilterFieldMeta) o;
    return Objects.equals(this.name, filterFieldMeta.name) &&
        Objects.equals(this.groupName, filterFieldMeta.groupName) &&
        Objects.equals(this.style, filterFieldMeta.style) &&
        Objects.equals(this.operations, filterFieldMeta.operations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, groupName, style, operations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterFieldMeta {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    groupName: ").append(toIndentedString(groupName)).append("\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
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

