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
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.BranchOperation;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The object branch data is used to hold all the operations about an object on a branch. This object is constructed when the first opartion is executed with the given object. 
 */
@ApiModel(description = "The object branch data is used to hold all the operations about an object on a branch. This object is constructed when the first opartion is executed with the given object. ")
@JsonPropertyOrder({
  BranchedObject.SOURCE_OBJECT_LATEST_URI,
  BranchedObject.BRANCHED_OBJECT_LATEST_URI,
  BranchedObject.OPERATIONS
})
@JsonTypeName("BranchedObject")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class BranchedObject {
  public static final String SOURCE_OBJECT_LATEST_URI = "sourceObjectLatestUri";
  private URI sourceObjectLatestUri;

  public static final String BRANCHED_OBJECT_LATEST_URI = "branchedObjectLatestUri";
  private URI branchedObjectLatestUri;

  public static final String OPERATIONS = "operations";
  private List<BranchOperation> operations = null;

  public BranchedObject() { 
  }

  public BranchedObject sourceObjectLatestUri(URI sourceObjectLatestUri) {
    
    this.sourceObjectLatestUri = sourceObjectLatestUri;
    return this;
  }

   /**
   * The latest uri of the source object.
   * @return sourceObjectLatestUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The latest uri of the source object.")
  @JsonProperty(SOURCE_OBJECT_LATEST_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getSourceObjectLatestUri() {
    return sourceObjectLatestUri;
  }


  @JsonProperty(SOURCE_OBJECT_LATEST_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSourceObjectLatestUri(URI sourceObjectLatestUri) {
    this.sourceObjectLatestUri = sourceObjectLatestUri;
  }


  public BranchedObject branchedObjectLatestUri(URI branchedObjectLatestUri) {
    
    this.branchedObjectLatestUri = branchedObjectLatestUri;
    return this;
  }

   /**
   * The latest uri of the branched object.
   * @return branchedObjectLatestUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The latest uri of the branched object.")
  @JsonProperty(BRANCHED_OBJECT_LATEST_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getBranchedObjectLatestUri() {
    return branchedObjectLatestUri;
  }


  @JsonProperty(BRANCHED_OBJECT_LATEST_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBranchedObjectLatestUri(URI branchedObjectLatestUri) {
    this.branchedObjectLatestUri = branchedObjectLatestUri;
  }


  public BranchedObject operations(List<BranchOperation> operations) {
    
    this.operations = operations;
    return this;
  }

  public BranchedObject addOperationsItem(BranchOperation operationsItem) {
    if (this.operations == null) {
      this.operations = new ArrayList<>();
    }
    this.operations.add(operationsItem);
    return this;
  }

   /**
   * The init, rebase and merge operations of the branched object. The init is the original construction of the branched object. The rebase is accepting the new version from the source and the merge is the  publishing the branched version as a new source version. 
   * @return operations
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The init, rebase and merge operations of the branched object. The init is the original construction of the branched object. The rebase is accepting the new version from the source and the merge is the  publishing the branched version as a new source version. ")
  @JsonProperty(OPERATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<BranchOperation> getOperations() {
    return operations;
  }


  @JsonProperty(OPERATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperations(List<BranchOperation> operations) {
    this.operations = operations;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BranchedObject branchedObject = (BranchedObject) o;
    return Objects.equals(this.sourceObjectLatestUri, branchedObject.sourceObjectLatestUri) &&
        Objects.equals(this.branchedObjectLatestUri, branchedObject.branchedObjectLatestUri) &&
        Objects.equals(this.operations, branchedObject.operations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceObjectLatestUri, branchedObjectLatestUri, operations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BranchedObject {\n");
    sb.append("    sourceObjectLatestUri: ").append(toIndentedString(sourceObjectLatestUri)).append("\n");
    sb.append("    branchedObjectLatestUri: ").append(toIndentedString(branchedObjectLatestUri)).append("\n");
    sb.append("    operations: ").append(toIndentedString(operations)).append("\n");
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
