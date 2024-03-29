/*
 * DatabaseDefinition api
 * The relational database schema definition with tables, columns, indices, primary and foreign keys. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.databasedefinition.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.databasedefinition.bean.AlterOperation;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The meta of an index in a database table. 
 */
@ApiModel(description = "The meta of an index in a database table. ")
@JsonPropertyOrder({
  IndexDefinition.SCHEMA,
  IndexDefinition.NAME,
  IndexDefinition.UNIQUE,
  IndexDefinition.COLUMN_NAMES,
  IndexDefinition.OPERATION
})
@JsonTypeName("IndexDefinition")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class IndexDefinition {
  public static final String SCHEMA = "schema";
  private String schema;

  public static final String NAME = "name";
  private String name;

  public static final String UNIQUE = "unique";
  private Boolean unique = false;

  public static final String COLUMN_NAMES = "columnNames";
  private List<String> columnNames = new ArrayList<>();

  public static final String OPERATION = "operation";
  private AlterOperation operation;

  public IndexDefinition() { 
  }

  public IndexDefinition schema(String schema) {
    
    this.schema = schema;
    return this;
  }

   /**
   * Get schema
   * @return schema
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SCHEMA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSchema() {
    return schema;
  }


  @JsonProperty(SCHEMA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSchema(String schema) {
    this.schema = schema;
  }


  public IndexDefinition name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  public IndexDefinition unique(Boolean unique) {
    
    this.unique = unique;
    return this;
  }

   /**
   * Get unique
   * @return unique
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(UNIQUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getUnique() {
    return unique;
  }


  @JsonProperty(UNIQUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUnique(Boolean unique) {
    this.unique = unique;
  }


  public IndexDefinition columnNames(List<String> columnNames) {
    
    this.columnNames = columnNames;
    return this;
  }

  public IndexDefinition addColumnNamesItem(String columnNamesItem) {
    this.columnNames.add(columnNamesItem);
    return this;
  }

   /**
   * Get columnNames
   * @return columnNames
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(COLUMN_NAMES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<String> getColumnNames() {
    return columnNames;
  }


  @JsonProperty(COLUMN_NAMES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }


  public IndexDefinition operation(AlterOperation operation) {
    
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

  public AlterOperation getOperation() {
    return operation;
  }


  @JsonProperty(OPERATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setOperation(AlterOperation operation) {
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
    IndexDefinition indexDefinition = (IndexDefinition) o;
    return Objects.equals(this.schema, indexDefinition.schema) &&
        Objects.equals(this.name, indexDefinition.name) &&
        Objects.equals(this.unique, indexDefinition.unique) &&
        Objects.equals(this.columnNames, indexDefinition.columnNames) &&
        Objects.equals(this.operation, indexDefinition.operation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema, name, unique, columnNames, operation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndexDefinition {\n");
    sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    unique: ").append(toIndentedString(unique)).append("\n");
    sb.append("    columnNames: ").append(toIndentedString(columnNames)).append("\n");
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

