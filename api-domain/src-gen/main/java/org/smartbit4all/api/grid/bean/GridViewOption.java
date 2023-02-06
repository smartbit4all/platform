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
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The configuration of the grid view. It can be a simple table grid but a complex card representation also. A model has one or more view option to render the content. 
 */
@ApiModel(description = "The configuration of the grid view. It can be a simple table grid but a complex card representation also. A model has one or more view option to render the content. ")
@JsonPropertyOrder({
  GridViewOption.LABEL,
  GridViewOption.ICON,
  GridViewOption.KIND,
  GridViewOption.COLUMNS
})
@JsonTypeName("GridViewOption")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GridViewOption {
  public static final String LABEL = "label";
  private String label;

  public static final String ICON = "icon";
  private String icon;

  /**
   * Gets or Sets kind
   */
  public enum KindEnum {
    TABLE("TABLE"),
    
    EXPANDABLECARDS1("EXPANDABLECARDS1");

    private String value;

    KindEnum(String value) {
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
    public static KindEnum fromValue(String value) {
      for (KindEnum b : KindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String KIND = "kind";
  private KindEnum kind;

  public static final String COLUMNS = "columns";
  private List<GridColumnMeta> columns = new ArrayList<>();

  public GridViewOption() { 
  }

  public GridViewOption label(String label) {
    
    this.label = label;
    return this;
  }

   /**
   * Get label
   * @return label
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLabel() {
    return label;
  }


  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLabel(String label) {
    this.label = label;
  }


  public GridViewOption icon(String icon) {
    
    this.icon = icon;
    return this;
  }

   /**
   * Get icon
   * @return icon
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ICON)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIcon() {
    return icon;
  }


  @JsonProperty(ICON)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIcon(String icon) {
    this.icon = icon;
  }


  public GridViewOption kind(KindEnum kind) {
    
    this.kind = kind;
    return this;
  }

   /**
   * Get kind
   * @return kind
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public KindEnum getKind() {
    return kind;
  }


  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKind(KindEnum kind) {
    this.kind = kind;
  }


  public GridViewOption columns(List<GridColumnMeta> columns) {
    
    this.columns = columns;
    return this;
  }

  public GridViewOption addColumnsItem(GridColumnMeta columnsItem) {
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

  public List<GridColumnMeta> getColumns() {
    return columns;
  }


  @JsonProperty(COLUMNS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setColumns(List<GridColumnMeta> columns) {
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
    GridViewOption gridViewOption = (GridViewOption) o;
    return Objects.equals(this.label, gridViewOption.label) &&
        Objects.equals(this.icon, gridViewOption.icon) &&
        Objects.equals(this.kind, gridViewOption.kind) &&
        Objects.equals(this.columns, gridViewOption.columns);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, icon, kind, columns);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridViewOption {\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
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
