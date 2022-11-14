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
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object contains the references and the collections of the given object. This does not have identity it is bound to  the StoaregObject by the ObjectStorage implementation. 
 */
@ApiModel(description = "This object contains the references and the collections of the given object. This does not have identity it is bound to  the StoaregObject by the ObjectStorage implementation. ")
@JsonPropertyOrder({
  StorageObjectRelationData.URI,
  StorageObjectRelationData.REFERENCES,
  StorageObjectRelationData.COLLECTIONS
})
@JsonTypeName("StorageObjectRelationData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StorageObjectRelationData {
  public static final String URI = "uri";
  private URI uri;

  public static final String REFERENCES = "references";
  private Map<String, ObjectReference> references = null;

  public static final String COLLECTIONS = "collections";
  private Map<String, ObjectReferenceList> collections = null;

  public StorageObjectRelationData() { 
  }

  public StorageObjectRelationData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The uri of the object represented by the storage object. 
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The uri of the object represented by the storage object. ")
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


  public StorageObjectRelationData references(Map<String, ObjectReference> references) {
    
    this.references = references;
    return this;
  }

  public StorageObjectRelationData putReferencesItem(String key, ObjectReference referencesItem) {
    if (this.references == null) {
      this.references = new HashMap<>();
    }
    this.references.put(key, referencesItem);
    return this;
  }

   /**
   * Get references
   * @return references
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(REFERENCES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, ObjectReference> getReferences() {
    return references;
  }


  @JsonProperty(REFERENCES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReferences(Map<String, ObjectReference> references) {
    this.references = references;
  }


  public StorageObjectRelationData collections(Map<String, ObjectReferenceList> collections) {
    
    this.collections = collections;
    return this;
  }

  public StorageObjectRelationData putCollectionsItem(String key, ObjectReferenceList collectionsItem) {
    if (this.collections == null) {
      this.collections = new HashMap<>();
    }
    this.collections.put(key, collectionsItem);
    return this;
  }

   /**
   * Get collections
   * @return collections
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(COLLECTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, ObjectReferenceList> getCollections() {
    return collections;
  }


  @JsonProperty(COLLECTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCollections(Map<String, ObjectReferenceList> collections) {
    this.collections = collections;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorageObjectRelationData storageObjectRelationData = (StorageObjectRelationData) o;
    return Objects.equals(this.uri, storageObjectRelationData.uri) &&
        Objects.equals(this.references, storageObjectRelationData.references) &&
        Objects.equals(this.collections, storageObjectRelationData.collections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, references, collections);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StorageObjectRelationData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    references: ").append(toIndentedString(references)).append("\n");
    sb.append("    collections: ").append(toIndentedString(collections)).append("\n");
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

