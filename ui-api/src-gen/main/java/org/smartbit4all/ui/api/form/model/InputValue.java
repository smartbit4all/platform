package org.smartbit4all.ui.api.form.model;

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
 * The value instance in the content.
 */
@ApiModel(description = "The value instance in the content.")

public class InputValue   {
  @JsonProperty("propertyUri")
  private URI propertyUri;

  @JsonProperty("propertyValue")
  private String propertyValue;

  public InputValue propertyUri(URI propertyUri) {
    this.propertyUri = propertyUri;
    return this;
  }

  /**
   * Get propertyUri
   * @return propertyUri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getPropertyUri() {
    return propertyUri;
  }

  public void setPropertyUri(URI propertyUri) {
    this.propertyUri = propertyUri;
  }

  public InputValue propertyValue(String propertyValue) {
    this.propertyValue = propertyValue;
    return this;
  }

  /**
   * Get propertyValue
   * @return propertyValue
  */
  @ApiModelProperty(value = "")


  public String getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(String propertyValue) {
    this.propertyValue = propertyValue;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InputValue inputValue = (InputValue) o;
    return Objects.equals(this.propertyUri, inputValue.propertyUri) &&
        Objects.equals(this.propertyValue, inputValue.propertyValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyUri, propertyValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InputValue {\n");
    
    sb.append("    propertyUri: ").append(toIndentedString(propertyUri)).append("\n");
    sb.append("    propertyValue: ").append(toIndentedString(propertyValue)).append("\n");
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
