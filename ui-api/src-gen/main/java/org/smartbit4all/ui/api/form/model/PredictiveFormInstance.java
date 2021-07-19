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
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An instantiated layout of an EntityFormDescription that uses a predictive data graph as a layout definition. This instance is the current state of the layouting.
 */
@ApiModel(description = "An instantiated layout of an EntityFormDescription that uses a predictive data graph as a layout definition. This instance is the current state of the layouting.")

public class PredictiveFormInstance   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("availableWidgets")
  @Valid
  private List<WidgetDescriptor> availableWidgets = null;

  @JsonProperty("visibleWidgets")
  @Valid
  private List<WidgetInstance> visibleWidgets = null;

  @JsonProperty("graph")
  private PredictiveInputGraphDescriptor graph;

  public PredictiveFormInstance uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The URI of this exact instance.
   * @return uri
  */
  @ApiModelProperty(value = "The URI of this exact instance.")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public PredictiveFormInstance availableWidgets(List<WidgetDescriptor> availableWidgets) {
    this.availableWidgets = availableWidgets;
    return this;
  }

  public PredictiveFormInstance addAvailableWidgetsItem(WidgetDescriptor availableWidgetsItem) {
    if (this.availableWidgets == null) {
      this.availableWidgets = new ArrayList<>();
    }
    this.availableWidgets.add(availableWidgetsItem);
    return this;
  }

  /**
   * The array that contains the widgets that are currently available for selection.
   * @return availableWidgets
  */
  @ApiModelProperty(value = "The array that contains the widgets that are currently available for selection.")

  @Valid

  public List<WidgetDescriptor> getAvailableWidgets() {
    return availableWidgets;
  }

  public void setAvailableWidgets(List<WidgetDescriptor> availableWidgets) {
    this.availableWidgets = availableWidgets;
  }

  public PredictiveFormInstance visibleWidgets(List<WidgetInstance> visibleWidgets) {
    this.visibleWidgets = visibleWidgets;
    return this;
  }

  public PredictiveFormInstance addVisibleWidgetsItem(WidgetInstance visibleWidgetsItem) {
    if (this.visibleWidgets == null) {
      this.visibleWidgets = new ArrayList<>();
    }
    this.visibleWidgets.add(visibleWidgetsItem);
    return this;
  }

  /**
   * The array that contains the widgets that are currently visible, as already filled data.
   * @return visibleWidgets
  */
  @ApiModelProperty(value = "The array that contains the widgets that are currently visible, as already filled data.")

  @Valid

  public List<WidgetInstance> getVisibleWidgets() {
    return visibleWidgets;
  }

  public void setVisibleWidgets(List<WidgetInstance> visibleWidgets) {
    this.visibleWidgets = visibleWidgets;
  }

  public PredictiveFormInstance graph(PredictiveInputGraphDescriptor graph) {
    this.graph = graph;
    return this;
  }

  /**
   * Get graph
   * @return graph
  */
  @ApiModelProperty(value = "")

  @Valid

  public PredictiveInputGraphDescriptor getGraph() {
    return graph;
  }

  public void setGraph(PredictiveInputGraphDescriptor graph) {
    this.graph = graph;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PredictiveFormInstance predictiveFormInstance = (PredictiveFormInstance) o;
    return Objects.equals(this.uri, predictiveFormInstance.uri) &&
        Objects.equals(this.availableWidgets, predictiveFormInstance.availableWidgets) &&
        Objects.equals(this.visibleWidgets, predictiveFormInstance.visibleWidgets) &&
        Objects.equals(this.graph, predictiveFormInstance.graph);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, availableWidgets, visibleWidgets, graph);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredictiveFormInstance {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    availableWidgets: ").append(toIndentedString(availableWidgets)).append("\n");
    sb.append("    visibleWidgets: ").append(toIndentedString(visibleWidgets)).append("\n");
    sb.append("    graph: ").append(toIndentedString(graph)).append("\n");
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

