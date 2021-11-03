/*
 * Navigation API
 * Navigation API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.ui.api.navigation.model;

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
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * NavigableViewDescriptor
 */
@JsonPropertyOrder({
  NavigableViewDescriptor.VIEW_NAME,
  NavigableViewDescriptor.VIEW_CLASS_NAME,
  NavigableViewDescriptor.TITLE,
  NavigableViewDescriptor.ICON
})
@JsonTypeName("NavigableViewDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigableViewDescriptor {
  public static final String VIEW_NAME = "viewName";
  private String viewName;

  public static final String VIEW_CLASS_NAME = "viewClassName";
  private String viewClassName;

  public static final String TITLE = "title";
  private String title;

  public static final String ICON = "icon";
  private String icon;


  public NavigableViewDescriptor viewName(String viewName) {
    
    this.viewName = viewName;
    return this;
  }

   /**
   * Get viewName
   * @return viewName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getViewName() {
    return viewName;
  }


  @JsonProperty(VIEW_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewName(String viewName) {
    this.viewName = viewName;
  }


  public NavigableViewDescriptor viewClassName(String viewClassName) {
    
    this.viewClassName = viewClassName;
    return this;
  }

   /**
   * Get viewClassName
   * @return viewClassName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW_CLASS_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getViewClassName() {
    return viewClassName;
  }


  @JsonProperty(VIEW_CLASS_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewClassName(String viewClassName) {
    this.viewClassName = viewClassName;
  }


  public NavigableViewDescriptor title(String title) {
    
    this.title = title;
    return this;
  }

   /**
   * Get title
   * @return title
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(TITLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTitle() {
    return title;
  }


  @JsonProperty(TITLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTitle(String title) {
    this.title = title;
  }


  public NavigableViewDescriptor icon(String icon) {
    
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NavigableViewDescriptor navigableViewDescriptor = (NavigableViewDescriptor) o;
    return Objects.equals(this.viewName, navigableViewDescriptor.viewName) &&
        Objects.equals(this.viewClassName, navigableViewDescriptor.viewClassName) &&
        Objects.equals(this.title, navigableViewDescriptor.title) &&
        Objects.equals(this.icon, navigableViewDescriptor.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(viewName, viewClassName, title, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigableViewDescriptor {\n");
    sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
    sb.append("    viewClassName: ").append(toIndentedString(viewClassName)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
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
