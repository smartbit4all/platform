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
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.grid.bean.GridSelectionType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The configuration of the grid view. It can be a simple table grid but a complex card representation also. A model has one or more view option to render the content. 
 */
@ApiModel(description = "The configuration of the grid view. It can be a simple table grid but a complex card representation also. A model has one or more view option to render the content. ")
@JsonPropertyOrder({
  GridViewDescriptor.LABEL,
  GridViewDescriptor.ICON,
  GridViewDescriptor.KIND,
  GridViewDescriptor.COLUMNS,
  GridViewDescriptor.SELECTION_MODE,
  GridViewDescriptor.SELECTION_TYPE,
  GridViewDescriptor.PRESERVE_SELECTION_ON_PAGE_CHANGE,
  GridViewDescriptor.SHOW_EDIT_COLUMNS
})
@JsonTypeName("GridViewDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GridViewDescriptor {
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

  public static final String SELECTION_MODE = "selectionMode";
  private GridSelectionMode selectionMode;

  public static final String SELECTION_TYPE = "selectionType";
  private GridSelectionType selectionType;

  public static final String PRESERVE_SELECTION_ON_PAGE_CHANGE = "preserveSelectionOnPageChange";
  private Boolean preserveSelectionOnPageChange;

  public static final String SHOW_EDIT_COLUMNS = "showEditColumns";
  private Boolean showEditColumns;

  public GridViewDescriptor() { 
  }

  public GridViewDescriptor label(String label) {
    
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


  public GridViewDescriptor icon(String icon) {
    
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


  public GridViewDescriptor kind(KindEnum kind) {
    
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


  public GridViewDescriptor columns(List<GridColumnMeta> columns) {
    
    this.columns = columns;
    return this;
  }

  public GridViewDescriptor addColumnsItem(GridColumnMeta columnsItem) {
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


  public GridViewDescriptor selectionMode(GridSelectionMode selectionMode) {
    
    this.selectionMode = selectionMode;
    return this;
  }

   /**
   * Get selectionMode
   * @return selectionMode
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTION_MODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public GridSelectionMode getSelectionMode() {
    return selectionMode;
  }


  @JsonProperty(SELECTION_MODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectionMode(GridSelectionMode selectionMode) {
    this.selectionMode = selectionMode;
  }


  public GridViewDescriptor selectionType(GridSelectionType selectionType) {
    
    this.selectionType = selectionType;
    return this;
  }

   /**
   * Get selectionType
   * @return selectionType
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTION_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public GridSelectionType getSelectionType() {
    return selectionType;
  }


  @JsonProperty(SELECTION_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectionType(GridSelectionType selectionType) {
    this.selectionType = selectionType;
  }


  public GridViewDescriptor preserveSelectionOnPageChange(Boolean preserveSelectionOnPageChange) {
    
    this.preserveSelectionOnPageChange = preserveSelectionOnPageChange;
    return this;
  }

   /**
   * Get preserveSelectionOnPageChange
   * @return preserveSelectionOnPageChange
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PRESERVE_SELECTION_ON_PAGE_CHANGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getPreserveSelectionOnPageChange() {
    return preserveSelectionOnPageChange;
  }


  @JsonProperty(PRESERVE_SELECTION_ON_PAGE_CHANGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPreserveSelectionOnPageChange(Boolean preserveSelectionOnPageChange) {
    this.preserveSelectionOnPageChange = preserveSelectionOnPageChange;
  }


  public GridViewDescriptor showEditColumns(Boolean showEditColumns) {
    
    this.showEditColumns = showEditColumns;
    return this;
  }

   /**
   * Get showEditColumns
   * @return showEditColumns
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SHOW_EDIT_COLUMNS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getShowEditColumns() {
    return showEditColumns;
  }


  @JsonProperty(SHOW_EDIT_COLUMNS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setShowEditColumns(Boolean showEditColumns) {
    this.showEditColumns = showEditColumns;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridViewDescriptor gridViewDescriptor = (GridViewDescriptor) o;
    return Objects.equals(this.label, gridViewDescriptor.label) &&
        Objects.equals(this.icon, gridViewDescriptor.icon) &&
        Objects.equals(this.kind, gridViewDescriptor.kind) &&
        Objects.equals(this.columns, gridViewDescriptor.columns) &&
        Objects.equals(this.selectionMode, gridViewDescriptor.selectionMode) &&
        Objects.equals(this.selectionType, gridViewDescriptor.selectionType) &&
        Objects.equals(this.preserveSelectionOnPageChange, gridViewDescriptor.preserveSelectionOnPageChange) &&
        Objects.equals(this.showEditColumns, gridViewDescriptor.showEditColumns);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, icon, kind, columns, selectionMode, selectionType, preserveSelectionOnPageChange, showEditColumns);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridViewDescriptor {\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    columns: ").append(toIndentedString(columns)).append("\n");
    sb.append("    selectionMode: ").append(toIndentedString(selectionMode)).append("\n");
    sb.append("    selectionType: ").append(toIndentedString(selectionType)).append("\n");
    sb.append("    preserveSelectionOnPageChange: ").append(toIndentedString(preserveSelectionOnPageChange)).append("\n");
    sb.append("    showEditColumns: ").append(toIndentedString(showEditColumns)).append("\n");
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

