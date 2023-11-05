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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * FilterExpressionBuilderApiConfig
 */
@JsonPropertyOrder({
  FilterExpressionBuilderApiConfig.READ_ONLY,
  FilterExpressionBuilderApiConfig.AVAILABLE_ACTIONS
})
@JsonTypeName("FilterExpressionBuilderApiConfig")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class FilterExpressionBuilderApiConfig {
  public static final String READ_ONLY = "readOnly";
  private Boolean readOnly = false;

  public static final String AVAILABLE_ACTIONS = "availableActions";
  private List<String> availableActions = new ArrayList<>();

  public FilterExpressionBuilderApiConfig() { 
  }

  public FilterExpressionBuilderApiConfig readOnly(Boolean readOnly) {
    
    this.readOnly = readOnly;
    return this;
  }

   /**
   * Get readOnly
   * @return readOnly
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(READ_ONLY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getReadOnly() {
    return readOnly;
  }


  @JsonProperty(READ_ONLY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }


  public FilterExpressionBuilderApiConfig availableActions(List<String> availableActions) {
    
    this.availableActions = availableActions;
    return this;
  }

  public FilterExpressionBuilderApiConfig addAvailableActionsItem(String availableActionsItem) {
    this.availableActions.add(availableActionsItem);
    return this;
  }

   /**
   * Get availableActions
   * @return availableActions
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(AVAILABLE_ACTIONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<String> getAvailableActions() {
    return availableActions;
  }


  @JsonProperty(AVAILABLE_ACTIONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setAvailableActions(List<String> availableActions) {
    this.availableActions = availableActions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterExpressionBuilderApiConfig filterExpressionBuilderApiConfig = (FilterExpressionBuilderApiConfig) o;
    return Objects.equals(this.readOnly, filterExpressionBuilderApiConfig.readOnly) &&
        Objects.equals(this.availableActions, filterExpressionBuilderApiConfig.availableActions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(readOnly, availableActions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterExpressionBuilderApiConfig {\n");
    sb.append("    readOnly: ").append(toIndentedString(readOnly)).append("\n");
    sb.append("    availableActions: ").append(toIndentedString(availableActions)).append("\n");
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
