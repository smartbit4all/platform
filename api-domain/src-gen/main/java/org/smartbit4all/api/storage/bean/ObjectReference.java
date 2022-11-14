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
import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * In generally if we have an object stored by a Storage then we might need to store some object references for this.  The related object references are more or less a list of URI or other identifier easy to serialize in every storage mechanism.  The Storage can publish events when the given object is changed. This can be filtered by this register.  The reference can be temporary so we can define a time limit when the Storage will remove the reference.  The relation can be renewed by adding it again and again. This object itself is managed by the ObjectStorage to store this in an  optimal way for the given storage mechanism. 
 */
@ApiModel(description = "In generally if we have an object stored by a Storage then we might need to store some object references for this.  The related object references are more or less a list of URI or other identifier easy to serialize in every storage mechanism.  The Storage can publish events when the given object is changed. This can be filtered by this register.  The reference can be temporary so we can define a time limit when the Storage will remove the reference.  The relation can be renewed by adding it again and again. This object itself is managed by the ObjectStorage to store this in an  optimal way for the given storage mechanism. ")
@JsonPropertyOrder({
  ObjectReference.URI,
  ObjectReference.REFERENCE_ID,
  ObjectReference.EXPIRATION_TIME
})
@JsonTypeName("ObjectReference")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectReference {
  public static final String URI = "uri";
  private URI uri;

  public static final String REFERENCE_ID = "referenceId";
  private String referenceId;

  public static final String EXPIRATION_TIME = "expirationTime";
  private OffsetDateTime expirationTime;

  public ObjectReference() { 
  }

  public ObjectReference uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The identifier of the referenced object. This is an URI that identifies the object in the domain. 
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The identifier of the referenced object. This is an URI that identifies the object in the domain. ")
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


  public ObjectReference referenceId(String referenceId) {
    
    this.referenceId = referenceId;
    return this;
  }

   /**
   * To separate the references from the same class the api can use this field to store specific data about the referred object. 
   * @return referenceId
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "To separate the references from the same class the api can use this field to store specific data about the referred object. ")
  @JsonProperty(REFERENCE_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getReferenceId() {
    return referenceId;
  }


  @JsonProperty(REFERENCE_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }


  public ObjectReference expirationTime(OffsetDateTime expirationTime) {
    
    this.expirationTime = expirationTime;
    return this;
  }

   /**
   * The expiration time for the reference.
   * @return expirationTime
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The expiration time for the reference.")
  @JsonProperty(EXPIRATION_TIME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OffsetDateTime getExpirationTime() {
    return expirationTime;
  }


  @JsonProperty(EXPIRATION_TIME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpirationTime(OffsetDateTime expirationTime) {
    this.expirationTime = expirationTime;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectReference objectReference = (ObjectReference) o;
    return Objects.equals(this.uri, objectReference.uri) &&
        Objects.equals(this.referenceId, objectReference.referenceId) &&
        Objects.equals(this.expirationTime, objectReference.expirationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, referenceId, expirationTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectReference {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    referenceId: ").append(toIndentedString(referenceId)).append("\n");
    sb.append("    expirationTime: ").append(toIndentedString(expirationTime)).append("\n");
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

