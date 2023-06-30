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
import java.util.List;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The object definition can be defined by some API definition language like OpenApi or so. On the backend the result is java class that describes all the properties and contained object definitions also. 
 */
@ApiModel(description = "The object definition can be defined by some API definition language like OpenApi or so. On the backend the result is java class that describes all the properties and contained object definitions also. ")
@JsonPropertyOrder({
  ObjectDefinitionData.URI,
  ObjectDefinitionData.QUALIFIED_NAME,
  ObjectDefinitionData.URI_PROPERTY,
  ObjectDefinitionData.KEY_PROPERTY,
  ObjectDefinitionData.PROPERTIES,
  ObjectDefinitionData.OUTGOING_REFERENCES
})
@JsonTypeName("ObjectDefinitionData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectDefinitionData {
  public static final String URI = "uri";
  private URI uri;

  public static final String QUALIFIED_NAME = "qualifiedName";
  private String qualifiedName;

  public static final String URI_PROPERTY = "uriProperty";
  private String uriProperty;

  public static final String KEY_PROPERTY = "keyProperty";
  private String keyProperty;

  public static final String PROPERTIES = "properties";
  private List<PropertyDefinitionData> properties = new ArrayList<>();

  public static final String OUTGOING_REFERENCES = "outgoingReferences";
  private List<ReferenceDefinitionData> outgoingReferences = new ArrayList<>();

  public ObjectDefinitionData() { 
  }

  public ObjectDefinitionData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The storage identifier of the given definition. It is calculated by the qualified name of the object. For example  object:/com/smartbit4all/mydomain/model/MyObject could be a calculated URI for a given reference. The object definition 
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The storage identifier of the given definition. It is calculated by the qualified name of the object. For example  object:/com/smartbit4all/mydomain/model/MyObject could be a calculated URI for a given reference. The object definition ")
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


  public ObjectDefinitionData qualifiedName(String qualifiedName) {
    
    this.qualifiedName = qualifiedName;
    return this;
  }

   /**
   * Get qualifiedName
   * @return qualifiedName
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getQualifiedName() {
    return qualifiedName;
  }


  @JsonProperty(QUALIFIED_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setQualifiedName(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }


  public ObjectDefinitionData uriProperty(String uriProperty) {
    
    this.uriProperty = uriProperty;
    return this;
  }

   /**
   * The name of the uri property that is the referential resource identifier for the object instances. Normally it is the uri but if the object does not have uri (identity in the storage level) then it can be the unique identifier or code also.  
   * @return uriProperty
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the uri property that is the referential resource identifier for the object instances. Normally it is the uri but if the object does not have uri (identity in the storage level) then it can be the unique identifier or code also.  ")
  @JsonProperty(URI_PROPERTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getUriProperty() {
    return uriProperty;
  }


  @JsonProperty(URI_PROPERTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setUriProperty(String uriProperty) {
    this.uriProperty = uriProperty;
  }


  public ObjectDefinitionData keyProperty(String keyProperty) {
    
    this.keyProperty = keyProperty;
    return this;
  }

   /**
   * The name of the business identifier property that identifies the object instances. Normally it is the uri but if the object does not have uri (identity in the storage level) then it can be the unique identifier or code also.  
   * @return keyProperty
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the business identifier property that identifies the object instances. Normally it is the uri but if the object does not have uri (identity in the storage level) then it can be the unique identifier or code also.  ")
  @JsonProperty(KEY_PROPERTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getKeyProperty() {
    return keyProperty;
  }


  @JsonProperty(KEY_PROPERTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKeyProperty(String keyProperty) {
    this.keyProperty = keyProperty;
  }


  public ObjectDefinitionData properties(List<PropertyDefinitionData> properties) {
    
    this.properties = properties;
    return this;
  }

  public ObjectDefinitionData addPropertiesItem(PropertyDefinitionData propertiesItem) {
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

  public List<PropertyDefinitionData> getProperties() {
    return properties;
  }


  @JsonProperty(PROPERTIES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setProperties(List<PropertyDefinitionData> properties) {
    this.properties = properties;
  }


  public ObjectDefinitionData outgoingReferences(List<ReferenceDefinitionData> outgoingReferences) {
    
    this.outgoingReferences = outgoingReferences;
    return this;
  }

  public ObjectDefinitionData addOutgoingReferencesItem(ReferenceDefinitionData outgoingReferencesItem) {
    this.outgoingReferences.add(outgoingReferencesItem);
    return this;
  }

   /**
   * Get outgoingReferences
   * @return outgoingReferences
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(OUTGOING_REFERENCES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<ReferenceDefinitionData> getOutgoingReferences() {
    return outgoingReferences;
  }


  @JsonProperty(OUTGOING_REFERENCES)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setOutgoingReferences(List<ReferenceDefinitionData> outgoingReferences) {
    this.outgoingReferences = outgoingReferences;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectDefinitionData objectDefinitionData = (ObjectDefinitionData) o;
    return Objects.equals(this.uri, objectDefinitionData.uri) &&
        Objects.equals(this.qualifiedName, objectDefinitionData.qualifiedName) &&
        Objects.equals(this.uriProperty, objectDefinitionData.uriProperty) &&
        Objects.equals(this.keyProperty, objectDefinitionData.keyProperty) &&
        Objects.equals(this.properties, objectDefinitionData.properties) &&
        Objects.equals(this.outgoingReferences, objectDefinitionData.outgoingReferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, qualifiedName, uriProperty, keyProperty, properties, outgoingReferences);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectDefinitionData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append("\n");
    sb.append("    uriProperty: ").append(toIndentedString(uriProperty)).append("\n");
    sb.append("    keyProperty: ").append(toIndentedString(keyProperty)).append("\n");
    sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
    sb.append("    outgoingReferences: ").append(toIndentedString(outgoingReferences)).append("\n");
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

