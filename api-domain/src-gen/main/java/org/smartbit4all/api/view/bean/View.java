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
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewEventHandler;
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
  View.VARIABLES,
  View.STATE,
  View.TYPE,
  View.CONTAINER_UUID,
  View.MODEL,
  View.CONSTRAINT,
  View.CLOSED_CHILDREN_VIEWS,
  View.DOWNLOADABLE_ITEMS,
  View.ACTIONS,
  View.EVENT_HANDLERS,
  View.WIDGET_MODELS,
  View.VALUE_SETS,
  View.KEEP_MODEL_ON_IMPLICIT_CLOSE,
  View.CALLBACKS,
  View.LAYOUTS,
  View.COMPONENT_LAYOUTS
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

  public static final String VARIABLES = "variables";
  private Map<String, Object> variables = new HashMap<>();

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

  public static final String ACTIONS = "actions";
  private List<UiAction> actions = new ArrayList<>();

  public static final String EVENT_HANDLERS = "eventHandlers";
  private List<ViewEventHandler> eventHandlers = new ArrayList<>();

  public static final String WIDGET_MODELS = "widgetModels";
  private Map<String, Object> widgetModels = new HashMap<>();

  public static final String VALUE_SETS = "valueSets";
  private Map<String, ValueSet> valueSets = new HashMap<>();

  public static final String KEEP_MODEL_ON_IMPLICIT_CLOSE = "keepModelOnImplicitClose";
  private Boolean keepModelOnImplicitClose;

  public static final String CALLBACKS = "callbacks";
  private Map<String, Object> callbacks = new HashMap<>();

  public static final String LAYOUTS = "layouts";
  private Map<String, SmartLayoutDefinition> layouts = new HashMap<>();

  public static final String COMPONENT_LAYOUTS = "componentLayouts";
  private Map<String, SmartComponentLayoutDefinition> componentLayouts = new HashMap<>();

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
   * The incoming parameters of the view. If we restart the given view with the same model then this parameters remain.
   * @return parameters
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The incoming parameters of the view. If we restart the given view with the same model then this parameters remain.")
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


  public View variables(Map<String, Object> variables) {
    
    this.variables = variables;
    return this;
  }

  public View putVariablesItem(String key, Object variablesItem) {
    this.variables.put(key, variablesItem);
    return this;
  }

   /**
   * The variable to store while the page is opened. It will be cleared if we open it again.
   * @return variables
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The variable to store while the page is opened. It will be cleared if we open it again.")
  @JsonProperty(VARIABLES)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.ALWAYS)

  public Map<String, Object> getVariables() {
    return variables;
  }


  @JsonProperty(VARIABLES)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.ALWAYS)
  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
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


  public View actions(List<UiAction> actions) {
    
    this.actions = actions;
    return this;
  }

  public View addActionsItem(UiAction actionsItem) {
    this.actions.add(actionsItem);
    return this;
  }

   /**
   * Get actions
   * @return actions
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(ACTIONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<UiAction> getActions() {
    return actions;
  }


  @JsonProperty(ACTIONS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setActions(List<UiAction> actions) {
    this.actions = actions;
  }


  public View eventHandlers(List<ViewEventHandler> eventHandlers) {
    
    this.eventHandlers = eventHandlers;
    return this;
  }

  public View addEventHandlersItem(ViewEventHandler eventHandlersItem) {
    this.eventHandlers.add(eventHandlersItem);
    return this;
  }

   /**
   * Get eventHandlers
   * @return eventHandlers
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(EVENT_HANDLERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<ViewEventHandler> getEventHandlers() {
    return eventHandlers;
  }


  @JsonProperty(EVENT_HANDLERS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setEventHandlers(List<ViewEventHandler> eventHandlers) {
    this.eventHandlers = eventHandlers;
  }


  public View widgetModels(Map<String, Object> widgetModels) {
    
    this.widgetModels = widgetModels;
    return this;
  }

  public View putWidgetModelsItem(String key, Object widgetModelsItem) {
    this.widgetModels.put(key, widgetModelsItem);
    return this;
  }

   /**
   * Get widgetModels
   * @return widgetModels
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(WIDGET_MODELS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, Object> getWidgetModels() {
    return widgetModels;
  }


  @JsonProperty(WIDGET_MODELS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setWidgetModels(Map<String, Object> widgetModels) {
    this.widgetModels = widgetModels;
  }


  public View valueSets(Map<String, ValueSet> valueSets) {
    
    this.valueSets = valueSets;
    return this;
  }

  public View putValueSetsItem(String key, ValueSet valueSetsItem) {
    this.valueSets.put(key, valueSetsItem);
    return this;
  }

   /**
   * Get valueSets
   * @return valueSets
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(VALUE_SETS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, ValueSet> getValueSets() {
    return valueSets;
  }


  @JsonProperty(VALUE_SETS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setValueSets(Map<String, ValueSet> valueSets) {
    this.valueSets = valueSets;
  }


  public View keepModelOnImplicitClose(Boolean keepModelOnImplicitClose) {
    
    this.keepModelOnImplicitClose = keepModelOnImplicitClose;
    return this;
  }

   /**
   * Get keepModelOnImplicitClose
   * @return keepModelOnImplicitClose
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(KEEP_MODEL_ON_IMPLICIT_CLOSE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getKeepModelOnImplicitClose() {
    return keepModelOnImplicitClose;
  }


  @JsonProperty(KEEP_MODEL_ON_IMPLICIT_CLOSE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKeepModelOnImplicitClose(Boolean keepModelOnImplicitClose) {
    this.keepModelOnImplicitClose = keepModelOnImplicitClose;
  }


  public View callbacks(Map<String, Object> callbacks) {
    
    this.callbacks = callbacks;
    return this;
  }

  public View putCallbacksItem(String key, Object callbacksItem) {
    this.callbacks.put(key, callbacksItem);
    return this;
  }

   /**
   * Get callbacks
   * @return callbacks
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CALLBACKS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, Object> getCallbacks() {
    return callbacks;
  }


  @JsonProperty(CALLBACKS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCallbacks(Map<String, Object> callbacks) {
    this.callbacks = callbacks;
  }


  public View layouts(Map<String, SmartLayoutDefinition> layouts) {
    
    this.layouts = layouts;
    return this;
  }

  public View putLayoutsItem(String key, SmartLayoutDefinition layoutsItem) {
    this.layouts.put(key, layoutsItem);
    return this;
  }

   /**
   * The layouts defined in the view. This named layouts can be used by the UI to render
   * @return layouts
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The layouts defined in the view. This named layouts can be used by the UI to render")
  @JsonProperty(LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, SmartLayoutDefinition> getLayouts() {
    return layouts;
  }


  @JsonProperty(LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setLayouts(Map<String, SmartLayoutDefinition> layouts) {
    this.layouts = layouts;
  }


  public View componentLayouts(Map<String, SmartComponentLayoutDefinition> componentLayouts) {
    
    this.componentLayouts = componentLayouts;
    return this;
  }

  public View putComponentLayoutsItem(String key, SmartComponentLayoutDefinition componentLayoutsItem) {
    this.componentLayouts.put(key, componentLayoutsItem);
    return this;
  }

   /**
   * The components layouts defined in the view. This named layouts can be used by the UI to render
   * @return componentLayouts
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The components layouts defined in the view. This named layouts can be used by the UI to render")
  @JsonProperty(COMPONENT_LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, SmartComponentLayoutDefinition> getComponentLayouts() {
    return componentLayouts;
  }


  @JsonProperty(COMPONENT_LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setComponentLayouts(Map<String, SmartComponentLayoutDefinition> componentLayouts) {
    this.componentLayouts = componentLayouts;
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
        Objects.equals(this.variables, view.variables) &&
        Objects.equals(this.state, view.state) &&
        Objects.equals(this.type, view.type) &&
        Objects.equals(this.containerUuid, view.containerUuid) &&
        Objects.equals(this.model, view.model) &&
        Objects.equals(this.constraint, view.constraint) &&
        Objects.equals(this.closedChildrenViews, view.closedChildrenViews) &&
        Objects.equals(this.downloadableItems, view.downloadableItems) &&
        Objects.equals(this.actions, view.actions) &&
        Objects.equals(this.eventHandlers, view.eventHandlers) &&
        Objects.equals(this.widgetModels, view.widgetModels) &&
        Objects.equals(this.valueSets, view.valueSets) &&
        Objects.equals(this.keepModelOnImplicitClose, view.keepModelOnImplicitClose) &&
        Objects.equals(this.callbacks, view.callbacks) &&
        Objects.equals(this.layouts, view.layouts) &&
        Objects.equals(this.componentLayouts, view.componentLayouts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, viewName, objectUri, branchUri, parameters, variables, state, type, containerUuid, model, constraint, closedChildrenViews, downloadableItems, actions, eventHandlers, widgetModels, valueSets, keepModelOnImplicitClose, callbacks, layouts, componentLayouts);
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
    sb.append("    variables: ").append(toIndentedString(variables)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    containerUuid: ").append(toIndentedString(containerUuid)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    constraint: ").append(toIndentedString(constraint)).append("\n");
    sb.append("    closedChildrenViews: ").append(toIndentedString(closedChildrenViews)).append("\n");
    sb.append("    downloadableItems: ").append(toIndentedString(downloadableItems)).append("\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
    sb.append("    eventHandlers: ").append(toIndentedString(eventHandlers)).append("\n");
    sb.append("    widgetModels: ").append(toIndentedString(widgetModels)).append("\n");
    sb.append("    valueSets: ").append(toIndentedString(valueSets)).append("\n");
    sb.append("    keepModelOnImplicitClose: ").append(toIndentedString(keepModelOnImplicitClose)).append("\n");
    sb.append("    callbacks: ").append(toIndentedString(callbacks)).append("\n");
    sb.append("    layouts: ").append(toIndentedString(layouts)).append("\n");
    sb.append("    componentLayouts: ").append(toIndentedString(componentLayouts)).append("\n");
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

