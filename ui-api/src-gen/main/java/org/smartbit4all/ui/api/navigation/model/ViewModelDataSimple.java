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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Represent a viewmodel&#39;s data.
 */
@ApiModel(description = "Represent a viewmodel's data.")
@JsonPropertyOrder({
  ViewModelDataSimple.UUID,
  ViewModelDataSimple.PATH,
  ViewModelDataSimple.NAVIGATION_TARGET,
  ViewModelDataSimple.CHILDREN
})
@JsonTypeName("ViewModelDataSimple")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ViewModelDataSimple {
  public static final String UUID = "uuid";
  private UUID uuid;

  public static final String PATH = "path";
  private String path;

  public static final String NAVIGATION_TARGET = "navigationTarget";
  private NavigationTarget navigationTarget;

  public static final String CHILDREN = "children";
  private Map<String, ViewModelDataSimple> children = new HashMap<>();

  public ViewModelDataSimple() { 
  }

  public ViewModelDataSimple uuid(UUID uuid) {
    
    this.uuid = uuid;
    return this;
  }

   /**
   * Get uuid
   * @return uuid
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(UUID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public UUID getUuid() {
    return uuid;
  }


  @JsonProperty(UUID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }


  public ViewModelDataSimple path(String path) {
    
    this.path = path;
    return this;
  }

   /**
   * Get path
   * @return path
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPath() {
    return path;
  }


  @JsonProperty(PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPath(String path) {
    this.path = path;
  }


  public ViewModelDataSimple navigationTarget(NavigationTarget navigationTarget) {
    
    this.navigationTarget = navigationTarget;
    return this;
  }

   /**
   * Get navigationTarget
   * @return navigationTarget
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(NAVIGATION_TARGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public NavigationTarget getNavigationTarget() {
    return navigationTarget;
  }


  @JsonProperty(NAVIGATION_TARGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setNavigationTarget(NavigationTarget navigationTarget) {
    this.navigationTarget = navigationTarget;
  }


  public ViewModelDataSimple children(Map<String, ViewModelDataSimple> children) {
    
    this.children = children;
    return this;
  }

  public ViewModelDataSimple putChildrenItem(String key, ViewModelDataSimple childrenItem) {
    this.children.put(key, childrenItem);
    return this;
  }

   /**
   * Get children
   * @return children
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CHILDREN)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, ViewModelDataSimple> getChildren() {
    return children;
  }


  @JsonProperty(CHILDREN)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setChildren(Map<String, ViewModelDataSimple> children) {
    this.children = children;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewModelDataSimple viewModelDataSimple = (ViewModelDataSimple) o;
    return Objects.equals(this.uuid, viewModelDataSimple.uuid) &&
        Objects.equals(this.path, viewModelDataSimple.path) &&
        Objects.equals(this.navigationTarget, viewModelDataSimple.navigationTarget) &&
        Objects.equals(this.children, viewModelDataSimple.children);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, path, navigationTarget, children);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewModelDataSimple {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    navigationTarget: ").append(toIndentedString(navigationTarget)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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

