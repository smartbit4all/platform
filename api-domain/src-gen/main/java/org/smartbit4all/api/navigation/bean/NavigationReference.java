/*
 * smartbit4all navigation api
 * smartbit4all navigation api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * UI: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.
 */
@ApiModel(description = "UI: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.")
@JsonPropertyOrder({
  NavigationReference.ID,
  NavigationReference.START_NODE,
  NavigationReference.END_NODE,
  NavigationReference.ASSOCIATION_NODE
})
@JsonTypeName("NavigationReference")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigationReference {
  public static final String ID = "id";
  private String id;

  public static final String START_NODE = "startNode";
  private NavigationNode startNode;

  public static final String END_NODE = "endNode";
  private NavigationNode endNode;

  public static final String ASSOCIATION_NODE = "associationNode";
  private NavigationNode associationNode;


  public NavigationReference id(String id) {
    
    this.id = id;
    return this;
  }

   /**
   * The unique identifier of the given association - UUID
   * @return id
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The unique identifier of the given association - UUID")
  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getId() {
    return id;
  }


  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(START_NODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationNode getStartNode() {
    return startNode;
  }


  @JsonProperty(START_NODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
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
  **/
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(END_NODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationNode getEndNode() {
    return endNode;
  }


  @JsonProperty(END_NODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
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
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ASSOCIATION_NODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public NavigationNode getAssociationNode() {
    return associationNode;
  }


  @JsonProperty(ASSOCIATION_NODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAssociationNode(NavigationNode associationNode) {
    this.associationNode = associationNode;
  }


  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

