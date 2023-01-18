/*
 * Filter API 2
 * Filter API 2
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.filterexpression.bean;

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
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldWidgetType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.value.bean.Value;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * FilterExpressionField
 */
@JsonPropertyOrder({
  FilterExpressionField.LABEL1,
  FilterExpressionField.LABEL2,
  FilterExpressionField.LABEL3,
  FilterExpressionField.ICON,
  FilterExpressionField.EXPRESSION_DATA,
  FilterExpressionField.POSSIBLE_OPERATIONS,
  FilterExpressionField.FILTER_FIELD_TYPE,
  FilterExpressionField.POSSIBLE_VALUES,
  FilterExpressionField.SELECTED_VALUES,
  FilterExpressionField.WIDGET_TYPE,
  FilterExpressionField.SUB_FIELD_LIST
})
@JsonTypeName("FilterExpressionField")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterExpressionField {
  public static final String LABEL1 = "label1";
  private String label1;

  public static final String LABEL2 = "label2";
  private String label2;

  public static final String LABEL3 = "label3";
  private String label3;

  public static final String ICON = "icon";
  private String icon;

  public static final String EXPRESSION_DATA = "expressionData";
  private FilterExpressionData expressionData;

  public static final String POSSIBLE_OPERATIONS = "possibleOperations";
  private List<FilterExpressionOperation> possibleOperations = null;

  public static final String FILTER_FIELD_TYPE = "filterFieldType";
  private FilterExpressionDataType filterFieldType;

  public static final String POSSIBLE_VALUES = "possibleValues";
  private List<Value> possibleValues = new ArrayList<>();

  public static final String SELECTED_VALUES = "selectedValues";
  private List<Value> selectedValues = new ArrayList<>();

  public static final String WIDGET_TYPE = "widgetType";
  private FilterExpressionFieldWidgetType widgetType;

  public static final String SUB_FIELD_LIST = "subFieldList";
  private FilterExpressionFieldList subFieldList;

  public FilterExpressionField() { 
  }

  public FilterExpressionField label1(String label1) {
    
    this.label1 = label1;
    return this;
  }

   /**
   * Get label1
   * @return label1
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(LABEL1)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getLabel1() {
    return label1;
  }


  @JsonProperty(LABEL1)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setLabel1(String label1) {
    this.label1 = label1;
  }


  public FilterExpressionField label2(String label2) {
    
    this.label2 = label2;
    return this;
  }

   /**
   * Get label2
   * @return label2
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LABEL2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLabel2() {
    return label2;
  }


  @JsonProperty(LABEL2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLabel2(String label2) {
    this.label2 = label2;
  }


  public FilterExpressionField label3(String label3) {
    
    this.label3 = label3;
    return this;
  }

   /**
   * Get label3
   * @return label3
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(LABEL3)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLabel3() {
    return label3;
  }


  @JsonProperty(LABEL3)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLabel3(String label3) {
    this.label3 = label3;
  }


  public FilterExpressionField icon(String icon) {
    
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


  public FilterExpressionField expressionData(FilterExpressionData expressionData) {
    
    this.expressionData = expressionData;
    return this;
  }

   /**
   * Get expressionData
   * @return expressionData
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(EXPRESSION_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionData getExpressionData() {
    return expressionData;
  }


  @JsonProperty(EXPRESSION_DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpressionData(FilterExpressionData expressionData) {
    this.expressionData = expressionData;
  }


  public FilterExpressionField possibleOperations(List<FilterExpressionOperation> possibleOperations) {
    
    this.possibleOperations = possibleOperations;
    return this;
  }

  public FilterExpressionField addPossibleOperationsItem(FilterExpressionOperation possibleOperationsItem) {
    if (this.possibleOperations == null) {
      this.possibleOperations = new ArrayList<>();
    }
    this.possibleOperations.add(possibleOperationsItem);
    return this;
  }

   /**
   * Get possibleOperations
   * @return possibleOperations
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(POSSIBLE_OPERATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<FilterExpressionOperation> getPossibleOperations() {
    return possibleOperations;
  }


  @JsonProperty(POSSIBLE_OPERATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPossibleOperations(List<FilterExpressionOperation> possibleOperations) {
    this.possibleOperations = possibleOperations;
  }


  public FilterExpressionField filterFieldType(FilterExpressionDataType filterFieldType) {
    
    this.filterFieldType = filterFieldType;
    return this;
  }

   /**
   * Get filterFieldType
   * @return filterFieldType
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(FILTER_FIELD_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionDataType getFilterFieldType() {
    return filterFieldType;
  }


  @JsonProperty(FILTER_FIELD_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFilterFieldType(FilterExpressionDataType filterFieldType) {
    this.filterFieldType = filterFieldType;
  }


  public FilterExpressionField possibleValues(List<Value> possibleValues) {
    
    this.possibleValues = possibleValues;
    return this;
  }

  public FilterExpressionField addPossibleValuesItem(Value possibleValuesItem) {
    this.possibleValues.add(possibleValuesItem);
    return this;
  }

   /**
   * Get possibleValues
   * @return possibleValues
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(POSSIBLE_VALUES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<Value> getPossibleValues() {
    return possibleValues;
  }


  @JsonProperty(POSSIBLE_VALUES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPossibleValues(List<Value> possibleValues) {
    this.possibleValues = possibleValues;
  }


  public FilterExpressionField selectedValues(List<Value> selectedValues) {
    
    this.selectedValues = selectedValues;
    return this;
  }

  public FilterExpressionField addSelectedValuesItem(Value selectedValuesItem) {
    this.selectedValues.add(selectedValuesItem);
    return this;
  }

   /**
   * Get selectedValues
   * @return selectedValues
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(SELECTED_VALUES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<Value> getSelectedValues() {
    return selectedValues;
  }


  @JsonProperty(SELECTED_VALUES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSelectedValues(List<Value> selectedValues) {
    this.selectedValues = selectedValues;
  }


  public FilterExpressionField widgetType(FilterExpressionFieldWidgetType widgetType) {
    
    this.widgetType = widgetType;
    return this;
  }

   /**
   * Get widgetType
   * @return widgetType
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(WIDGET_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionFieldWidgetType getWidgetType() {
    return widgetType;
  }


  @JsonProperty(WIDGET_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setWidgetType(FilterExpressionFieldWidgetType widgetType) {
    this.widgetType = widgetType;
  }


  public FilterExpressionField subFieldList(FilterExpressionFieldList subFieldList) {
    
    this.subFieldList = subFieldList;
    return this;
  }

   /**
   * Get subFieldList
   * @return subFieldList
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SUB_FIELD_LIST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionFieldList getSubFieldList() {
    return subFieldList;
  }


  @JsonProperty(SUB_FIELD_LIST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubFieldList(FilterExpressionFieldList subFieldList) {
    this.subFieldList = subFieldList;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpressionField filterExpressionField = (FilterExpressionField) o;
    return Objects.equals(this.label1, filterExpressionField.label1) &&
        Objects.equals(this.label2, filterExpressionField.label2) &&
        Objects.equals(this.label3, filterExpressionField.label3) &&
        Objects.equals(this.icon, filterExpressionField.icon) &&
        Objects.equals(this.expressionData, filterExpressionField.expressionData) &&
        Objects.equals(this.possibleOperations, filterExpressionField.possibleOperations) &&
        Objects.equals(this.filterFieldType, filterExpressionField.filterFieldType) &&
        Objects.equals(this.possibleValues, filterExpressionField.possibleValues) &&
        Objects.equals(this.selectedValues, filterExpressionField.selectedValues) &&
        Objects.equals(this.widgetType, filterExpressionField.widgetType) &&
        Objects.equals(this.subFieldList, filterExpressionField.subFieldList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label1, label2, label3, icon, expressionData, possibleOperations, filterFieldType, possibleValues, selectedValues, widgetType, subFieldList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpressionField {\n");
    sb.append("    label1: ").append(toIndentedString(label1)).append("\n");
    sb.append("    label2: ").append(toIndentedString(label2)).append("\n");
    sb.append("    label3: ").append(toIndentedString(label3)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    expressionData: ").append(toIndentedString(expressionData)).append("\n");
    sb.append("    possibleOperations: ").append(toIndentedString(possibleOperations)).append("\n");
    sb.append("    filterFieldType: ").append(toIndentedString(filterFieldType)).append("\n");
    sb.append("    possibleValues: ").append(toIndentedString(possibleValues)).append("\n");
    sb.append("    selectedValues: ").append(toIndentedString(selectedValues)).append("\n");
    sb.append("    widgetType: ").append(toIndentedString(widgetType)).append("\n");
    sb.append("    subFieldList: ").append(toIndentedString(subFieldList)).append("\n");
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
