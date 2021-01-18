package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * InlineObject
 */

public class InlineObject   {
  @JsonProperty("entryMetaUri")
  private URI entryMetaUri;

  @JsonProperty("objectUri")
  private URI objectUri;

  public InlineObject entryMetaUri(URI entryMetaUri) {
    this.entryMetaUri = entryMetaUri;
    return this;
  }

  /**
   * Get entryMetaUri
   * @return entryMetaUri
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public URI getEntryMetaUri() {
    return entryMetaUri;
  }

  public void setEntryMetaUri(URI entryMetaUri) {
    this.entryMetaUri = entryMetaUri;
  }

  public InlineObject objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  /**
   * Get objectUri
   * @return objectUri
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public URI getObjectUri() {
    return objectUri;
  }

  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineObject inlineObject = (InlineObject) o;
    return Objects.equals(this.entryMetaUri, inlineObject.entryMetaUri) &&
        Objects.equals(this.objectUri, inlineObject.objectUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entryMetaUri, objectUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineObject {\n");
    
    sb.append("    entryMetaUri: ").append(toIndentedString(entryMetaUri)).append("\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
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

