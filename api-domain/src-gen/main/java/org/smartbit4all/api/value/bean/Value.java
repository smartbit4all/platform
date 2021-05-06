package org.smartbit4all.api.value.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Value
 */

public class Value   {
  @JsonProperty("objectUri")
  private URI objectUri;

  @JsonProperty("displayValue")
  private String displayValue;

  @JsonProperty("iconCode")
  private String iconCode;

  public Value objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  /**
   * The uri the object of the value can be accessed.
   * @return objectUri
  */
  @ApiModelProperty(required = true, value = "The uri the object of the value can be accessed.")
  @NotNull

  @Valid

  public URI getObjectUri() {
    return objectUri;
  }

  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }

  public Value displayValue(String displayValue) {
    this.displayValue = displayValue;
    return this;
  }

  /**
   * The string value that can be shown on ui. It might be a label code or the exact label.
   * @return displayValue
  */
  @ApiModelProperty(required = true, value = "The string value that can be shown on ui. It might be a label code or the exact label.")
  @NotNull


  public String getDisplayValue() {
    return displayValue;
  }

  public void setDisplayValue(String displayValue) {
    this.displayValue = displayValue;
  }

  public Value iconCode(String iconCode) {
    this.iconCode = iconCode;
    return this;
  }

  /**
   * The code of icon that can be associatied with the value. It may be a uri.
   * @return iconCode
  */
  @ApiModelProperty(value = "The code of icon that can be associatied with the value. It may be a uri.")


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
    Value value = (Value) o;
    return Objects.equals(this.objectUri, value.objectUri) &&
        Objects.equals(this.displayValue, value.displayValue) &&
        Objects.equals(this.iconCode, value.iconCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUri, displayValue, iconCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Value {\n");
    
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    displayValue: ").append(toIndentedString(displayValue)).append("\n");
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

