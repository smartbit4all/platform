package org.smartbit4all.api.navigation.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * UI: The stateful object on the client side. Contains the reference to the NavigationEntry and also the references to the parent and to the children
 */
@ApiModel(description = "UI: The stateful object on the client side. Contains the reference to the NavigationEntry and also the references to the parent and to the children")

public class NavigationNode   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("entry")
  private NavigationEntry entry;

  @JsonProperty("associations")
  @Valid
  private List<NavigationAssociation> associations = null;

  public NavigationNode id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of the given node - UUID
   * @return id
  */
  @ApiModelProperty(required = true, value = "The unique identifier of the given node - UUID")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NavigationNode entry(NavigationEntry entry) {
    this.entry = entry;
    return this;
  }

  /**
   * Get entry
   * @return entry
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public NavigationEntry getEntry() {
    return entry;
  }

  public void setEntry(NavigationEntry entry) {
    this.entry = entry;
  }

  public NavigationNode associations(List<NavigationAssociation> associations) {
    this.associations = associations;
    return this;
  }

  public NavigationNode addAssociationsItem(NavigationAssociation associationsItem) {
    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.add(associationsItem);
    return this;
  }

  /**
   * The possible navigation directions.
   * @return associations
  */
  @ApiModelProperty(value = "The possible navigation directions.")

  @Valid

  public List<NavigationAssociation> getAssociations() {
    return associations;
  }

  public void setAssociations(List<NavigationAssociation> associations) {
    this.associations = associations;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationNode navigationNode = (NavigationNode) o;
    return Objects.equals(this.id, navigationNode.id) &&
        Objects.equals(this.entry, navigationNode.entry) &&
        Objects.equals(this.associations, navigationNode.associations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, entry, associations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationNode {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    entry: ").append(toIndentedString(entry)).append("\n");
    sb.append("    associations: ").append(toIndentedString(associations)).append("\n");
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

