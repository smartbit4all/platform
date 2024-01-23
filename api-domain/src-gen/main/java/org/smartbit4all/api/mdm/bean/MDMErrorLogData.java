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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Import and export operation error log data.
 */
@ApiModel(description = "Import and export operation error log data.")
@JsonPropertyOrder({
  MDMErrorLogData.ROW_NUM,
  MDMErrorLogData.COLUMN,
  MDMErrorLogData.ERROR
})
@JsonTypeName("MDMErrorLogData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MDMErrorLogData {
  public static final String ROW_NUM = "rowNum";
  private Integer rowNum;

  public static final String COLUMN = "column";
  private String column;

  public static final String ERROR = "error";
  private String error;

  public MDMErrorLogData() { 
  }

  public MDMErrorLogData rowNum(Integer rowNum) {
    
    this.rowNum = rowNum;
    return this;
  }

   /**
   * Get rowNum
   * @return rowNum
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ROW_NUM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getRowNum() {
    return rowNum;
  }


  @JsonProperty(ROW_NUM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRowNum(Integer rowNum) {
    this.rowNum = rowNum;
  }


  public MDMErrorLogData column(String column) {
    
    this.column = column;
    return this;
  }

   /**
   * Get column
   * @return column
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(COLUMN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getColumn() {
    return column;
  }


  @JsonProperty(COLUMN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setColumn(String column) {
    this.column = column;
  }


  public MDMErrorLogData error(String error) {
    
    this.error = error;
    return this;
  }

   /**
   * Get error
   * @return error
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ERROR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getError() {
    return error;
  }


  @JsonProperty(ERROR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setError(String error) {
    this.error = error;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MDMErrorLogData mdMErrorLogData = (MDMErrorLogData) o;
    return Objects.equals(this.rowNum, mdMErrorLogData.rowNum) &&
        Objects.equals(this.column, mdMErrorLogData.column) &&
        Objects.equals(this.error, mdMErrorLogData.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rowNum, column, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MDMErrorLogData {\n");
    sb.append("    rowNum: ").append(toIndentedString(rowNum)).append("\n");
    sb.append("    column: ").append(toIndentedString(column)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

