package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.ui.api.form.model.FormDataContent;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphDescriptor;
import org.smartbit4all.ui.api.form.model.PropertyWidgetDescriptor;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The whole layout, that describes already collected pieces of data as the content, and the possible choices as well.
 */
@ApiModel(description = "The whole layout, that describes already collected pieces of data as the content, and the possible choices as well.")

public class PredictiveFormInstance   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("activeContent")
  private UUID activeContent;

  @JsonProperty("availableChoices")
  @Valid
  private List<PropertyWidgetDescriptor> availableChoices = null;

  @JsonProperty("content")
  private FormDataContent content;

  @JsonProperty("graph")
  private PredictiveInputGraphDescriptor graph;

  public PredictiveFormInstance uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public PredictiveFormInstance activeContent(UUID activeContent) {
    this.activeContent = activeContent;
    return this;
  }

  /**
   * The uuid of the active content. It's the context of the modification.
   * @return activeContent
  */
  @ApiModelProperty(value = "The uuid of the active content. It's the context of the modification.")

  @Valid

  public UUID getActiveContent() {
    return activeContent;
  }

  public void setActiveContent(UUID activeContent) {
    this.activeContent = activeContent;
  }

  public PredictiveFormInstance availableChoices(List<PropertyWidgetDescriptor> availableChoices) {
    this.availableChoices = availableChoices;
    return this;
  }

  public PredictiveFormInstance addAvailableChoicesItem(PropertyWidgetDescriptor availableChoicesItem) {
    if (this.availableChoices == null) {
      this.availableChoices = new ArrayList<>();
    }
    this.availableChoices.add(availableChoicesItem);
    return this;
  }

  /**
   * Get availableChoices
   * @return availableChoices
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<PropertyWidgetDescriptor> getAvailableChoices() {
    return availableChoices;
  }

  public void setAvailableChoices(List<PropertyWidgetDescriptor> availableChoices) {
    this.availableChoices = availableChoices;
  }

  public PredictiveFormInstance content(FormDataContent content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
  */
  @ApiModelProperty(value = "")

  @Valid

  public FormDataContent getContent() {
    return content;
  }

  public void setContent(FormDataContent content) {
    this.content = content;
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
        Objects.equals(this.activeContent, predictiveFormInstance.activeContent) &&
        Objects.equals(this.availableChoices, predictiveFormInstance.availableChoices) &&
        Objects.equals(this.content, predictiveFormInstance.content) &&
        Objects.equals(this.graph, predictiveFormInstance.graph);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, activeContent, availableChoices, content, graph);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredictiveFormInstance {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    activeContent: ").append(toIndentedString(activeContent)).append("\n");
    sb.append("    availableChoices: ").append(toIndentedString(availableChoices)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

