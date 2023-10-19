/*
 * MasterDataManagement api
 * null
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.mdm.bean;

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
 * This object represents an ongoing modification in an MDMDefinition. It is strored inline in  the MDMDefinitionState, and may be global, entry or group level (just like BranchingStrategy). 
 */
@ApiModel(description = "This object represents an ongoing modification in an MDMDefinition. It is strored inline in  the MDMDefinitionState, and may be global, entry or group level (just like BranchingStrategy). ")
@JsonPropertyOrder({
  MDMModification.BRANCH_URI,
  MDMModification.CREATED,
  MDMModification.APPROVER
})
@JsonTypeName("MDMModification")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MDMModification {
  public static final String BRANCH_URI = "branchUri";
  private URI branchUri;

  public static final String CREATED = "created";
  private UserActivityLog created = null;

  public static final String APPROVER = "approver";
  private URI approver;

  public MDMModification() { 
  }

  public MDMModification branchUri(URI branchUri) {
    
    this.branchUri = branchUri;
    return this;
  }

   /**
   * Get branchUri
   * @return branchUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(BRANCH_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getBranchUri() {
    return branchUri;
  }


  @JsonProperty(BRANCH_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBranchUri(URI branchUri) {
    this.branchUri = branchUri;
  }


  public MDMModification created(UserActivityLog created) {
    
    this.created = created;
    return this;
  }

   /**
   * Get created
   * @return created
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
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


  public MDMModification approver(URI approver) {
    
    this.approver = approver;
    return this;
  }

   /**
   * Get approver
   * @return approver
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(APPROVER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getApprover() {
    return approver;
  }


  @JsonProperty(APPROVER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setApprover(URI approver) {
    this.approver = approver;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MDMModification mdMModification = (MDMModification) o;
    return Objects.equals(this.branchUri, mdMModification.branchUri) &&
        Objects.equals(this.created, mdMModification.created) &&
        Objects.equals(this.approver, mdMModification.approver);
  }

  @Override
  public int hashCode() {
    return Objects.hash(branchUri, created, approver);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MDMModification {\n");
    sb.append("    branchUri: ").append(toIndentedString(branchUri)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    approver: ").append(toIndentedString(approver)).append("\n");
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
