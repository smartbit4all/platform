package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterGroupLabel
 */

public class FilterGroupLabel   {
  @JsonProperty("labelCode")
  private String labelCode;

  @JsonProperty("iconCode")
  private String iconCode;

  public FilterGroupLabel labelCode(String labelCode) {
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

  public FilterGroupLabel iconCode(String iconCode) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterGroupLabel filterGroupLabel = (FilterGroupLabel) o;
    return Objects.equals(this.labelCode, filterGroupLabel.labelCode) &&
        Objects.equals(this.iconCode, filterGroupLabel.iconCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(labelCode, iconCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterGroupLabel {\n");
    
    sb.append("    labelCode: ").append(toIndentedString(labelCode)).append("\n");
    sb.append("    iconCode: ").append(toIndentedString(iconCode)).append("\n");
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

