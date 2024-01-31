/*
 * View API
 * View API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.view.bean;

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
 * Style data holder for client
 */
@ApiModel(description = "Style data holder for client")
@JsonPropertyOrder({
  Style.STYLE,
  Style.CLASSES_TO_ADD,
  Style.CLASSES_TO_REMOVE
})
@JsonTypeName("Style")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class Style {
  public static final String STYLE = "style";
  private Map<String, String> style = new HashMap<>();

  public static final String CLASSES_TO_ADD = "classesToAdd";
  private List<String> classesToAdd = new ArrayList<>();

  public static final String CLASSES_TO_REMOVE = "classesToRemove";
  private List<String> classesToRemove = new ArrayList<>();

  public Style() { 
  }

  public Style style(Map<String, String> style) {
    
    this.style = style;
    return this;
  }

  public Style putStyleItem(String key, String styleItem) {
    this.style.put(key, styleItem);
    return this;
  }

   /**
   * Get style
   * @return style
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(STYLE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public Map<String, String> getStyle() {
    return style;
  }


  @JsonProperty(STYLE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setStyle(Map<String, String> style) {
    this.style = style;
  }


  public Style classesToAdd(List<String> classesToAdd) {
    
    this.classesToAdd = classesToAdd;
    return this;
  }

  public Style addClassesToAddItem(String classesToAddItem) {
    this.classesToAdd.add(classesToAddItem);
    return this;
  }

   /**
   * Get classesToAdd
   * @return classesToAdd
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CLASSES_TO_ADD)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<String> getClassesToAdd() {
    return classesToAdd;
  }


  @JsonProperty(CLASSES_TO_ADD)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setClassesToAdd(List<String> classesToAdd) {
    this.classesToAdd = classesToAdd;
  }


  public Style classesToRemove(List<String> classesToRemove) {
    
    this.classesToRemove = classesToRemove;
    return this;
  }

  public Style addClassesToRemoveItem(String classesToRemoveItem) {
    this.classesToRemove.add(classesToRemoveItem);
    return this;
  }

   /**
   * Get classesToRemove
   * @return classesToRemove
  **/
  @javax.annotation.Nonnull
  @NotNull
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(CLASSES_TO_REMOVE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<String> getClassesToRemove() {
    return classesToRemove;
  }


  @JsonProperty(CLASSES_TO_REMOVE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setClassesToRemove(List<String> classesToRemove) {
    this.classesToRemove = classesToRemove;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Style style = (Style) o;
    return Objects.equals(this.style, style.style) &&
        Objects.equals(this.classesToAdd, style.classesToAdd) &&
        Objects.equals(this.classesToRemove, style.classesToRemove);
  }

  @Override
  public int hashCode() {
    return Objects.hash(style, classesToAdd, classesToRemove);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Style {\n");
    sb.append("    style: ").append(toIndentedString(style)).append("\n");
    sb.append("    classesToAdd: ").append(toIndentedString(classesToAdd)).append("\n");
    sb.append("    classesToRemove: ").append(toIndentedString(classesToRemove)).append("\n");
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
