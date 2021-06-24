package org.smartbit4all.api.navigation.bean;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.navigation.bean.NavigationNode;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The instance of a navigation that refers to navigation config and root nodes.
 */
@ApiModel(description = "The instance of a navigation that refers to navigation config and root nodes.")

public class Navigation   {
  @JsonProperty("roots")
  @Valid
  private List<NavigationNode> roots = null;

  @JsonProperty("configUri")
  private URI configUri;

  public Navigation roots(List<NavigationNode> roots) {
    this.roots = roots;
    return this;
  }

  public Navigation addRootsItem(NavigationNode rootsItem) {
    if (this.roots == null) {
      this.roots = new ArrayList<>();
    }
    this.roots.add(rootsItem);
    return this;
  }

  /**
   * Get roots
   * @return roots
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<NavigationNode> getRoots() {
    return roots;
  }

  public void setRoots(List<NavigationNode> roots) {
    this.roots = roots;
  }

  public Navigation configUri(URI configUri) {
    this.configUri = configUri;
    return this;
  }

  /**
   * The uri of the configuration that drives the given navigation instance.
   * @return configUri
  */
  @ApiModelProperty(value = "The uri of the configuration that drives the given navigation instance.")

  @Valid

  public URI getConfigUri() {
    return configUri;
  }

  public void setConfigUri(URI configUri) {
    this.configUri = configUri;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Navigation navigation = (Navigation) o;
    return Objects.equals(this.roots, navigation.roots) &&
        Objects.equals(this.configUri, navigation.configUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roots, configUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Navigation {\n");
    
    sb.append("    roots: ").append(toIndentedString(roots)).append("\n");
    sb.append("    configUri: ").append(toIndentedString(configUri)).append("\n");
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

