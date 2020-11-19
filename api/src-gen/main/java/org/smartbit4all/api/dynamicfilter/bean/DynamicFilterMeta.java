package org.smartbit4all.api.dynamicfilter.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Descriptor of a possible filter field. This field doesn&#39;t have to correspond to an existing entity&#39;s property, it is simple a way of filtering.
 */
@ApiModel(description = "Descriptor of a possible filter field. This field doesn't have to correspond to an existing entity's property, it is simple a way of filtering.")

public class DynamicFilterMeta   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("groupName")
  private String groupName;

  @JsonProperty("operations")
  @Valid
  private List<DynamicFilterOperation> operations = null;

  public DynamicFilterMeta name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the filterable field.
   * @return name
  */
  @ApiModelProperty(value = "Name of the filterable field.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DynamicFilterMeta groupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  /**
   * Get groupName
   * @return groupName
  */
  @ApiModelProperty(value = "")


  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public DynamicFilterMeta operations(List<DynamicFilterOperation> operations) {
    this.operations = operations;
    return this;
  }

  public DynamicFilterMeta addOperationsItem(DynamicFilterOperation operationsItem) {
    if (this.operations == null) {
      this.operations = new ArrayList<>();
    }
    this.operations.add(operationsItem);
    return this;
  }

  /**
   * Get operations
   * @return operations
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<DynamicFilterOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<DynamicFilterOperation> operations) {
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
    DynamicFilterMeta dynamicFilterMeta = (DynamicFilterMeta) o;
    return Objects.equals(this.name, dynamicFilterMeta.name) &&
        Objects.equals(this.groupName, dynamicFilterMeta.groupName) &&
        Objects.equals(this.operations, dynamicFilterMeta.operations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, groupName, operations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterMeta {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    groupName: ").append(toIndentedString(groupName)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
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

