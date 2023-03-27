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
import java.util.ArrayList;
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
 * View
 */
@JsonPropertyOrder({
  View.UUID,
  View.VIEW_NAME,
  View.OBJECT_URI,
  View.BRANCH_URI,
  View.PARAMETERS,
  View.STATE,
  View.TYPE,
  View.CONTAINER_UUID,
  View.MODEL,
  View.CONSTRAINT,
  View.CLOSED_CHILDREN_VIEWS,
  View.DOWNLOADABLE_ITEMS
})
@JsonTypeName("View")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class View {
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

  public static final String CONTAINER_UUID = "containerUuid";
  private UUID containerUuid;

  public static final String MODEL = "model";
  private Object model;

  public static final String CONSTRAINT = "constraint";
  private ViewConstraint constraint;

  public static final String CLOSED_CHILDREN_VIEWS = "closedChildrenViews";
  private List<View> closedChildrenViews = new ArrayList<>();

  public static final String DOWNLOADABLE_ITEMS = "downloadableItems";
  private Map<String, URI> downloadableItems = new HashMap<>();

  public View() { 
  }

  public View uuid(UUID uuid) {
    
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


  public View viewName(String viewName) {
    
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


  public View objectUri(URI objectUri) {
    
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


  public View branchUri(URI branchUri) {
    
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


  public View parameters(Map<String, Object> parameters) {
    
    this.parameters = parameters;
    return this;
  }

  public View putParametersItem(String key, Object parametersItem) {
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


  public View state(ViewState state) {
    
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


  public View type(ViewType type) {
    
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


  public View containerUuid(UUID containerUuid) {
    
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


  public View model(Object model) {
    
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


  public View constraint(ViewConstraint constraint) {
    
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


  public View closedChildrenViews(List<View> closedChildrenViews) {
    
    this.closedChildrenViews = closedChildrenViews;
    return this;
  }

  public View addClosedChildrenViewsItem(View closedChildrenViewsItem) {
    this.closedChildrenViews.add(closedChildrenViewsItem);
    return this;
  }

   /**
   * Get closedChildrenViews
   * @return closedChildrenViews
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CLOSED_CHILDREN_VIEWS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<View> getClosedChildrenViews() {
    return closedChildrenViews;
  }


  @JsonProperty(CLOSED_CHILDREN_VIEWS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setClosedChildrenViews(List<View> closedChildrenViews) {
    this.closedChildrenViews = closedChildrenViews;
  }


  public View downloadableItems(Map<String, URI> downloadableItems) {
    
    this.downloadableItems = downloadableItems;
    return this;
  }

  public View putDownloadableItemsItem(String key, URI downloadableItemsItem) {
    this.downloadableItems.put(key, downloadableItemsItem);
    return this;
  }

   /**
   * Get downloadableItems
   * @return downloadableItems
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(DOWNLOADABLE_ITEMS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, URI> getDownloadableItems() {
    return downloadableItems;
  }


  @JsonProperty(DOWNLOADABLE_ITEMS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDownloadableItems(Map<String, URI> downloadableItems) {
    this.downloadableItems = downloadableItems;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    View view = (View) o;
    return Objects.equals(this.uuid, view.uuid) &&
        Objects.equals(this.viewName, view.viewName) &&
        Objects.equals(this.objectUri, view.objectUri) &&
        Objects.equals(this.branchUri, view.branchUri) &&
        Objects.equals(this.parameters, view.parameters) &&
        Objects.equals(this.state, view.state) &&
        Objects.equals(this.type, view.type) &&
        Objects.equals(this.containerUuid, view.containerUuid) &&
        Objects.equals(this.model, view.model) &&
        Objects.equals(this.constraint, view.constraint) &&
        Objects.equals(this.closedChildrenViews, view.closedChildrenViews) &&
        Objects.equals(this.downloadableItems, view.downloadableItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, viewName, objectUri, branchUri, parameters, state, type, containerUuid, model, constraint, closedChildrenViews, downloadableItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class View {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    branchUri: ").append(toIndentedString(branchUri)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    containerUuid: ").append(toIndentedString(containerUuid)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    constraint: ").append(toIndentedString(constraint)).append("\n");
    sb.append("    closedChildrenViews: ").append(toIndentedString(closedChildrenViews)).append("\n");
    sb.append("    downloadableItems: ").append(toIndentedString(downloadableItems)).append("\n");
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

