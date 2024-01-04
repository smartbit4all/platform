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
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * This is a mapping definition that describes the mapping between two domain object. 
 */
@ApiModel(description = "This is a mapping definition that describes the mapping between two domain object. ")
@JsonPropertyOrder({
  ObjectPropertySet.TYPE_QUALIFIED_NAME,
  ObjectPropertySet.PROPERTIES
})
@JsonTypeName("ObjectPropertySet")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectPropertySet {
  public static final String TYPE_QUALIFIED_NAME = "typeQualifiedName";
  private String typeQualifiedName;

  public static final String PROPERTIES = "properties";
  private List<List<String>> properties = new ArrayList<>();

  public ObjectPropertySet() { 
  }

  public ObjectPropertySet typeQualifiedName(String typeQualifiedName) {
    
    this.typeQualifiedName = typeQualifiedName;
    return this;
  }

   /**
   * Get typeQualifiedName
   * @return typeQualifiedName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(TYPE_QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTypeQualifiedName() {
    return typeQualifiedName;
  }


  @JsonProperty(TYPE_QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTypeQualifiedName(String typeQualifiedName) {
    this.typeQualifiedName = typeQualifiedName;
  }


  public ObjectPropertySet properties(List<List<String>> properties) {
    
    this.properties = properties;
    return this;
  }

  public ObjectPropertySet addPropertiesItem(List<String> propertiesItem) {
    this.properties.add(propertiesItem);
    return this;
  }

   /**
   * Get properties
   * @return properties
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<List<String>> getProperties() {
    return properties;
  }


  @JsonProperty(PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setProperties(List<List<String>> properties) {
    this.properties = properties;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectPropertySet objectPropertySet = (ObjectPropertySet) o;
    return Objects.equals(this.typeQualifiedName, objectPropertySet.typeQualifiedName) &&
        Objects.equals(this.properties, objectPropertySet.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeQualifiedName, properties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectPropertySet {\n");
    sb.append("    typeQualifiedName: ").append(toIndentedString(typeQualifiedName)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
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

