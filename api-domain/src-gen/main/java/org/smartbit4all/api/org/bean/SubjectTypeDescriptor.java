/*
 * org api
 * org api
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.org.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.object.bean.LangString;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The ACL subject type is all the subject types that are available in th application. Every subject type must provide SearchIndex to be able to select the given object. This type defines how get the unique identifier. 
 */
@ApiModel(description = "The ACL subject type is all the subject types that are available in th application. Every subject type must provide SearchIndex to be able to select the given object. This type defines how get the unique identifier. ")
@JsonPropertyOrder({
  SubjectTypeDescriptor.NAME,
  SubjectTypeDescriptor.TITLE,
  SubjectTypeDescriptor.SELECTION_CONFIG,
  SubjectTypeDescriptor.API_NAME
})
@JsonTypeName("SubjectTypeDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SubjectTypeDescriptor {
  public static final String NAME = "name";
  private String name;

  public static final String TITLE = "title";
  private LangString title = null;

  public static final String SELECTION_CONFIG = "selectionConfig";
  private SearchPageConfig selectionConfig = null;

  public static final String API_NAME = "apiName";
  private String apiName;

  public SubjectTypeDescriptor() { 
  }

  public SubjectTypeDescriptor name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The name of the subject type.
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The name of the subject type.")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setName(String name) {
    this.name = name;
  }


  public SubjectTypeDescriptor title(LangString title) {
    
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(TITLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LangString getTitle() {
    return title;
  }


  @JsonProperty(TITLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTitle(LangString title) {
    this.title = title;
  }


  public SubjectTypeDescriptor selectionConfig(SearchPageConfig selectionConfig) {
    
    this.selectionConfig = selectionConfig;
    return this;
  }

   /**
   * The parameters to open the SearchIndexResultPage for the selection. It defines the name of the search index and also the columns of the grid and the filter model. 
   * @return selectionConfig
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The parameters to open the SearchIndexResultPage for the selection. It defines the name of the search index and also the columns of the grid and the filter model. ")
  @JsonProperty(SELECTION_CONFIG)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public SearchPageConfig getSelectionConfig() {
    return selectionConfig;
  }


  @JsonProperty(SELECTION_CONFIG)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setSelectionConfig(SearchPageConfig selectionConfig) {
    this.selectionConfig = selectionConfig;
  }


  public SubjectTypeDescriptor apiName(String apiName) {
    
    this.apiName = apiName;
    return this;
  }

   /**
   * The name of the api that manages the given subject. We have to provide the necessary apis in advance before we add  
   * @return apiName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the api that manages the given subject. We have to provide the necessary apis in advance before we add  ")
  @JsonProperty(API_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getApiName() {
    return apiName;
  }


  @JsonProperty(API_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setApiName(String apiName) {
    this.apiName = apiName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubjectTypeDescriptor subjectTypeDescriptor = (SubjectTypeDescriptor) o;
    return Objects.equals(this.name, subjectTypeDescriptor.name) &&
        Objects.equals(this.title, subjectTypeDescriptor.title) &&
        Objects.equals(this.selectionConfig, subjectTypeDescriptor.selectionConfig) &&
        Objects.equals(this.apiName, subjectTypeDescriptor.apiName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, title, selectionConfig, apiName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubjectTypeDescriptor {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    selectionConfig: ").append(toIndentedString(selectionConfig)).append("\n");
    sb.append("    apiName: ").append(toIndentedString(apiName)).append("\n");
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

