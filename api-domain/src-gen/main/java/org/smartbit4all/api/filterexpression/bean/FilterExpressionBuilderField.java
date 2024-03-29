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
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The available filter fields contains the pre setup filter field as a template. It can be easily copied and added to the filter field list. 
 */
@ApiModel(description = "The available filter fields contains the pre setup filter field as a template. It can be easily copied and added to the filter field list. ")
@JsonPropertyOrder({
  FilterExpressionBuilderField.LABEL,
  FilterExpressionBuilderField.FIELD_TEMPLATE,
  FilterExpressionBuilderField.LIMIT_OF_USAGE,
  FilterExpressionBuilderField.INSTANCE_IDS
})
@JsonTypeName("FilterExpressionBuilderField")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterExpressionBuilderField {
  public static final String LABEL = "label";
  private String label;

  public static final String FIELD_TEMPLATE = "fieldTemplate";
  private FilterExpressionField fieldTemplate;

  public static final String LIMIT_OF_USAGE = "limitOfUsage";
  private Integer limitOfUsage;

  public static final String INSTANCE_IDS = "instanceIds";
  private List<String> instanceIds = null;

  public FilterExpressionBuilderField() { 
  }

  public FilterExpressionBuilderField label(String label) {
    
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


  public FilterExpressionBuilderField fieldTemplate(FilterExpressionField fieldTemplate) {
    
    this.fieldTemplate = fieldTemplate;
    return this;
  }

   /**
   * Get fieldTemplate
   * @return fieldTemplate
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(FIELD_TEMPLATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public FilterExpressionField getFieldTemplate() {
    return fieldTemplate;
  }


  @JsonProperty(FIELD_TEMPLATE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFieldTemplate(FilterExpressionField fieldTemplate) {
    this.fieldTemplate = fieldTemplate;
  }


  public FilterExpressionBuilderField limitOfUsage(Integer limitOfUsage) {
    
    this.limitOfUsage = limitOfUsage;
    return this;
  }

   /**
   * If less or equal zero then there is no limit how many time we can use the given filter. Else it is the limit where the typical value is 1 becase we can use the given filter field only once. 
   * @return limitOfUsage
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If less or equal zero then there is no limit how many time we can use the given filter. Else it is the limit where the typical value is 1 becase we can use the given filter field only once. ")
  @JsonProperty(LIMIT_OF_USAGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Integer getLimitOfUsage() {
    return limitOfUsage;
  }


  @JsonProperty(LIMIT_OF_USAGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLimitOfUsage(Integer limitOfUsage) {
    this.limitOfUsage = limitOfUsage;
  }


  public FilterExpressionBuilderField instanceIds(List<String> instanceIds) {
    
    this.instanceIds = instanceIds;
    return this;
  }

  public FilterExpressionBuilderField addInstanceIdsItem(String instanceIdsItem) {
    if (this.instanceIds == null) {
      this.instanceIds = new ArrayList<>();
    }
    this.instanceIds.add(instanceIdsItem);
    return this;
  }

   /**
   * The unique identifiers of fields initiated from this builder field in the workplace.
   * @return instanceIds
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The unique identifiers of fields initiated from this builder field in the workplace.")
  @JsonProperty(INSTANCE_IDS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getInstanceIds() {
    return instanceIds;
  }


  @JsonProperty(INSTANCE_IDS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInstanceIds(List<String> instanceIds) {
    this.instanceIds = instanceIds;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpressionBuilderField filterExpressionBuilderField = (FilterExpressionBuilderField) o;
    return Objects.equals(this.label, filterExpressionBuilderField.label) &&
        Objects.equals(this.fieldTemplate, filterExpressionBuilderField.fieldTemplate) &&
        Objects.equals(this.limitOfUsage, filterExpressionBuilderField.limitOfUsage) &&
        Objects.equals(this.instanceIds, filterExpressionBuilderField.instanceIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, fieldTemplate, limitOfUsage, instanceIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpressionBuilderField {\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    fieldTemplate: ").append(toIndentedString(fieldTemplate)).append("\n");
    sb.append("    limitOfUsage: ").append(toIndentedString(limitOfUsage)).append("\n");
    sb.append("    instanceIds: ").append(toIndentedString(instanceIds)).append("\n");
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

