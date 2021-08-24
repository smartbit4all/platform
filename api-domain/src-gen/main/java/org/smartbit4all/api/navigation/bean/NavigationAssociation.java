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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.smartbit4all.api.navigation.bean.NavigationReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * UI: The navigable associations belong to the given node.
 */
@ApiModel(description = "UI: The navigable associations belong to the given node.")
@JsonPropertyOrder({
  NavigationAssociation.ID,
  NavigationAssociation.NODE,
  NavigationAssociation.META_URI,
  NavigationAssociation.LAST_NAVIGATION,
  NavigationAssociation.HIDDEN,
  NavigationAssociation.REFERENCES
})
@JsonTypeName("NavigationAssociation")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigationAssociation {
  public static final String ID = "id";
  private String id;

  public static final String NODE = "node";
  private NavigationNode node;

  public static final String META_URI = "metaUri";
  private URI metaUri;

  public static final String LAST_NAVIGATION = "lastNavigation";
  private Integer lastNavigation;

  public static final String HIDDEN = "hidden";
  private Boolean hidden;

  public static final String REFERENCES = "references";
  private List<NavigationReference> references = null;


  public NavigationAssociation id(String id) {
    
    this.id = id;
    return this;
  }

   /**
   * The unique identifier of the given association - UUID
   * @return id
  **/
  @NotNull
  @ApiModelProperty(required = true, value = "The unique identifier of the given association - UUID")
  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getId() {
    return id;
  }


  @JsonProperty(ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
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
  **/
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(NODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationNode getNode() {
    return node;
  }


  @JsonProperty(NODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setNode(NavigationNode node) {
    this.node = node;
  }


  public NavigationAssociation metaUri(URI metaUri) {
    
    this.metaUri = metaUri;
    return this;
  }

   /**
   * Get metaUri
   * @return metaUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(META_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getMetaUri() {
    return metaUri;
  }


  @JsonProperty(META_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMetaUri(URI metaUri) {
    this.metaUri = metaUri;
  }


  public NavigationAssociation lastNavigation(Integer lastNavigation) {
    
    this.lastNavigation = lastNavigation;
    return this;
  }

   /**
   * The last navigation time (in millis) when the given association was retrieved. If null then it has never been navigated.
   * @return lastNavigation
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The last navigation time (in millis) when the given association was retrieved. If null then it has never been navigated.")
  @JsonProperty(LAST_NAVIGATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getLastNavigation() {
    return lastNavigation;
  }


  @JsonProperty(LAST_NAVIGATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The association can be hidden. If a node has only one association then it can be an option.")
  @JsonProperty(HIDDEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getHidden() {
    return hidden;
  }


  @JsonProperty(HIDDEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The navigation association instances.")
  @JsonProperty(REFERENCES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<NavigationReference> getReferences() {
    return references;
  }


  @JsonProperty(REFERENCES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReferences(List<NavigationReference> references) {
    this.references = references;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationAssociation navigationAssociation = (NavigationAssociation) o;
    return Objects.equals(this.id, navigationAssociation.id) &&
        Objects.equals(this.node, navigationAssociation.node) &&
        Objects.equals(this.metaUri, navigationAssociation.metaUri) &&
        Objects.equals(this.lastNavigation, navigationAssociation.lastNavigation) &&
        Objects.equals(this.hidden, navigationAssociation.hidden) &&
        Objects.equals(this.references, navigationAssociation.references);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, node, metaUri, lastNavigation, hidden, references);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationAssociation {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    node: ").append(toIndentedString(node)).append("\n");
    sb.append("    metaUri: ").append(toIndentedString(metaUri)).append("\n");
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

