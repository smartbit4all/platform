package org.smartbit4all.api.navigation.bean;

import java.net.URI;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * API: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.
 */
@ApiModel(description = "API: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.")

public class NavigationReferenceEntry   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("startEntryUri")
  private URI startEntryUri;

  @JsonProperty("endEntry")
  private NavigationEntry endEntry;

  @JsonProperty("associationEntry")
  private NavigationEntry associationEntry;

  public NavigationReferenceEntry id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The identifier of the reference that is constructed as startEntry.uri + [ \"-\" assocEntry.uri + ] \"-\" + endEntry.uri
   * @return id
  */
  @ApiModelProperty(required = true, value = "The identifier of the reference that is constructed as startEntry.uri + [ \"-\" assocEntry.uri + ] \"-\" + endEntry.uri")
  @NotNull


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NavigationReferenceEntry startEntryUri(URI startEntryUri) {
    this.startEntryUri = startEntryUri;
    return this;
  }

  /**
   * The URI of the start entry
   * @return startEntryUri
  */
  @ApiModelProperty(value = "The URI of the start entry")

  @Valid

  public URI getStartEntryUri() {
    return startEntryUri;
  }

  public void setStartEntryUri(URI startEntryUri) {
    this.startEntryUri = startEntryUri;
  }

  public NavigationReferenceEntry endEntry(NavigationEntry endEntry) {
    this.endEntry = endEntry;
    return this;
  }

  /**
   * Get endEntry
   * @return endEntry
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationEntry getEndEntry() {
    return endEntry;
  }

  public void setEndEntry(NavigationEntry endEntry) {
    this.endEntry = endEntry;
  }

  public NavigationReferenceEntry associationEntry(NavigationEntry associationEntry) {
    this.associationEntry = associationEntry;
    return this;
  }

  /**
   * Get associationEntry
   * @return associationEntry
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationEntry getAssociationEntry() {
    return associationEntry;
  }

  public void setAssociationEntry(NavigationEntry associationEntry) {
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
    NavigationReferenceEntry navigationReferenceEntry = (NavigationReferenceEntry) o;
    return Objects.equals(this.id, navigationReferenceEntry.id) &&
        Objects.equals(this.startEntryUri, navigationReferenceEntry.startEntryUri) &&
        Objects.equals(this.endEntry, navigationReferenceEntry.endEntry) &&
        Objects.equals(this.associationEntry, navigationReferenceEntry.associationEntry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startEntryUri, endEntry, associationEntry);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationReferenceEntry {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    startEntryUri: ").append(toIndentedString(startEntryUri)).append("\n");
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

