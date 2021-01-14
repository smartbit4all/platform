package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * InlineObject1
 */

public class InlineObject1   {
  @JsonProperty("objectUri")
  private URI objectUri;

  @JsonProperty("associationMetaUris")
  @Valid
  private List<URI> associationMetaUris = null;

  public InlineObject1 objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  /**
   * The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided.
   * @return objectUri
  */
  @ApiModelProperty(required = true, value = "The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided.")
  @NotNull

  @Valid

  public URI getObjectUri() {
    return objectUri;
  }

  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }

  public InlineObject1 associationMetaUris(List<URI> associationMetaUris) {
    this.associationMetaUris = associationMetaUris;
    return this;
  }

  public InlineObject1 addAssociationMetaUrisItem(URI associationMetaUrisItem) {
    if (this.associationMetaUris == null) {
      this.associationMetaUris = new ArrayList<>();
    }
    this.associationMetaUris.add(associationMetaUrisItem);
    return this;
  }

  /**
   * The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta
   * @return associationMetaUris
  */
  @ApiModelProperty(value = "The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta")

  @Valid

  public List<URI> getAssociationMetaUris() {
    return associationMetaUris;
  }

  public void setAssociationMetaUris(List<URI> associationMetaUris) {
    this.associationMetaUris = associationMetaUris;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineObject1 inlineObject1 = (InlineObject1) o;
    return Objects.equals(this.objectUri, inlineObject1.objectUri) &&
        Objects.equals(this.associationMetaUris, inlineObject1.associationMetaUris);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUri, associationMetaUris);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineObject1 {\n");
    
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    associationMetaUris: ").append(toIndentedString(associationMetaUris)).append("\n");
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

