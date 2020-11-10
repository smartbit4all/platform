package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.dynamicfilter.bean.DynamicFilterOption;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Descriptor of a possible filter field. This field doesn&#39;t have to correspond to an existing entity&#39;s property, it is simple a way of filtering.
 */
@ApiModel(description = "Descriptor of a possible filter field. This field doesn't have to correspond to an existing entity's property, it is simple a way of filtering.")

public class DynamicFilterDescriptor   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("options")
  @Valid
  private List<DynamicFilterOption> options = null;

  public DynamicFilterDescriptor name(String name) {
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

  public DynamicFilterDescriptor options(List<DynamicFilterOption> options) {
    this.options = options;
    return this;
  }

  public DynamicFilterDescriptor addOptionsItem(DynamicFilterOption optionsItem) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(optionsItem);
    return this;
  }

  /**
   * Get options
   * @return options
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<DynamicFilterOption> getOptions() {
    return options;
  }

  public void setOptions(List<DynamicFilterOption> options) {
    this.options = options;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterDescriptor dynamicFilterDescriptor = (DynamicFilterDescriptor) o;
    return Objects.equals(this.name, dynamicFilterDescriptor.name) &&
        Objects.equals(this.options, dynamicFilterDescriptor.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, options);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterDescriptor {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
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

