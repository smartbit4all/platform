/*
 * Grid api
 * The grid api is resposible for the grid components that shows a list of item. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.grid.bean;

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
import org.smartbit4all.api.grid.bean.GridRow;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object containt the page information of the content. The currently seen row range, the total row cont and other options. 
 */
@ApiModel(description = "This object containt the page information of the content. The currently seen row range, the total row cont and other options. ")
@JsonPropertyOrder({
  GridPage.LOWER_BOUND,
  GridPage.UPPER_BOUND,
  GridPage.ROWS
})
@JsonTypeName("GridPage")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GridPage {
  public static final String LOWER_BOUND = "lowerBound";
  private Integer lowerBound;

  public static final String UPPER_BOUND = "upperBound";
  private Integer upperBound;

  public static final String ROWS = "rows";
  private List<GridRow> rows = null;

  public GridPage() { 
  }

  public GridPage lowerBound(Integer lowerBound) {
    
    this.lowerBound = lowerBound;
    return this;
  }

   /**
   * The index of the first row in the current page. Startes from 1 and inclusive.
   * @return lowerBound
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The index of the first row in the current page. Startes from 1 and inclusive.")
  @JsonProperty(LOWER_BOUND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getLowerBound() {
    return lowerBound;
  }


  @JsonProperty(LOWER_BOUND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLowerBound(Integer lowerBound) {
    this.lowerBound = lowerBound;
  }


  public GridPage upperBound(Integer upperBound) {
    
    this.upperBound = upperBound;
    return this;
  }

   /**
   * The index of the last row in the current page inclusive.
   * @return upperBound
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The index of the last row in the current page inclusive.")
  @JsonProperty(UPPER_BOUND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getUpperBound() {
    return upperBound;
  }


  @JsonProperty(UPPER_BOUND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUpperBound(Integer upperBound) {
    this.upperBound = upperBound;
  }


  public GridPage rows(List<GridRow> rows) {
    
    this.rows = rows;
    return this;
  }

  public GridPage addRowsItem(GridRow rowsItem) {
    if (this.rows == null) {
      this.rows = new ArrayList<>();
    }
    this.rows.add(rowsItem);
    return this;
  }

   /**
   * Get rows
   * @return rows
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ROWS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<GridRow> getRows() {
    return rows;
  }


  @JsonProperty(ROWS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRows(List<GridRow> rows) {
    this.rows = rows;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridPage gridPage = (GridPage) o;
    return Objects.equals(this.lowerBound, gridPage.lowerBound) &&
        Objects.equals(this.upperBound, gridPage.upperBound) &&
        Objects.equals(this.rows, gridPage.rows);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lowerBound, upperBound, rows);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridPage {\n");
    sb.append("    lowerBound: ").append(toIndentedString(lowerBound)).append("\n");
    sb.append("    upperBound: ").append(toIndentedString(upperBound)).append("\n");
    sb.append("    rows: ").append(toIndentedString(rows)).append("\n");
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
