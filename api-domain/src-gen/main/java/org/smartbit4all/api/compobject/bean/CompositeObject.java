package org.smartbit4all.api.compobject.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.compobject.bean.ComposeableObject;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * CompositeObject
 */

public class CompositeObject   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("objects")
  @Valid
  private List<ComposeableObject> objects = null;

  public CompositeObject uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public URI getUri() {
    return uri;
  }

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
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<ComposeableObject> getObjects() {
    return objects;
  }

  public void setObjects(List<ComposeableObject> objects) {
    this.objects = objects;
  }


  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

