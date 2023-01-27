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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This mapping binds the objects of the domain with the entities of the search index. By default all the properties are included in the search index. But we can have positive and negative exceptions to specify the exact attributes to include. 
 */
@ApiModel(description = "This mapping binds the objects of the domain with the entities of the search index. By default all the properties are included in the search index. But we can have positive and negative exceptions to specify the exact attributes to include. ")
@JsonPropertyOrder({
  SearchIndexPropertyDefinitionData.PROPERTY_PATH,
  SearchIndexPropertyDefinitionData.INCLUDE
})
@JsonTypeName("SearchIndexPropertyDefinitionData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SearchIndexPropertyDefinitionData {
  public static final String PROPERTY_PATH = "propertyPath";
  private String propertyPath;

  public static final String INCLUDE = "include";
  private Boolean include;

  public SearchIndexPropertyDefinitionData() { 
  }

  public SearchIndexPropertyDefinitionData propertyPath(String propertyPath) {
    
    this.propertyPath = propertyPath;
    return this;
  }

   /**
   * Get propertyPath
   * @return propertyPath
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PROPERTY_PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPropertyPath() {
    return propertyPath;
  }


  @JsonProperty(PROPERTY_PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPropertyPath(String propertyPath) {
    this.propertyPath = propertyPath;
  }


  public SearchIndexPropertyDefinitionData include(Boolean include) {
    
    this.include = include;
    return this;
  }

   /**
   * Get include
   * @return include
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(INCLUDE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getInclude() {
    return include;
  }


  @JsonProperty(INCLUDE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setInclude(Boolean include) {
    this.include = include;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchIndexPropertyDefinitionData searchIndexPropertyDefinitionData = (SearchIndexPropertyDefinitionData) o;
    return Objects.equals(this.propertyPath, searchIndexPropertyDefinitionData.propertyPath) &&
        Objects.equals(this.include, searchIndexPropertyDefinitionData.include);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyPath, include);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchIndexPropertyDefinitionData {\n");
    sb.append("    propertyPath: ").append(toIndentedString(propertyPath)).append("\n");
    sb.append("    include: ").append(toIndentedString(include)).append("\n");
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

