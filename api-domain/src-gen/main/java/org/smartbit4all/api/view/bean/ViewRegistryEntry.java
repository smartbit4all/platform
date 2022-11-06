/*
 * View API
 * View API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.view.bean;

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
 * Descriptor of a possible view.
 */
@ApiModel(description = "Descriptor of a possible view.")
@JsonPropertyOrder({
  ViewRegistryEntry.VIEW_NAME,
  ViewRegistryEntry.PARENT_VIEW
})
@JsonTypeName("ViewRegistryEntry")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ViewRegistryEntry {
  public static final String VIEW_NAME = "viewName";
  private String viewName;

  public static final String PARENT_VIEW = "parentView";
  private String parentView;


  public ViewRegistryEntry viewName(String viewName) {
    
    this.viewName = viewName;
    return this;
  }

   /**
   * Name of the view. Must be unique.
   * @return viewName
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "Name of the view. Must be unique.")
  @JsonProperty(VIEW_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getViewName() {
    return viewName;
  }


  @JsonProperty(VIEW_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setViewName(String viewName) {
    this.viewName = viewName;
  }


  public ViewRegistryEntry parentView(String parentView) {
    
    this.parentView = parentView;
    return this;
  }

   /**
   * Name of parentView, may be null.
   * @return parentView
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Name of parentView, may be null.")
  @JsonProperty(PARENT_VIEW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getParentView() {
    return parentView;
  }


  @JsonProperty(PARENT_VIEW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setParentView(String parentView) {
    this.parentView = parentView;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewRegistryEntry viewRegistryEntry = (ViewRegistryEntry) o;
    return Objects.equals(this.viewName, viewRegistryEntry.viewName) &&
        Objects.equals(this.parentView, viewRegistryEntry.parentView);
  }

  @Override
  public int hashCode() {
    return Objects.hash(viewName, parentView);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewRegistryEntry {\n");
    sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
    sb.append("    parentView: ").append(toIndentedString(parentView)).append("\n");
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
