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
import org.smartbit4all.api.object.bean.ObjectReferenceDefinitionData;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The reference definition is a directed navigation between objects. Its name is unique inside the source object that can have only one outgoing reference with the same name. In the target object the source object and the name is unique together but the name itself is not enough to identify an incoming reference. 
 */
@ApiModel(description = "The reference definition is a directed navigation between objects. Its name is unique inside the source object that can have only one outgoing reference with the same name. In the target object the source object and the name is unique together but the name itself is not enough to identify an incoming reference. ")
@JsonPropertyOrder({
  ReferenceDefinitionData.URI,
  ReferenceDefinitionData.CONTAINMENT,
  ReferenceDefinitionData.BACK_REFERENCE,
  ReferenceDefinitionData.SOURCE,
  ReferenceDefinitionData.TARGET_OBJECT_NAME
})
@JsonTypeName("ReferenceDefinitionData")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class ReferenceDefinitionData {
  public static final String URI = "uri";
  private URI uri;

  public static final String CONTAINMENT = "containment";
  private Boolean containment;

  public static final String BACK_REFERENCE = "backReference";
  private URI backReference;

  public static final String SOURCE = "source";
  private ObjectReferenceDefinitionData source;

  public static final String TARGET_OBJECT_NAME = "targetObjectName";
  private String targetObjectName;

  public ReferenceDefinitionData() { 
  }

  public ReferenceDefinitionData uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * The storage identifier of the given definition. It is calculated by the qualified name of the object and the name name of the reference. For example object:/com/smartbit4all/mydomain/model/MyObject/firstReference could be a calculated URI for a given reference. By deafult it could be a good idea to name the reference by the name of the property that contains the reference value. 
   * @return uri
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "The storage identifier of the given definition. It is calculated by the qualified name of the object and the name name of the reference. For example object:/com/smartbit4all/mydomain/model/MyObject/firstReference could be a calculated URI for a given reference. By deafult it could be a good idea to name the reference by the name of the property that contains the reference value. ")
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


  public ReferenceDefinitionData containment(Boolean containment) {
    
    this.containment = containment;
    return this;
  }

   /**
   * The containment means that the source contains the target object. The target object is accessible only from the source. 
   * @return containment
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The containment means that the source contains the target object. The target object is accessible only from the source. ")
  @JsonProperty(CONTAINMENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getContainment() {
    return containment;
  }


  @JsonProperty(CONTAINMENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setContainment(Boolean containment) {
    this.containment = containment;
  }


  public ReferenceDefinitionData backReference(URI backReference) {
    
    this.backReference = backReference;
    return this;
  }

   /**
   * If an association between two object is navigable into both direction then this uri refers to the opposite reference.
   * @return backReference
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "If an association between two object is navigable into both direction then this uri refers to the opposite reference.")
  @JsonProperty(BACK_REFERENCE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getBackReference() {
    return backReference;
  }


  @JsonProperty(BACK_REFERENCE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setBackReference(URI backReference) {
    this.backReference = backReference;
  }


  public ReferenceDefinitionData source(ObjectReferenceDefinitionData source) {
    
    this.source = source;
    return this;
  }

   /**
   * Get source
   * @return source
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(SOURCE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ObjectReferenceDefinitionData getSource() {
    return source;
  }


  @JsonProperty(SOURCE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSource(ObjectReferenceDefinitionData source) {
    this.source = source;
  }


  public ReferenceDefinitionData targetObjectName(String targetObjectName) {
    
    this.targetObjectName = targetObjectName;
    return this;
  }

   /**
   * The name of the target object.
   * @return targetObjectName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the target object.")
  @JsonProperty(TARGET_OBJECT_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTargetObjectName() {
    return targetObjectName;
  }


  @JsonProperty(TARGET_OBJECT_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTargetObjectName(String targetObjectName) {
    this.targetObjectName = targetObjectName;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferenceDefinitionData referenceDefinitionData = (ReferenceDefinitionData) o;
    return Objects.equals(this.uri, referenceDefinitionData.uri) &&
        Objects.equals(this.containment, referenceDefinitionData.containment) &&
        Objects.equals(this.backReference, referenceDefinitionData.backReference) &&
        Objects.equals(this.source, referenceDefinitionData.source) &&
        Objects.equals(this.targetObjectName, referenceDefinitionData.targetObjectName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, containment, backReference, source, targetObjectName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferenceDefinitionData {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    containment: ").append(toIndentedString(containment)).append("\n");
    sb.append("    backReference: ").append(toIndentedString(backReference)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    targetObjectName: ").append(toIndentedString(targetObjectName)).append("\n");
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

