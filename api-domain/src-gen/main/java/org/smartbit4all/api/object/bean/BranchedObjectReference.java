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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The branched object reference is a reference to an object. It always has an identifier that is the uri if the object is saved on its own. In case of the contained objects it can be a property of the object and if it is a value as a value then the identifier is  the value itself. 
 */
@ApiModel(description = "The branched object reference is a reference to an object. It always has an identifier that is the uri if the object is saved on its own. In case of the contained objects it can be a property of the object and if it is a value as a value then the identifier is  the value itself. ")
@JsonPropertyOrder({
  BranchedObjectReference.IDENTIFIER,
  BranchedObjectReference.REFERENCE_TYPE,
  BranchedObjectReference.OBJECT_AS_VALUE
})
@JsonTypeName("BranchedObjectReference")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class BranchedObjectReference {
  public static final String IDENTIFIER = "identifier";
  private String identifier;

  /**
   * Gets or Sets referenceType
   */
  public enum ReferenceTypeEnum {
    URI_BASED("URI_BASED"),
    
    CONTAINED_OBJECT("CONTAINED_OBJECT"),
    
    CONTAINED_VALUE("CONTAINED_VALUE");

    private String value;

    ReferenceTypeEnum(String value) {
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
    public static ReferenceTypeEnum fromValue(String value) {
      for (ReferenceTypeEnum b : ReferenceTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String REFERENCE_TYPE = "referenceType";
  private ReferenceTypeEnum referenceType;

  public static final String OBJECT_AS_VALUE = "objectAsValue";
  private Object objectAsValue;

  public BranchedObjectReference() { 
  }

  public BranchedObjectReference identifier(String identifier) {
    
    this.identifier = identifier;
    return this;
  }

   /**
   * The stringified version of the identifier.
   * @return identifier
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The stringified version of the identifier.")
  @JsonProperty(IDENTIFIER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getIdentifier() {
    return identifier;
  }


  @JsonProperty(IDENTIFIER)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  public BranchedObjectReference referenceType(ReferenceTypeEnum referenceType) {
    
    this.referenceType = referenceType;
    return this;
  }

   /**
   * Get referenceType
   * @return referenceType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(REFERENCE_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ReferenceTypeEnum getReferenceType() {
    return referenceType;
  }


  @JsonProperty(REFERENCE_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReferenceType(ReferenceTypeEnum referenceType) {
    this.referenceType = referenceType;
  }


  public BranchedObjectReference objectAsValue(Object objectAsValue) {
    
    this.objectAsValue = objectAsValue;
    return this;
  }

   /**
   * If the reference is an object then we store the object as is in this value. It is necessary because the object is contained so it is the only way to access a deleted object for example. 
   * @return objectAsValue
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If the reference is an object then we store the object as is in this value. It is necessary because the object is contained so it is the only way to access a deleted object for example. ")
  @JsonProperty(OBJECT_AS_VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Object getObjectAsValue() {
    return objectAsValue;
  }


  @JsonProperty(OBJECT_AS_VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setObjectAsValue(Object objectAsValue) {
    this.objectAsValue = objectAsValue;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BranchedObjectReference branchedObjectReference = (BranchedObjectReference) o;
    return Objects.equals(this.identifier, branchedObjectReference.identifier) &&
        Objects.equals(this.referenceType, branchedObjectReference.referenceType) &&
        Objects.equals(this.objectAsValue, branchedObjectReference.objectAsValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, referenceType, objectAsValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BranchedObjectReference {\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    referenceType: ").append(toIndentedString(referenceType)).append("\n");
    sb.append("    objectAsValue: ").append(toIndentedString(objectAsValue)).append("\n");
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

