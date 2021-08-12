package org.smartbit4all.api.compobject.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.smartbit4all.api.compobject.bean.ComposeableObjectDef;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ComposeableObject
 */

public class ComposeableObject   {
  @JsonProperty("definition")
  private ComposeableObjectDef definition;

  @JsonProperty("objectUri")
  private URI objectUri;

  public ComposeableObject definition(ComposeableObjectDef definition) {
    this.definition = definition;
    return this;
  }

  /**
   * Get definition
   * @return definition
  */
  @ApiModelProperty(value = "")

  @Valid

  public ComposeableObjectDef getDefinition() {
    return definition;
  }

  public void setDefinition(ComposeableObjectDef definition) {
    this.definition = definition;
  }

  public ComposeableObject objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  /**
   * Get objectUri
   * @return objectUri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getObjectUri() {
    return objectUri;
  }

  public void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComposeableObject composeableObject = (ComposeableObject) o;
    return Objects.equals(this.definition, composeableObject.definition) &&
        Objects.equals(this.objectUri, composeableObject.objectUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(definition, objectUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComposeableObject {\n");
    
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
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

