/*
 * Composite Object API
 * Composite Object API
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: info@it4all.hu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.smartbit4all.api.compobject.bean;

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
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * CompositeObjectAssociation
 */
@JsonPropertyOrder({
  CompositeObjectAssociation.ASSOC_DEF_URI,
  CompositeObjectAssociation.CHILD_DEF_URI,
  CompositeObjectAssociation.CHILD_RECURSIVE
})
@JsonTypeName("CompositeObjectAssociation")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CompositeObjectAssociation {
  public static final String ASSOC_DEF_URI = "assocDefUri";
  private URI assocDefUri;

  public static final String CHILD_DEF_URI = "childDefUri";
  private URI childDefUri;

  public static final String CHILD_RECURSIVE = "childRecursive";
  private Boolean childRecursive = true;


  public CompositeObjectAssociation assocDefUri(URI assocDefUri) {
    
    this.assocDefUri = assocDefUri;
    return this;
  }

   /**
   * Get assocDefUri
   * @return assocDefUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ASSOC_DEF_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getAssocDefUri() {
    return assocDefUri;
  }


  @JsonProperty(ASSOC_DEF_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAssocDefUri(URI assocDefUri) {
    this.assocDefUri = assocDefUri;
  }


  public CompositeObjectAssociation childDefUri(URI childDefUri) {
    
    this.childDefUri = childDefUri;
    return this;
  }

   /**
   * Get childDefUri
   * @return childDefUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(CHILD_DEF_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getChildDefUri() {
    return childDefUri;
  }


  @JsonProperty(CHILD_DEF_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChildDefUri(URI childDefUri) {
    this.childDefUri = childDefUri;
  }


  public CompositeObjectAssociation childRecursive(Boolean childRecursive) {
    
    this.childRecursive = childRecursive;
    return this;
  }

   /**
   * Get childRecursive
   * @return childRecursive
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(CHILD_RECURSIVE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Boolean getChildRecursive() {
    return childRecursive;
  }


  @JsonProperty(CHILD_RECURSIVE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setChildRecursive(Boolean childRecursive) {
    this.childRecursive = childRecursive;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompositeObjectAssociation compositeObjectAssociation = (CompositeObjectAssociation) o;
    return Objects.equals(this.assocDefUri, compositeObjectAssociation.assocDefUri) &&
        Objects.equals(this.childDefUri, compositeObjectAssociation.childDefUri) &&
        Objects.equals(this.childRecursive, compositeObjectAssociation.childRecursive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(assocDefUri, childDefUri, childRecursive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompositeObjectAssociation {\n");
    sb.append("    assocDefUri: ").append(toIndentedString(assocDefUri)).append("\n");
    sb.append("    childDefUri: ").append(toIndentedString(childDefUri)).append("\n");
    sb.append("    childRecursive: ").append(toIndentedString(childRecursive)).append("\n");
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

