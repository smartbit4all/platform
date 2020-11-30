package org.smartbit4all.api.navigation.bean;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * API: This meta describes and entry that collabortes in a navigation. This is part of the configuration.
 */
@ApiModel(description = "API: This meta describes and entry that collabortes in a navigation. This is part of the configuration.")

public class NavigationEntryMeta   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("name")
  private String name;

  @JsonProperty("associations")
  @Valid
  private List<NavigationAssociationMeta> associations = null;

  public NavigationEntryMeta uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The uri of the resource that uniquely identifies the given entry inside the navigation api.
   * @return uri
  */
  @ApiModelProperty(required = true, value = "The uri of the resource that uniquely identifies the given entry inside the navigation api.")
  @NotNull

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public NavigationEntryMeta name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The well-formed name of the association.
   * @return name
  */
  @ApiModelProperty(required = true, value = "The well-formed name of the association.")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public NavigationEntryMeta associations(List<NavigationAssociationMeta> associations) {
    this.associations = associations;
    return this;
  }

  public NavigationEntryMeta addAssociationsItem(NavigationAssociationMeta associationsItem) {
    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.add(associationsItem);
    return this;
  }

  /**
   * Get associations
   * @return associations
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<NavigationAssociationMeta> getAssociations() {
    return associations;
  }

  public void setAssociations(List<NavigationAssociationMeta> associations) {
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
    NavigationEntryMeta navigationEntryMeta = (NavigationEntryMeta) o;
    return Objects.equals(this.uri, navigationEntryMeta.uri) &&
        Objects.equals(this.name, navigationEntryMeta.name) &&
        Objects.equals(this.associations, navigationEntryMeta.associations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, associations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationEntryMeta {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

