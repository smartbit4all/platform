package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.smartbit4all.ui.api.form.model.FormDataContent;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * This form instance contains a well-defined layout for all properties of the entities. Every property is visible and accessible for the user. The exact layout is missing!!!
 */
@ApiModel(description = "This form instance contains a well-defined layout for all properties of the entities. Every property is visible and accessible for the user. The exact layout is missing!!!")

public class FixedLayoutFormInstance   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("content")
  private FormDataContent content;

  public FixedLayoutFormInstance uri(URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Get uri
   * @return uri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public FixedLayoutFormInstance content(FormDataContent content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
  */
  @ApiModelProperty(value = "")

  @Valid

  public FormDataContent getContent() {
    return content;
  }

  public void setContent(FormDataContent content) {
    this.content = content;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FixedLayoutFormInstance fixedLayoutFormInstance = (FixedLayoutFormInstance) o;
    return Objects.equals(this.uri, fixedLayoutFormInstance.uri) &&
        Objects.equals(this.content, fixedLayoutFormInstance.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FixedLayoutFormInstance {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

