/*
 * Grid api
 * The grid api is resposible for the grid components that shows a list of item. 
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.grid.bean;

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
 * This is the backend object to setup the grid access api. The parameterization contain all the possible parameters requiered to identify the API to call or even the InvocationRequest to use. Register into the parameters of the View parameters as grid.identifier.config 
 */
@ApiModel(description = "This is the backend object to setup the grid access api. The parameterization contain all the possible parameters requiered to identify the API to call or even the InvocationRequest to use. Register into the parameters of the View parameters as grid.identifier.config ")
@JsonPropertyOrder({
  GridDataAccessConfig.DATA_URI,
  GridDataAccessConfig.PROPERTY_PATH,
  GridDataAccessConfig.API_CLASS,
  GridDataAccessConfig.KIND
})
@JsonTypeName("GridDataAccessConfig")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class GridDataAccessConfig {
  public static final String DATA_URI = "dataUri";
  private URI dataUri;

  public static final String PROPERTY_PATH = "propertyPath";
  private String propertyPath;

  public static final String API_CLASS = "apiClass";
  private String apiClass;

  /**
   * Gets or Sets kind
   */
  public enum KindEnum {
    TABLEDATA("TABLEDATA"),
    
    OBJECTLIST("OBJECTLIST"),
    
    APIBASED("APIBASED"),
    
    INVOCATIONS("INVOCATIONS");

    private String value;

    KindEnum(String value) {
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
    public static KindEnum fromValue(String value) {
      for (KindEnum b : KindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String KIND = "kind";
  private KindEnum kind;

  public GridDataAccessConfig() { 
  }

  public GridDataAccessConfig dataUri(URI dataUri) {
    
    this.dataUri = dataUri;
    return this;
  }

   /**
   * The uri of the object that contains the data of the list.
   * @return dataUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "The uri of the object that contains the data of the list.")
  @JsonProperty(DATA_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getDataUri() {
    return dataUri;
  }


  @JsonProperty(DATA_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDataUri(URI dataUri) {
    this.dataUri = dataUri;
  }


  public GridDataAccessConfig propertyPath(String propertyPath) {
    
    this.propertyPath = propertyPath;
    return this;
  }

   /**
   * The property path to access the data of the grid in the referred object.
   * @return propertyPath
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The property path to access the data of the grid in the referred object.")
  @JsonProperty(PROPERTY_PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPropertyPath() {
    return propertyPath;
  }


  @JsonProperty(PROPERTY_PATH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPropertyPath(String propertyPath) {
    this.propertyPath = propertyPath;
  }


  public GridDataAccessConfig apiClass(String apiClass) {
    
    this.apiClass = apiClass;
    return this;
  }

   /**
   * Get apiClass
   * @return apiClass
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(API_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getApiClass() {
    return apiClass;
  }


  @JsonProperty(API_CLASS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setApiClass(String apiClass) {
    this.apiClass = apiClass;
  }


  public GridDataAccessConfig kind(KindEnum kind) {
    
    this.kind = kind;
    return this;
  }

   /**
   * Get kind
   * @return kind
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public KindEnum getKind() {
    return kind;
  }


  @JsonProperty(KIND)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKind(KindEnum kind) {
    this.kind = kind;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridDataAccessConfig gridDataAccessConfig = (GridDataAccessConfig) o;
    return Objects.equals(this.dataUri, gridDataAccessConfig.dataUri) &&
        Objects.equals(this.propertyPath, gridDataAccessConfig.propertyPath) &&
        Objects.equals(this.apiClass, gridDataAccessConfig.apiClass) &&
        Objects.equals(this.kind, gridDataAccessConfig.kind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataUri, propertyPath, apiClass, kind);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GridDataAccessConfig {\n");
    sb.append("    dataUri: ").append(toIndentedString(dataUri)).append("\n");
    sb.append("    propertyPath: ").append(toIndentedString(propertyPath)).append("\n");
    sb.append("    apiClass: ").append(toIndentedString(apiClass)).append("\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
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
