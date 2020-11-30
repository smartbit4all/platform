package org.smartbit4all.api.navigation.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * API: Describes all the entries and associations in the given navigation.
 */
@ApiModel(description = "API: Describes all the entries and associations in the given navigation.")

public class NavigationConfig   {
  @JsonProperty("entries")
  @Valid
  private List<NavigationEntryMeta> entries = null;

  @JsonProperty("associations")
  @Valid
  private List<NavigationAssociationMeta> associations = null;

  public NavigationConfig entries(List<NavigationEntryMeta> entries) {
    this.entries = entries;
    return this;
  }

  public NavigationConfig addEntriesItem(NavigationEntryMeta entriesItem) {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    this.entries.add(entriesItem);
    return this;
  }

  /**
   * The available entries in the given navigation config.
   * @return entries
  */
  @ApiModelProperty(value = "The available entries in the given navigation config.")

  @Valid

  public List<NavigationEntryMeta> getEntries() {
    return entries;
  }

  public void setEntries(List<NavigationEntryMeta> entries) {
    this.entries = entries;
  }

  public NavigationConfig associations(List<NavigationAssociationMeta> associations) {
    this.associations = associations;
    return this;
  }

  public NavigationConfig addAssociationsItem(NavigationAssociationMeta associationsItem) {
    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.add(associationsItem);
    return this;
  }

  /**
   * The navigable associations in the given navigation. In case of a navigation tree these are the openable sub trees.
   * @return associations
  */
  @ApiModelProperty(value = "The navigable associations in the given navigation. In case of a navigation tree these are the openable sub trees.")

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
    NavigationConfig navigationConfig = (NavigationConfig) o;
    return Objects.equals(this.entries, navigationConfig.entries) &&
        Objects.equals(this.associations, navigationConfig.associations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries, associations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationConfig {\n");
    
    sb.append("    entries: ").append(toIndentedString(entries)).append("\n");
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

