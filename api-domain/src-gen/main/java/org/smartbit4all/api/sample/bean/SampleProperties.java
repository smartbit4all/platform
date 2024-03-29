/*
 * The sample domain
 * Contains object for testing purposes.
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.sample.bean;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * An example object containing some named inline fields and an inline string-string map. 
 */
@ApiModel(description = "An example object containing some named inline fields and an inline string-string map. ")
@JsonPropertyOrder({
  SampleProperties.PRIMARY,
  SampleProperties.SECONDARY,
  SampleProperties.ETC
})
@JsonTypeName("SampleProperties")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class SampleProperties {
  public static final String PRIMARY = "primary";
  private String primary;

  public static final String SECONDARY = "secondary";
  private String secondary;

  public static final String ETC = "etc";
  private Map<String, String> etc = null;

  public SampleProperties() { 
  }

  public SampleProperties primary(String primary) {
    
    this.primary = primary;
    return this;
  }

   /**
   * Get primary
   * @return primary
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(PRIMARY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getPrimary() {
    return primary;
  }


  @JsonProperty(PRIMARY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPrimary(String primary) {
    this.primary = primary;
  }


  public SampleProperties secondary(String secondary) {
    
    this.secondary = secondary;
    return this;
  }

   /**
   * Get secondary
   * @return secondary
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(SECONDARY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSecondary() {
    return secondary;
  }


  @JsonProperty(SECONDARY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSecondary(String secondary) {
    this.secondary = secondary;
  }


  public SampleProperties etc(Map<String, String> etc) {
    
    this.etc = etc;
    return this;
  }

  public SampleProperties putEtcItem(String key, String etcItem) {
    if (this.etc == null) {
      this.etc = new HashMap<>();
    }
    this.etc.put(key, etcItem);
    return this;
  }

   /**
   * Get etc
   * @return etc
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(ETC)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, String> getEtc() {
    return etc;
  }


  @JsonProperty(ETC)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEtc(Map<String, String> etc) {
    this.etc = etc;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SampleProperties sampleProperties = (SampleProperties) o;
    return Objects.equals(this.primary, sampleProperties.primary) &&
        Objects.equals(this.secondary, sampleProperties.secondary) &&
        Objects.equals(this.etc, sampleProperties.etc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(primary, secondary, etc);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SampleProperties {\n");
    sb.append("    primary: ").append(toIndentedString(primary)).append("\n");
    sb.append("    secondary: ").append(toIndentedString(secondary)).append("\n");
    sb.append("    etc: ").append(toIndentedString(etc)).append("\n");
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

