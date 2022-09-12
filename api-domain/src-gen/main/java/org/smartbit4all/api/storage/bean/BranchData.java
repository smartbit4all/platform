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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.storage.bean.ObjectBranchData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The branch data is used to identify the changes of the objects in on a branch. The object can be constructed and  saved without any operation. If the original object is saved on this branch then the operation is registered into  this object and executed directly. 
 */
@ApiModel(description = "The branch data is used to identify the changes of the objects in on a branch. The object can be constructed and  saved without any operation. If the original object is saved on this branch then the operation is registered into  this object and executed directly. ")
@JsonPropertyOrder({
  BranchData.URI,
  BranchData.CAPTION,
  BranchData.CREATED_AT,
  BranchData.CREATED_BY_URI,
  BranchData.CREATED_BY,
  BranchData.OBJECTS
})
@JsonTypeName("BranchData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class BranchData {
  public static final String URI = "uri";
  private URI uri;

  public static final String CAPTION = "caption";
  private String caption;

  public static final String CREATED_AT = "createdAt";
  private OffsetDateTime createdAt;

  public static final String CREATED_BY_URI = "createdByUri";
  private URI createdByUri;

  public static final String CREATED_BY = "createdBy";
  private String createdBy;

  public static final String OBJECTS = "objects";
  private Map<String, ObjectBranchData> objects = new HashMap<>();


  public BranchData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The uri of the object.
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The uri of the object.")
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


  public BranchData caption(String caption) {
    
    this.caption = caption;
    return this;
  }

   /**
   * The branch could be used as business domain object. If we collect the versions of an object then useng the caption we can identify the intent of the modification. 
   * @return caption
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The branch could be used as business domain object. If we collect the versions of an object then useng the caption we can identify the intent of the modification. ")
  @JsonProperty(CAPTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCaption() {
    return caption;
  }


  @JsonProperty(CAPTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCaption(String caption) {
    this.caption = caption;
  }


  public BranchData createdAt(OffsetDateTime createdAt) {
    
    this.createdAt = createdAt;
    return this;
  }

   /**
   * The exact date time when the given version was created at.
   * @return createdAt
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The exact date time when the given version was created at.")
  @JsonProperty(CREATED_AT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }


  @JsonProperty(CREATED_AT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }


  public BranchData createdByUri(URI createdByUri) {
    
    this.createdByUri = createdByUri;
    return this;
  }

   /**
   * The reference of the user or any other participant who created the given version.
   * @return createdByUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The reference of the user or any other participant who created the given version.")
  @JsonProperty(CREATED_BY_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getCreatedByUri() {
    return createdByUri;
  }


  @JsonProperty(CREATED_BY_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCreatedByUri(URI createdByUri) {
    this.createdByUri = createdByUri;
  }


  public BranchData createdBy(String createdBy) {
    
    this.createdBy = createdBy;
    return this;
  }

   /**
   * The display name of the user or any other participant who created the given version.
   * @return createdBy
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The display name of the user or any other participant who created the given version.")
  @JsonProperty(CREATED_BY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCreatedBy() {
    return createdBy;
  }


  @JsonProperty(CREATED_BY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }


  public BranchData objects(Map<String, ObjectBranchData> objects) {
    
    this.objects = objects;
    return this;
  }

  public BranchData putObjectsItem(String key, ObjectBranchData objectsItem) {
    this.objects.put(key, objectsItem);
    return this;
  }

   /**
   * Get objects
   * @return objects
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(OBJECTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, ObjectBranchData> getObjects() {
    return objects;
  }


  @JsonProperty(OBJECTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setObjects(Map<String, ObjectBranchData> objects) {
    this.objects = objects;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BranchData branchData = (BranchData) o;
    return Objects.equals(this.uri, branchData.uri) &&
        Objects.equals(this.caption, branchData.caption) &&
        Objects.equals(this.createdAt, branchData.createdAt) &&
        Objects.equals(this.createdByUri, branchData.createdByUri) &&
        Objects.equals(this.createdBy, branchData.createdBy) &&
        Objects.equals(this.objects, branchData.objects);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, caption, createdAt, createdByUri, createdBy, objects);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BranchData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    caption: ").append(toIndentedString(caption)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    createdByUri: ").append(toIndentedString(createdByUri)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    objects: ").append(toIndentedString(objects)).append("\n");
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
