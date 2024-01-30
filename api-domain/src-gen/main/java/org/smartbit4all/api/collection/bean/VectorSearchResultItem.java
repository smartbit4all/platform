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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * The vector search result items. 
 */
@ApiModel(description = "The vector search result items. ")
@JsonPropertyOrder({
  VectorSearchResultItem.SCORE,
  VectorSearchResultItem.VALUE
})
@JsonTypeName("VectorSearchResultItem")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class VectorSearchResultItem {
  public static final String SCORE = "score";
  private Float score;

  public static final String VALUE = "value";
  private Map<String, Object> value = null;

  public VectorSearchResultItem() { 
  }

  public VectorSearchResultItem score(Float score) {
    
    this.score = score;
    return this;
  }

   /**
   * The score of the found item.
   * @return score
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The score of the found item.")
  @JsonProperty(SCORE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Float getScore() {
    return score;
  }


  @JsonProperty(SCORE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setScore(Float score) {
    this.score = score;
  }


  public VectorSearchResultItem value(Map<String, Object> value) {
    
    this.value = value;
    return this;
  }

  public VectorSearchResultItem putValueItem(String key, Object valueItem) {
    if (this.value == null) {
      this.value = new HashMap<>();
    }
    this.value.put(key, valueItem);
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(VALUE)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.USE_DEFAULTS)

  public Map<String, Object> getValue() {
    return value;
  }


  @JsonProperty(VALUE)
  @JsonInclude(content = JsonInclude.Include.ALWAYS, value = JsonInclude.Include.USE_DEFAULTS)
  public void setValue(Map<String, Object> value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VectorSearchResultItem vectorSearchResultItem = (VectorSearchResultItem) o;
    return Objects.equals(this.score, vectorSearchResultItem.score) &&
        Objects.equals(this.value, vectorSearchResultItem.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(score, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VectorSearchResultItem {\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

