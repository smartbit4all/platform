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
import org.smartbit4all.api.storage.bean.ObjectVersion;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The basic data object of the storages. This holds the version history and refers the current version. Also has named references  and collections. 
 */
@ApiModel(description = "The basic data object of the storages. This holds the version history and refers the current version. Also has named references  and collections. ")
@JsonPropertyOrder({
  StorageObjectData.URI,
  StorageObjectData.CURRENT_VERSION,
  StorageObjectData.CLASS_NAME,
  StorageObjectData.PENDING_VERSION,
  StorageObjectData.DELETED
})
@JsonTypeName("StorageObjectData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StorageObjectData {
  public static final String URI = "uri";
  private URI uri;

  public static final String CURRENT_VERSION = "currentVersion";
  private ObjectVersion currentVersion;

  public static final String CLASS_NAME = "className";
  private String className;

  public static final String PENDING_VERSION = "pendingVersion";
  private ObjectVersion pendingVersion;

  public static final String DELETED = "deleted";
  private Boolean deleted = false;

  public StorageObjectData() { 
  }

  public StorageObjectData uri(URI uri) {
    
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


  public StorageObjectData currentVersion(ObjectVersion currentVersion) {
    
    this.currentVersion = currentVersion;
    return this;
  }

   /**
   * Get currentVersion
   * @return currentVersion
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CURRENT_VERSION)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public ObjectVersion getCurrentVersion() {
    return currentVersion;
  }


  @JsonProperty(CURRENT_VERSION)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCurrentVersion(ObjectVersion currentVersion) {
    this.currentVersion = currentVersion;
  }


  public StorageObjectData className(String className) {
    
    this.className = className;
    return this;
  }

   /**
   * The fully qualified name of the bean class. Normally the URI of the given object can contain the class but also the StorageObjectData contains it. If we have this in the StorageObjectData then we use this. If it is missing then we try to figure out from the URI. 
   * @return className
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The fully qualified name of the bean class. Normally the URI of the given object can contain the class but also the StorageObjectData contains it. If we have this in the StorageObjectData then we use this. If it is missing then we try to figure out from the URI. ")
  @JsonProperty(CLASS_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getClassName() {
    return className;
  }


  @JsonProperty(CLASS_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setClassName(String className) {
    this.className = className;
  }


  public StorageObjectData pendingVersion(ObjectVersion pendingVersion) {
    
    this.pendingVersion = pendingVersion;
    return this;
  }

   /**
   * Get pendingVersion
   * @return pendingVersion
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(PENDING_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ObjectVersion getPendingVersion() {
    return pendingVersion;
  }


  @JsonProperty(PENDING_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPendingVersion(ObjectVersion pendingVersion) {
    this.pendingVersion = pendingVersion;
  }


  public StorageObjectData deleted(Boolean deleted) {
    
    this.deleted = deleted;
    return this;
  }

   /**
   * The deleted flag is set when the given object is deleted. The deletion is always logical so we know that the object exists but we also know that it is already inactivated. 
   * @return deleted
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The deleted flag is set when the given object is deleted. The deletion is always logical so we know that the object exists but we also know that it is already inactivated. ")
  @JsonProperty(DELETED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getDeleted() {
    return deleted;
  }


  @JsonProperty(DELETED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorageObjectData storageObjectData = (StorageObjectData) o;
    return Objects.equals(this.uri, storageObjectData.uri) &&
        Objects.equals(this.currentVersion, storageObjectData.currentVersion) &&
        Objects.equals(this.className, storageObjectData.className) &&
        Objects.equals(this.pendingVersion, storageObjectData.pendingVersion) &&
        Objects.equals(this.deleted, storageObjectData.deleted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, currentVersion, className, pendingVersion, deleted);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StorageObjectData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    currentVersion: ").append(toIndentedString(currentVersion)).append("\n");
    sb.append("    className: ").append(toIndentedString(className)).append("\n");
    sb.append("    pendingVersion: ").append(toIndentedString(pendingVersion)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
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

