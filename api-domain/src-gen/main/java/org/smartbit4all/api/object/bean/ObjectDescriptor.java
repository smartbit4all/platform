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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The extension descriptor defines the data layout of an object. The properties can be any DocumentProperty available as built in or extension properties. These properties are organized into layouts. The layouts are named to be able to identify the placeholder on the page. The extension contains the constraint and rule definitions for the given object let it be document or folder. 
 */
@ApiModel(description = "The extension descriptor defines the data layout of an object. The properties can be any DocumentProperty available as built in or extension properties. These properties are organized into layouts. The layouts are named to be able to identify the placeholder on the page. The extension contains the constraint and rule definitions for the given object let it be document or folder. ")
@JsonPropertyOrder({
  ObjectDescriptor.URI,
  ObjectDescriptor.NAME,
  ObjectDescriptor.DEFINITION_PROPERTIES,
  ObjectDescriptor.EXTENSION_PROPERTIES,
  ObjectDescriptor.LAYOUT_DESCRIPTOR,
  ObjectDescriptor.OBJECT_DEFINITION
})
@JsonTypeName("ObjectDescriptor")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectDescriptor {
  public static final String URI = "uri";
  private URI uri;

  public static final String NAME = "name";
  private String name;

  public static final String DEFINITION_PROPERTIES = "definitionProperties";
  private Map<String, URI> definitionProperties = new HashMap<>();

  public static final String EXTENSION_PROPERTIES = "extensionProperties";
  private Map<String, URI> extensionProperties = new HashMap<>();

  public static final String LAYOUT_DESCRIPTOR = "layoutDescriptor";
  private URI layoutDescriptor;

  public static final String OBJECT_DEFINITION = "objectDefinition";
  private URI objectDefinition;

  public ObjectDescriptor() { 
  }

  public ObjectDescriptor uri(URI uri) {
    
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


  public ObjectDescriptor name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * The extension name is a unique and unmodifiable identifier of the given extension. It is used to construct dynamic object definitions so it must match with the requirements of the an object name (ANSI characters with no white spaces) 
   * @return name
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The extension name is a unique and unmodifiable identifier of the given extension. It is used to construct dynamic object definitions so it must match with the requirements of the an object name (ANSI characters with no white spaces) ")
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


  public ObjectDescriptor definitionProperties(Map<String, URI> definitionProperties) {
    
    this.definitionProperties = definitionProperties;
    return this;
  }

  public ObjectDescriptor putDefinitionPropertiesItem(String key, URI definitionPropertiesItem) {
    this.definitionProperties.put(key, definitionPropertiesItem);
    return this;
  }

   /**
   * The properties extracted from the relevant ObjectDefinition. 
   * @return definitionProperties
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The properties extracted from the relevant ObjectDefinition. ")
  @JsonProperty(DEFINITION_PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, URI> getDefinitionProperties() {
    return definitionProperties;
  }


  @JsonProperty(DEFINITION_PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setDefinitionProperties(Map<String, URI> definitionProperties) {
    this.definitionProperties = definitionProperties;
  }


  public ObjectDescriptor extensionProperties(Map<String, URI> extensionProperties) {
    
    this.extensionProperties = extensionProperties;
    return this;
  }

  public ObjectDescriptor putExtensionPropertiesItem(String key, URI extensionPropertiesItem) {
    this.extensionProperties.put(key, extensionPropertiesItem);
    return this;
  }

   /**
   * The properties configured by extending the base ObjectDefinition 
   * @return extensionProperties
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The properties configured by extending the base ObjectDefinition ")
  @JsonProperty(EXTENSION_PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, URI> getExtensionProperties() {
    return extensionProperties;
  }


  @JsonProperty(EXTENSION_PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setExtensionProperties(Map<String, URI> extensionProperties) {
    this.extensionProperties = extensionProperties;
  }


  public ObjectDescriptor layoutDescriptor(URI layoutDescriptor) {
    
    this.layoutDescriptor = layoutDescriptor;
    return this;
  }

   /**
   * Get layoutDescriptor
   * @return layoutDescriptor
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(LAYOUT_DESCRIPTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getLayoutDescriptor() {
    return layoutDescriptor;
  }


  @JsonProperty(LAYOUT_DESCRIPTOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setLayoutDescriptor(URI layoutDescriptor) {
    this.layoutDescriptor = layoutDescriptor;
  }


  public ObjectDescriptor objectDefinition(URI objectDefinition) {
    
    this.objectDefinition = objectDefinition;
    return this;
  }

   /**
   * Get objectDefinition
   * @return objectDefinition
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OBJECT_DEFINITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getObjectDefinition() {
    return objectDefinition;
  }


  @JsonProperty(OBJECT_DEFINITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setObjectDefinition(URI objectDefinition) {
    this.objectDefinition = objectDefinition;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectDescriptor objectDescriptor = (ObjectDescriptor) o;
    return Objects.equals(this.uri, objectDescriptor.uri) &&
        Objects.equals(this.name, objectDescriptor.name) &&
        Objects.equals(this.definitionProperties, objectDescriptor.definitionProperties) &&
        Objects.equals(this.extensionProperties, objectDescriptor.extensionProperties) &&
        Objects.equals(this.layoutDescriptor, objectDescriptor.layoutDescriptor) &&
        Objects.equals(this.objectDefinition, objectDescriptor.objectDefinition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, name, definitionProperties, extensionProperties, layoutDescriptor, objectDefinition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectDescriptor {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    definitionProperties: ").append(toIndentedString(definitionProperties)).append("\n");
    sb.append("    extensionProperties: ").append(toIndentedString(extensionProperties)).append("\n");
    sb.append("    layoutDescriptor: ").append(toIndentedString(layoutDescriptor)).append("\n");
    sb.append("    objectDefinition: ").append(toIndentedString(objectDefinition)).append("\n");
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

