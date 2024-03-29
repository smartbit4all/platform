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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.object.bean.SnapshotDataRef;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The snapshot is the copy of the ObjectNode at a given time. It contains recursively all the snapshots of the referred objects that were available when the ObjetNode was loaded. Taking a snapshot can be executed on a  loaded ObjectNode. 
 */
@ApiModel(description = "The snapshot is the copy of the ObjectNode at a given time. It contains recursively all the snapshots of the referred objects that were available when the ObjetNode was loaded. Taking a snapshot can be executed on a  loaded ObjectNode. ")
@JsonPropertyOrder({
  SnapshotData.OBJECT_URI,
  SnapshotData.QUALIFIED_NAME,
  SnapshotData.STORAGE_SCHEMA,
  SnapshotData.VERSION_NR,
  SnapshotData.INCLUDE_DATA,
  SnapshotData.LATEST_URI,
  SnapshotData.OBJECT_AS_MAP,
  SnapshotData.REFERENCES,
  SnapshotData.REFERENCE_LISTS,
  SnapshotData.REFERENCE_MAPS,
  SnapshotData.RESULT_URI
})
@JsonTypeName("SnapshotData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SnapshotData {
  public static final String OBJECT_URI = "objectUri";
  private URI objectUri;

  public static final String QUALIFIED_NAME = "qualifiedName";
  private String qualifiedName;

  public static final String STORAGE_SCHEMA = "storageSchema";
  private String storageSchema;

  public static final String VERSION_NR = "versionNr";
  private Long versionNr;

  public static final String INCLUDE_DATA = "includeData";
  private Boolean includeData = true;

  public static final String LATEST_URI = "latestUri";
  private Boolean latestUri = false;

  public static final String OBJECT_AS_MAP = "objectAsMap";
  private Map<String, Object> objectAsMap = null;

  public static final String REFERENCES = "references";
  private Map<String, SnapshotDataRef> references = new HashMap<>();

  public static final String REFERENCE_LISTS = "referenceLists";
  private Map<String, List<SnapshotDataRef>> referenceLists = new HashMap<>();

  public static final String REFERENCE_MAPS = "referenceMaps";
  private Map<String, Map<String, SnapshotDataRef>> referenceMaps = new HashMap<>();

  public static final String RESULT_URI = "resultUri";
  private URI resultUri;

  public SnapshotData() { 
  }

  public SnapshotData objectUri(URI objectUri) {
    
    this.objectUri = objectUri;
    return this;
  }

   /**
   * Get objectUri
   * @return objectUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getObjectUri() {
    return objectUri;
  }


  @JsonProperty(OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }


  public SnapshotData qualifiedName(String qualifiedName) {
    
    this.qualifiedName = qualifiedName;
    return this;
  }

   /**
   * Get qualifiedName
   * @return qualifiedName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getQualifiedName() {
    return qualifiedName;
  }


  @JsonProperty(QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setQualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }


  public SnapshotData storageSchema(String storageSchema) {
    
    this.storageSchema = storageSchema;
    return this;
  }

   /**
   * Get storageSchema
   * @return storageSchema
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(STORAGE_SCHEMA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getStorageSchema() {
    return storageSchema;
  }


  @JsonProperty(STORAGE_SCHEMA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStorageSchema(String storageSchema) {
    this.storageSchema = storageSchema;
  }


  public SnapshotData versionNr(Long versionNr) {
    
    this.versionNr = versionNr;
    return this;
  }

   /**
   * Get versionNr
   * @return versionNr
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
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


  public SnapshotData includeData(Boolean includeData) {
    
    this.includeData = includeData;
    return this;
  }

   /**
   * Indicates whether the data of the node is included in the snapshot.
   * @return includeData
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Indicates whether the data of the node is included in the snapshot.")
  @JsonProperty(INCLUDE_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getIncludeData() {
    return includeData;
  }


  @JsonProperty(INCLUDE_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIncludeData(Boolean includeData) {
    this.includeData = includeData;
  }


  public SnapshotData latestUri(Boolean latestUri) {
    
    this.latestUri = latestUri;
    return this;
  }

   /**
   * Indicates whether the data of the node contains latest uri in the snapshot. It counts only if it doesn&#39;t contain the data. 
   * @return latestUri
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Indicates whether the data of the node contains latest uri in the snapshot. It counts only if it doesn't contain the data. ")
  @JsonProperty(LATEST_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getLatestUri() {
    return latestUri;
  }


  @JsonProperty(LATEST_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLatestUri(Boolean latestUri) {
    this.latestUri = latestUri;
  }


  public SnapshotData objectAsMap(Map<String, Object> objectAsMap) {
    
    this.objectAsMap = objectAsMap;
    return this;
  }

  public SnapshotData putObjectAsMapItem(String key, Object objectAsMapItem) {
    if (this.objectAsMap == null) {
      this.objectAsMap = new HashMap<>();
    }
    this.objectAsMap.put(key, objectAsMapItem);
    return this;
  }

   /**
   * Get objectAsMap
   * @return objectAsMap
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(OBJECT_AS_MAP)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, Object> getObjectAsMap() {
    return objectAsMap;
  }


  @JsonProperty(OBJECT_AS_MAP)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.USE_DEFAULTS)
  public void setObjectAsMap(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
  }


  public SnapshotData references(Map<String, SnapshotDataRef> references) {
    
    this.references = references;
    return this;
  }

  public SnapshotData putReferencesItem(String key, SnapshotDataRef referencesItem) {
    this.references.put(key, referencesItem);
    return this;
  }

   /**
   * Get references
   * @return references
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(REFERENCES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, SnapshotDataRef> getReferences() {
    return references;
  }


  @JsonProperty(REFERENCES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setReferences(Map<String, SnapshotDataRef> references) {
    this.references = references;
  }


  public SnapshotData referenceLists(Map<String, List<SnapshotDataRef>> referenceLists) {
    
    this.referenceLists = referenceLists;
    return this;
  }

  public SnapshotData putReferenceListsItem(String key, List<SnapshotDataRef> referenceListsItem) {
    this.referenceLists.put(key, referenceListsItem);
    return this;
  }

   /**
   * Get referenceLists
   * @return referenceLists
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(REFERENCE_LISTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, List<SnapshotDataRef>> getReferenceLists() {
    return referenceLists;
  }


  @JsonProperty(REFERENCE_LISTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setReferenceLists(Map<String, List<SnapshotDataRef>> referenceLists) {
    this.referenceLists = referenceLists;
  }


  public SnapshotData referenceMaps(Map<String, Map<String, SnapshotDataRef>> referenceMaps) {
    
    this.referenceMaps = referenceMaps;
    return this;
  }

  public SnapshotData putReferenceMapsItem(String key, Map<String, SnapshotDataRef> referenceMapsItem) {
    this.referenceMaps.put(key, referenceMapsItem);
    return this;
  }

   /**
   * Get referenceMaps
   * @return referenceMaps
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(REFERENCE_MAPS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, Map<String, SnapshotDataRef>> getReferenceMaps() {
    return referenceMaps;
  }


  @JsonProperty(REFERENCE_MAPS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setReferenceMaps(Map<String, Map<String, SnapshotDataRef>> referenceMaps) {
    this.referenceMaps = referenceMaps;
  }


  public SnapshotData resultUri(URI resultUri) {
    
    this.resultUri = resultUri;
    return this;
  }

   /**
   * The object URI after an ObjectApi.save. It can be used to read the result Uri after save. 
   * @return resultUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The object URI after an ObjectApi.save. It can be used to read the result Uri after save. ")
  @JsonProperty(RESULT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getResultUri() {
    return resultUri;
  }


  @JsonProperty(RESULT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setResultUri(URI resultUri) {
    this.resultUri = resultUri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SnapshotData snapshotData = (SnapshotData) o;
    return Objects.equals(this.objectUri, snapshotData.objectUri) &&
        Objects.equals(this.qualifiedName, snapshotData.qualifiedName) &&
        Objects.equals(this.storageSchema, snapshotData.storageSchema) &&
        Objects.equals(this.versionNr, snapshotData.versionNr) &&
        Objects.equals(this.includeData, snapshotData.includeData) &&
        Objects.equals(this.latestUri, snapshotData.latestUri) &&
        Objects.equals(this.objectAsMap, snapshotData.objectAsMap) &&
        Objects.equals(this.references, snapshotData.references) &&
        Objects.equals(this.referenceLists, snapshotData.referenceLists) &&
        Objects.equals(this.referenceMaps, snapshotData.referenceMaps) &&
        Objects.equals(this.resultUri, snapshotData.resultUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(objectUri, qualifiedName, storageSchema, versionNr, includeData, latestUri, objectAsMap, references, referenceLists, referenceMaps, resultUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SnapshotData {\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
    sb.append("    storageSchema: ").append(toIndentedString(storageSchema)).append("\n");
    sb.append("    versionNr: ").append(toIndentedString(versionNr)).append("\n");
    sb.append("    includeData: ").append(toIndentedString(includeData)).append("\n");
    sb.append("    latestUri: ").append(toIndentedString(latestUri)).append("\n");
    sb.append("    objectAsMap: ").append(toIndentedString(objectAsMap)).append("\n");
    sb.append("    references: ").append(toIndentedString(references)).append("\n");
    sb.append("    referenceLists: ").append(toIndentedString(referenceLists)).append("\n");
    sb.append("    referenceMaps: ").append(toIndentedString(referenceMaps)).append("\n");
    sb.append("    resultUri: ").append(toIndentedString(resultUri)).append("\n");
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

