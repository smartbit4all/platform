package org.smartbit4all.ui.api.filter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.filter.model.FilterGroupModel;
import org.smartbit4all.ui.api.filter.model.FilterGroupSelectorModel;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DynamicFilterModel
 */

public class DynamicFilterModel   {
  @JsonProperty("filterConfigMode")
  private org.smartbit4all.api.filter.bean.FilterConfigMode filterConfigMode = null;

  @JsonProperty("selectors")
  @Valid
  private List<FilterGroupSelectorModel> selectors = new ArrayList<>();

  @JsonProperty("root")
  private FilterGroupModel root;

  public DynamicFilterModel filterConfigMode(org.smartbit4all.api.filter.bean.FilterConfigMode filterConfigMode) {
    this.filterConfigMode = filterConfigMode;
    return this;
  }

  /**
   * Get filterConfigMode
   * @return filterConfigMode
  */
  @ApiModelProperty(value = "")

  @Valid

  public org.smartbit4all.api.filter.bean.FilterConfigMode getFilterConfigMode() {
    return filterConfigMode;
  }

  public void setFilterConfigMode(org.smartbit4all.api.filter.bean.FilterConfigMode filterConfigMode) {
    this.filterConfigMode = filterConfigMode;
  }

  public DynamicFilterModel selectors(List<FilterGroupSelectorModel> selectors) {
    this.selectors = selectors;
    return this;
  }

  public DynamicFilterModel addSelectorsItem(FilterGroupSelectorModel selectorsItem) {
    this.selectors.add(selectorsItem);
    return this;
  }

  /**
   * Get selectors
   * @return selectors
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<FilterGroupSelectorModel> getSelectors() {
    return selectors;
  }

  public void setSelectors(List<FilterGroupSelectorModel> selectors) {
    this.selectors = selectors;
  }

  public DynamicFilterModel root(FilterGroupModel root) {
    this.root = root;
    return this;
  }

  /**
   * Get root
   * @return root
  */
  @ApiModelProperty(value = "")

  @Valid

  public FilterGroupModel getRoot() {
    return root;
  }

  public void setRoot(FilterGroupModel root) {
    this.root = root;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DynamicFilterModel dynamicFilterModel = (DynamicFilterModel) o;
    return Objects.equals(this.filterConfigMode, dynamicFilterModel.filterConfigMode) &&
        Objects.equals(this.selectors, dynamicFilterModel.selectors) &&
        Objects.equals(this.root, dynamicFilterModel.root);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterConfigMode, selectors, root);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DynamicFilterModel {\n");
    
    sb.append("    filterConfigMode: ").append(toIndentedString(filterConfigMode)).append("\n");
    sb.append("    selectors: ").append(toIndentedString(selectors)).append("\n");
    sb.append("    root: ").append(toIndentedString(root)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

