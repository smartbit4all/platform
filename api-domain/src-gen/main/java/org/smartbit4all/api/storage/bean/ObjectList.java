/*
 * storage api
 * The storage api is a generic possibility to store and load objects.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.storage.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The list of references as a container object for the Api.  
 */
@ApiModel(description = "The list of references as a container object for the Api.  ")
@JsonPropertyOrder({
  ObjectList.URI,
  ObjectList.URIS
})
@JsonTypeName("ObjectList")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectList {
  public static final String URI = "uri";
  private URI uri;

  public static final String URIS = "uris";
  private Map<String, URI> uris = null;


  public ObjectList uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public ObjectList uris(Map<String, URI> uris) {
    
    this.uris = uris;
    return this;
  }

  public ObjectList putUrisItem(String key, URI urisItem) {
    if (this.uris == null) {
      this.uris = new HashMap<>();
    }
    this.uris.put(key, urisItem);
    return this;
  }

   /**
   * Get uris
   * @return uris
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URIS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, URI> getUris() {
    return uris;
  }


  @JsonProperty(URIS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUris(Map<String, URI> uris) {
    this.uris = uris;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectList objectList = (ObjectList) o;
    return Objects.equals(this.uri, objectList.uri) &&
        Objects.equals(this.uris, objectList.uris);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, uris);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectList {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    uris: ").append(toIndentedString(uris)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

