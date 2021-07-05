package org.smartbit4all.ui.api.form.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.api.form.model.FormDataContent;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The detail list of data contents identified by a name.
 */
@ApiModel(description = "The detail list of data contents identified by a name.")

public class FormDataDetailContent   {
  @JsonProperty("entityUri")
  private URI entityUri;

  @JsonProperty("contents")
  @Valid
  private List<FormDataContent> contents = null;

  public FormDataDetailContent entityUri(URI entityUri) {
    this.entityUri = entityUri;
    return this;
  }

  /**
   * The uri reference for the entity definition belong to the given record. This entity uri defines the entityUris of the contents as well. This must be validated while editing.
   * @return entityUri
  */
  @ApiModelProperty(value = "The uri reference for the entity definition belong to the given record. This entity uri defines the entityUris of the contents as well. This must be validated while editing.")

  @Valid

  public URI getEntityUri() {
    return entityUri;
  }

  public void setEntityUri(URI entityUri) {
    this.entityUri = entityUri;
  }

  public FormDataDetailContent contents(List<FormDataContent> contents) {
    this.contents = contents;
    return this;
  }

  public FormDataDetailContent addContentsItem(FormDataContent contentsItem) {
    if (this.contents == null) {
      this.contents = new ArrayList<>();
    }
    this.contents.add(contentsItem);
    return this;
  }

  /**
   * Get contents
   * @return contents
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<FormDataContent> getContents() {
    return contents;
  }

  public void setContents(List<FormDataContent> contents) {
    this.contents = contents;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FormDataDetailContent formDataDetailContent = (FormDataDetailContent) o;
    return Objects.equals(this.entityUri, formDataDetailContent.entityUri) &&
        Objects.equals(this.contents, formDataDetailContent.contents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entityUri, contents);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FormDataDetailContent {\n");
    
    sb.append("    entityUri: ").append(toIndentedString(entityUri)).append("\n");
    sb.append("    contents: ").append(toIndentedString(contents)).append("\n");
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

