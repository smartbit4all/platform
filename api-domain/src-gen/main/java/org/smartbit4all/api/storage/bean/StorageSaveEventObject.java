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
 * When StorageObjects are saved the saving process may manage StorageSaveEvents which also can be stored. For this purpose this object can be used. A StorageSaveEventObject point to the next event. This way a linked list can be created of these events. 
 */
@ApiModel(description = "When StorageObjects are saved the saving process may manage StorageSaveEvents which also can be stored. For this purpose this object can be used. A StorageSaveEventObject point to the next event. This way a linked list can be created of these events. ")
@JsonPropertyOrder({
  StorageSaveEventObject.URI,
  StorageSaveEventObject.OLD_VERSION,
  StorageSaveEventObject.NEW_VERSION,
  StorageSaveEventObject.NEXT_EVENT
})
@JsonTypeName("StorageSaveEventObject")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class StorageSaveEventObject {
  public static final String URI = "uri";
  private URI uri;

  public static final String OLD_VERSION = "oldVersion";
  private URI oldVersion;

  public static final String NEW_VERSION = "newVersion";
  private URI newVersion;

  public static final String NEXT_EVENT = "nextEvent";
  private URI nextEvent;


  public StorageSaveEventObject uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The identifier of the StorageSaveEventObject object.   
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The identifier of the StorageSaveEventObject object.   ")
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


  public StorageSaveEventObject oldVersion(URI oldVersion) {
    
    this.oldVersion = oldVersion;
    return this;
  }

   /**
   * The uri of the object before the save event. It can be null if the object is a new instance. 
   * @return oldVersion
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri of the object before the save event. It can be null if the object is a new instance. ")
  @JsonProperty(OLD_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getOldVersion() {
    return oldVersion;
  }


  @JsonProperty(OLD_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOldVersion(URI oldVersion) {
    this.oldVersion = oldVersion;
  }


  public StorageSaveEventObject newVersion(URI newVersion) {
    
    this.newVersion = newVersion;
    return this;
  }

   /**
   * The uri of the object after the save event. 
   * @return newVersion
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri of the object after the save event. ")
  @JsonProperty(NEW_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getNewVersion() {
    return newVersion;
  }


  @JsonProperty(NEW_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNewVersion(URI newVersion) {
    this.newVersion = newVersion;
  }


  public StorageSaveEventObject nextEvent(URI nextEvent) {
    
    this.nextEvent = nextEvent;
    return this;
  }

   /**
   * The uri of the next save event. 
   * @return nextEvent
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri of the next save event. ")
  @JsonProperty(NEXT_EVENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getNextEvent() {
    return nextEvent;
  }


  @JsonProperty(NEXT_EVENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNextEvent(URI nextEvent) {
    this.nextEvent = nextEvent;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StorageSaveEventObject storageSaveEventObject = (StorageSaveEventObject) o;
    return Objects.equals(this.uri, storageSaveEventObject.uri) &&
        Objects.equals(this.oldVersion, storageSaveEventObject.oldVersion) &&
        Objects.equals(this.newVersion, storageSaveEventObject.newVersion) &&
        Objects.equals(this.nextEvent, storageSaveEventObject.nextEvent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, oldVersion, newVersion, nextEvent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StorageSaveEventObject {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    oldVersion: ").append(toIndentedString(oldVersion)).append("\n");
    sb.append("    newVersion: ").append(toIndentedString(newVersion)).append("\n");
    sb.append("    nextEvent: ").append(toIndentedString(nextEvent)).append("\n");
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
