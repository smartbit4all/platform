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
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * CompositeObject
 */
@JsonPropertyOrder({
  CompositeObject.URI,
  CompositeObject.OBJECTS
})
@JsonTypeName("CompositeObject")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class CompositeObject {
  public static final String URI = "uri";
  private URI uri;

  public static final String OBJECTS = "objects";
  private List<ComposeableObject> objects = null;


  public CompositeObject uri(URI uri) {
    
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


  public CompositeObject objects(List<ComposeableObject> objects) {
    
    this.objects = objects;
    return this;
  }

  public CompositeObject addObjectsItem(ComposeableObject objectsItem) {
    if (this.objects == null) {
      this.objects = new ArrayList<>();
    }
    this.objects.add(objectsItem);
    return this;
  }

   /**
   * Get objects
   * @return objects
  **/
  @javax.annotation.Nullable
  @Valid
  @ApiModelProperty(value = "")
  @JsonProperty(OBJECTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<ComposeableObject> getObjects() {
    return objects;
  }


  @JsonProperty(OBJECTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setObjects(List<ComposeableObject> objects) {
    this.objects = objects;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompositeObject compositeObject = (CompositeObject) o;
    return Objects.equals(this.uri, compositeObject.uri) &&
        Objects.equals(this.objects, compositeObject.objects);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, objects);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompositeObject {\n");
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    objects: ").append(toIndentedString(objects)).append("\n");
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
