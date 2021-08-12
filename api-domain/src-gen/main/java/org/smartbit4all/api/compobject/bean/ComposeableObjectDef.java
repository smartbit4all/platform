package org.smartbit4all.api.compobject.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ComposeableObjectDef
 */

public class ComposeableObjectDef   {
  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("apiUri")
  private URI apiUri;

  @JsonProperty("name")
  private String name;

  @JsonProperty("viewName")
  private String viewName;

  @JsonProperty("icon")
  private String icon;

  public ComposeableObjectDef uri(URI uri) {
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

  public ComposeableObjectDef apiUri(URI apiUri) {
    this.apiUri = apiUri;
    return this;
  }

  /**
   * Get apiUri
   * @return apiUri
  */
  @ApiModelProperty(value = "")

  @Valid

  public URI getApiUri() {
    return apiUri;
  }

  public void setApiUri(URI apiUri) {
    this.apiUri = apiUri;
  }

  public ComposeableObjectDef name(String name) {
    this.name = name;
    return this;
  }

  /**
   * NavigationEntry name.
   * @return name
  */
  @ApiModelProperty(value = "NavigationEntry name.")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ComposeableObjectDef viewName(String viewName) {
    this.viewName = viewName;
    return this;
  }

  /**
   * Get viewName
   * @return viewName
  */
  @ApiModelProperty(value = "")


  public String getViewName() {
    return viewName;
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public ComposeableObjectDef icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Get icon
   * @return icon
  */
  @ApiModelProperty(value = "")


  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComposeableObjectDef composeableObjectDef = (ComposeableObjectDef) o;
    return Objects.equals(this.uri, composeableObjectDef.uri) &&
        Objects.equals(this.apiUri, composeableObjectDef.apiUri) &&
        Objects.equals(this.name, composeableObjectDef.name) &&
        Objects.equals(this.viewName, composeableObjectDef.viewName) &&
        Objects.equals(this.icon, composeableObjectDef.icon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, apiUri, name, viewName, icon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComposeableObjectDef {\n");
    
    sb.append("    uri: ").append(toIndentedString(uri)).append("\n");
    sb.append("    apiUri: ").append(toIndentedString(apiUri)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    viewName: ").append(toIndentedString(viewName)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
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

