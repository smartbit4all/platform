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


package org.smartbit4all.api.formdefinition.bean;

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
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetDirection;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartMatrixModel;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetHint;
import org.smartbit4all.api.value.bean.Value;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The layout definition of the SmartTextField widget.
 */
@ApiModel(description = "The layout definition of the SmartTextField widget.")
@JsonPropertyOrder({
  SmartWidgetDefinition.TYPE,
  SmartWidgetDefinition.KEY,
  SmartWidgetDefinition.LABEL,
  SmartWidgetDefinition.PLACEHOLDER,
  SmartWidgetDefinition.PREFIX,
  SmartWidgetDefinition.SUFFIX,
  SmartWidgetDefinition.MASK,
  SmartWidgetDefinition.ICON,
  SmartWidgetDefinition.ICON_COLOR,
  SmartWidgetDefinition.SHOW_LABEL,
  SmartWidgetDefinition.CSS_CLASS,
  SmartWidgetDefinition.CSS_LABEL_CLASS,
  SmartWidgetDefinition.IS_PASSWORD,
  SmartWidgetDefinition.VALUES,
  SmartWidgetDefinition.CHILDREN_COMPONENTS,
  SmartWidgetDefinition.SELECTION,
  SmartWidgetDefinition.DIRECTION,
  SmartWidgetDefinition.MATRIX,
  SmartWidgetDefinition.HINT
})
@JsonTypeName("SmartWidgetDefinition")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SmartWidgetDefinition {
  public static final String TYPE = "type";
  private SmartFormWidgetType type;

  public static final String KEY = "key";
  private String key;

  public static final String LABEL = "label";
  private String label;

  public static final String PLACEHOLDER = "placeholder";
  private String placeholder;

  public static final String PREFIX = "prefix";
  private String prefix;

  public static final String SUFFIX = "suffix";
  private String suffix;

  public static final String MASK = "mask";
  private String mask;

  public static final String ICON = "icon";
  private String icon;

  public static final String ICON_COLOR = "iconColor";
  private String iconColor;

  public static final String SHOW_LABEL = "showLabel";
  private Boolean showLabel = true;

  public static final String CSS_CLASS = "cssClass";
  private String cssClass;

  public static final String CSS_LABEL_CLASS = "cssLabelClass";
  private String cssLabelClass;

  public static final String IS_PASSWORD = "isPassword";
  private Boolean isPassword = false;

  public static final String VALUES = "values";
  private List<Value> values = null;

  public static final String CHILDREN_COMPONENTS = "childrenComponents";
  private List<SmartWidgetDefinition> childrenComponents = null;

  public static final String SELECTION = "selection";
  private SelectionDefinition selection;

  public static final String DIRECTION = "direction";
  private SmartFormWidgetDirection direction;

  public static final String MATRIX = "matrix";
  private SmartMatrixModel matrix;

  public static final String HINT = "hint";
  private SmartWidgetHint hint;

  public SmartWidgetDefinition() { 
  }

  public SmartWidgetDefinition type(SmartFormWidgetType type) {
    
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

  public SmartFormWidgetType getType() {
    return type;
  }


  @JsonProperty(TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setType(SmartFormWidgetType type) {
    this.type = type;
  }


  public SmartWidgetDefinition key(String key) {
    
    this.key = key;
    return this;
  }

   /**
   * The path of the desired parameter in the object.
   * @return key
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The path of the desired parameter in the object.")
  @JsonProperty(KEY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getKey() {
    return key;
  }


  @JsonProperty(KEY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setKey(String key) {
    this.key = key;
  }


  public SmartWidgetDefinition label(String label) {
    
    this.label = label;
    return this;
  }

   /**
   * The label of the text field
   * @return label
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The label of the text field")
  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getLabel() {
    return label;
  }


  @JsonProperty(LABEL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setLabel(String label) {
    this.label = label;
  }


  public SmartWidgetDefinition placeholder(String placeholder) {
    
    this.placeholder = placeholder;
    return this;
  }

   /**
   * The placeholder of the text field
   * @return placeholder
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The placeholder of the text field")
  @JsonProperty(PLACEHOLDER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPlaceholder() {
    return placeholder;
  }


  @JsonProperty(PLACEHOLDER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }


  public SmartWidgetDefinition prefix(String prefix) {
    
    this.prefix = prefix;
    return this;
  }

   /**
   * Text that appears as a prefix
   * @return prefix
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Text that appears as a prefix")
  @JsonProperty(PREFIX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPrefix() {
    return prefix;
  }


  @JsonProperty(PREFIX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }


  public SmartWidgetDefinition suffix(String suffix) {
    
    this.suffix = suffix;
    return this;
  }

   /**
   * Text that appears as a suffix
   * @return suffix
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Text that appears as a suffix")
  @JsonProperty(SUFFIX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSuffix() {
    return suffix;
  }


  @JsonProperty(SUFFIX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }


  public SmartWidgetDefinition mask(String mask) {
    
    this.mask = mask;
    return this;
  }

   /**
   * A descriptor that masks the input
   * @return mask
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A descriptor that masks the input")
  @JsonProperty(MASK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getMask() {
    return mask;
  }


  @JsonProperty(MASK)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMask(String mask) {
    this.mask = mask;
  }


  public SmartWidgetDefinition icon(String icon) {
    
    this.icon = icon;
    return this;
  }

   /**
   * An icon which appears as a suffix
   * @return icon
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "An icon which appears as a suffix")
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


  public SmartWidgetDefinition iconColor(String iconColor) {
    
    this.iconColor = iconColor;
    return this;
  }

   /**
   * Material theme class of the icon
   * @return iconColor
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Material theme class of the icon")
  @JsonProperty(ICON_COLOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIconColor() {
    return iconColor;
  }


  @JsonProperty(ICON_COLOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIconColor(String iconColor) {
    this.iconColor = iconColor;
  }


  public SmartWidgetDefinition showLabel(Boolean showLabel) {
    
    this.showLabel = showLabel;
    return this;
  }

   /**
   * Defines if the label appears above the widget or not
   * @return showLabel
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Defines if the label appears above the widget or not")
  @JsonProperty(SHOW_LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getShowLabel() {
    return showLabel;
  }


  @JsonProperty(SHOW_LABEL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setShowLabel(Boolean showLabel) {
    this.showLabel = showLabel;
  }


  public SmartWidgetDefinition cssClass(String cssClass) {
    
    this.cssClass = cssClass;
    return this;
  }

   /**
   * Optional css class of the widget
   * @return cssClass
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Optional css class of the widget")
  @JsonProperty(CSS_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCssClass() {
    return cssClass;
  }


  @JsonProperty(CSS_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCssClass(String cssClass) {
    this.cssClass = cssClass;
  }


  public SmartWidgetDefinition cssLabelClass(String cssLabelClass) {
    
    this.cssLabelClass = cssLabelClass;
    return this;
  }

   /**
   * Optional css class of the label of the widget
   * @return cssLabelClass
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Optional css class of the label of the widget")
  @JsonProperty(CSS_LABEL_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getCssLabelClass() {
    return cssLabelClass;
  }


  @JsonProperty(CSS_LABEL_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCssLabelClass(String cssLabelClass) {
    this.cssLabelClass = cssLabelClass;
  }


  public SmartWidgetDefinition isPassword(Boolean isPassword) {
    
    this.isPassword = isPassword;
    return this;
  }

   /**
   * Defines if the widget is a password input
   * @return isPassword
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Defines if the widget is a password input")
  @JsonProperty(IS_PASSWORD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getIsPassword() {
    return isPassword;
  }


  @JsonProperty(IS_PASSWORD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIsPassword(Boolean isPassword) {
    this.isPassword = isPassword;
  }


  public SmartWidgetDefinition values(List<Value> values) {
    
    this.values = values;
    return this;
  }

  public SmartWidgetDefinition addValuesItem(Value valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

   /**
   * Get values
   * @return values
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(VALUES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Value> getValues() {
    return values;
  }


  @JsonProperty(VALUES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValues(List<Value> values) {
    this.values = values;
  }


  public SmartWidgetDefinition childrenComponents(List<SmartWidgetDefinition> childrenComponents) {
    
    this.childrenComponents = childrenComponents;
    return this;
  }

  public SmartWidgetDefinition addChildrenComponentsItem(SmartWidgetDefinition childrenComponentsItem) {
    if (this.childrenComponents == null) {
      this.childrenComponents = new ArrayList<>();
    }
    this.childrenComponents.add(childrenComponentsItem);
    return this;
  }

   /**
   * Get childrenComponents
   * @return childrenComponents
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CHILDREN_COMPONENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<SmartWidgetDefinition> getChildrenComponents() {
    return childrenComponents;
  }


  @JsonProperty(CHILDREN_COMPONENTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChildrenComponents(List<SmartWidgetDefinition> childrenComponents) {
    this.childrenComponents = childrenComponents;
  }


  public SmartWidgetDefinition selection(SelectionDefinition selection) {
    
    this.selection = selection;
    return this;
  }

   /**
   * Get selection
   * @return selection
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SELECTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SelectionDefinition getSelection() {
    return selection;
  }


  @JsonProperty(SELECTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSelection(SelectionDefinition selection) {
    this.selection = selection;
  }


  public SmartWidgetDefinition direction(SmartFormWidgetDirection direction) {
    
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

  public SmartFormWidgetDirection getDirection() {
    return direction;
  }


  @JsonProperty(DIRECTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDirection(SmartFormWidgetDirection direction) {
    this.direction = direction;
  }


  public SmartWidgetDefinition matrix(SmartMatrixModel matrix) {
    
    this.matrix = matrix;
    return this;
  }

   /**
   * Get matrix
   * @return matrix
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(MATRIX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SmartMatrixModel getMatrix() {
    return matrix;
  }


  @JsonProperty(MATRIX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setMatrix(SmartMatrixModel matrix) {
    this.matrix = matrix;
  }


  public SmartWidgetDefinition hint(SmartWidgetHint hint) {
    
    this.hint = hint;
    return this;
  }

   /**
   * Get hint
   * @return hint
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(HINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SmartWidgetHint getHint() {
    return hint;
  }


  @JsonProperty(HINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setHint(SmartWidgetHint hint) {
    this.hint = hint;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SmartWidgetDefinition smartWidgetDefinition = (SmartWidgetDefinition) o;
    return Objects.equals(this.type, smartWidgetDefinition.type) &&
        Objects.equals(this.key, smartWidgetDefinition.key) &&
        Objects.equals(this.label, smartWidgetDefinition.label) &&
        Objects.equals(this.placeholder, smartWidgetDefinition.placeholder) &&
        Objects.equals(this.prefix, smartWidgetDefinition.prefix) &&
        Objects.equals(this.suffix, smartWidgetDefinition.suffix) &&
        Objects.equals(this.mask, smartWidgetDefinition.mask) &&
        Objects.equals(this.icon, smartWidgetDefinition.icon) &&
        Objects.equals(this.iconColor, smartWidgetDefinition.iconColor) &&
        Objects.equals(this.showLabel, smartWidgetDefinition.showLabel) &&
        Objects.equals(this.cssClass, smartWidgetDefinition.cssClass) &&
        Objects.equals(this.cssLabelClass, smartWidgetDefinition.cssLabelClass) &&
        Objects.equals(this.isPassword, smartWidgetDefinition.isPassword) &&
        Objects.equals(this.values, smartWidgetDefinition.values) &&
        Objects.equals(this.childrenComponents, smartWidgetDefinition.childrenComponents) &&
        Objects.equals(this.selection, smartWidgetDefinition.selection) &&
        Objects.equals(this.direction, smartWidgetDefinition.direction) &&
        Objects.equals(this.matrix, smartWidgetDefinition.matrix) &&
        Objects.equals(this.hint, smartWidgetDefinition.hint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, key, label, placeholder, prefix, suffix, mask, icon, iconColor, showLabel, cssClass, cssLabelClass, isPassword, values, childrenComponents, selection, direction, matrix, hint);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SmartWidgetDefinition {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    placeholder: ").append(toIndentedString(placeholder)).append("\n");
    sb.append("    prefix: ").append(toIndentedString(prefix)).append("\n");
    sb.append("    suffix: ").append(toIndentedString(suffix)).append("\n");
    sb.append("    mask: ").append(toIndentedString(mask)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    iconColor: ").append(toIndentedString(iconColor)).append("\n");
    sb.append("    showLabel: ").append(toIndentedString(showLabel)).append("\n");
    sb.append("    cssClass: ").append(toIndentedString(cssClass)).append("\n");
    sb.append("    cssLabelClass: ").append(toIndentedString(cssLabelClass)).append("\n");
    sb.append("    isPassword: ").append(toIndentedString(isPassword)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
    sb.append("    childrenComponents: ").append(toIndentedString(childrenComponents)).append("\n");
    sb.append("    selection: ").append(toIndentedString(selection)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    matrix: ").append(toIndentedString(matrix)).append("\n");
    sb.append("    hint: ").append(toIndentedString(hint)).append("\n");
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

