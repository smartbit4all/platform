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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.Style;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.ValueSet;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Model of a UI component. Contains all information in one object, which are needed  to render a fully functionin component. 
 */
@ApiModel(description = "Model of a UI component. Contains all information in one object, which are needed  to render a fully functionin component. ")
@JsonPropertyOrder({
  ComponentModel.UUID,
  ComponentModel.NAME,
  ComponentModel.DATA,
  ComponentModel.CONSTRAINTS,
  ComponentModel.LAYOUTS,
  ComponentModel.COMPONENT_LAYOUTS,
  ComponentModel.ACTIONS,
  ComponentModel.VALUE_SETS,
  ComponentModel.WIDGETS,
  ComponentModel.STYLE
})
@JsonTypeName("ComponentModel")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ComponentModel {
  public static final String UUID = "uuid";
  private UUID uuid;

  public static final String NAME = "name";
  private String name;

  public static final String DATA = "data";
  private Object data;

  public static final String CONSTRAINTS = "constraints";
  private List<ComponentConstraint> constraints = new ArrayList<>();

  public static final String LAYOUTS = "layouts";
  private Map<String, SmartLayoutDefinition> layouts = new HashMap<>();

  public static final String COMPONENT_LAYOUTS = "componentLayouts";
  private Map<String, SmartComponentLayoutDefinition> componentLayouts = null;

  public static final String ACTIONS = "actions";
  private List<UiAction> actions = new ArrayList<>();

  public static final String VALUE_SETS = "valueSets";
  private Map<String, ValueSet> valueSets = null;

  public static final String WIDGETS = "widgets";
  private List<String> widgets = null;

  public static final String STYLE = "style";
  private Style style;

  public ComponentModel() { 
  }

  public ComponentModel uuid(UUID uuid) {
    
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


  public ComponentModel name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
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


  public ComponentModel data(Object data) {
    
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getData() {
    return data;
  }


  @JsonProperty(DATA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setData(Object data) {
    this.data = data;
  }


  public ComponentModel constraints(List<ComponentConstraint> constraints) {
    
    this.constraints = constraints;
    return this;
  }

  public ComponentModel addConstraintsItem(ComponentConstraint constraintsItem) {
    this.constraints.add(constraintsItem);
    return this;
  }

   /**
   * Get constraints
   * @return constraints
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CONSTRAINTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<ComponentConstraint> getConstraints() {
    return constraints;
  }


  @JsonProperty(CONSTRAINTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setConstraints(List<ComponentConstraint> constraints) {
    this.constraints = constraints;
  }


  public ComponentModel layouts(Map<String, SmartLayoutDefinition> layouts) {
    
    this.layouts = layouts;
    return this;
  }

  public ComponentModel putLayoutsItem(String key, SmartLayoutDefinition layoutsItem) {
    this.layouts.put(key, layoutsItem);
    return this;
  }

   /**
   * Get layouts
   * @return layouts
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
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


  public ComponentModel componentLayouts(Map<String, SmartComponentLayoutDefinition> componentLayouts) {
    
    this.componentLayouts = componentLayouts;
    return this;
  }

  public ComponentModel putComponentLayoutsItem(String key, SmartComponentLayoutDefinition componentLayoutsItem) {
    if (this.componentLayouts == null) {
      this.componentLayouts = new HashMap<>();
    }
    this.componentLayouts.put(key, componentLayoutsItem);
    return this;
  }

   /**
   * Get componentLayouts
   * @return componentLayouts
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(COMPONENT_LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, SmartComponentLayoutDefinition> getComponentLayouts() {
    return componentLayouts;
  }


  @JsonProperty(COMPONENT_LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setComponentLayouts(Map<String, SmartComponentLayoutDefinition> componentLayouts) {
    this.componentLayouts = componentLayouts;
  }


  public ComponentModel actions(List<UiAction> actions) {
    
    this.actions = actions;
    return this;
  }

  public ComponentModel addActionsItem(UiAction actionsItem) {
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


  public ComponentModel valueSets(Map<String, ValueSet> valueSets) {
    
    this.valueSets = valueSets;
    return this;
  }

  public ComponentModel putValueSetsItem(String key, ValueSet valueSetsItem) {
    if (this.valueSets == null) {
      this.valueSets = new HashMap<>();
    }
    this.valueSets.put(key, valueSetsItem);
    return this;
  }

   /**
   * Get valueSets
   * @return valueSets
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VALUE_SETS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, ValueSet> getValueSets() {
    return valueSets;
  }


  @JsonProperty(VALUE_SETS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValueSets(Map<String, ValueSet> valueSets) {
    this.valueSets = valueSets;
  }


  public ComponentModel widgets(List<String> widgets) {
    
    this.widgets = widgets;
    return this;
  }

  public ComponentModel addWidgetsItem(String widgetsItem) {
    if (this.widgets == null) {
      this.widgets = new ArrayList<>();
    }
    this.widgets.add(widgetsItem);
    return this;
  }

   /**
   * List of widgets (widgetIds).
   * @return widgets
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "List of widgets (widgetIds).")
  @JsonProperty(WIDGETS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getWidgets() {
    return widgets;
  }


  @JsonProperty(WIDGETS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setWidgets(List<String> widgets) {
    this.widgets = widgets;
  }


  public ComponentModel style(Style style) {
    
    this.style = style;
    return this;
  }

   /**
   * Get style
   * @return style
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(STYLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Style getStyle() {
    return style;
  }


  @JsonProperty(STYLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setStyle(Style style) {
    this.style = style;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComponentModel componentModel = (ComponentModel) o;
    return Objects.equals(this.uuid, componentModel.uuid) &&
        Objects.equals(this.name, componentModel.name) &&
        Objects.equals(this.data, componentModel.data) &&
        Objects.equals(this.constraints, componentModel.constraints) &&
        Objects.equals(this.layouts, componentModel.layouts) &&
        Objects.equals(this.componentLayouts, componentModel.componentLayouts) &&
        Objects.equals(this.actions, componentModel.actions) &&
        Objects.equals(this.valueSets, componentModel.valueSets) &&
        Objects.equals(this.widgets, componentModel.widgets) &&
        Objects.equals(this.style, componentModel.style);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, data, constraints, layouts, componentLayouts, actions, valueSets, widgets, style);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComponentModel {\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    constraints: ").append(toIndentedString(constraints)).append("\n");
    sb.append("    layouts: ").append(toIndentedString(layouts)).append("\n");
    sb.append("    componentLayouts: ").append(toIndentedString(componentLayouts)).append("\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
    sb.append("    valueSets: ").append(toIndentedString(valueSets)).append("\n");
    sb.append("    widgets: ").append(toIndentedString(widgets)).append("\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
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

