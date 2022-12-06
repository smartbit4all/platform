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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.api.view.bean.ViewType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * ViewData
 */
@JsonPropertyOrder({
  ViewData.UUID,
  ViewData.VIEW_NAME,
  ViewData.OBJECT_URI,
  ViewData.BRANCH_URI,
  ViewData.PARAMETERS,
  ViewData.STATE,
  ViewData.TYPE,
  ViewData.CONSTRAINT,
  ViewData.CONTAINER_UUID,
  ViewData.MODEL
})
@JsonTypeName("ViewData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ViewData {
  public static final String UUID = "uuid";
  private UUID uuid;

  public static final String VIEW_NAME = "viewName";
  private String viewName;

  public static final String OBJECT_URI = "objectUri";
  private URI objectUri;

  public static final String BRANCH_URI = "branchUri";
  private URI branchUri;

  public static final String PARAMETERS = "parameters";
  private Map<String, Object> parameters = new HashMap<>();

  public static final String STATE = "state";
  private ViewState state = ViewState.TO_OPEN;

  public static final String TYPE = "type";
  private ViewType type = ViewType.NORMAL;

  public static final String CONSTRAINT = "constraint";
  private ViewConstraint constraint;

  public static final String CONTAINER_UUID = "containerUuid";
  private UUID containerUuid;

  public static final String MODEL = "model";
  private Object model;

  public ViewData() { 
  }

  public ViewData uuid(UUID uuid) {
    
    this.uuid = uuid;
    return this;
  }

   /**
   * Unique identifier of the View.
   * @return uuid
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "Unique identifier of the View.")
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


  public ViewData viewName(String viewName) {
    
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


  public ViewData objectUri(URI objectUri) {
    
    this.objectUri = objectUri;
    return this;
  }

   /**
   * Get objectUri
   * @return objectUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getObjectUri() {
    return objectUri;
  }


  @JsonProperty(OBJECT_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }


  public ViewData branchUri(URI branchUri) {
    
    this.branchUri = branchUri;
    return this;
  }

   /**
   * Get branchUri
   * @return branchUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(BRANCH_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getBranchUri() {
    return branchUri;
  }


  @JsonProperty(BRANCH_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBranchUri(URI branchUri) {
    this.branchUri = branchUri;
  }


  public ViewData parameters(Map<String, Object> parameters) {
    
    this.parameters = parameters;
    return this;
  }

  public ViewData putParametersItem(String key, Object parametersItem) {
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


  public ViewData state(ViewState state) {
    
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

  public ViewState getState() {
    return state;
  }


  @JsonProperty(STATE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setState(ViewState state) {
    this.state = state;
  }


  public ViewData type(ViewType type) {
    
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

  public ViewType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setType(ViewType type) {
    this.type = type;
  }


  public ViewData constraint(ViewConstraint constraint) {
    
    this.constraint = constraint;
    return this;
  }

   /**
   * Get constraint
   * @return constraint
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CONSTRAINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ViewConstraint getConstraint() {
    return constraint;
  }


  @JsonProperty(CONSTRAINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setConstraint(ViewConstraint constraint) {
    this.constraint = constraint;
  }


  public ViewData containerUuid(UUID containerUuid) {
    
    this.containerUuid = containerUuid;
    return this;
  }

   /**
   * Get containerUuid
   * @return containerUuid
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CONTAINER_UUID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public UUID getContainerUuid() {
    return containerUuid;
  }


  @JsonProperty(CONTAINER_UUID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setContainerUuid(UUID containerUuid) {
    this.containerUuid = containerUuid;
  }


  public ViewData model(Object model) {
    
    this.model = model;
    return this;
  }

   /**
   * Get model
   * @return model
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(MODEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getModel() {
    return model;
  }


  @JsonProperty(MODEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setModel(Object model) {
    this.model = model;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ViewData viewData = (ViewData) o;
    return Objects.equals(this.uuid, viewData.uuid) &&
        Objects.equals(this.viewName, viewData.viewName) &&
        Objects.equals(this.objectUri, viewData.objectUri) &&
        Objects.equals(this.branchUri, viewData.branchUri) &&
        Objects.equals(this.parameters, viewData.parameters) &&
        Objects.equals(this.state, viewData.state) &&
        Objects.equals(this.type, viewData.type) &&
        Objects.equals(this.constraint, viewData.constraint) &&
        Objects.equals(this.containerUuid, viewData.containerUuid) &&
        Objects.equals(this.model, viewData.model);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, viewName, objectUri, branchUri, parameters, state, type, constraint, containerUuid, model);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ViewData {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    branchUri: ").append(toIndentedString(branchUri)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    constraint: ").append(toIndentedString(constraint)).append("\n");
    sb.append("    containerUuid: ").append(toIndentedString(containerUuid)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
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

