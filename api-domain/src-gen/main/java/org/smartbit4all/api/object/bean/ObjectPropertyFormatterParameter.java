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
 * This setting information to resolve a necessary parameters for the message format. 
 */
@ApiModel(description = "This setting information to resolve a necessary parameters for the message format. ")
@JsonPropertyOrder({
  ObjectPropertyFormatterParameter.PROPERTY_URI
})
@JsonTypeName("ObjectPropertyFormatterParameter")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ObjectPropertyFormatterParameter {
  public static final String PROPERTY_URI = "propertyUri";
  private URI propertyUri;

  public ObjectPropertyFormatterParameter() { 
  }

  public ObjectPropertyFormatterParameter propertyUri(URI propertyUri) {
    
    this.propertyUri = propertyUri;
    return this;
  }

   /**
   * The uri of the property in the resolver context. By default the object itself is named as object. 
   * @return propertyUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri of the property in the resolver context. By default the object itself is named as object. ")
  @JsonProperty(PROPERTY_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getPropertyUri() {
    return propertyUri;
  }


  @JsonProperty(PROPERTY_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPropertyUri(URI propertyUri) {
    this.propertyUri = propertyUri;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectPropertyFormatterParameter objectPropertyFormatterParameter = (ObjectPropertyFormatterParameter) o;
    return Objects.equals(this.propertyUri, objectPropertyFormatterParameter.propertyUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ObjectPropertyFormatterParameter {\n");
    sb.append("    propertyUri: ").append(toIndentedString(propertyUri)).append("\n");
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
