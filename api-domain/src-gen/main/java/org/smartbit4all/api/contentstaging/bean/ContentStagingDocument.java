package org.smartbit4all.api.contentstaging.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ContentStagingDocument
 */

public class ContentStagingDocument   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("key")
  private UUID key;

  @JsonProperty("contentUri")
  private URI contentUri;

  public ContentStagingDocument uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public ContentStagingDocument key(UUID key) {
    this.key = key;
    return this;
  }

  /**
   * Get key
   * @return key
  */
  @ApiModelProperty(value = "")

  @Valid

  public UUID getKey() {
    return key;
  }

  public void setKey(UUID key) {
    this.key = key;
  }

  public ContentStagingDocument contentUri(URI contentUri) {
    this.contentUri = contentUri;
    return this;
  }

  /**
   * Get contentUri
   * @return contentUri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getContentUri() {
    return contentUri;
  }

  public void setContentUri(URI contentUri) {
    this.contentUri = contentUri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentStagingDocument contentStagingDocument = (ContentStagingDocument) o;
    return Objects.equals(this.uri, contentStagingDocument.uri) &&
        Objects.equals(this.key, contentStagingDocument.key) &&
        Objects.equals(this.contentUri, contentStagingDocument.contentUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, key, contentUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentStagingDocument {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    contentUri: ").append(toIndentedString(contentUri)).append("\n");
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

