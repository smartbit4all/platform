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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The property definition can be defined by some API definition language like OpenApi or so. On the backend the result is java class that describes all the properties and contained object definitions also. This definition is responsible for the properties. 
 */
@ApiModel(description = "The property definition can be defined by some API definition language like OpenApi or so. On the backend the result is java class that describes all the properties and contained object definitions also. This definition is responsible for the properties. ")
@JsonPropertyOrder({
  PropertyDefinitionData.URI,
  PropertyDefinitionData.NAME,
  PropertyDefinitionData.TYPE_CLASS
})
@JsonTypeName("PropertyDefinitionData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class PropertyDefinitionData {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String TYPE_CLASS = "typeClass";
  private String typeClass;

  public PropertyDefinitionData() { 
  }

  public PropertyDefinitionData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The storage identifier of the given definition. It is calculated by the qualified name of the object. For example  object:/com/smartbit4all/mydomain/model/MyObject/firstProperty could be a calculated URI for a given reference. 
   * @return uri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The storage identifier of the given definition. It is calculated by the qualified name of the object. For example  object:/com/smartbit4all/mydomain/model/MyObject/firstProperty could be a calculated URI for a given reference. ")
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


  public PropertyDefinitionData name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The property name.
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The property name.")
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


  public PropertyDefinitionData typeClass(String typeClass) {
    
    this.typeClass = typeClass;
    return this;
  }

   /**
   * The qualified name of the type class in java. Like java.lang.String If we need to convert this type to any other platform then we need a conversion for this. 
   * @return typeClass
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The qualified name of the type class in java. Like java.lang.String If we need to convert this type to any other platform then we need a conversion for this. ")
  @JsonProperty(TYPE_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTypeClass() {
    return typeClass;
  }


  @JsonProperty(TYPE_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTypeClass(String typeClass) {
    this.typeClass = typeClass;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyDefinitionData propertyDefinitionData = (PropertyDefinitionData) o;
    return Objects.equals(this.uri, propertyDefinitionData.uri) &&
        Objects.equals(this.name, propertyDefinitionData.name) &&
        Objects.equals(this.typeClass, propertyDefinitionData.typeClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, typeClass);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyDefinitionData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    typeClass: ").append(toIndentedString(typeClass)).append("\n");
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
