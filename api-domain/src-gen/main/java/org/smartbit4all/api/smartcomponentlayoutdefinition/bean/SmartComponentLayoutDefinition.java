/*
 * Form layout definition
 * Contains form layout definition objects.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.smartcomponentlayoutdefinition.bean;

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
import java.util.List;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.ComponentType;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentWidgetDefinition;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * SmartComponentLayoutDefinition
 */
@JsonPropertyOrder({
  SmartComponentLayoutDefinition.TYPE,
  SmartComponentLayoutDefinition.EXPANDABLE,
  SmartComponentLayoutDefinition.EXPANDABLE_SECTION_LABEL,
  SmartComponentLayoutDefinition.DIRECTION,
  SmartComponentLayoutDefinition.PARENT_COMPONENT,
  SmartComponentLayoutDefinition.COMPONENTS,
  SmartComponentLayoutDefinition.WIDGET,
  SmartComponentLayoutDefinition.FORM
})
@JsonTypeName("SmartComponentLayoutDefinition")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SmartComponentLayoutDefinition {
  public static final String TYPE = "type";
  private ComponentType type;

  public static final String EXPANDABLE = "expandable";
  private Boolean expandable;

  public static final String EXPANDABLE_SECTION_LABEL = "expandableSectionLabel";
  private String expandableSectionLabel;

  public static final String DIRECTION = "direction";
  private LayoutDirection direction;

  public static final String PARENT_COMPONENT = "parentComponent";
  private Object parentComponent;

  public static final String COMPONENTS = "components";
  private List<SmartComponentLayoutDefinition> components = null;

  public static final String WIDGET = "widget";
  private SmartComponentWidgetDefinition widget;

  public static final String FORM = "form";
  private List<SmartWidgetDefinition> form = null;

  public SmartComponentLayoutDefinition() { 
  }

  public SmartComponentLayoutDefinition type(ComponentType type) {
    
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

  public ComponentType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setType(ComponentType type) {
    this.type = type;
  }


  public SmartComponentLayoutDefinition expandable(Boolean expandable) {
    
    this.expandable = expandable;
    return this;
  }

   /**
   * Get expandable
   * @return expandable
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(EXPANDABLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getExpandable() {
    return expandable;
  }


  @JsonProperty(EXPANDABLE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpandable(Boolean expandable) {
    this.expandable = expandable;
  }


  public SmartComponentLayoutDefinition expandableSectionLabel(String expandableSectionLabel) {
    
    this.expandableSectionLabel = expandableSectionLabel;
    return this;
  }

   /**
   * Get expandableSectionLabel
   * @return expandableSectionLabel
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(EXPANDABLE_SECTION_LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getExpandableSectionLabel() {
    return expandableSectionLabel;
  }


  @JsonProperty(EXPANDABLE_SECTION_LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setExpandableSectionLabel(String expandableSectionLabel) {
    this.expandableSectionLabel = expandableSectionLabel;
  }


  public SmartComponentLayoutDefinition direction(LayoutDirection direction) {
    
    this.direction = direction;
    return this;
  }

   /**
   * Get direction
   * @return direction
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DIRECTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LayoutDirection getDirection() {
    return direction;
  }


  @JsonProperty(DIRECTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDirection(LayoutDirection direction) {
    this.direction = direction;
  }


  public SmartComponentLayoutDefinition parentComponent(Object parentComponent) {
    
    this.parentComponent = parentComponent;
    return this;
  }

   /**
   * This is a placeholder for SmartComponentApiClient. You might want to describe this further.
   * @return parentComponent
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "This is a placeholder for SmartComponentApiClient. You might want to describe this further.")
  @JsonProperty(PARENT_COMPONENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getParentComponent() {
    return parentComponent;
  }


  @JsonProperty(PARENT_COMPONENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setParentComponent(Object parentComponent) {
    this.parentComponent = parentComponent;
  }


  public SmartComponentLayoutDefinition components(List<SmartComponentLayoutDefinition> components) {
    
    this.components = components;
    return this;
  }

  public SmartComponentLayoutDefinition addComponentsItem(SmartComponentLayoutDefinition componentsItem) {
    if (this.components == null) {
      this.components = new ArrayList<>();
    }
    this.components.add(componentsItem);
    return this;
  }

   /**
   * Get components
   * @return components
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(COMPONENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<SmartComponentLayoutDefinition> getComponents() {
    return components;
  }


  @JsonProperty(COMPONENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setComponents(List<SmartComponentLayoutDefinition> components) {
    this.components = components;
  }


  public SmartComponentLayoutDefinition widget(SmartComponentWidgetDefinition widget) {
    
    this.widget = widget;
    return this;
  }

   /**
   * Get widget
   * @return widget
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(WIDGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SmartComponentWidgetDefinition getWidget() {
    return widget;
  }


  @JsonProperty(WIDGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setWidget(SmartComponentWidgetDefinition widget) {
    this.widget = widget;
  }


  public SmartComponentLayoutDefinition form(List<SmartWidgetDefinition> form) {
    
    this.form = form;
    return this;
  }

  public SmartComponentLayoutDefinition addFormItem(SmartWidgetDefinition formItem) {
    if (this.form == null) {
      this.form = new ArrayList<>();
    }
    this.form.add(formItem);
    return this;
  }

   /**
   * Get form
   * @return form
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(FORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<SmartWidgetDefinition> getForm() {
    return form;
  }


  @JsonProperty(FORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setForm(List<SmartWidgetDefinition> form) {
    this.form = form;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SmartComponentLayoutDefinition smartComponentLayoutDefinition = (SmartComponentLayoutDefinition) o;
    return Objects.equals(this.type, smartComponentLayoutDefinition.type) &&
        Objects.equals(this.expandable, smartComponentLayoutDefinition.expandable) &&
        Objects.equals(this.expandableSectionLabel, smartComponentLayoutDefinition.expandableSectionLabel) &&
        Objects.equals(this.direction, smartComponentLayoutDefinition.direction) &&
        Objects.equals(this.parentComponent, smartComponentLayoutDefinition.parentComponent) &&
        Objects.equals(this.components, smartComponentLayoutDefinition.components) &&
        Objects.equals(this.widget, smartComponentLayoutDefinition.widget) &&
        Objects.equals(this.form, smartComponentLayoutDefinition.form);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, expandable, expandableSectionLabel, direction, parentComponent, components, widget, form);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SmartComponentLayoutDefinition {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    expandable: ").append(toIndentedString(expandable)).append("\n");
    sb.append("    expandableSectionLabel: ").append(toIndentedString(expandableSectionLabel)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    parentComponent: ").append(toIndentedString(parentComponent)).append("\n");
    sb.append("    components: ").append(toIndentedString(components)).append("\n");
    sb.append("    widget: ").append(toIndentedString(widget)).append("\n");
    sb.append("    form: ").append(toIndentedString(form)).append("\n");
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
