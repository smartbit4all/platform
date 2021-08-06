package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
  private List<String> stringValues = new ArrayList<>();

  @JsonProperty("intValues")
  @Valid
  private List<Integer> intValues = new ArrayList<>();

  @JsonProperty("doubleValues")
  @Valid
  private List<java.lang.Double> doubleValues = new ArrayList<>();

  @JsonProperty("dateValues")
  @Valid
  private List<java.time.LocalDateTime> dateValues = new ArrayList<>();

  @JsonProperty("binaryDataValues")
  @Valid
  private List<org.smartbit4all.types.binarydata.BinaryData> binaryDataValues = new ArrayList<>();

  @JsonProperty("widgets")
  @Valid
  private List<WidgetInstance> widgets = null;

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
    this.stringValues.add(stringValuesItem);
    return this;
  }

  /**
   * Array of the string values that the widget contains.
   * @return stringValues
  */
  @ApiModelProperty(required = true, value = "Array of the string values that the widget contains.")
  @NotNull


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
    this.intValues.add(intValuesItem);
    return this;
  }

  /**
   * Array of the integer values that the widget contains.
   * @return intValues
  */
  @ApiModelProperty(required = true, value = "Array of the integer values that the widget contains.")
  @NotNull


  public List<Integer> getIntValues() {
    return intValues;
  }

  public void setIntValues(List<Integer> intValues) {
    this.intValues = intValues;
  }

  public WidgetInstance doubleValues(List<java.lang.Double> doubleValues) {
    this.doubleValues = doubleValues;
    return this;
  }

  public WidgetInstance addDoubleValuesItem(java.lang.Double doubleValuesItem) {
    this.doubleValues.add(doubleValuesItem);
    return this;
  }

  /**
   * Array of the double values that the widget contains.
   * @return doubleValues
  */
  @ApiModelProperty(required = true, value = "Array of the double values that the widget contains.")
  @NotNull

  @Valid

  public List<java.lang.Double> getDoubleValues() {
    return doubleValues;
  }

  public void setDoubleValues(List<java.lang.Double> doubleValues) {
    this.doubleValues = doubleValues;
  }

  public WidgetInstance dateValues(List<java.time.LocalDateTime> dateValues) {
    this.dateValues = dateValues;
    return this;
  }

  public WidgetInstance addDateValuesItem(java.time.LocalDateTime dateValuesItem) {
    this.dateValues.add(dateValuesItem);
    return this;
  }

  /**
   * Array of the date values that the widget contains.
   * @return dateValues
  */
  @ApiModelProperty(required = true, value = "Array of the date values that the widget contains.")
  @NotNull

  @Valid

  public List<java.time.LocalDateTime> getDateValues() {
    return dateValues;
  }

  public void setDateValues(List<java.time.LocalDateTime> dateValues) {
    this.dateValues = dateValues;
  }

  public WidgetInstance binaryDataValues(List<org.smartbit4all.types.binarydata.BinaryData> binaryDataValues) {
    this.binaryDataValues = binaryDataValues;
    return this;
  }

  public WidgetInstance addBinaryDataValuesItem(org.smartbit4all.types.binarydata.BinaryData binaryDataValuesItem) {
    this.binaryDataValues.add(binaryDataValuesItem);
    return this;
  }

  /**
   * Array of the binary data values that the widget contains.
   * @return binaryDataValues
  */
  @ApiModelProperty(required = true, value = "Array of the binary data values that the widget contains.")
  @NotNull

  @Valid

  public List<org.smartbit4all.types.binarydata.BinaryData> getBinaryDataValues() {
    return binaryDataValues;
  }

  public void setBinaryDataValues(List<org.smartbit4all.types.binarydata.BinaryData> binaryDataValues) {
    this.binaryDataValues = binaryDataValues;
  }

  public WidgetInstance widgets(List<WidgetInstance> widgets) {
    this.widgets = widgets;
    return this;
  }

  public WidgetInstance addWidgetsItem(WidgetInstance widgetsItem) {
    if (this.widgets == null) {
      this.widgets = new ArrayList<>();
    }
    this.widgets.add(widgetsItem);
    return this;
  }

  /**
   * Array of the contained widget instances.
   * @return widgets
  */
  @ApiModelProperty(value = "Array of the contained widget instances.")

  @Valid

  public List<WidgetInstance> getWidgets() {
    return widgets;
  }

  public void setWidgets(List<WidgetInstance> widgets) {
    this.widgets = widgets;
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
        Objects.equals(this.doubleValues, widgetInstance.doubleValues) &&
        Objects.equals(this.dateValues, widgetInstance.dateValues) &&
        Objects.equals(this.binaryDataValues, widgetInstance.binaryDataValues) &&
        Objects.equals(this.widgets, widgetInstance.widgets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptorUri, stringValues, intValues, doubleValues, dateValues, binaryDataValues, widgets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WidgetInstance {\n");
    
    sb.append("    descriptorUri: ").append(toIndentedString(descriptorUri)).append("\n");
    sb.append("    stringValues: ").append(toIndentedString(stringValues)).append("\n");
    sb.append("    intValues: ").append(toIndentedString(intValues)).append("\n");
    sb.append("    doubleValues: ").append(toIndentedString(doubleValues)).append("\n");
    sb.append("    dateValues: ").append(toIndentedString(dateValues)).append("\n");
    sb.append("    binaryDataValues: ").append(toIndentedString(binaryDataValues)).append("\n");
    sb.append("    widgets: ").append(toIndentedString(widgets)).append("\n");
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

