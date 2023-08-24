/*
 * Object api
 * The object api responsible for the domain object meta information including the object definitions and the relations among them. These objects are stored because the modules can contribute. The modules have their own ObjectApi that manages the storage and ensure the up-to-date view of the current data. The algorithms are running on the ObjectApi cache refreshed periodically. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.object.bean;

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
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The object property is the descriptor of a property available for a management. 
 */
@ApiModel(description = "The object property is the descriptor of a property available for a management. ")
@JsonPropertyOrder({
  ObjectPropertyDescriptor.URI,
  ObjectPropertyDescriptor.PROPERTY_NAME,
  ObjectPropertyDescriptor.PROPERTY_KIND,
  ObjectPropertyDescriptor.PROPERTY_QUALIFIED_NAME,
  ObjectPropertyDescriptor.REFERENCED_TYPE_QUALIFIED_NAME,
  ObjectPropertyDescriptor.AGGREGATION,
  ObjectPropertyDescriptor.PROPERTY_STRUCTURE,
  ObjectPropertyDescriptor.VALUE_SET,
  ObjectPropertyDescriptor.VALUE_SET_NAME,
  ObjectPropertyDescriptor.WIDGET,
  ObjectPropertyDescriptor.DEFAULT_VALUE,
  ObjectPropertyDescriptor.DEFAULT_CONSTRAINT,
  ObjectPropertyDescriptor.BUILT_IN
})
@JsonTypeName("ObjectPropertyDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectPropertyDescriptor {
  public static final String URI = "uri";
  private URI uri;

  public static final String PROPERTY_NAME = "propertyName";
  private String propertyName;

  /**
   * Denotes whether this property is a navigable storage reference or an inline value on the host object. 
   */
  public enum PropertyKindEnum {
    INLINE("INLINE"),
    
    REFERENCE("REFERENCE");

    private String value;

    PropertyKindEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PropertyKindEnum fromValue(String value) {
      for (PropertyKindEnum b : PropertyKindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String PROPERTY_KIND = "propertyKind";
  private PropertyKindEnum propertyKind;

  public static final String PROPERTY_QUALIFIED_NAME = "propertyQualifiedName";
  private String propertyQualifiedName;

  public static final String REFERENCED_TYPE_QUALIFIED_NAME = "referencedTypeQualifiedName";
  private String referencedTypeQualifiedName;

  public static final String AGGREGATION = "aggregation";
  private AggregationKind aggregation = AggregationKind.NONE;

  public static final String PROPERTY_STRUCTURE = "propertyStructure";
  private ReferencePropertyKind propertyStructure;

  public static final String VALUE_SET = "valueSet";
  private URI valueSet;

  public static final String VALUE_SET_NAME = "valueSetName";
  private String valueSetName;

  public static final String WIDGET = "widget";
  private SmartWidgetDefinition widget = null;

  public static final String DEFAULT_VALUE = "defaultValue";
  private Object defaultValue;

  public static final String DEFAULT_CONSTRAINT = "defaultConstraint";
  private ObjectConstraintDescriptor defaultConstraint;

  public static final String BUILT_IN = "builtIn";
  private Boolean builtIn = false;

  public ObjectPropertyDescriptor() { 
  }

  public ObjectPropertyDescriptor uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public ObjectPropertyDescriptor propertyName(String propertyName) {
    
    this.propertyName = propertyName;
    return this;
  }

   /**
   * The string name of the property to be used to access it on its host object. 
   * @return propertyName
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The string name of the property to be used to access it on its host object. ")
  @JsonProperty(PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getPropertyName() {
    return propertyName;
  }


  @JsonProperty(PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }


  public ObjectPropertyDescriptor propertyKind(PropertyKindEnum propertyKind) {
    
    this.propertyKind = propertyKind;
    return this;
  }

   /**
   * Denotes whether this property is a navigable storage reference or an inline value on the host object. 
   * @return propertyKind
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "Denotes whether this property is a navigable storage reference or an inline value on the host object. ")
  @JsonProperty(PROPERTY_KIND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public PropertyKindEnum getPropertyKind() {
    return propertyKind;
  }


  @JsonProperty(PROPERTY_KIND)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPropertyKind(PropertyKindEnum propertyKind) {
    this.propertyKind = propertyKind;
  }


  public ObjectPropertyDescriptor propertyQualifiedName(String propertyQualifiedName) {
    
    this.propertyQualifiedName = propertyQualifiedName;
    return this;
  }

   /**
   * The class of the object denoted by this property. This is the actual type used, thus for referential properties this value is likely \&quot;java.net.URI\&quot;. 
   * @return propertyQualifiedName
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The class of the object denoted by this property. This is the actual type used, thus for referential properties this value is likely \"java.net.URI\". ")
  @JsonProperty(PROPERTY_QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getPropertyQualifiedName() {
    return propertyQualifiedName;
  }


  @JsonProperty(PROPERTY_QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPropertyQualifiedName(String propertyQualifiedName) {
    this.propertyQualifiedName = propertyQualifiedName;
  }


  public ObjectPropertyDescriptor referencedTypeQualifiedName(String referencedTypeQualifiedName) {
    
    this.referencedTypeQualifiedName = referencedTypeQualifiedName;
    return this;
  }

   /**
   * The class of the object referenced by the property. This shall have a value only, if this property is a REFERENCE property kind 
   * @return referencedTypeQualifiedName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The class of the object referenced by the property. This shall have a value only, if this property is a REFERENCE property kind ")
  @JsonProperty(REFERENCED_TYPE_QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getReferencedTypeQualifiedName() {
    return referencedTypeQualifiedName;
  }


  @JsonProperty(REFERENCED_TYPE_QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReferencedTypeQualifiedName(String referencedTypeQualifiedName) {
    this.referencedTypeQualifiedName = referencedTypeQualifiedName;
  }


  public ObjectPropertyDescriptor aggregation(AggregationKind aggregation) {
    
    this.aggregation = aggregation;
    return this;
  }

   /**
   * Get aggregation
   * @return aggregation
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(AGGREGATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public AggregationKind getAggregation() {
    return aggregation;
  }


  @JsonProperty(AGGREGATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAggregation(AggregationKind aggregation) {
    this.aggregation = aggregation;
  }


  public ObjectPropertyDescriptor propertyStructure(ReferencePropertyKind propertyStructure) {
    
    this.propertyStructure = propertyStructure;
    return this;
  }

   /**
   * Get propertyStructure
   * @return propertyStructure
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(PROPERTY_STRUCTURE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public ReferencePropertyKind getPropertyStructure() {
    return propertyStructure;
  }


  @JsonProperty(PROPERTY_STRUCTURE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPropertyStructure(ReferencePropertyKind propertyStructure) {
    this.propertyStructure = propertyStructure;
  }


  public ObjectPropertyDescriptor valueSet(URI valueSet) {
    
    this.valueSet = valueSet;
    return this;
  }

   /**
   * The ValueSet containing the valid objects eligible for this property (if any). The value set shall contain objects with the applicable qualified name above. If this property may have any value (such as free-form text), this value set is not interpreted. 
   * @return valueSet
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The ValueSet containing the valid objects eligible for this property (if any). The value set shall contain objects with the applicable qualified name above. If this property may have any value (such as free-form text), this value set is not interpreted. ")
  @JsonProperty(VALUE_SET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getValueSet() {
    return valueSet;
  }


  @JsonProperty(VALUE_SET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValueSet(URI valueSet) {
    this.valueSet = valueSet;
  }


  public ObjectPropertyDescriptor valueSetName(String valueSetName) {
    
    this.valueSetName = valueSetName;
    return this;
  }

   /**
   * ... 
   * @return valueSetName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "... ")
  @JsonProperty(VALUE_SET_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getValueSetName() {
    return valueSetName;
  }


  @JsonProperty(VALUE_SET_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setValueSetName(String valueSetName) {
    this.valueSetName = valueSetName;
  }


  public ObjectPropertyDescriptor widget(SmartWidgetDefinition widget) {
    
    this.widget = widget;
    return this;
  }

   /**
   * The widget to use to render this property by default. If none are supplied during the construction of this instance, this property may be initialised using heuristics. 
   * @return widget
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The widget to use to render this property by default. If none are supplied during the construction of this instance, this property may be initialised using heuristics. ")
  @JsonProperty(WIDGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public SmartWidgetDefinition getWidget() {
    return widget;
  }


  @JsonProperty(WIDGET)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setWidget(SmartWidgetDefinition widget) {
    this.widget = widget;
  }


  public ObjectPropertyDescriptor defaultValue(Object defaultValue) {
    
    this.defaultValue = defaultValue;
    return this;
  }

   /**
   * The value this property should hold when an object owning this property is initialised. 
   * @return defaultValue
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The value this property should hold when an object owning this property is initialised. ")
  @JsonProperty(DEFAULT_VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getDefaultValue() {
    return defaultValue;
  }


  @JsonProperty(DEFAULT_VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
  }


  public ObjectPropertyDescriptor defaultConstraint(ObjectConstraintDescriptor defaultConstraint) {
    
    this.defaultConstraint = defaultConstraint;
    return this;
  }

   /**
   * Get defaultConstraint
   * @return defaultConstraint
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DEFAULT_CONSTRAINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ObjectConstraintDescriptor getDefaultConstraint() {
    return defaultConstraint;
  }


  @JsonProperty(DEFAULT_CONSTRAINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDefaultConstraint(ObjectConstraintDescriptor defaultConstraint) {
    this.defaultConstraint = defaultConstraint;
  }


  public ObjectPropertyDescriptor builtIn(Boolean builtIn) {
    
    this.builtIn = builtIn;
    return this;
  }

   /**
   * Marks whether this property shall be treated as a &#39;built-in&#39; quality of the host object. Clients are not allowed to remove a &#39;built-in&#39; property from an object descriptor. 
   * @return builtIn
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Marks whether this property shall be treated as a 'built-in' quality of the host object. Clients are not allowed to remove a 'built-in' property from an object descriptor. ")
  @JsonProperty(BUILT_IN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getBuiltIn() {
    return builtIn;
  }


  @JsonProperty(BUILT_IN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBuiltIn(Boolean builtIn) {
    this.builtIn = builtIn;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectPropertyDescriptor objectPropertyDescriptor = (ObjectPropertyDescriptor) o;
    return Objects.equals(this.uri, objectPropertyDescriptor.uri) &&
        Objects.equals(this.propertyName, objectPropertyDescriptor.propertyName) &&
        Objects.equals(this.propertyKind, objectPropertyDescriptor.propertyKind) &&
        Objects.equals(this.propertyQualifiedName, objectPropertyDescriptor.propertyQualifiedName) &&
        Objects.equals(this.referencedTypeQualifiedName, objectPropertyDescriptor.referencedTypeQualifiedName) &&
        Objects.equals(this.aggregation, objectPropertyDescriptor.aggregation) &&
        Objects.equals(this.propertyStructure, objectPropertyDescriptor.propertyStructure) &&
        Objects.equals(this.valueSet, objectPropertyDescriptor.valueSet) &&
        Objects.equals(this.valueSetName, objectPropertyDescriptor.valueSetName) &&
        Objects.equals(this.widget, objectPropertyDescriptor.widget) &&
        Objects.equals(this.defaultValue, objectPropertyDescriptor.defaultValue) &&
        Objects.equals(this.defaultConstraint, objectPropertyDescriptor.defaultConstraint) &&
        Objects.equals(this.builtIn, objectPropertyDescriptor.builtIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, propertyName, propertyKind, propertyQualifiedName, referencedTypeQualifiedName, aggregation, propertyStructure, valueSet, valueSetName, widget, defaultValue, defaultConstraint, builtIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectPropertyDescriptor {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    propertyName: ").append(toIndentedString(propertyName)).append("\n");
    sb.append("    propertyKind: ").append(toIndentedString(propertyKind)).append("\n");
    sb.append("    propertyQualifiedName: ").append(toIndentedString(propertyQualifiedName)).append("\n");
    sb.append("    referencedTypeQualifiedName: ").append(toIndentedString(referencedTypeQualifiedName)).append("\n");
    sb.append("    aggregation: ").append(toIndentedString(aggregation)).append("\n");
    sb.append("    propertyStructure: ").append(toIndentedString(propertyStructure)).append("\n");
    sb.append("    valueSet: ").append(toIndentedString(valueSet)).append("\n");
    sb.append("    valueSetName: ").append(toIndentedString(valueSetName)).append("\n");
    sb.append("    widget: ").append(toIndentedString(widget)).append("\n");
    sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append("\n");
    sb.append("    defaultConstraint: ").append(toIndentedString(defaultConstraint)).append("\n");
    sb.append("    builtIn: ").append(toIndentedString(builtIn)).append("\n");
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

