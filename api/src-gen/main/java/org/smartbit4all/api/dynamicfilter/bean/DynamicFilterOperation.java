package org.smartbit4all.api.dynamicfilter.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Operations supported by dynamic filter api.
 */
@ApiModel(description = "Operations supported by dynamic filter api.")

public class DynamicFilterOperation   {
  @JsonProperty("code")
  private String code;

  @JsonProperty("displayValue")
  private String displayValue;

  @JsonProperty("icon")
  private String icon;

  public DynamicFilterOperation code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  */
  @ApiModelProperty(value = "")


  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public DynamicFilterOperation displayValue(String displayValue) {
    this.displayValue = displayValue;
    return this;
  }

  /**
   * Get displayValue
   * @return displayValue
  */
  @ApiModelProperty(value = "")


  public String getDisplayValue() {
    return displayValue;
  }

  public void setDisplayValue(String displayValue) {
    this.displayValue = displayValue;
  }

  public DynamicFilterOperation icon(String icon) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterOperation dynamicFilterOperation = (DynamicFilterOperation) o;
    return Objects.equals(this.code, dynamicFilterOperation.code) &&
        Objects.equals(this.displayValue, dynamicFilterOperation.displayValue) &&
        Objects.equals(this.icon, dynamicFilterOperation.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, displayValue, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterOperation {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    displayValue: ").append(toIndentedString(displayValue)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
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

