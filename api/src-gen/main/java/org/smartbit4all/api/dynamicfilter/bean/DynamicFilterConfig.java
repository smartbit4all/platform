package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterDescriptor;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterGroupDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DynamicFilterConfig
 */

public class DynamicFilterConfig   {
  @JsonProperty("dynamicFilterDescriptors")
  @Valid
  private List<DynamicFilterDescriptor> dynamicFilterDescriptors = null;

  @JsonProperty("dynamicFilterGroupDescriptors")
  @Valid
  private List<DynamicFilterGroupDescriptor> dynamicFilterGroupDescriptors = null;

  public DynamicFilterConfig dynamicFilterDescriptors(List<DynamicFilterDescriptor> dynamicFilterDescriptors) {
    this.dynamicFilterDescriptors = dynamicFilterDescriptors;
    return this;
  }

  public DynamicFilterConfig addDynamicFilterDescriptorsItem(DynamicFilterDescriptor dynamicFilterDescriptorsItem) {
    if (this.dynamicFilterDescriptors == null) {
      this.dynamicFilterDescriptors = new ArrayList<>();
    }
    this.dynamicFilterDescriptors.add(dynamicFilterDescriptorsItem);
    return this;
  }

  /**
   * Get dynamicFilterDescriptors
   * @return dynamicFilterDescriptors
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<DynamicFilterDescriptor> getDynamicFilterDescriptors() {
    return dynamicFilterDescriptors;
  }

  public void setDynamicFilterDescriptors(List<DynamicFilterDescriptor> dynamicFilterDescriptors) {
    this.dynamicFilterDescriptors = dynamicFilterDescriptors;
  }

  public DynamicFilterConfig dynamicFilterGroupDescriptors(List<DynamicFilterGroupDescriptor> dynamicFilterGroupDescriptors) {
    this.dynamicFilterGroupDescriptors = dynamicFilterGroupDescriptors;
    return this;
  }

  public DynamicFilterConfig addDynamicFilterGroupDescriptorsItem(DynamicFilterGroupDescriptor dynamicFilterGroupDescriptorsItem) {
    if (this.dynamicFilterGroupDescriptors == null) {
      this.dynamicFilterGroupDescriptors = new ArrayList<>();
    }
    this.dynamicFilterGroupDescriptors.add(dynamicFilterGroupDescriptorsItem);
    return this;
  }

  /**
   * Get dynamicFilterGroupDescriptors
   * @return dynamicFilterGroupDescriptors
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<DynamicFilterGroupDescriptor> getDynamicFilterGroupDescriptors() {
    return dynamicFilterGroupDescriptors;
  }

  public void setDynamicFilterGroupDescriptors(List<DynamicFilterGroupDescriptor> dynamicFilterGroupDescriptors) {
    this.dynamicFilterGroupDescriptors = dynamicFilterGroupDescriptors;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterConfig dynamicFilterConfig = (DynamicFilterConfig) o;
    return Objects.equals(this.dynamicFilterDescriptors, dynamicFilterConfig.dynamicFilterDescriptors) &&
        Objects.equals(this.dynamicFilterGroupDescriptors, dynamicFilterConfig.dynamicFilterGroupDescriptors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dynamicFilterDescriptors, dynamicFilterGroupDescriptors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterConfig {\n");
    
    sb.append("    dynamicFilterDescriptors: ").append(toIndentedString(dynamicFilterDescriptors)).append("\n");
    sb.append("    dynamicFilterGroupDescriptors: ").append(toIndentedString(dynamicFilterGroupDescriptors)).append("\n");
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

