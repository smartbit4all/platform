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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This is the generic reference to another storage object. This object has no data but the object URI itself. So there is no need to have history at all. This object is used only by the storage itself to form a special link to an object. It is included into the storage set container and can be used to iterate through the referred objects. Of course this results a two phase read but this is the trade off for the flexibility. 
 */
@ApiModel(description = "This is the generic reference to another storage object. This object has no data but the object URI itself. So there is no need to have history at all. This object is used only by the storage itself to form a special link to an object. It is included into the storage set container and can be used to iterate through the referred objects. Of course this results a two phase read but this is the trade off for the flexibility. ")
@JsonPropertyOrder({
  StorageObjectReference.URI,
  StorageObjectReference.OBJECT_URI
})
@JsonTypeName("StorageObjectReference")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StorageObjectReference {
  public static final String URI = "uri";
  private URI uri;

  public static final String OBJECT_URI = "objectUri";
  private URI objectUri;

  public StorageObjectReference() { 
  }

  public StorageObjectReference uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The identifier of the StorageObjectReference object.   
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The identifier of the StorageObjectReference object.   ")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public StorageObjectReference objectUri(URI objectUri) {
    
    this.objectUri = objectUri;
    return this;
  }

   /**
   * The uri of the original object refrred by the reference object.   
   * @return objectUri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The uri of the original object refrred by the reference object.   ")
  @JsonProperty(OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getObjectUri() {
    return objectUri;
  }


  @JsonProperty(OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorageObjectReference storageObjectReference = (StorageObjectReference) o;
    return Objects.equals(this.uri, storageObjectReference.uri) &&
        Objects.equals(this.objectUri, storageObjectReference.objectUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, objectUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StorageObjectReference {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
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

