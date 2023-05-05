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
 * The context object of a property value resolution with the name of the object in the given context and the uri of this. 
 */
@ApiModel(description = "The context object of a property value resolution with the name of the object in the given context and the uri of this. ")
@JsonPropertyOrder({
  ObjectPropertyResolverContextObject.NAME,
  ObjectPropertyResolverContextObject.URI
})
@JsonTypeName("ObjectPropertyResolverContextObject")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectPropertyResolverContextObject {
  public static final String NAME = "name";
  private String name;

  public static final String URI = "uri";
  private URI uri;

  public ObjectPropertyResolverContextObject() { 
  }

  public ObjectPropertyResolverContextObject name(String name) {
    
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
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


  public ObjectPropertyResolverContextObject uri(URI uri) {
    
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectPropertyResolverContextObject objectPropertyResolverContextObject = (ObjectPropertyResolverContextObject) o;
    return Objects.equals(this.name, objectPropertyResolverContextObject.name) &&
        Objects.equals(this.uri, objectPropertyResolverContextObject.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, uri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectPropertyResolverContextObject {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
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
