package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Descriptor of a graph that defines input values of the PredictiveFormInstance. The available choices are calculated by this graph while editing the PredictiveFormInstance.
 */
@ApiModel(description = "Descriptor of a graph that defines input values of the PredictiveFormInstance. The available choices are calculated by this graph while editing the PredictiveFormInstance.")

public class PredictiveInputGraphDescriptor   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("rootNodes")
  @Valid
  private List<PredictiveInputGraphNode> rootNodes = null;

  @JsonProperty("nodes")
  @Valid
  private List<PredictiveInputGraphNode> nodes = null;

  public PredictiveInputGraphDescriptor uri(URI uri) {
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

  public PredictiveInputGraphDescriptor rootNodes(List<PredictiveInputGraphNode> rootNodes) {
    this.rootNodes = rootNodes;
    return this;
  }

  public PredictiveInputGraphDescriptor addRootNodesItem(PredictiveInputGraphNode rootNodesItem) {
    if (this.rootNodes == null) {
      this.rootNodes = new ArrayList<>();
    }
    this.rootNodes.add(rootNodesItem);
    return this;
  }

  /**
   * Get rootNodes
   * @return rootNodes
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<PredictiveInputGraphNode> getRootNodes() {
    return rootNodes;
  }

  public void setRootNodes(List<PredictiveInputGraphNode> rootNodes) {
    this.rootNodes = rootNodes;
  }

  public PredictiveInputGraphDescriptor nodes(List<PredictiveInputGraphNode> nodes) {
    this.nodes = nodes;
    return this;
  }

  public PredictiveInputGraphDescriptor addNodesItem(PredictiveInputGraphNode nodesItem) {
    if (this.nodes == null) {
      this.nodes = new ArrayList<>();
    }
    this.nodes.add(nodesItem);
    return this;
  }

  /**
   * all the nodes held by the graph descriptor. We need these if we want to maintain a mapping of the URIs and the nodes, which we do.
   * @return nodes
  */
  @ApiModelProperty(value = "all the nodes held by the graph descriptor. We need these if we want to maintain a mapping of the URIs and the nodes, which we do.")

  @Valid

  public List<PredictiveInputGraphNode> getNodes() {
    return nodes;
  }

  public void setNodes(List<PredictiveInputGraphNode> nodes) {
    this.nodes = nodes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PredictiveInputGraphDescriptor predictiveInputGraphDescriptor = (PredictiveInputGraphDescriptor) o;
    return Objects.equals(this.uri, predictiveInputGraphDescriptor.uri) &&
        Objects.equals(this.rootNodes, predictiveInputGraphDescriptor.rootNodes) &&
        Objects.equals(this.nodes, predictiveInputGraphDescriptor.nodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, rootNodes, nodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredictiveInputGraphDescriptor {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    rootNodes: ").append(toIndentedString(rootNodes)).append("\n");
    sb.append("    nodes: ").append(toIndentedString(nodes)).append("\n");
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

