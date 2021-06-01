package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterFieldSelectorModel
 */

public class FilterFieldSelectorModel   {
  @JsonProperty("labelCode")
  private String labelCode;

  @JsonProperty("iconCode")
  private String iconCode;

  @JsonProperty("style")
  private String style;

  @JsonProperty("operations")
  @Valid
  private List<org.smartbit4all.api.filter.bean.FilterOperation> operations = null;

  @JsonProperty("enabled")
  private Boolean enabled;

  public FilterFieldSelectorModel labelCode(String labelCode) {
    this.labelCode = labelCode;
    return this;
  }

  /**
   * Get labelCode
   * @return labelCode
  */
  @ApiModelProperty(value = "")


  public String getLabelCode() {
    return labelCode;
  }

  public void setLabelCode(String labelCode) {
    this.labelCode = labelCode;
  }

  public FilterFieldSelectorModel iconCode(String iconCode) {
    this.iconCode = iconCode;
    return this;
  }

  /**
   * Get iconCode
   * @return iconCode
  */
  @ApiModelProperty(value = "")


  public String getIconCode() {
    return iconCode;
  }

  public void setIconCode(String iconCode) {
    this.iconCode = iconCode;
  }

  public FilterFieldSelectorModel style(String style) {
    this.style = style;
    return this;
  }

  /**
   * Get style
   * @return style
  */
  @ApiModelProperty(value = "")


  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public FilterFieldSelectorModel operations(List<org.smartbit4all.api.filter.bean.FilterOperation> operations) {
    this.operations = operations;
    return this;
  }

  public FilterFieldSelectorModel addOperationsItem(org.smartbit4all.api.filter.bean.FilterOperation operationsItem) {
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

  public List<org.smartbit4all.api.filter.bean.FilterOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<org.smartbit4all.api.filter.bean.FilterOperation> operations) {
    this.operations = operations;
  }

  public FilterFieldSelectorModel enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
  */
  @ApiModelProperty(value = "")


  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterFieldSelectorModel filterFieldSelectorModel = (FilterFieldSelectorModel) o;
    return Objects.equals(this.labelCode, filterFieldSelectorModel.labelCode) &&
        Objects.equals(this.iconCode, filterFieldSelectorModel.iconCode) &&
        Objects.equals(this.style, filterFieldSelectorModel.style) &&
        Objects.equals(this.operations, filterFieldSelectorModel.operations) &&
        Objects.equals(this.enabled, filterFieldSelectorModel.enabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(labelCode, iconCode, style, operations, enabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterFieldSelectorModel {\n");
    
    sb.append("    labelCode: ").append(toIndentedString(labelCode)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
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

