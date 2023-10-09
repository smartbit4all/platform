/*
 * org api
 * org api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.org.bean;

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
import org.smartbit4all.api.org.bean.BulkUpdateOperation;
import org.smartbit4all.api.org.bean.Group;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GroupOfGroupUpdate
 */
@JsonPropertyOrder({
  GroupOfGroupUpdate.URI,
  GroupOfGroupUpdate.PARENT_GROUP,
  GroupOfGroupUpdate.CHILD_GROUP,
  GroupOfGroupUpdate.OPERATION
})
@JsonTypeName("GroupOfGroupUpdate")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GroupOfGroupUpdate {
  public static final String URI = "uri";
  private URI uri;

  public static final String PARENT_GROUP = "parentGroup";
  private Group parentGroup;

  public static final String CHILD_GROUP = "childGroup";
  private Group childGroup;

  public static final String OPERATION = "operation";
  private BulkUpdateOperation operation;

  public GroupOfGroupUpdate() { 
  }

  public GroupOfGroupUpdate uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public GroupOfGroupUpdate parentGroup(Group parentGroup) {
    
    this.parentGroup = parentGroup;
    return this;
  }

   /**
   * Get parentGroup
   * @return parentGroup
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(PARENT_GROUP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Group getParentGroup() {
    return parentGroup;
  }


  @JsonProperty(PARENT_GROUP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setParentGroup(Group parentGroup) {
    this.parentGroup = parentGroup;
  }


  public GroupOfGroupUpdate childGroup(Group childGroup) {
    
    this.childGroup = childGroup;
    return this;
  }

   /**
   * Get childGroup
   * @return childGroup
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CHILD_GROUP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Group getChildGroup() {
    return childGroup;
  }


  @JsonProperty(CHILD_GROUP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChildGroup(Group childGroup) {
    this.childGroup = childGroup;
  }


  public GroupOfGroupUpdate operation(BulkUpdateOperation operation) {
    
    this.operation = operation;
    return this;
  }

   /**
   * Get operation
   * @return operation
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public BulkUpdateOperation getOperation() {
    return operation;
  }


  @JsonProperty(OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperation(BulkUpdateOperation operation) {
    this.operation = operation;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupOfGroupUpdate groupOfGroupUpdate = (GroupOfGroupUpdate) o;
    return Objects.equals(this.uri, groupOfGroupUpdate.uri) &&
        Objects.equals(this.parentGroup, groupOfGroupUpdate.parentGroup) &&
        Objects.equals(this.childGroup, groupOfGroupUpdate.childGroup) &&
        Objects.equals(this.operation, groupOfGroupUpdate.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, parentGroup, childGroup, operation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupOfGroupUpdate {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    parentGroup: ").append(toIndentedString(parentGroup)).append("\n");
    sb.append("    childGroup: ").append(toIndentedString(childGroup)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
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
