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
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.compobject.bean.CompositeObjectAssociation;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * CompositeObjectDef
 */
@JsonPropertyOrder({
  CompositeObjectDef.URI,
  CompositeObjectDef.DEF_URI,
  CompositeObjectDef.ASSOCIATIONS
})
@JsonTypeName("CompositeObjectDef")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CompositeObjectDef {
  public static final String URI = "uri";
  private URI uri;

  public static final String DEF_URI = "defUri";
  private URI defUri;

  public static final String ASSOCIATIONS = "associations";
  private List<CompositeObjectAssociation> associations = null;


  public CompositeObjectDef uri(URI uri) {
    
    this.uri = uri;
    return this;
  }

   /**
   * Get uri
   * @return uri
  **/
  @NotNull
  @Valid
  @ApiModelProperty(required = true, value = "")
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


  public CompositeObjectDef defUri(URI defUri) {
    
    this.defUri = defUri;
    return this;
  }

   /**
   * Get defUri
   * @return defUri
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(DEF_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public URI getDefUri() {
    return defUri;
  }


  @JsonProperty(DEF_URI)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDefUri(URI defUri) {
    this.defUri = defUri;
  }


  public CompositeObjectDef associations(List<CompositeObjectAssociation> associations) {
    
    this.associations = associations;
    return this;
  }

  public CompositeObjectDef addAssociationsItem(CompositeObjectAssociation associationsItem) {
    if (this.associations == null) {
      this.associations = new ArrayList<>();
    }
    this.associations.add(associationsItem);
    return this;
  }

   /**
   * Get associations
   * @return associations
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(ASSOCIATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<CompositeObjectAssociation> getAssociations() {
    return associations;
  }


  @JsonProperty(ASSOCIATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAssociations(List<CompositeObjectAssociation> associations) {
    this.associations = associations;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompositeObjectDef compositeObjectDef = (CompositeObjectDef) o;
    return Objects.equals(this.uri, compositeObjectDef.uri) &&
        Objects.equals(this.defUri, compositeObjectDef.defUri) &&
        Objects.equals(this.associations, compositeObjectDef.associations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, defUri, associations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompositeObjectDef {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    defUri: ").append(toIndentedString(defUri)).append("\n");
    sb.append("    associations: ").append(toIndentedString(associations)).append("\n");
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
