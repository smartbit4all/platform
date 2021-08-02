package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The form UI meta descriptor of an entity. This form descriptor contains a given layout reference to use while showing the data fields of the given entity.
 */
@ApiModel(description = "The form UI meta descriptor of an entity. This form descriptor contains a given layout reference to use while showing the data fields of the given entity.")

public class EntityFormDescriptor   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("predictiveLayout")
  private PredictiveInputGraphDescriptor predictiveLayout;

  @JsonProperty("widgets")
  @Valid
  private List<WidgetDescriptor> widgets = null;

  public EntityFormDescriptor uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The URI of this exact descriptor.
   * @return uri
  */
  @ApiModelProperty(value = "The URI of this exact descriptor.")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public EntityFormDescriptor predictiveLayout(PredictiveInputGraphDescriptor predictiveLayout) {
    this.predictiveLayout = predictiveLayout;
    return this;
  }

  /**
   * Get predictiveLayout
   * @return predictiveLayout
  */
  @ApiModelProperty(value = "")

  @Valid

  public PredictiveInputGraphDescriptor getPredictiveLayout() {
    return predictiveLayout;
  }

  public void setPredictiveLayout(PredictiveInputGraphDescriptor predictiveLayout) {
    this.predictiveLayout = predictiveLayout;
  }

  public EntityFormDescriptor widgets(List<WidgetDescriptor> widgets) {
    this.widgets = widgets;
    return this;
  }

  public EntityFormDescriptor addWidgetsItem(WidgetDescriptor widgetsItem) {
    if (this.widgets == null) {
      this.widgets = new ArrayList<>();
    }
    this.widgets.add(widgetsItem);
    return this;
  }

  /**
   * Get widgets
   * @return widgets
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<WidgetDescriptor> getWidgets() {
    return widgets;
  }

  public void setWidgets(List<WidgetDescriptor> widgets) {
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
    EntityFormDescriptor entityFormDescriptor = (EntityFormDescriptor) o;
    return Objects.equals(this.uri, entityFormDescriptor.uri) &&
        Objects.equals(this.predictiveLayout, entityFormDescriptor.predictiveLayout) &&
        Objects.equals(this.widgets, entityFormDescriptor.widgets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, predictiveLayout, widgets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EntityFormDescriptor {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    predictiveLayout: ").append(toIndentedString(predictiveLayout)).append("\n");
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

