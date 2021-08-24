/*
 * Form API
 * API for the predictive filling of forms, where a tree structure contains which pieces of data follow other pieces.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.PredictiveInputGraphNode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Descriptor of a graph that defines input values of the PredictiveFormInstance. The available choices are calculated by this graph while editing the PredictiveFormInstance.
 */
@ApiModel(description = "Descriptor of a graph that defines input values of the PredictiveFormInstance. The available choices are calculated by this graph while editing the PredictiveFormInstance.")
@JsonPropertyOrder({
  PredictiveInputGraphDescriptor.URI,
  PredictiveInputGraphDescriptor.ROOT_NODES
})
@JsonTypeName("PredictiveInputGraphDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PredictiveInputGraphDescriptor {
  public static final String URI = "uri";
  private URI uri;

  public static final String ROOT_NODES = "rootNodes";
  private List<PredictiveInputGraphNode> rootNodes = null;


  public PredictiveInputGraphDescriptor uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ROOT_NODES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<PredictiveInputGraphNode> getRootNodes() {
    return rootNodes;
  }


  @JsonProperty(ROOT_NODES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRootNodes(List<PredictiveInputGraphNode> rootNodes) {
    this.rootNodes = rootNodes;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PredictiveInputGraphDescriptor predictiveInputGraphDescriptor = (PredictiveInputGraphDescriptor) o;
    return Objects.equals(this.uri, predictiveInputGraphDescriptor.uri) &&
        Objects.equals(this.rootNodes, predictiveInputGraphDescriptor.rootNodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, rootNodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PredictiveInputGraphDescriptor {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    rootNodes: ").append(toIndentedString(rootNodes)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

