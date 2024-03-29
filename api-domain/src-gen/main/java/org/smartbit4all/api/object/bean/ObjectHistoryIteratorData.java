/*
 * Object api
 * The object api responsible for the domain object meta information including the object definitions and the relations among them. These objects are stored because the modules can contribute. The modules have their own ObjectApi that manages the storage and ensure the up-to-date view of the current data. The algorithms are running on the ObjectApi cache refreshed periodically. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.object.bean;

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
import org.smartbit4all.api.session.bean.UserActivityLog;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object represents an object version from the object history. The history can be a huge list where the uris of the history items computed from the original object uri and the version number. It must be easy to compute the version uri because there is no single object that contains all the histories. If we create a list for iterating over the history then this object can be the model of the given page. 
 */
@ApiModel(description = "This object represents an object version from the object history. The history can be a huge list where the uris of the history items computed from the original object uri and the version number. It must be easy to compute the version uri because there is no single object that contains all the histories. If we create a list for iterating over the history then this object can be the model of the given page. ")
@JsonPropertyOrder({
  ObjectHistoryIteratorData.VERSION_URI,
  ObjectHistoryIteratorData.VERSION_NR,
  ObjectHistoryIteratorData.CREATED
})
@JsonTypeName("ObjectHistoryIteratorData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectHistoryIteratorData {
  public static final String VERSION_URI = "versionUri";
  private URI versionUri;

  public static final String VERSION_NR = "versionNr";
  private Long versionNr;

  public static final String CREATED = "created";
  private UserActivityLog created = null;

  public ObjectHistoryIteratorData() { 
  }

  public ObjectHistoryIteratorData versionUri(URI versionUri) {
    
    this.versionUri = versionUri;
    return this;
  }

   /**
   * The uri version uri of the object history entry.
   * @return versionUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri version uri of the object history entry.")
  @JsonProperty(VERSION_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getVersionUri() {
    return versionUri;
  }


  @JsonProperty(VERSION_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setVersionUri(URI versionUri) {
    this.versionUri = versionUri;
  }


  public ObjectHistoryIteratorData versionNr(Long versionNr) {
    
    this.versionNr = versionNr;
    return this;
  }

   /**
   * The serial version number of the object history entry.
   * @return versionNr
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The serial version number of the object history entry.")
  @JsonProperty(VERSION_NR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getVersionNr() {
    return versionNr;
  }


  @JsonProperty(VERSION_NR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setVersionNr(Long versionNr) {
    this.versionNr = versionNr;
  }


  public ObjectHistoryIteratorData created(UserActivityLog created) {
    
    this.created = created;
    return this;
  }

   /**
   * The version creation information.
   * @return created
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The version creation information.")
  @JsonProperty(CREATED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UserActivityLog getCreated() {
    return created;
  }


  @JsonProperty(CREATED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCreated(UserActivityLog created) {
    this.created = created;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectHistoryIteratorData objectHistoryIteratorData = (ObjectHistoryIteratorData) o;
    return Objects.equals(this.versionUri, objectHistoryIteratorData.versionUri) &&
        Objects.equals(this.versionNr, objectHistoryIteratorData.versionNr) &&
        Objects.equals(this.created, objectHistoryIteratorData.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versionUri, versionNr, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectHistoryIteratorData {\n");
    sb.append("    versionUri: ").append(toIndentedString(versionUri)).append("\n");
    sb.append("    versionNr: ").append(toIndentedString(versionNr)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
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

