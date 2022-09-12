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
 * The data of the operation in a branch. 
 */
@ApiModel(description = "The data of the operation in a branch. ")
@JsonPropertyOrder({
  BranchOperation.SOURCE_URI,
  BranchOperation.TARGET_URI,
  BranchOperation.OPERATION_TYPE,
  BranchOperation.CREATED_AT,
  BranchOperation.CREATED_BY_URI,
  BranchOperation.CREATED_BY,
  BranchOperation.OPERATION
})
@JsonTypeName("BranchOperation")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class BranchOperation {
  public static final String SOURCE_URI = "sourceUri";
  private URI sourceUri;

  public static final String TARGET_URI = "targetUri";
  private URI targetUri;

  /**
   * Gets or Sets operationType
   */
  public enum OperationTypeEnum {
    INIT("init"),
    
    REBASE("rebase"),
    
    MERGE("merge");

    private String value;

    OperationTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OperationTypeEnum fromValue(String value) {
      for (OperationTypeEnum b : OperationTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String OPERATION_TYPE = "operationType";
  private OperationTypeEnum operationType;

  public static final String CREATED_AT = "createdAt";
  private OffsetDateTime createdAt;

  public static final String CREATED_BY_URI = "createdByUri";
  private URI createdByUri;

  public static final String CREATED_BY = "createdBy";
  private String createdBy;

  public static final String OPERATION = "operation";
  private String operation;


  public BranchOperation sourceUri(URI sourceUri) {
    
    this.sourceUri = sourceUri;
    return this;
  }

   /**
   * Get sourceUri
   * @return sourceUri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(SOURCE_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getSourceUri() {
    return sourceUri;
  }


  @JsonProperty(SOURCE_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSourceUri(URI sourceUri) {
    this.sourceUri = sourceUri;
  }


  public BranchOperation targetUri(URI targetUri) {
    
    this.targetUri = targetUri;
    return this;
  }

   /**
   * Get targetUri
   * @return targetUri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(TARGET_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getTargetUri() {
    return targetUri;
  }


  @JsonProperty(TARGET_URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setTargetUri(URI targetUri) {
    this.targetUri = targetUri;
  }


  public BranchOperation operationType(OperationTypeEnum operationType) {
    
    this.operationType = operationType;
    return this;
  }

   /**
   * Get operationType
   * @return operationType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(OPERATION_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public OperationTypeEnum getOperationType() {
    return operationType;
  }


  @JsonProperty(OPERATION_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperationType(OperationTypeEnum operationType) {
    this.operationType = operationType;
  }


  public BranchOperation createdAt(OffsetDateTime createdAt) {
    
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


  public BranchOperation createdByUri(URI createdByUri) {
    
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


  public BranchOperation createdBy(String createdBy) {
    
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


  public BranchOperation operation(String operation) {
    
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BranchOperation branchOperation = (BranchOperation) o;
    return Objects.equals(this.sourceUri, branchOperation.sourceUri) &&
        Objects.equals(this.targetUri, branchOperation.targetUri) &&
        Objects.equals(this.operationType, branchOperation.operationType) &&
        Objects.equals(this.createdAt, branchOperation.createdAt) &&
        Objects.equals(this.createdByUri, branchOperation.createdByUri) &&
        Objects.equals(this.createdBy, branchOperation.createdBy) &&
        Objects.equals(this.operation, branchOperation.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceUri, targetUri, operationType, createdAt, createdByUri, createdBy, operation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BranchOperation {\n");
    sb.append("    sourceUri: ").append(toIndentedString(sourceUri)).append("\n");
    sb.append("    targetUri: ").append(toIndentedString(targetUri)).append("\n");
    sb.append("    operationType: ").append(toIndentedString(operationType)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    createdByUri: ").append(toIndentedString(createdByUri)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
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
