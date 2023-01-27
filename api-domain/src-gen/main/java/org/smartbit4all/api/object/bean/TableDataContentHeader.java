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
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.object.bean.TableDataContentColumn;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object is a table data header that defines the columns of the table data. 
 */
@ApiModel(description = "This object is a table data header that defines the columns of the table data. ")
@JsonPropertyOrder({
  TableDataContentHeader.COLUMNS
})
@JsonTypeName("TableDataContentHeader")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class TableDataContentHeader {
  public static final String COLUMNS = "columns";
  private List<TableDataContentColumn> columns = new ArrayList<>();

  public TableDataContentHeader() { 
  }

  public TableDataContentHeader columns(List<TableDataContentColumn> columns) {
    
    this.columns = columns;
    return this;
  }

  public TableDataContentHeader addColumnsItem(TableDataContentColumn columnsItem) {
    this.columns.add(columnsItem);
    return this;
  }

   /**
   * Get columns
   * @return columns
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(COLUMNS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<TableDataContentColumn> getColumns() {
    return columns;
  }


  @JsonProperty(COLUMNS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setColumns(List<TableDataContentColumn> columns) {
    this.columns = columns;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TableDataContentHeader tableDataContentHeader = (TableDataContentHeader) o;
    return Objects.equals(this.columns, tableDataContentHeader.columns);
  }

  @Override
  public int hashCode() {
    return Objects.hash(columns);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TableDataContentHeader {\n");
    sb.append("    columns: ").append(toIndentedString(columns)).append("\n");
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
