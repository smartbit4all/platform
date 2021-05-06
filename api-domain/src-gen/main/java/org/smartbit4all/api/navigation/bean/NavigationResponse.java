package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NavigationResponse
 */

public class NavigationResponse   {
  @JsonProperty("associationMetaUri")
  private URI associationMetaUri;

  @JsonProperty("referenceEntries")
  @Valid
  private List<NavigationReferenceEntry> referenceEntries = null;

  public NavigationResponse associationMetaUri(URI associationMetaUri) {
    this.associationMetaUri = associationMetaUri;
    return this;
  }

  /**
   * Get associationMetaUri
   * @return associationMetaUri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getAssociationMetaUri() {
    return associationMetaUri;
  }

  public void setAssociationMetaUri(URI associationMetaUri) {
    this.associationMetaUri = associationMetaUri;
  }

  public NavigationResponse referenceEntries(List<NavigationReferenceEntry> referenceEntries) {
    this.referenceEntries = referenceEntries;
    return this;
  }

  public NavigationResponse addReferenceEntriesItem(NavigationReferenceEntry referenceEntriesItem) {
    if (this.referenceEntries == null) {
      this.referenceEntries = new ArrayList<>();
    }
    this.referenceEntries.add(referenceEntriesItem);
    return this;
  }

  /**
   * Get referenceEntries
   * @return referenceEntries
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<NavigationReferenceEntry> getReferenceEntries() {
    return referenceEntries;
  }

  public void setReferenceEntries(List<NavigationReferenceEntry> referenceEntries) {
    this.referenceEntries = referenceEntries;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigationResponse navigationResponse = (NavigationResponse) o;
    return Objects.equals(this.associationMetaUri, navigationResponse.associationMetaUri) &&
        Objects.equals(this.referenceEntries, navigationResponse.referenceEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(associationMetaUri, referenceEntries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationResponse {\n");
    
    sb.append("    associationMetaUri: ").append(toIndentedString(associationMetaUri)).append("\n");
    sb.append("    referenceEntries: ").append(toIndentedString(referenceEntries)).append("\n");
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

