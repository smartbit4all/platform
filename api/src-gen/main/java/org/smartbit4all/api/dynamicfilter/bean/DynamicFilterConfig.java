package org.smartbit4all.api.dynamicfilter.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * DynamicFilterConfig
 */

public class DynamicFilterConfig   {
  @JsonProperty("dynamicFilterMetas")
  @Valid
  private List<DynamicFilterMeta> dynamicFilterMetas = null;

  @JsonProperty("dynamicFilterGroupMetas")
  @Valid
  private List<DynamicFilterGroupMeta> dynamicFilterGroupMetas = null;

  public DynamicFilterConfig dynamicFilterMetas(List<DynamicFilterMeta> dynamicFilterMetas) {
    this.dynamicFilterMetas = dynamicFilterMetas;
    return this;
  }

  public DynamicFilterConfig addDynamicFilterMetasItem(DynamicFilterMeta dynamicFilterMetasItem) {
    if (this.dynamicFilterMetas == null) {
      this.dynamicFilterMetas = new ArrayList<>();
    }
    this.dynamicFilterMetas.add(dynamicFilterMetasItem);
    return this;
  }

  /**
   * Get dynamicFilterMetas
   * @return dynamicFilterMetas
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<DynamicFilterMeta> getDynamicFilterMetas() {
    return dynamicFilterMetas;
  }

  public void setDynamicFilterMetas(List<DynamicFilterMeta> dynamicFilterMetas) {
    this.dynamicFilterMetas = dynamicFilterMetas;
  }

  public DynamicFilterConfig dynamicFilterGroupMetas(List<DynamicFilterGroupMeta> dynamicFilterGroupMetas) {
    this.dynamicFilterGroupMetas = dynamicFilterGroupMetas;
    return this;
  }

  public DynamicFilterConfig addDynamicFilterGroupMetasItem(DynamicFilterGroupMeta dynamicFilterGroupMetasItem) {
    if (this.dynamicFilterGroupMetas == null) {
      this.dynamicFilterGroupMetas = new ArrayList<>();
    }
    this.dynamicFilterGroupMetas.add(dynamicFilterGroupMetasItem);
    return this;
  }

  /**
   * Get dynamicFilterGroupMetas
   * @return dynamicFilterGroupMetas
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<DynamicFilterGroupMeta> getDynamicFilterGroupMetas() {
    return dynamicFilterGroupMetas;
  }

  public void setDynamicFilterGroupMetas(List<DynamicFilterGroupMeta> dynamicFilterGroupMetas) {
    this.dynamicFilterGroupMetas = dynamicFilterGroupMetas;
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
    return Objects.equals(this.dynamicFilterMetas, dynamicFilterConfig.dynamicFilterMetas) &&
        Objects.equals(this.dynamicFilterGroupMetas, dynamicFilterConfig.dynamicFilterGroupMetas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dynamicFilterMetas, dynamicFilterGroupMetas);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterConfig {\n");
    
    sb.append("    dynamicFilterMetas: ").append(toIndentedString(dynamicFilterMetas)).append("\n");
    sb.append("    dynamicFilterGroupMetas: ").append(toIndentedString(dynamicFilterGroupMetas)).append("\n");
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

