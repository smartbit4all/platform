package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An instantiated widget on a form refering its descriptor and containing the widget instance data.
 */
@ApiModel(description = "An instantiated widget on a form refering its descriptor and containing the widget instance data.")

public class WidgetInstance   {
  @JsonProperty("descriptorUri")
  private URI descriptorUri;

  @JsonProperty("stringValues")
  @Valid
  private List<String> stringValues = null;

  @JsonProperty("intValues")
  @Valid
  private List<Integer> intValues = null;

  @JsonProperty("doubleValues")
  @Valid
  private List<BigDecimal> doubleValues = null;

  public WidgetInstance descriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
    return this;
  }

  /**
   * The reference to the widget descriptor.
   * @return descriptorUri
  */
  @ApiModelProperty(value = "The reference to the widget descriptor.")

  @Valid

  public URI getDescriptorUri() {
    return descriptorUri;
  }

  public void setDescriptorUri(URI descriptorUri) {
    this.descriptorUri = descriptorUri;
  }

  public WidgetInstance stringValues(List<String> stringValues) {
    this.stringValues = stringValues;
    return this;
  }

  public WidgetInstance addStringValuesItem(String stringValuesItem) {
    if (this.stringValues == null) {
      this.stringValues = new ArrayList<>();
    }
    this.stringValues.add(stringValuesItem);
    return this;
  }

  /**
   * Array of the string values that the widget contains.
   * @return stringValues
  */
  @ApiModelProperty(value = "Array of the string values that the widget contains.")


  public List<String> getStringValues() {
    return stringValues;
  }

  public void setStringValues(List<String> stringValues) {
    this.stringValues = stringValues;
  }

  public WidgetInstance intValues(List<Integer> intValues) {
    this.intValues = intValues;
    return this;
  }

  public WidgetInstance addIntValuesItem(Integer intValuesItem) {
    if (this.intValues == null) {
      this.intValues = new ArrayList<>();
    }
    this.intValues.add(intValuesItem);
    return this;
  }

  /**
   * Array of the integer values that the widget contains.
   * @return intValues
  */
  @ApiModelProperty(value = "Array of the integer values that the widget contains.")


  public List<Integer> getIntValues() {
    return intValues;
  }

  public void setIntValues(List<Integer> intValues) {
    this.intValues = intValues;
  }

  public WidgetInstance doubleValues(List<BigDecimal> doubleValues) {
    this.doubleValues = doubleValues;
    return this;
  }

  public WidgetInstance addDoubleValuesItem(BigDecimal doubleValuesItem) {
    if (this.doubleValues == null) {
      this.doubleValues = new ArrayList<>();
    }
    this.doubleValues.add(doubleValuesItem);
    return this;
  }

  /**
   * Array of the double values that the widget contains.
   * @return doubleValues
  */
  @ApiModelProperty(value = "Array of the double values that the widget contains.")

  @Valid

  public List<BigDecimal> getDoubleValues() {
    return doubleValues;
  }

  public void setDoubleValues(List<BigDecimal> doubleValues) {
    this.doubleValues = doubleValues;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WidgetInstance widgetInstance = (WidgetInstance) o;
    return Objects.equals(this.descriptorUri, widgetInstance.descriptorUri) &&
        Objects.equals(this.stringValues, widgetInstance.stringValues) &&
        Objects.equals(this.intValues, widgetInstance.intValues) &&
        Objects.equals(this.doubleValues, widgetInstance.doubleValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptorUri, stringValues, intValues, doubleValues);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WidgetInstance {\n");
    
    sb.append("    descriptorUri: ").append(toIndentedString(descriptorUri)).append("\n");
    sb.append("    stringValues: ").append(toIndentedString(stringValues)).append("\n");
    sb.append("    intValues: ").append(toIndentedString(intValues)).append("\n");
    sb.append("    doubleValues: ").append(toIndentedString(doubleValues)).append("\n");
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

