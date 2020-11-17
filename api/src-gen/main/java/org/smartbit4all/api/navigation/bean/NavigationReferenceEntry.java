package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * API: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.
 */
@ApiModel(description = "API: The instance of the association. It is a directed from source to target. If we have data entry on the association itself then we can set this entry to the associationEntry.")

public class NavigationReferenceEntry   {
  @JsonProperty("id")
  private String id;

  @JsonProperty("startEntry")
  private NavigationEntry startEntry;

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

  public NavigationReferenceEntry startEntry(NavigationEntry startEntry) {
    this.startEntry = startEntry;
    return this;
  }

  /**
   * Get startEntry
   * @return startEntry
  */
  @ApiModelProperty(value = "")

  @Valid

  public NavigationEntry getStartEntry() {
    return startEntry;
  }

  public void setStartEntry(NavigationEntry startEntry) {
    this.startEntry = startEntry;
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
        Objects.equals(this.startEntry, navigationReferenceEntry.startEntry) &&
        Objects.equals(this.endEntry, navigationReferenceEntry.endEntry) &&
        Objects.equals(this.associationEntry, navigationReferenceEntry.associationEntry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, startEntry, endEntry, associationEntry);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationReferenceEntry {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

