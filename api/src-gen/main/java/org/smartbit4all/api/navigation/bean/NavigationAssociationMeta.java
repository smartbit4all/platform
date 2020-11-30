package org.smartbit4all.api.navigation.bean;

import java.net.URI;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * API: The association meta describes a possible navigation between entry metas. It is part of the configuration.
 */
@ApiModel(description = "API: The association meta describes a possible navigation between entry metas. It is part of the configuration.")

public class NavigationAssociationMeta   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("name")
  private String name;

  @JsonProperty("startEntry")
  private NavigationEntryMeta startEntry;

  @JsonProperty("endEntry")
  private NavigationEntryMeta endEntry;

  @JsonProperty("associationEntry")
  private NavigationEntryMeta associationEntry;

  public NavigationAssociationMeta uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * The uri of the resource that uniquely identifies the given navigation inside the navigation api.
   * @return uri
  */
  @ApiModelProperty(required = true, value = "The uri of the resource that uniquely identifies the given navigation inside the navigation api.")
  @NotNull

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public NavigationAssociationMeta name(String name) {
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

  public NavigationAssociationMeta startEntry(NavigationEntryMeta startEntry) {
    this.startEntry = startEntry;
    return this;
  }

  /**
   * Get startEntry
   * @return startEntry
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public NavigationEntryMeta getStartEntry() {
    return startEntry;
  }

  public void setStartEntry(NavigationEntryMeta startEntry) {
    this.startEntry = startEntry;
  }

  public NavigationAssociationMeta endEntry(NavigationEntryMeta endEntry) {
    this.endEntry = endEntry;
    return this;
  }

  /**
   * Get endEntry
   * @return endEntry
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public NavigationEntryMeta getEndEntry() {
    return endEntry;
  }

  public void setEndEntry(NavigationEntryMeta endEntry) {
    this.endEntry = endEntry;
  }

  public NavigationAssociationMeta associationEntry(NavigationEntryMeta associationEntry) {
    this.associationEntry = associationEntry;
    return this;
  }

  /**
   * Get associationEntry
   * @return associationEntry
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationEntryMeta getAssociationEntry() {
    return associationEntry;
  }

  public void setAssociationEntry(NavigationEntryMeta associationEntry) {
    this.associationEntry = associationEntry;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationAssociationMeta navigationAssociationMeta = (NavigationAssociationMeta) o;
    return Objects.equals(this.uri, navigationAssociationMeta.uri) &&
        Objects.equals(this.name, navigationAssociationMeta.name) &&
        Objects.equals(this.startEntry, navigationAssociationMeta.startEntry) &&
        Objects.equals(this.endEntry, navigationAssociationMeta.endEntry) &&
        Objects.equals(this.associationEntry, navigationAssociationMeta.associationEntry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, startEntry, endEntry, associationEntry);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationAssociationMeta {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    startEntry: ").append(toIndentedString(startEntry)).append("\n");
    sb.append("    endEntry: ").append(toIndentedString(endEntry)).append("\n");
    sb.append("    associationEntry: ").append(toIndentedString(associationEntry)).append("\n");
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

