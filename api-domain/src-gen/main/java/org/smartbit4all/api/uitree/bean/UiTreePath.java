/*
 * Uitree Api
 * Tree domain objects
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.uitree.bean;

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
import java.util.List;
import org.smartbit4all.api.uitree.bean.UiTreePathPart;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * UiTreePath
 */
@JsonPropertyOrder({
  UiTreePath.PARTS
})
@JsonTypeName("UiTreePath")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class UiTreePath {
  public static final String PARTS = "parts";
  private List<UiTreePathPart> parts = new ArrayList<>();

  public UiTreePath() { 
  }

  public UiTreePath parts(List<UiTreePathPart> parts) {
    
    this.parts = parts;
    return this;
  }

  public UiTreePath addPartsItem(UiTreePathPart partsItem) {
    this.parts.add(partsItem);
    return this;
  }

   /**
   * Get parts
   * @return parts
  **/
  @javax.annotation.Nonnull
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(PARTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<UiTreePathPart> getParts() {
    return parts;
  }


  @JsonProperty(PARTS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setParts(List<UiTreePathPart> parts) {
    this.parts = parts;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UiTreePath uiTreePath = (UiTreePath) o;
    return Objects.equals(this.parts, uiTreePath.parts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UiTreePath {\n");
    sb.append("    parts: ").append(toIndentedString(parts)).append("\n");
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

