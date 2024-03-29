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
import org.smartbit4all.api.storage.bean.ObjectAspect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The reference for a given version of the storage object. It contains all the properties about the version like time, user uri,  api operation that created the version. 
 */
@ApiModel(description = "The reference for a given version of the storage object. It contains all the properties about the version like time, user uri,  api operation that created the version. ")
@JsonPropertyOrder({
  ObjectVersion.SERIAL_NO_DATA,
  ObjectVersion.SERIAL_NO_RELATION,
  ObjectVersion.TRANSACTION_ID,
  ObjectVersion.CREATED_AT,
  ObjectVersion.CREATED_BY_URI,
  ObjectVersion.CREATED_BY,
  ObjectVersion.OPERATION,
  ObjectVersion.REBASED_FROM_URI,
  ObjectVersion.COMMON_ANCESTOR_URI,
  ObjectVersion.MERGED_WITH_URI,
  ObjectVersion.ASPECTS
})
@JsonTypeName("ObjectVersion")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectVersion {
  public static final String SERIAL_NO_DATA = "serialNoData";
  private Long serialNoData;

  public static final String SERIAL_NO_RELATION = "serialNoRelation";
  private Long serialNoRelation;

  public static final String TRANSACTION_ID = "transactionId";
  private String transactionId;

  public static final String CREATED_AT = "createdAt";
  private OffsetDateTime createdAt;

  public static final String CREATED_BY_URI = "createdByUri";
  private URI createdByUri;

  public static final String CREATED_BY = "createdBy";
  private String createdBy;

  public static final String OPERATION = "operation";
  private String operation;

  public static final String REBASED_FROM_URI = "rebasedFromUri";
  private URI rebasedFromUri;

  public static final String COMMON_ANCESTOR_URI = "commonAncestorUri";
  private URI commonAncestorUri;

  public static final String MERGED_WITH_URI = "mergedWithUri";
  private URI mergedWithUri;

  public static final String ASPECTS = "aspects";
  private Map<String, ObjectAspect> aspects = null;

  public ObjectVersion() { 
  }

  public ObjectVersion serialNoData(Long serialNoData) {
    
    this.serialNoData = serialNoData;
    return this;
  }

   /**
   * If it is set then there is an attached version data.
   * @return serialNoData
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If it is set then there is an attached version data.")
  @JsonProperty(SERIAL_NO_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getSerialNoData() {
    return serialNoData;
  }


  @JsonProperty(SERIAL_NO_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSerialNoData(Long serialNoData) {
    this.serialNoData = serialNoData;
  }


  public ObjectVersion serialNoRelation(Long serialNoRelation) {
    
    this.serialNoRelation = serialNoRelation;
    return this;
  }

   /**
   * If it is set then there is an attached version relation set.
   * @return serialNoRelation
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If it is set then there is an attached version relation set.")
  @JsonProperty(SERIAL_NO_RELATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Long getSerialNoRelation() {
    return serialNoRelation;
  }


  @JsonProperty(SERIAL_NO_RELATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSerialNoRelation(Long serialNoRelation) {
    this.serialNoRelation = serialNoRelation;
  }


  public ObjectVersion transactionId(String transactionId) {
    
    this.transactionId = transactionId;
    return this;
  }

   /**
   * The unique identifier of the transaction that constructed this version from the object. It could be used to double check if a given transaction was successful. 
   * @return transactionId
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The unique identifier of the transaction that constructed this version from the object. It could be used to double check if a given transaction was successful. ")
  @JsonProperty(TRANSACTION_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTransactionId() {
    return transactionId;
  }


  @JsonProperty(TRANSACTION_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }


  public ObjectVersion createdAt(OffsetDateTime createdAt) {
    
    this.createdAt = createdAt;
    return this;
  }

   /**
   * The exact date time when the given version was created at.
   * @return createdAt
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The exact date time when the given version was created at.")
  @JsonProperty(CREATED_AT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }


  @JsonProperty(CREATED_AT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }


  public ObjectVersion createdByUri(URI createdByUri) {
    
    this.createdByUri = createdByUri;
    return this;
  }

   /**
   * The reference of the user or any other participant who created the given version.
   * @return createdByUri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The reference of the user or any other participant who created the given version.")
  @JsonProperty(CREATED_BY_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getCreatedByUri() {
    return createdByUri;
  }


  @JsonProperty(CREATED_BY_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCreatedByUri(URI createdByUri) {
    this.createdByUri = createdByUri;
  }


  public ObjectVersion createdBy(String createdBy) {
    
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


  public ObjectVersion operation(String operation) {
    
    this.operation = operation;
    return this;
  }

   /**
   * The Api and the operation that was created the given version of the object.
   * @return operation
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The Api and the operation that was created the given version of the object.")
  @JsonProperty(OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getOperation() {
    return operation;
  }


  @JsonProperty(OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperation(String operation) {
    this.operation = operation;
  }


  public ObjectVersion rebasedFromUri(URI rebasedFromUri) {
    
    this.rebasedFromUri = rebasedFromUri;
    return this;
  }

   /**
   * The reference of the object version the version is based on. In case of branching this is the URI of the source  object version if it is a new object on the branch. 
   * @return rebasedFromUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The reference of the object version the version is based on. In case of branching this is the URI of the source  object version if it is a new object on the branch. ")
  @JsonProperty(REBASED_FROM_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getRebasedFromUri() {
    return rebasedFromUri;
  }


  @JsonProperty(REBASED_FROM_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRebasedFromUri(URI rebasedFromUri) {
    this.rebasedFromUri = rebasedFromUri;
  }


  public ObjectVersion commonAncestorUri(URI commonAncestorUri) {
    
    this.commonAncestorUri = commonAncestorUri;
    return this;
  }

   /**
   * The reference of the object version the version is based on. In the result of a merge operation it is the URI of the common ancestor. 
   * @return commonAncestorUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The reference of the object version the version is based on. In the result of a merge operation it is the URI of the common ancestor. ")
  @JsonProperty(COMMON_ANCESTOR_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getCommonAncestorUri() {
    return commonAncestorUri;
  }


  @JsonProperty(COMMON_ANCESTOR_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCommonAncestorUri(URI commonAncestorUri) {
    this.commonAncestorUri = commonAncestorUri;
  }


  public ObjectVersion mergedWithUri(URI mergedWithUri) {
    
    this.mergedWithUri = mergedWithUri;
    return this;
  }

   /**
   * The reference of the object version the version is based on. In case of branching this is the URI of the source  object version. 
   * @return mergedWithUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The reference of the object version the version is based on. In case of branching this is the URI of the source  object version. ")
  @JsonProperty(MERGED_WITH_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getMergedWithUri() {
    return mergedWithUri;
  }


  @JsonProperty(MERGED_WITH_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMergedWithUri(URI mergedWithUri) {
    this.mergedWithUri = mergedWithUri;
  }


  public ObjectVersion aspects(Map<String, ObjectAspect> aspects) {
    
    this.aspects = aspects;
    return this;
  }

  public ObjectVersion putAspectsItem(String key, ObjectAspect aspectsItem) {
    if (this.aspects == null) {
      this.aspects = new HashMap<>();
    }
    this.aspects.put(key, aspectsItem);
    return this;
  }

   /**
   * The aspects are named objects attached to the given object version. 
   * @return aspects
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The aspects are named objects attached to the given object version. ")
  @JsonProperty(ASPECTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, ObjectAspect> getAspects() {
    return aspects;
  }


  @JsonProperty(ASPECTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAspects(Map<String, ObjectAspect> aspects) {
    this.aspects = aspects;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectVersion objectVersion = (ObjectVersion) o;
    return Objects.equals(this.serialNoData, objectVersion.serialNoData) &&
        Objects.equals(this.serialNoRelation, objectVersion.serialNoRelation) &&
        Objects.equals(this.transactionId, objectVersion.transactionId) &&
        Objects.equals(this.createdAt, objectVersion.createdAt) &&
        Objects.equals(this.createdByUri, objectVersion.createdByUri) &&
        Objects.equals(this.createdBy, objectVersion.createdBy) &&
        Objects.equals(this.operation, objectVersion.operation) &&
        Objects.equals(this.rebasedFromUri, objectVersion.rebasedFromUri) &&
        Objects.equals(this.commonAncestorUri, objectVersion.commonAncestorUri) &&
        Objects.equals(this.mergedWithUri, objectVersion.mergedWithUri) &&
        Objects.equals(this.aspects, objectVersion.aspects);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serialNoData, serialNoRelation, transactionId, createdAt, createdByUri, createdBy, operation, rebasedFromUri, commonAncestorUri, mergedWithUri, aspects);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectVersion {\n");
    sb.append("    serialNoData: ").append(toIndentedString(serialNoData)).append("\n");
    sb.append("    serialNoRelation: ").append(toIndentedString(serialNoRelation)).append("\n");
    sb.append("    transactionId: ").append(toIndentedString(transactionId)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    createdByUri: ").append(toIndentedString(createdByUri)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    rebasedFromUri: ").append(toIndentedString(rebasedFromUri)).append("\n");
    sb.append("    commonAncestorUri: ").append(toIndentedString(commonAncestorUri)).append("\n");
    sb.append("    mergedWithUri: ").append(toIndentedString(mergedWithUri)).append("\n");
    sb.append("    aspects: ").append(toIndentedString(aspects)).append("\n");
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

