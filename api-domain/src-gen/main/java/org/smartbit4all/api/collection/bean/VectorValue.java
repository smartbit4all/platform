/*
 * collection api
 * collection api for the stored colections.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.collection.bean;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The vector value is the result of an embedding operation of any kind. It can be the result of a text or a picture  or whatever else. It is normally constructed by a Converter that creates the result from the input object. 
 */
@ApiModel(description = "The vector value is the result of an embedding operation of any kind. It can be the result of a text or a picture  or whatever else. It is normally constructed by a Converter that creates the result from the input object. ")
@JsonPropertyOrder({
  VectorValue.ID_PATH,
  VectorValue.INPUT_TYPE,
  VectorValue.INPUT_OBJECT,
  VectorValue.CONVERSION,
  VectorValue.VECTOR
})
@JsonTypeName("VectorValue")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class VectorValue {
  public static final String ID_PATH = "idPath";
  private List<String> idPath = null;

  public static final String INPUT_TYPE = "inputType";
  private String inputType;

  public static final String INPUT_OBJECT = "inputObject";
  private Map<String, Object> inputObject = new HashMap<>();

  public static final String CONVERSION = "conversion";
  private String conversion;

  public static final String VECTOR = "vector";
  private List<Float> vector = new ArrayList<>();

  public VectorValue() { 
  }

  public VectorValue idPath(List<String> idPath) {
    
    this.idPath = idPath;
    return this;
  }

  public VectorValue addIdPathItem(String idPathItem) {
    if (this.idPath == null) {
      this.idPath = new ArrayList<>();
    }
    this.idPath.add(idPathItem);
    return this;
  }

   /**
   * A unique identifier of the VectorValue.
   * @return idPath
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A unique identifier of the VectorValue.")
  @JsonProperty(ID_PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getIdPath() {
    return idPath;
  }


  @JsonProperty(ID_PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIdPath(List<String> idPath) {
    this.idPath = idPath;
  }


  public VectorValue inputType(String inputType) {
    
    this.inputType = inputType;
    return this;
  }

   /**
   * The type of the input object.
   * @return inputType
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The type of the input object.")
  @JsonProperty(INPUT_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getInputType() {
    return inputType;
  }


  @JsonProperty(INPUT_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setInputType(String inputType) {
    this.inputType = inputType;
  }


  public VectorValue inputObject(Map<String, Object> inputObject) {
    
    this.inputObject = inputObject;
    return this;
  }

  public VectorValue putInputObjectItem(String key, Object inputObjectItem) {
    this.inputObject.put(key, inputObjectItem);
    return this;
  }

   /**
   * The input object itself.
   * @return inputObject
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "The input object itself.")
  @JsonProperty(INPUT_OBJECT)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.ALWAYS)

  public Map<String, Object> getInputObject() {
    return inputObject;
  }


  @JsonProperty(INPUT_OBJECT)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.ALWAYS)
  public void setInputObject(Map<String, Object> inputObject) {
    this.inputObject = inputObject;
  }


  public VectorValue conversion(String conversion) {
    
    this.conversion = conversion;
    return this;
  }

   /**
   * The name fo the conversion algorithm like ada2 in case of string.
   * @return conversion
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name fo the conversion algorithm like ada2 in case of string.")
  @JsonProperty(CONVERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getConversion() {
    return conversion;
  }


  @JsonProperty(CONVERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setConversion(String conversion) {
    this.conversion = conversion;
  }


  public VectorValue vector(List<Float> vector) {
    
    this.vector = vector;
    return this;
  }

  public VectorValue addVectorItem(Float vectorItem) {
    this.vector.add(vectorItem);
    return this;
  }

   /**
   * Get vector
   * @return vector
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(VECTOR)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<Float> getVector() {
    return vector;
  }


  @JsonProperty(VECTOR)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setVector(List<Float> vector) {
    this.vector = vector;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VectorValue vectorValue = (VectorValue) o;
    return Objects.equals(this.idPath, vectorValue.idPath) &&
        Objects.equals(this.inputType, vectorValue.inputType) &&
        Objects.equals(this.inputObject, vectorValue.inputObject) &&
        Objects.equals(this.conversion, vectorValue.conversion) &&
        Objects.equals(this.vector, vectorValue.vector);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idPath, inputType, inputObject, conversion, vector);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VectorValue {\n");
    sb.append("    idPath: ").append(toIndentedString(idPath)).append("\n");
    sb.append("    inputType: ").append(toIndentedString(inputType)).append("\n");
    sb.append("    inputObject: ").append(toIndentedString(inputObject)).append("\n");
    sb.append("    conversion: ").append(toIndentedString(conversion)).append("\n");
    sb.append("    vector: ").append(toIndentedString(vector)).append("\n");
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

