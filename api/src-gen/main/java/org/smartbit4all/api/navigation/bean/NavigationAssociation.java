package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * UI: The navigable associations belong to the given node.
 */
@ApiModel(description = "UI: The navigable associations belong to the given node.")

public class NavigationAssociation   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("node")
  private NavigationNode node;

  @JsonProperty("meta")
  private NavigationAssociationMeta meta;

  @JsonProperty("lastNavigation")
  private Integer lastNavigation;

  @JsonProperty("hidden")
  private Boolean hidden;

  @JsonProperty("references")
  @Valid
  private List<NavigationReference> references = null;

  public NavigationAssociation id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the given association - UUID
   * @return id
  */
  @ApiModelProperty(required = true, value = "The unique identifier of the given association - UUID")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NavigationAssociation node(NavigationNode node) {
    this.node = node;
    return this;
  }

  /**
   * Get node
   * @return node
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public NavigationNode getNode() {
    return node;
  }

  public void setNode(NavigationNode node) {
    this.node = node;
  }

  public NavigationAssociation meta(NavigationAssociationMeta meta) {
    this.meta = meta;
    return this;
  }

  /**
   * Get meta
   * @return meta
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationAssociationMeta getMeta() {
    return meta;
  }

  public void setMeta(NavigationAssociationMeta meta) {
    this.meta = meta;
  }

  public NavigationAssociation lastNavigation(Integer lastNavigation) {
    this.lastNavigation = lastNavigation;
    return this;
  }

  /**
   * The last navigation time (in millis) when the given association was retrieved. If null then it has never been navigated.
   * @return lastNavigation
  */
  @ApiModelProperty(value = "The last navigation time (in millis) when the given association was retrieved. If null then it has never been navigated.")


  public Integer getLastNavigation() {
    return lastNavigation;
  }

  public void setLastNavigation(Integer lastNavigation) {
    this.lastNavigation = lastNavigation;
  }

  public NavigationAssociation hidden(Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  /**
   * The association can be hidden. If a node has only one association then it can be an option.
   * @return hidden
  */
  @ApiModelProperty(value = "The association can be hidden. If a node has only one association then it can be an option.")


  public Boolean getHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  public NavigationAssociation references(List<NavigationReference> references) {
    this.references = references;
    return this;
  }

  public NavigationAssociation addReferencesItem(NavigationReference referencesItem) {
    if (this.references == null) {
      this.references = new ArrayList<>();
    }
    this.references.add(referencesItem);
    return this;
  }

  /**
   * The navigation association instances.
   * @return references
  */
  @ApiModelProperty(value = "The navigation association instances.")

  @Valid

  public List<NavigationReference> getReferences() {
    return references;
  }

  public void setReferences(List<NavigationReference> references) {
    this.references = references;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationAssociation navigationAssociation = (NavigationAssociation) o;
    return Objects.equals(this.id, navigationAssociation.id) &&
        Objects.equals(this.node, navigationAssociation.node) &&
        Objects.equals(this.meta, navigationAssociation.meta) &&
        Objects.equals(this.lastNavigation, navigationAssociation.lastNavigation) &&
        Objects.equals(this.hidden, navigationAssociation.hidden) &&
        Objects.equals(this.references, navigationAssociation.references);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, node, meta, lastNavigation, hidden, references);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationAssociation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    node: ").append(toIndentedString(node)).append("\n");
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
    sb.append("    lastNavigation: ").append(toIndentedString(lastNavigation)).append("\n");
    sb.append("    hidden: ").append(toIndentedString(hidden)).append("\n");
    sb.append("    references: ").append(toIndentedString(references)).append("\n");
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

