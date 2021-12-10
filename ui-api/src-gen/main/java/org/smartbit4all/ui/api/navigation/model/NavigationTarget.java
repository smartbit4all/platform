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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetState;
import org.smartbit4all.ui.api.navigation.model.NavigationTargetType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * NavigationTarget
 */
@JsonPropertyOrder({
  NavigationTarget.UUID,
  NavigationTarget.VIEW_NAME,
  NavigationTarget.VIEW_OBJECT_URI,
  NavigationTarget.PARAMETERS,
  NavigationTarget.STATE,
  NavigationTarget.TYPE,
  NavigationTarget.FULL_SIZE,
  NavigationTarget.TITLE,
  NavigationTarget.ICON
})
@JsonTypeName("NavigationTarget")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class NavigationTarget {
  public static final String UUID = "uuid";
  private UUID uuid;

  public static final String VIEW_NAME = "viewName";
  private String viewName;

  public static final String VIEW_OBJECT_URI = "viewObjectUri";
  private URI viewObjectUri;

  public static final String PARAMETERS = "parameters";
  private Map<String, Object> parameters = new HashMap<>();

  public static final String STATE = "state";
  private NavigationTargetState state = NavigationTargetState.TO_OPEN;

  public static final String TYPE = "type";
  private NavigationTargetType type = NavigationTargetType.NORMAL;

  public static final String FULL_SIZE = "fullSize";
  private Boolean fullSize = false;

  public static final String TITLE = "title";
  private String title;

  public static final String ICON = "icon";
  private String icon;


  public NavigationTarget uuid(UUID uuid) {
    
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


  public NavigationTarget viewName(String viewName) {
    
    this.viewName = viewName;
    return this;
  }

   /**
   * Get viewName
   * @return viewName
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
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


  public NavigationTarget viewObjectUri(URI viewObjectUri) {
    
    this.viewObjectUri = viewObjectUri;
    return this;
  }

   /**
   * Get viewObjectUri
   * @return viewObjectUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VIEW_OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getViewObjectUri() {
    return viewObjectUri;
  }


  @JsonProperty(VIEW_OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setViewObjectUri(URI viewObjectUri) {
    this.viewObjectUri = viewObjectUri;
  }


  public NavigationTarget parameters(Map<String, Object> parameters) {
    
    this.parameters = parameters;
    return this;
  }

  public NavigationTarget putParametersItem(String key, Object parametersItem) {
    this.parameters.put(key, parametersItem);
    return this;
  }

   /**
   * Get parameters
   * @return parameters
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(PARAMETERS)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.ALWAYS)

  public Map<String, Object> getParameters() {
    return parameters;
  }


  @JsonProperty(PARAMETERS)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.ALWAYS)
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }


  public NavigationTarget state(NavigationTargetState state) {
    
    this.state = state;
    return this;
  }

   /**
   * Get state
   * @return state
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationTargetState getState() {
    return state;
  }


  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setState(NavigationTargetState state) {
    this.state = state;
  }


  public NavigationTarget type(NavigationTargetType type) {
    
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public NavigationTargetType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setType(NavigationTargetType type) {
    this.type = type;
  }


  public NavigationTarget fullSize(Boolean fullSize) {
    
    this.fullSize = fullSize;
    return this;
  }

   /**
   * Get fullSize
   * @return fullSize
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(FULL_SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getFullSize() {
    return fullSize;
  }


  @JsonProperty(FULL_SIZE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFullSize(Boolean fullSize) {
    this.fullSize = fullSize;
  }


  public NavigationTarget title(String title) {
    
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


  public NavigationTarget icon(String icon) {
    
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
    NavigationTarget navigationTarget = (NavigationTarget) o;
    return Objects.equals(this.uuid, navigationTarget.uuid) &&
        Objects.equals(this.viewName, navigationTarget.viewName) &&
        Objects.equals(this.viewObjectUri, navigationTarget.viewObjectUri) &&
        Objects.equals(this.parameters, navigationTarget.parameters) &&
        Objects.equals(this.state, navigationTarget.state) &&
        Objects.equals(this.type, navigationTarget.type) &&
        Objects.equals(this.fullSize, navigationTarget.fullSize) &&
        Objects.equals(this.title, navigationTarget.title) &&
        Objects.equals(this.icon, navigationTarget.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, viewName, viewObjectUri, parameters, state, type, fullSize, title, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NavigationTarget {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
    sb.append("    viewObjectUri: ").append(toIndentedString(viewObjectUri)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    fullSize: ").append(toIndentedString(fullSize)).append("\n");
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

