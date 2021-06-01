package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.ui.api.filter.model.FilterLabelPosition;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterFieldLabel
 */

public class FilterFieldLabel   {
  @JsonProperty("code")
  private String code;

  @JsonProperty("position")
  private FilterLabelPosition position;

  @JsonProperty("duplicateNum")
  private Integer duplicateNum = 0;

  public FilterFieldLabel code(String code) {
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

  public FilterFieldLabel position(FilterLabelPosition position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterLabelPosition getPosition() {
    return position;
  }

  public void setPosition(FilterLabelPosition position) {
    this.position = position;
  }

  public FilterFieldLabel duplicateNum(Integer duplicateNum) {
    this.duplicateNum = duplicateNum;
    return this;
  }

  /**
   * Get duplicateNum
   * @return duplicateNum
  */
  @ApiModelProperty(value = "")


  public Integer getDuplicateNum() {
    return duplicateNum;
  }

  public void setDuplicateNum(Integer duplicateNum) {
    this.duplicateNum = duplicateNum;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterFieldLabel filterFieldLabel = (FilterFieldLabel) o;
    return Objects.equals(this.code, filterFieldLabel.code) &&
        Objects.equals(this.position, filterFieldLabel.position) &&
        Objects.equals(this.duplicateNum, filterFieldLabel.duplicateNum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, position, duplicateNum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterFieldLabel {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
    sb.append("    duplicateNum: ").append(toIndentedString(duplicateNum)).append("\n");
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

