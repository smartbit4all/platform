package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * UI: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.
 */
@ApiModel(description = "UI: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.")

public class NavigationReference   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("startNode")
  private NavigationNode startNode;

  @JsonProperty("endNode")
  private NavigationNode endNode;

  @JsonProperty("associationNode")
  private NavigationNode associationNode;

  public NavigationReference id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the given association - UUID
   * @return id
  */
  @ApiModelProperty(value = "The unique identifier of the given association - UUID")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NavigationReference startNode(NavigationNode startNode) {
    this.startNode = startNode;
    return this;
  }

  /**
   * Get startNode
   * @return startNode
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public NavigationNode getStartNode() {
    return startNode;
  }

  public void setStartNode(NavigationNode startNode) {
    this.startNode = startNode;
  }

  public NavigationReference endNode(NavigationNode endNode) {
    this.endNode = endNode;
    return this;
  }

  /**
   * Get endNode
   * @return endNode
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public NavigationNode getEndNode() {
    return endNode;
  }

  public void setEndNode(NavigationNode endNode) {
    this.endNode = endNode;
  }

  public NavigationReference associationNode(NavigationNode associationNode) {
    this.associationNode = associationNode;
    return this;
  }

  /**
   * Get associationNode
   * @return associationNode
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationNode getAssociationNode() {
    return associationNode;
  }

  public void setAssociationNode(NavigationNode associationNode) {
    this.associationNode = associationNode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationReference navigationReference = (NavigationReference) o;
    return Objects.equals(this.id, navigationReference.id) &&
        Objects.equals(this.startNode, navigationReference.startNode) &&
        Objects.equals(this.endNode, navigationReference.endNode) &&
        Objects.equals(this.associationNode, navigationReference.associationNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startNode, endNode, associationNode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationReference {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    startNode: ").append(toIndentedString(startNode)).append("\n");
    sb.append("    endNode: ").append(toIndentedString(endNode)).append("\n");
    sb.append("    associationNode: ").append(toIndentedString(associationNode)).append("\n");
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

