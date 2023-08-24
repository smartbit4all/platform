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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * Defines the desired layout for displaying a particular object and the various constraints to be enforced on it. 
 */
@ApiModel(description = "Defines the desired layout for displaying a particular object and the various constraints to be enforced on it. ")
@JsonPropertyOrder({
  ObjectLayoutDescriptor.URI,
  ObjectLayoutDescriptor.NAME,
  ObjectLayoutDescriptor.LAYOUTS,
  ObjectLayoutDescriptor.CONSTRAINTS
})
@JsonTypeName("ObjectLayoutDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectLayoutDescriptor {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String LAYOUTS = "layouts";
  private Map<String, SmartComponentLayoutDefinition> layouts = null;

  public static final String CONSTRAINTS = "constraints";
  private List<ObjectConstraintDescriptor> constraints = null;

  public ObjectLayoutDescriptor() { 
  }

  public ObjectLayoutDescriptor uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getUri() {
    return uri;
  }


  @JsonProperty(URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUri(URI uri) {
    this.uri = uri;
  }


  public ObjectLayoutDescriptor name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * A unique string name for the layout descriptor to be used as a key in mappings. 
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A unique string name for the layout descriptor to be used as a key in mappings. ")
  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getName() {
    return name;
  }


  @JsonProperty(NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setName(String name) {
    this.name = name;
  }


  public ObjectLayoutDescriptor layouts(Map<String, SmartComponentLayoutDefinition> layouts) {
    
    this.layouts = layouts;
    return this;
  }

  public ObjectLayoutDescriptor putLayoutsItem(String key, SmartComponentLayoutDefinition layoutsItem) {
    if (this.layouts == null) {
      this.layouts = new HashMap<>();
    }
    this.layouts.put(key, layoutsItem);
    return this;
  }

   /**
   * The layout definition for the object extension. The layouts are identified by their logical placeholder name that helps to identify the position of the given layout on the view. 
   * @return layouts
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The layout definition for the object extension. The layouts are identified by their logical placeholder name that helps to identify the position of the given layout on the view. ")
  @JsonProperty(LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, SmartComponentLayoutDefinition> getLayouts() {
    return layouts;
  }


  @JsonProperty(LAYOUTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLayouts(Map<String, SmartComponentLayoutDefinition> layouts) {
    this.layouts = layouts;
  }


  public ObjectLayoutDescriptor constraints(List<ObjectConstraintDescriptor> constraints) {
    
    this.constraints = constraints;
    return this;
  }

  public ObjectLayoutDescriptor addConstraintsItem(ObjectConstraintDescriptor constraintsItem) {
    if (this.constraints == null) {
      this.constraints = new ArrayList<>();
    }
    this.constraints.add(constraintsItem);
    return this;
  }

   /**
   * The constraints to be enforced for the object&#39;s layout. The condition for the application of a certain constraint descriptor can be customised by providing invocation request definitions to be used as predicates. 
   * @return constraints
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The constraints to be enforced for the object's layout. The condition for the application of a certain constraint descriptor can be customised by providing invocation request definitions to be used as predicates. ")
  @JsonProperty(CONSTRAINTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<ObjectConstraintDescriptor> getConstraints() {
    return constraints;
  }


  @JsonProperty(CONSTRAINTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setConstraints(List<ObjectConstraintDescriptor> constraints) {
    this.constraints = constraints;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectLayoutDescriptor objectLayoutDescriptor = (ObjectLayoutDescriptor) o;
    return Objects.equals(this.uri, objectLayoutDescriptor.uri) &&
        Objects.equals(this.name, objectLayoutDescriptor.name) &&
        Objects.equals(this.layouts, objectLayoutDescriptor.layouts) &&
        Objects.equals(this.constraints, objectLayoutDescriptor.constraints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, layouts, constraints);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectLayoutDescriptor {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    layouts: ").append(toIndentedString(layouts)).append("\n");
    sb.append("    constraints: ").append(toIndentedString(constraints)).append("\n");
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

