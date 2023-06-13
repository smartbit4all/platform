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
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridDataAccessConfig;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridView;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This object represents the client model of a grid component. 
 */
@ApiModel(description = "This object represents the client model of a grid component. ")
@JsonPropertyOrder({
  GridModel.VIEW_UUID,
  GridModel.AVAILABLE_VIEWS,
  GridModel.VIEW,
  GridModel.ACCESS_CONFIG,
  GridModel.PAGE,
  GridModel.TOTAL_ROW_COUNT,
  GridModel.SELECTED_ROW_COUNT,
  GridModel.ALL_ROWS_SELECTED,
  GridModel.PAGE_SIZE,
  GridModel.PAGE_SIZE_OPTIONS,
  GridModel.DEFAULT_ROW_ACTIONS
})
@JsonTypeName("GridModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GridModel {
  public static final String VIEW_UUID = "viewUuid";
  private UUID viewUuid;

  public static final String AVAILABLE_VIEWS = "availableViews";
  private List<GridView> availableViews = null;

  public static final String VIEW = "view";
  private GridView view;

  public static final String ACCESS_CONFIG = "accessConfig";
  private GridDataAccessConfig accessConfig;

  public static final String PAGE = "page";
  private GridPage page;

  public static final String TOTAL_ROW_COUNT = "totalRowCount";
  private Integer totalRowCount;

  public static final String SELECTED_ROW_COUNT = "selectedRowCount";
  private Integer selectedRowCount;

  public static final String ALL_ROWS_SELECTED = "allRowsSelected";
  private Boolean allRowsSelected;

  public static final String PAGE_SIZE = "pageSize";
  private Integer pageSize;

  public static final String PAGE_SIZE_OPTIONS = "pageSizeOptions";
  private List<Integer> pageSizeOptions = null;

  public static final String DEFAULT_ROW_ACTIONS = "defaultRowActions";
  private List<String> defaultRowActions = null;

  public GridModel() { 
  }

  public GridModel viewUuid(UUID viewUuid) {
    
    this.viewUuid = viewUuid;
    return this;
  }

   /**
   * Get viewUuid
   * @return viewUuid
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW_UUID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UUID getViewUuid() {
    return viewUuid;
  }


  @JsonProperty(VIEW_UUID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewUuid(UUID viewUuid) {
    this.viewUuid = viewUuid;
  }


  public GridModel availableViews(List<GridView> availableViews) {
    
    this.availableViews = availableViews;
    return this;
  }

  public GridModel addAvailableViewsItem(GridView availableViewsItem) {
    if (this.availableViews == null) {
      this.availableViews = new ArrayList<>();
    }
    this.availableViews.add(availableViewsItem);
    return this;
  }

   /**
   * Get availableViews
   * @return availableViews
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(AVAILABLE_VIEWS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<GridView> getAvailableViews() {
    return availableViews;
  }


  @JsonProperty(AVAILABLE_VIEWS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAvailableViews(List<GridView> availableViews) {
    this.availableViews = availableViews;
  }


  public GridModel view(GridView view) {
    
    this.view = view;
    return this;
  }

   /**
   * Get view
   * @return view
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public GridView getView() {
    return view;
  }


  @JsonProperty(VIEW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setView(GridView view) {
    this.view = view;
  }


  public GridModel accessConfig(GridDataAccessConfig accessConfig) {
    
    this.accessConfig = accessConfig;
    return this;
  }

   /**
   * Get accessConfig
   * @return accessConfig
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ACCESS_CONFIG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public GridDataAccessConfig getAccessConfig() {
    return accessConfig;
  }


  @JsonProperty(ACCESS_CONFIG)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAccessConfig(GridDataAccessConfig accessConfig) {
    this.accessConfig = accessConfig;
  }


  public GridModel page(GridPage page) {
    
    this.page = page;
    return this;
  }

   /**
   * Get page
   * @return page
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(PAGE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public GridPage getPage() {
    return page;
  }


  @JsonProperty(PAGE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPage(GridPage page) {
    this.page = page;
  }


  public GridModel totalRowCount(Integer totalRowCount) {
    
    this.totalRowCount = totalRowCount;
    return this;
  }

   /**
   * Get totalRowCount
   * @return totalRowCount
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(TOTAL_ROW_COUNT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getTotalRowCount() {
    return totalRowCount;
  }


  @JsonProperty(TOTAL_ROW_COUNT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTotalRowCount(Integer totalRowCount) {
    this.totalRowCount = totalRowCount;
  }


  public GridModel selectedRowCount(Integer selectedRowCount) {
    
    this.selectedRowCount = selectedRowCount;
    return this;
  }

   /**
   * Get selectedRowCount
   * @return selectedRowCount
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTED_ROW_COUNT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getSelectedRowCount() {
    return selectedRowCount;
  }


  @JsonProperty(SELECTED_ROW_COUNT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelectedRowCount(Integer selectedRowCount) {
    this.selectedRowCount = selectedRowCount;
  }


  public GridModel allRowsSelected(Boolean allRowsSelected) {
    
    this.allRowsSelected = allRowsSelected;
    return this;
  }

   /**
   * Get allRowsSelected
   * @return allRowsSelected
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ALL_ROWS_SELECTED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getAllRowsSelected() {
    return allRowsSelected;
  }


  @JsonProperty(ALL_ROWS_SELECTED)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAllRowsSelected(Boolean allRowsSelected) {
    this.allRowsSelected = allRowsSelected;
  }


  public GridModel pageSize(Integer pageSize) {
    
    this.pageSize = pageSize;
    return this;
  }

   /**
   * Get pageSize
   * @return pageSize
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PAGE_SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getPageSize() {
    return pageSize;
  }


  @JsonProperty(PAGE_SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }


  public GridModel pageSizeOptions(List<Integer> pageSizeOptions) {
    
    this.pageSizeOptions = pageSizeOptions;
    return this;
  }

  public GridModel addPageSizeOptionsItem(Integer pageSizeOptionsItem) {
    if (this.pageSizeOptions == null) {
      this.pageSizeOptions = new ArrayList<>();
    }
    this.pageSizeOptions.add(pageSizeOptionsItem);
    return this;
  }

   /**
   * Get pageSizeOptions
   * @return pageSizeOptions
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PAGE_SIZE_OPTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Integer> getPageSizeOptions() {
    return pageSizeOptions;
  }


  @JsonProperty(PAGE_SIZE_OPTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPageSizeOptions(List<Integer> pageSizeOptions) {
    this.pageSizeOptions = pageSizeOptions;
  }


  public GridModel defaultRowActions(List<String> defaultRowActions) {
    
    this.defaultRowActions = defaultRowActions;
    return this;
  }

  public GridModel addDefaultRowActionsItem(String defaultRowActionsItem) {
    if (this.defaultRowActions == null) {
      this.defaultRowActions = new ArrayList<>();
    }
    this.defaultRowActions.add(defaultRowActionsItem);
    return this;
  }

   /**
   * Get defaultRowActions
   * @return defaultRowActions
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(DEFAULT_ROW_ACTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getDefaultRowActions() {
    return defaultRowActions;
  }


  @JsonProperty(DEFAULT_ROW_ACTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDefaultRowActions(List<String> defaultRowActions) {
    this.defaultRowActions = defaultRowActions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridModel gridModel = (GridModel) o;
    return Objects.equals(this.viewUuid, gridModel.viewUuid) &&
        Objects.equals(this.availableViews, gridModel.availableViews) &&
        Objects.equals(this.view, gridModel.view) &&
        Objects.equals(this.accessConfig, gridModel.accessConfig) &&
        Objects.equals(this.page, gridModel.page) &&
        Objects.equals(this.totalRowCount, gridModel.totalRowCount) &&
        Objects.equals(this.selectedRowCount, gridModel.selectedRowCount) &&
        Objects.equals(this.allRowsSelected, gridModel.allRowsSelected) &&
        Objects.equals(this.pageSize, gridModel.pageSize) &&
        Objects.equals(this.pageSizeOptions, gridModel.pageSizeOptions) &&
        Objects.equals(this.defaultRowActions, gridModel.defaultRowActions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(viewUuid, availableViews, view, accessConfig, page, totalRowCount, selectedRowCount, allRowsSelected, pageSize, pageSizeOptions, defaultRowActions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridModel {\n");
    sb.append("    viewUuid: ").append(toIndentedString(viewUuid)).append("\n");
    sb.append("    availableViews: ").append(toIndentedString(availableViews)).append("\n");
    sb.append("    view: ").append(toIndentedString(view)).append("\n");
    sb.append("    accessConfig: ").append(toIndentedString(accessConfig)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    totalRowCount: ").append(toIndentedString(totalRowCount)).append("\n");
    sb.append("    selectedRowCount: ").append(toIndentedString(selectedRowCount)).append("\n");
    sb.append("    allRowsSelected: ").append(toIndentedString(allRowsSelected)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    pageSizeOptions: ").append(toIndentedString(pageSizeOptions)).append("\n");
    sb.append("    defaultRowActions: ").append(toIndentedString(defaultRowActions)).append("\n");
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

